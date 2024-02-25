
源码仓库地址：[yelanyanyu/winter-framework (github.com)](https://github.com/yelanyanyu/winter-framework)
# 基本内容
这个项目预计完成一个小型的类 spring 的项目。要实现的功能是：
+ 实现 DispatcherServlet 的分发功能；
+ 实现 HandlerMapping 和 HandlerAdapter 匹配 url 的功能；
+ 实现 PathVariable 解析的功能；
+ 实现 Get 和 Post 分发请求的功能；
+ 实现 ViewResolver 视图的解析功能；
+ 支持 jsp 的解析；
+ 支持 boot 应用开发；
+ 支持 aop 功能；
+ 支持 jdbc；
## 开发以及测试环境
Java 17 + tomcat 10.1.17 + servlet-api 6.0 + maven 3.9.4
IDE：IntelliJ IDEA 2023.2 + postman

## 未来开发规划
- [x] JdbcTemplate 以及事务支持，用于简化数据库的操作； ✅ 2023-12-21
- [x] 将 tomcat 8 升级为 10，Servlet 3.1 升级为 Servlet 4.0； ✅ 2023-12-21
- [x] 提供原生 Filter 支持，支持用户在 boot 程序中注册 Filter； ✅ 2023-12-23
- [ ] 提供原生 Servlet 支持，支持用户在 boot 程序中注册 Servlet；
- [ ] 优化 context 源码：
   - 对于 BeanDefinition 的创建，构造器的方法参数实在太多了，可以考虑使用工厂方法模式进行创建；
   - 增加 Bean 注解 value 的支持；
- [x] AOP 支持； ✅ 2023-12-21
- [x] 实现类似于 Boot 的支持，使用默认配置，简化配置操作； ✅ 2023-12-23
- [x] 优化 boot 配置功能，使得实现 spring-boot 一样的直接启动（在非 ide 环境下），而不需要解压 jar； ✅ 2024-02-25
- [ ] 代码重构与简化；
- [ ] 实现文件上传和下载的 api 简化；
- [ ] 性能优化，实现静态资源的 cache 缓存功能；
- [ ] 支持异步；

## 目录结构
```text
├─.github
│  └─ISSUE_TEMPLATE
├─.idea
│  ├─artifacts
│  └─inspectionProfiles
├─framework-test # 测试程序
│  └─src
│      └─main
│          ├─java
│          │  └─com
│          │      └─yelanyanyu
│          │          ├─bean
│          │          ├─config
│          │          └─controller
│          ├─resources
│          └─webapp
│              └─WEB-INF # 资源目录
│                  └─views
├─mvc-context # 容器实现
│  ├─logs
│  └─src
│      ├─main
│      │  ├─java
│      │  │  └─com
│      │  │      └─yelanyanyu
│      │  │          ├─annotation # 注解
│      │  │          ├─context # 容器实现
│      │  │          ├─exception # 自定义异常
│      │  │          ├─io # 读取 bean 以及 配置文件相关
│      │  │          └─util # 工具类
│      │  └─resources
│      └─test
│          ├─java
│          │  └─com
│          │      ├─bean
│          │      └─yelanyanyu
│          └─resources
└─mvc-webmvc # web mvc实现
    ├─logs
    └─src
        ├─main
        │  ├─java
        │  │  └─com
        │  │      └─yelanyanyu
        │  │          └─webmvc
        │  │              ├─annotation # 注解
        │  │              ├─bean 
        │  │              ├─exception # 自定义注解
        │  │              ├─util # 工具类
        │  │              └─view # 视图与视图解析器
        │  └─resources
        └─test
            ├─java
            │  └─com
            │      └─yelanyanyu
            │          └─bean
            └─resources

```


# 快速开始
>本章旨在快速上手该框架。
## JSP 开发
### 项目配置
假设我们要开发一个 jsp 的前后端半分离项目。
1. 在源目录下新建配置 `.../config/WebMvcConfiguration.java`，该类用注解 `@Configuration` 修饰，表示该类是一个配置类；
```java
@Configuration
public class WebConfiguration {
}
```

2. 定义视图解析器 InternalResourceViewResolver：
```java
@Configuration
public class WebConfiguration {
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver irv = new InternalResourceViewResolver();
        irv.setPrefix("/WEB-INF/views/");
        irv.setSuffix(".jsp");
        return irv;
    }
}
```
>**解读：**
>+ `irv.setPrefix("/WEB-INF/views/");` 表示资源的前缀，该前缀将与后面的 `@GetMapping` 和 `@PostMapping` 搭配使用；
>+ `irv.setSuffix(".jsp");` 标识资源的后缀，请求转发的资源将与该后缀拼接，若资源名为 `success`，则将会拼接成 `success.jsp`；
>+ `@Bean` 是工厂注解，若有方法被 `@Bean` 修饰，则该方法的返回值将被加入 ioc 容器，在本例中，会将视图解析器 irv 注册到 ioc；

3. 在顶层目录新建一个根配置类 `WebConfig`，要求其他类都是该配置类的同级，或者子级，比如说：
   ![image.png](https://yelanyanyu-img-bed.oss-cn-hangzhou.aliyuncs.com/img/20231120211504.png)
```java
@ComponentScan
@Configuration
@Import(WebMvcConfiguration.class)
@Order(1)
public class WebConfig {
}
```

>**解读：**
> + `@Configuration` 表示该类是配置类；
> + `@ComponentScan` 可以指定，要扫描的包（指定那些类应该加入容器维护）；
> + `@Import(WebMvcConfiguration.class)` 表示引入刚刚定义的配置类；
> + `@Order(1)` 表示，当容器启动时，会优先加载这个类；

***
> #### 注意
> + 请最好把 jsp 等资源放置在目录 `/WEB-INF/` 中，以确保资源被正确的被中间件（例如 Tomcat）加载；
> + 资源仅仅只支持后缀 `.jsp`，请不要更改；
### 快速编写 Controller
1. 通过在类上使用注解 `@Controller` 或者 `@RestController` 使得该类成为 controller。
   例如：
```java
@Controller
public class TestController {
}
```

2. 通过在该类声明一个方法，并且加上注解 `@GetMapping` 使得声明一个接受以 get 为请求类型的 Handler 方法，通过 `@PostMapping` 接收 Post 型的请求。例如：
```java
@Controller
public class TestController {
    @GetMapping("/t1")
    public ModelAndView t1() {
        return null;
    }

    @PostMapping("/test/t2")
    public ModelAndView t2() {
        return null;
    }
}

```
>解读：
>+ 我们可以通过修改注解的 value 值，来制定 uri，使得每一个请求 url 可以调用相应的方法进行处理；
>+ 假如我们的 context-path 为 `/test`，那么若想要调用 `t2()`，对应的请求 url 就为 `http://your-ip-address:port/test/t2`；

3. 我们可以创建 `ModelAndView` 对象来向 Request 域中添加变量，并且将 request 请求转发到固定资源，或者重定向：
```java
@Controller
public class TestController {
    @GetMapping("/t1")
    public ModelAndView t1() {
	    ModelAndView mv = new ModelAndView("forward:success");
        return mv;
    }

    @PostMapping("/test/t2")
    public ModelAndView t2() {
        ModelAndView mv = new ModelAndView("redirect:success");
        return mv;
    }
}
```
>**解读：**
>+ 当方法 `t1()`，被调用后，将会转发到资源 `[prefix]success.jsp`，其中 prefix 是初始配置中设定的值；
>+ 当方法 `t2()` 被调用后，将会将页面重定向到 `http://ip:port/success`。注意，重定向将不会与之前设定的后缀 `.jsp` 进行拼接；

## 基于 JSON 的前后端分离
该项目最主要支持的是 json 数据的返回，从而实现前后端的完全分离。还可以返回二进制流以用于文件的上传和下载。

返回 json 数据的核心是以下几种注解：
+ 使用 `@RestController` 注解来指定 Controller；
+ 使用 `@ResponseBody` 来注释方法；
+ 使用 `@RequestBody` 来注释 handler 参数；

### RestController
#### 序列化对象
如果一个类被 `@RestController` 注释，那么该类中的任何方法的返回值都会被序列化为 JSON 格式的数据，返回。举个例子，假如我们有一个对象 Member，如果我们在方法中返回 Member 对象，那么底层就会将其序列化成为 json 数据：
```java
@RestController
public class DemoController {
    @GetMapping("/t3")
    public Member t3() {
        return new Member("asdfasf", 100);
    }
}
```
返回的数据：
```json
{"name":"asdfasf","id":100}
```
#### 序列化字符串
如果方法返回的是 String 类型的字符串，那么也会 json 的形式进行解析返回：
```java
@RestController
public class DemoController {
    @GetMapping("/t4")
    public String t4() {
        return "{\"name\":\"asdfasf\",\"id\":100}";
    }
}
```
返回的数据：
```json
{"name":"asdfasf","id":100}
```

#### 返回二进制流
由于有文件上传和下载的需求，所以返回非 json 格式的二进制流也是必须的。我们可以通过返回一个 byte 数组也返回二进制流。
```java
@RestController
public class DemoController {
    @GetMapping("/t5")
    public byte[] t5() {
	    // read from something
        return new byte[100];
    }
}
```

### ResponseBody
假如一个类并没有被 `@RestController` 注释，那么我们仍然可以通过注释 `@ResponseBody` 来返回 json 格式的数据或者二进制流。

```java
@Controller
public class DemoController {
    @GetMapping("/t3")
    @ResponseBody
    public Member t3() {
        return new Member("asdfasf", 100);
    }

    @GetMapping("/t4")
    @ResponseBody
    public String t4() {
        return "{\"name\":\"asdfasf\",\"id\":100}";
    }

    @GetMapping("/t5")
    @ResponseBody
    public byte[] t5() {
        return new byte[100];
    }
}
```
> **解读：**
>  + 上述代码的效果和被注解 `@RestController` 注释的效果是相同的；

### RequestBody
如果有一个方法中的某个参数被 `@RequestBody` 注释，那么该参数就会封装 json 形式的 request 参数。

**方法：**
```java
@RestController
@Slf4j
public class DemoController {
    @GetMapping("/t6")
    public Member t6(@RequestBody Member member, @RequestParam("id") Integer id) {
        log.info("id: {}", id);
        return member;
    }
}
```

**请求参数：**
```text
body: {"name":"wdnmd","id":100}
url: http://localhost:8080/mvc/t6?id=200
```

**结果返回：**
```json
{
    "name": "wdnmd",
    "id": 100
}
```
```text
console: id: 200
```

> **解读：**
>  + 参数 `member` 被 `@RequestBody` 注释时，request 域中的 json 格式的参数就会被自动封装成一个对象，并且不影响其他参数的接受；

## 其他功能
### PathVariable
该框架还支持路径变量解析，直接上使用案例：
```java
@RestController
public class DemoController {
    @GetMapping("/t7/{name}/{id}")
    public Member t7(@PathVariable("name") String name, @PathVariable("id") Integer id) {
        return new Member(name, id);
    }
}
```

我们测试的 url 为：`http://localhost:8080/mvc/t7/zhangsan/999`。
返回的结果为：
```json
{
    "name": "zhangsan",
    "id": 999
}
```

# mvc-context
[[003-ioc容器]]
实现该项目的重要组件就是 ioc 容器，ioc 容器保存所有类的映像——bean 实例，并且在配对的请求到来时，调用相应的 controller，还实现了用户可以高度自定义的配置选项。保证了开闭原则。

## 基本架构
![AnnotationConfigApplicationContext.png](https://yelanyanyu-img-bed.oss-cn-hangzhou.aliyuncs.com/img/AnnotationConfigApplicationContext.png)
> **解读：**
>  + 其中 AnnotationConfigApplicationContext 是具体的实现，完成了容器的主要功能，这个将在之后介绍；
>  + ConfigurableApplicationContext 是 AnnotationConfigApplicationContext 的抽象，用来提供给开发者进行拓展的接口，我们可以通过容器进行二次开发。提供了诸如查找 bean 定义等相关方法。便于对容器进行二次开发；
>  + ApplicationContext 则是提供给用户层的进一步抽象，对外开发了获取容器内的 bean 等相关方法，在 web-mvc 模块中，正是使用了这个抽象；

## 加载 bean 的基本流程
> + 创建 BeanDefinition；
> + 创建 Bean 实例；
> + 初始化 Bean 并解决强依赖注入问题；
> + 调用 BeanPostProcessor；
> + 解决弱依赖注入问题；
> + 调用 `@PostConstruct` 注释的 init 方法；
> + 当容器关闭的时候，调用所有 bean 的 destroy 方法（被注解 `@PostConstruct` 注释）；

### 创建 BeanDefinition
一个 bean 通常比较复杂，如果我们只保留 bean 实例 Object 对象，那么信息就太少，会对后面的反射解析，注解解析造成极大的麻烦。所以，我们首先需要抽象出一个 Bean 的定义。

```java
@Data
public class BeanDefinition implements Comparable<BeanDefinition> {
    /**
     * bean实例的名称
     */
    private final String name;
    /**
     * bean 的class对象
     */
    private final Class<?> beanClass;
    /**
     * bean 对象实例
     */
    private Object instance = null;
    /**
     * bean 构造器
     */
    private final Constructor<?> constructor;
    /**
     * 工厂方法名称
     */
    private final String factoryName;
    /**
     * 工厂方法对象
     */
    private final Method factoryMethod;
    /**
     * bean 的顺序
     */
    private final int order;
    /**
     * 是否被 @Primary 注释
     */
    private final boolean primary;
    /**
     * init and destroy
     */
    private boolean init = false;
    private String initMethodName;
    private String destroyMethodName;
    private Method initMethod;
    private Method destoyMethod;
}
```

为什么要设置工厂方法？因为，Bean 的注入行为有两种，一是直接注释了 `@Component` 注解的 bean；另一类是配置类（被 `@Configuration` 注释的）中，被 `@Bean` 注释的方法，该方法也应该返回一个 bean，被 `@Bean` 注释的方法就是工厂方法，该工厂方法用于返回 bean 实例，而不是由容器内部创建。

故而，扫描的 bean 就可以大致分为三种情况：
+ 被 `@Bean` 注释的 bean 需要以工厂方法注入 ioc；
+ 被 `@Component` 注释的 bean 需要以构造方法注册到 ioc，而不是以工厂方法的形式；
+ 被 `@Configuration` 注释的 bean 以普通方法注册，但是其中被 `@Bean` 注释的方法要以第一种方式注入；

如何扫描 bean 呢？需要提供如下几个信息：
+ 基准包，也就是这个目录下的所有类都需要被考虑到；
+ 必要的配置文件。一些类的某些属性是需要通过配置文件依赖注入的，所以，必须提供一份配置文件的 Bean ；

扫描什么资源？以 `.class` 为结尾的资源。至于为什么，这里涉及到 jvm 的知识就不再过多赘述了。如何扫描所有资源呢？传统的方法是写一个 dfs 深度优先搜索，但是 java 8 的 stream 操作可以极大的简化这一过程。

当所有的资源都扫描完毕后，会以全类名保存，以便之后反射生成实例使用。

### 初始化 Bean
当所有的 BeanDefinition 都已经创建完毕，那么就需要尝试创建 Bean 实例了。在这个阶段要解决的问题主要是循环依赖的问题。

依赖问题，涉及到的注解有： `@Value`， `@Autowired`。什么是依赖，这里就不再介绍了。

对于以工厂方法注入的 Bean 和以构造器方法注入的 bean ，需要考虑强依赖循环的问题。对于其他方法注入的 Bean，需要解决弱依赖注入的问题。

要解决依赖问题，最好的办法就是两步走：一，创建实例；二，注入。而强依赖是无法分成两步的，所以，在 bean 初始化的时候，就要去判断是否存在强依赖注入，对于这种情况，就应该直接报错。而，弱循环依赖，就可以使用这种方法解决。

这就是为什么不一步到位的说是“创建 bean”，而一定要说初始化 bean 了。

总而言之，初始化 Bean 的工作就是：
+ 检查并排除强依赖注入问题；
+ 通过工厂方法或者构造器进行强依赖注入；
+ 生成一些的 Bean 实例；

### 创建 Bean 实例
还有弱依赖的问题需要解决，这一步要完成的工作是创建所有的 Bean。

故而，这阶段的任务是：
+ 注入 bean 的弱依赖；
+ 生成 Bean 实例；
+ 调用 Bean 的 init 方法（被 `@PostConstruct` 注释的方法）；

### BeanPostProcessor 的实现
该类的作用其实就是类的替换，实际环境中，我们会将原来的类替换成一个代理类。

该类型的类的创建时机很重要，通常应该放在 `@Configuration` 类的后面被注册到 ioc 容器中。因为在后面的 bean 的依赖注入中，需要使用 BeanPostProcessor 来进行类运行类型的替换。

故而，在创建 Bean 实例后，紧接着，就应该调用 BeanPostProcessor 的方法，来进行类的替换。

还有一个重要的问题是，什么时候要拿到原始的对象，什么时候应该拿到代理对象？这里直接说结论：
+ 若 A 依赖 B，那么 A 在注入 B 对象时，应该注入 B 的 proxy 对象。只要创建 Bean 完成后，立刻调用 `BeanPostProcessor` 就实现了替换，后续其他 Bean 引用的肯定就是 Proxy 了；
+ 若 A 依赖 B，那么当要再 B 注入对象时，应该注入到 B 的原始对象；

需要明确的两个需求：一是我们必须想办法要代理对象的方法得到调用；二是，我们需要当代理对象运行到原始对象的方法时，需要依赖注入的对象起到效果。例如，A 对象有代理对象 AP，其中 A 有属性 B。那么当 AP 调用特定方法 `m1()` 的使用，必定有一步是会执行到 A 定义的方法 `m1()` 的，假如 A 的方法 `m1()` 需要使用到属性 B，而属性 B 又没有注入就会出现空指针异常。


## 基本使用
大体上，spring 框架中的常用功能，这里都有。

但是，有几个功能的应用以及原理是需要特别说明的。

### Value
通过该注解，可以读取自定义的配置。你可以通过新建 `application.yml` 或者 `application.properties` 来自定义配置。同时，你也可以获取一些系统的默认配置。

>### 注意：
>+ yaml 文件和 properties 文件只会读取一个，yaml 文件的优先级高于 properties 文件。也就是说，当两种文件同时存在的时候，ioc 只会加载 yaml 文件；

### Autowired
由于 spring 框架中注解 `@Resource` 和 `@Autowired` 的使用场景和原理都极其类似，所以，我们省略 `@Resource` 的实现。

注意，`@Autowired` 不仅仅支持以成员的方法注入，还可以支持在构造器，在工厂方法参数中注入。

### PostConstruct
该注解可以用来绑定 init 方法，当 Bean 创建完毕后（包括实例创建，依赖注入后），就会调用这个方法。

以 `Class01` 为例：
```java
@Configuration
public class Class01 {
    @PostConstruct
    public void init() {
        System.out.println("++++++++++++++++++");
    }
}
```
>### 解读：
>+ 当该类创建完毕后，就会输出 `++++++++++++++++++` 到控制台；


### PreDestroy
该注解用来指示类被销毁（容器被关闭时，由 JVM 自动进行垃圾回收销毁）的时候，调用的方法。

还是以 `Class01` 为例：
```java
@Configuration
public class Class01 {
    @PreDestroy
    public void destroy() {
        System.out.println("-------------------");
    }
}
```

当 spring 容器执行 `close()` 方法的时候，就会自动调用每个组件的该方法，所以对于 `Class01`，会在控制台输出 `-------------------`。


### Bean
#### 使用
Bean 注解只在工厂方法（被 `@Configuration` 注释的组件）中出现，Bean 注解只能用来修饰方法，被其注释的方法返回的对象会自动注册到 ioc 中管理。
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    String value() default "";

    String initMethod() default "";

    String destroyMethod() default "";
}
```
>### 解读
>+ value 属性用来表示返回的 bean 的名字，如果不指定，默认以方法名为 bean 的名字；
>+ initMethod 可以用来指定该 bean 的 init 方法，init 方法不能拥有参数；
>+ destroyMethod 可以用来指定该 bean 的 destroy 方法，destroy 方法也不能拥有参数；
>+ 注意，init 和 destroy 方法都必须定义在 bean 的 Class 定义内，而不是工厂方法中；



***
#### 实现原理
我们只对如何实现 initMethod 和 destroyMethod 做说明。主要说明，当 `@Bean(initMethod = "init", destroyMethod = "destroy")` 这个功能是如何实现的。

1. 当我们创建 BeanDefinition 的时候，我们会找到所有被 `@Bean` 注释的方法，然后对于扫描的所有方法，我们都会获取 Bean 注解的值，也就是 initMethod 和 destroyMethod 的值，将其以字符串的形式保存到该 Bean 的 BeanDefinition 中，等待到之后调用；
2. 关键在于是如何通过 name 就调用该方法的；
3. 调用方法的时机就不过多介绍了，我们重点关注源码中的 `callMethod(...)` 方法的实现：
```java
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
```
> #### 解读：
> + 可以看到，当方法实例为 null 的时候，是通过在 bean 实例中寻找对应名字的 init 和 destroy 方法，从而调用的；
> + 而如果实例不为空（只有非工厂方法注入的 Bean 才会不为空），就会直接调用该方法；


# mvc-aop
[[005-aop]]
使用该类可以实现 spring 中的面向切面编程，我们提供了三个注解 `@Around`、`@Before`、`@After`，三个注解仅仅只对类生效。即，被任一注解修饰的类内部的所有方法都会被代理。

注解在使用的时候，需要指定相应的 Handler bean name。用户需要自行实现 Handler，即实现 InvocationHandler 接口实现 around 逻辑或者继承我们提供的 `AfterInvocationHandlerAdapter` 实现 after 的逻辑，继承 `BeforeInvocationHandlerAdapter` 实现 before 的逻辑。

最后需要将这些 handler 都用注解 `@Component` 注释，将其加入 ioc 中。


# mvc-jdbc
[[006-JDBC与事务]]
提供了 JdbcTemplate 来简化数据库操作。

提供了注解 `@Transactional` 注解来实现声明式事务，仅仅只在类上使用。使用方法与 spring 完全一样。

默认的传播模式为 `REQUIRED`。出错就全局回滚。这是默认的模式。

# mvc-boot
使用内嵌的 tomcat 完全不同通过 `web.xml` 就可以启动一个 web 项目。支持，直接使用 `java -jar xxx.war` 的方式启动 web 项目。

具体的使用细节，可以看 [[007-boot]]。
