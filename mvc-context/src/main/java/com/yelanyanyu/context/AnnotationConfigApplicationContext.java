package com.yelanyanyu.context;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.yelanyanyu.annotation.*;
import com.yelanyanyu.exception.*;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.io.ResourceResolver;
import com.yelanyanyu.util.ClassPathUtils;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class AnnotationConfigApplicationContext implements ConfigurableApplicationContext {
    PropertyResolver propertyResolver;
    Map<String, BeanDefinition> beans;
    Set<String> creatingBeanNames;
    List<BeanPostProcessor> postProcessors;

    /**
     * @param configClass      配置类的class对象，里面有ComponentScan注解
     * @param propertyResolver 静态配置集合
     */
    public AnnotationConfigApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
        Set<String> strings = scanForClassNames(configClass);
        this.beans = createBeanDefinitions(strings);
        this.creatingBeanNames = new ConcurrentHashSet<>();
        this.postProcessors = new ArrayList<>();

        /*
            创建bean实例
         */
        // 解决强循环依赖
        this.beans.values().stream().filter(this::isConfigurationDef).map(def -> {
            createBeanAsEarlySingleton(def);
            return def.getName();
        }).toList();

        // 注入BeanPostProcessor
        List<BeanPostProcessor> processors = this.beans.values().stream().
                filter(this::isBeanPostProcessorDefinition).
                sorted().
                map(def -> (BeanPostProcessor) createBeanAsEarlySingleton(def)).
                toList();

        this.postProcessors.addAll(processors);


        // 解决普通的循环依赖并注入

        // 找出所有未被注入的bean，即instance==null的类
        List<BeanDefinition> defs = this.beans.values().stream().filter(def -> def.getInstance() == null).sorted().toList();
        defs.forEach(def -> {
            if (def.getInstance() == null) {
                createBeanAsEarlySingleton(def);
            }
        });

        // 弱依赖注入
        this.beans.values().forEach(this::injectBean);

        // 调用@PostConstruct 注释的init方法
        this.beans.values().forEach(this::initBean);

        if (log.isDebugEnabled()) {
            this.beans.values().stream().sorted().forEach(def -> {
                log.debug("bean initialized: {}", def);
            });
        }
    }

    boolean isBeanPostProcessorDefinition(BeanDefinition def) {
        return BeanPostProcessor.class.isAssignableFrom(def.getBeanClass());
    }

    /**
     * 调用@PostConstruct 注释的init方法
     *
     * @param def
     */
    void initBean(BeanDefinition def) {
        try {
            callMethod(def.getInstance(), def.getInitMethod(), def.getInitMethodName());
            this.postProcessors.forEach(beanPostProcessor -> {
                Object postInstance = beanPostProcessor.postProcessAfterInitialization(def.getInstance(), def.getName());
                if (postInstance != def.getInstance()) {
                    def.setInstance(postInstance);
                }
            });
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * invoke the init and destroy methods
     *
     * @param instance
     * @param method
     * @param namedMethod
     */
    private void callMethod(Object instance, Method method, String namedMethod) throws InvocationTargetException, IllegalAccessException {
        if (method != null) {
            method.invoke(instance);
        } else if (namedMethod != null) {
            Method mn = ClassPathUtils.findMethodByName(instance.getClass(), namedMethod);
            mn.setAccessible(true);
            mn.invoke(instance);
        }
    }

    /**
     * Perform dependency injection for the bean represented by 'def'
     *
     * @param def def
     */
    void injectBean(BeanDefinition def) {
        // The object should not be a proxy object got by 'def.getInstance()'.
        Object originBean = getOriginBean(def);
        try {
            injectProperties(def, def.getBeanClass(), originBean);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the original bean
     * @param def
     * @return
     */
    Object getOriginBean(BeanDefinition def) {
        Object instance = def.getInstance();
        ArrayList<BeanPostProcessor> reversedBeanPostProcessors = new ArrayList<>(this.postProcessors);
        // 处理多次代理的情况, 需要用倒序遍历的方式解决
        Collections.reverse(reversedBeanPostProcessors);
        for (BeanPostProcessor beanPostProcessor : reversedBeanPostProcessors) {
            // 调用方法恢复 bean
            Object restoredInstance = beanPostProcessor.postProcessOnSetProperty(instance, def.getName());
            if (restoredInstance != instance) {
                instance = restoredInstance;
            }
        }
        return instance;
    }

    void injectProperties(BeanDefinition def, Class<?> clazz, Object bean) throws InvocationTargetException, IllegalAccessException {
        for (Field f : clazz.getDeclaredFields()) {
            tryInjectProperties(def, clazz, bean, f);
        }
        for (Method m : clazz.getDeclaredMethods()) {
            tryInjectProperties(def, clazz, bean, m);
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            injectProperties(def, superclass, bean);
        }
    }

    /**
     * inject a single property, which could be annotated with @Value or @Autowired.
     *
     * @param def              def
     * @param clazz            clazz
     * @param bean             bean
     * @param accessibleObject field or method
     */
    void tryInjectProperties(BeanDefinition def, Class<?> clazz, Object bean, AccessibleObject accessibleObject) throws IllegalAccessException, InvocationTargetException {
        Value value = accessibleObject.getAnnotation(Value.class);
        Autowired autowired = accessibleObject.getAnnotation(Autowired.class);

        if (value == null && autowired == null) {
            return;
        }
        // 检查工作
        Field field = null;
        // setter 方法
        Method method = null;
        if (accessibleObject instanceof Field f) {
            checkFieldOrMethod(f);
            f.setAccessible(true);
            field = f;
        }
        if (accessibleObject instanceof Method m) {
            checkFieldOrMethod(m);
            if (!m.getName().startsWith("set") || m.getParameterCount() != 1) {
                throw new BeanDefinitionException(
                        String.format("cannot inject non-setter method %s for bean %s", m.getName(), def.getBeanClass().getSimpleName())
                );
            }
            m.setAccessible(true);
            method = m;
        }

        String accName = field != null ? field.getName() : method.getName();
        if (value != null && autowired != null) {
            throw new BeanCreationException(String.format(
                    "inject error when inject field %s for bean %s: one field cannot have both @Autowired and @Value",
                    accName, def.getBeanClass().getName()
            ));
        }

        Class<?> accType = field != null ? field.getType() : method.getReturnType();
        if (value != null) {
            Object property = this.propertyResolver.getProperty(value.value(), accType);
            if (field != null) {
                field.set(bean, property);
            }
            if (method != null) {
                method.invoke(bean, property);
            }
        }

        if (autowired != null) {
            String name = autowired.name();
            boolean required = autowired.value();
            Object dependObj = name.isEmpty() ? findBean(accType) : findBean(accType, name);
            if (required && dependObj == null) {
                throw new UnsatisfiedDependencyException("required but no such bean  " + dependObj);
            }

            // 注入bean
            if (dependObj != null) {
                if (field != null) {
                    field.set(bean, dependObj);
                }
                if (method != null) {
                    method.invoke(bean, dependObj);
                }
            }
        }
    }

    /**
     * 检查是否可以注入 ：1. 静态方法无法注入；2. final方法无法注入
     *
     * @param m member
     */
    void checkFieldOrMethod(Member m) {
        int mod = m.getModifiers();
        if (Modifier.isStatic(mod)) {
            throw new BeanDefinitionException("cannot inject static field: " + m);
        }
        if (Modifier.isFinal(mod)) {
            throw new BeanDefinitionException("cannot inject final field: " + m);
        }
    }

    /**
     * 判断def所代表的类是否被@Configuration注释
     *
     * @param def def
     * @return boolean
     */
    boolean isConfigurationDef(BeanDefinition def) {
        return ClassPathUtils.findAnnotation(def.getBeanClass(), Configuration.class) != null;
    }

    /**
     * 创建bean实例，但是还未初始化, 具体来说，创建由构造器和工厂方法注入的bean
     *
     * @param def def
     * @return obj
     */
    @Override
    public Object createBeanAsEarlySingleton(BeanDefinition def) {
        log.debug("try create Bean '{}' as early singleton: {}", def.getName(), def.getBeanClass().getName());
        // 解决强循环依赖
        if (!this.creatingBeanNames.add(def.getName())) {
            throw new UnsatisfiedDependencyException("Duplicated bean named " + def.getName() + " are creating");
        }

        Executable creatFn = def.getFactoryName() == null ? def.getConstructor() : def.getFactoryMethod();
        // 强依赖注入, 通过工厂方法或构造方法注入
        Parameter[] parameters = creatFn.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class<?> type = param.getType();
            //检查参数是否有 @Value 和 @Autowired
            Value v = param.getAnnotation(Value.class);
            Autowired autowired = param.getAnnotation(Autowired.class);
            if (v != null && autowired == null) {
                args[i] = this.propertyResolver.getProperty(v.value(), type);
            } else if (autowired != null && v == null) {
                // 如果bean是工厂，则不能用@Autowired创建, 因为工厂应该是最先被实例化的
                if (isConfigurationDef(def)) {
                    throw new BeanCreationException(String.format("Cannot specify @Autowired when creating @Configuration bean %s : %s",
                            def.getName(), def.getBeanClass().getName()));
                }

                String name = autowired.name();
                boolean required = autowired.value();
                // 得到依赖的bean的BeanDefinition
                BeanDefinition dependenceDef = name.isEmpty() ? findBeanDefinition(type) : findBeanDefinition(name, type);
                // 看看是否已经注入
                Object obj = Objects.requireNonNull(dependenceDef).getInstance();
                if (required && obj == null) {
                    obj = createBeanAsEarlySingleton(dependenceDef);
                }
                args[i] = obj;
            } else {
                args[i] = null;
            }
        }
        Object instance = null;
        // 通过构造器创建
        if (def.getFactoryName() == null) {
            try {
                instance = def.getConstructor().newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new BeanCreationException(String.format("Failed to create bean %s : %s with constructor %s",
                        def.getBeanClass().getSimpleName(), def.getName(), def.getConstructor().getName()));
            }
        } else { // 通过工厂方法(即被@Configuration注释的类)创建
            Object bean = getBean(def.getFactoryName());
            try {
                instance = def.getFactoryMethod().invoke(bean, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanCreationException(String.format("Failed to create bean %s : %s with factory %s", def.getBeanClass().getSimpleName(),
                        def.getName(), def.getFactoryName()));
            }
        }
        def.setInstance(instance);


        for (BeanPostProcessor processor : this.postProcessors) {
            Object proc = processor.postProcessBeforeInitialization(def.getInstance(), def.getName());

            if (proc != def.getInstance()) {
                log.debug("bean '{}' was replaced by the processor: {}", def.getName(), processor.getClass().getName());
                def.setInstance(proc);
            }
        }


        return def.getInstance();
    }

    @Override
    @Nullable
    public <T> T getBean(String name) {
        BeanDefinition def = findBeanDefinition(name);
        if (def == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s'.", name));
        }
        return (T) def.getRequiredInstance();
    }

    @Override
    @Nullable
    public <T> T getBean(String name, Class<T> type) {
        T bean = findBean(type, name);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(String.format(
                    "No bean defined with type '%s' and name '%s'.", type, name
            ));
        }
        return bean;
    }

    @Override
    public <T> List<T> getBeans(Class<T> type) {
        return (List<T>) findBeanDefinitions(type).stream().map(def -> def.getRequiredInstance()).sorted().toList();
    }

    @Override
    public void close() {
        this.beans.values().stream().filter(this::hasDestroyMethod).forEach(def -> {
            Method m = def.getDestoyMethod();
            // watch out, it has to be the origin bean
            Object instance = getOriginBean(def);
            if (instance == null) {
                throw new BeanDefinitionException(String.format(
                        "bean instance is null when executing pre-destroy method for bean '%s'", def.getBeanClass().getName()));
            }
            if (m.getParameterCount() != 0) {
                throw new BeanDefinitionException(String.format(
                        "pre-destroy method '%s: %s' has multiple parameters", def.getBeanClass().getName(), def.getDestoyMethod()));
            }
            try {
                callMethod(def.getInstance(), m, def.getDestroyMethodName());
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    boolean hasDestroyMethod(BeanDefinition def) {
        return def.getDestoyMethod() != null;
    }

    @Override
    public boolean containsBean(String name) {
        return findBeanDefinition(name) != null;
    }

    @Override
    @Nullable
    public <T> T getBean(Class<T> type) {
        T bean = findBean(type);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(String.format(
                    "No bean defined with type '%s'", type
            ));
        }
        return bean;
    }

    public Map<String, BeanDefinition> getBeans() {
        return beans;
    }

    /**
     * find all classes from the given package of @ComponentScan
     *
     * @param config class where has the @Component
     * @return name set of all the classes
     */
    public Set<String> scanForClassNames(Class<?> config) {
        Set<String> res = new ConcurrentHashSet<>(16);
        ComponentScan scan = ClassPathUtils.findAnnotation(config, ComponentScan.class);
        String[] scanPackage = scan == null || scan.value().length == 0 ? new String[]{config.getPackage().getName()} : scan.value();
        for (String pkg : scanPackage) {
            ResourceResolver rr = new ResourceResolver(pkg);
            List<String> classNames = rr.scan(resource -> {
                String name = resource.name();
                if (name.endsWith(".class")) {
                    return name.substring(0, name.length() - 6).
                            replace("/", ".").replace("\\", ".");
                }
                return null;
            });
            res.addAll(classNames);
        }
        Import impAnno = config.getAnnotation(Import.class);
        if (impAnno != null) {
            for (Class<?> clazz : impAnno.value()) {
                res.add(clazz.getName());
            }
        }
        return res;
    }

    Map<String, BeanDefinition> createBeanDefinitions(Set<String> classNames) {
        ConcurrentHashMap<String, BeanDefinition> defs = new ConcurrentHashMap<>();
        //扫描所有以类的形式呈现的bean
        for (String className : classNames) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Component component = ClassPathUtils.findAnnotation(clazz, Component.class);
            log.debug("found component in bean: {}", clazz.getName());
            if (component != null) { //要注入map
                String beanName = ClassPathUtils.getBeanName(clazz);
                BeanDefinition def = new BeanDefinition(beanName, clazz,
                        getSuitableConstructor(clazz),
                        getOrder(clazz),
                        clazz.isAnnotationPresent(Primary.class),
                        null, null, ClassPathUtils.findAnnotationMethod(PostConstruct.class, clazz),
                        ClassPathUtils.findAnnotationMethod(PreDestroy.class, clazz));

                addBeanDefinition(defs, def);
                log.debug("define bean: {}", def);
                Configuration configuration = ClassPathUtils.findAnnotation(clazz, Configuration.class);
                //若类中还有@Bean注解，那么就要以工厂模式注入
                if (configuration != null) {
                    scanFactoryMethods(beanName, clazz, defs);
                }

            }
        }
        return defs;
    }

    /**
     * 找到合适的构造器, 一个合适的构造器只能有一个
     *
     * @param clazz bean class
     * @return constructor
     */
    Constructor<?> getSuitableConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            Constructor<?>[] dc = clazz.getDeclaredConstructors();
            if (dc.length != 1) {
                throw new BeanDefinitionException("only one constructor allowed in bean " + ClassPathUtils.getBeanName(clazz));
            }
        }
        if (constructors.length != 1) {
            throw new BeanDefinitionException("only one public constructor allowed in bean " + ClassPathUtils.getBeanName(clazz));
        }
        return constructors[0];
    }

    int getOrder(Class<?> clazz) {
        Order order = clazz.getAnnotation(Order.class);
        int value = Integer.MAX_VALUE;
        if (order != null) {
            value = order.value();
        }
        return value;
    }

    int getOrder(Method method) {
        Order order = method.getAnnotation(Order.class);
        int value = Integer.MAX_VALUE;
        if (order != null) {
            value = order.value();
        }
        return value;
    }

    /**
     * put the bean with annotation @Bean into def map
     *
     * @param beanNameFactory name of bean with @Configuration and has @Bean method
     * @param clazz
     * @param defs
     */
    void scanFactoryMethods(String beanNameFactory, Class<?> clazz, Map<String, BeanDefinition> defs) {
        for (Method method : clazz.getDeclaredMethods()) {
            Bean bean = method.getAnnotation(Bean.class);
            if (bean != null) {
                Class<?> returnType = method.getReturnType();
                BeanDefinition def = new BeanDefinition(ClassPathUtils.getBeanName(method), returnType,
                        getSuitableConstructor(clazz),
                        getOrder(clazz),
                        clazz.isAnnotationPresent(Primary.class),
                        bean.initMethod().isEmpty() ? null : bean.initMethod(),
                        bean.destroyMethod().isEmpty() ? null : bean.destroyMethod(),
                        null, null,
                        method, beanNameFactory);
                addBeanDefinition(defs, def);
                log.debug("define bean: {}", def);
            }
        }
    }

    /**
     * findBean 和 getBean 逻辑类似，区别在于前者可以返回null，后者不会返回null，而回直接报错
     *
     * @param type
     * @param name
     * @return
     */
    @Nullable
    protected <T> T findBean(Class<T> type, String name) {
        BeanDefinition def = findBeanDefinition(name, type);
        if (def == null) {
            return null;
        }
        return (T) def.getRequiredInstance();
    }

    @Nullable
    protected <T> T findBean(Class<T> type) {
        BeanDefinition def = findBeanDefinition(type);
        if (def == null) {
            return null;
        }
        return (T) def.getRequiredInstance();
    }


    /**
     * add bean def into beans, multiple beans are not allowed
     *
     * @param defs beans map
     * @param def  bean def
     */
    void addBeanDefinition(Map<String, BeanDefinition> defs, BeanDefinition def) {
        //判断bean是否重复
        if (defs.put(def.getName(), def) != null) {
            throw new NoUniqueBeanDefinitionException(String.format("bean %s should be unique", def.getName()));
        }
    }


    /**
     * 得到所有类型为type的bean信息，包括声明类型为type的bean
     *
     * @param type ABC.class
     * @return list
     */
    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return this.beans.values().stream().filter(def -> type.isAssignableFrom(def.getBeanClass())).sorted().collect(Collectors.toList());
    }

    /**
     * Get the unique singleton bean through the 'get(ABC.class)'.
     *
     * @param type ABC.class
     * @return beandef
     */
    @Override
    @Nullable
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> defs = findBeanDefinitions(type);
        if (defs == null || defs.isEmpty()) {
            return null;
        }
        log.debug("defs: {}", defs);
        if (defs.size() == 1) {
            return defs.get(0);
        }
        //如果找到的对象不唯一就遍历找出有primary注释的bean
        List<BeanDefinition> collect = defs.stream().filter(BeanDefinition::isPrimary).toList();
        if (collect.size() == 1) {
            return collect.get(0);
        }
        if (collect.isEmpty()) {
            throw new NoUniqueBeanDefinitionException(String.format("有重复的被@Primary注释的bean, 名字为: %s", type.getName()));
        } else {
            throw new NoUniqueBeanDefinitionException(String.format("有重复的被@Primary注释的bean, 名字为: %s", type.getName()));
        }
    }

    @Override
    @Nullable
    public BeanDefinition findBeanDefinition(String name) {
        return this.beans.get(name);
    }

    /**
     * Find the BeanDefinition based on name and type. If the Name does not exist, return null. If exists but type does not match, throw an exception.
     *
     * @param type
     * @param name
     * @return
     */
    @Override
    @Nullable
    public BeanDefinition findBeanDefinition(String name, Class<?> type) {
        BeanDefinition def_name = findBeanDefinition(name);
        if (def_name == null) {
            return null;
        }
        if (!type.isAssignableFrom(def_name.getBeanClass())) {
            throw new BeanNotOfRequiredTypeException(String.format("bean %s is not matched with type %s", name, type.getSimpleName()));
        }
        return def_name;
    }
}
