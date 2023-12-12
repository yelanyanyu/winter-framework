# winter-framework
源码仓库地址：[yelanyanyu/winter-framework (github.com)](https://github.com/yelanyanyu/winter-framework)
# 基本内容
这个项目预计完成一个小型的类 spring 的项目。要实现的功能是：
+ 实现 DispatcherServlet 的分发功能；
+ 实现 HandlerMapping 和 HandlerAdapter 匹配 url 的功能；
+ 实现 PathVariable 解析的功能；
+ 实现 Get 和 Post 分发请求的功能；
+ 实现 ViewResolver 视图的解析功能；
+ 支持 jsp 的解析；
## 开发以及测试环境
Java 17 + tomcat 8 + servlet-api 3.1 + maven 3.9.4
IDE：IntelliJ IDEA 2023.2 + postman

## 未来开发规划
- [ ] JdbcTemplate 以及事务支持，用于简化数据库的操作；
- [ ] AOP 支持；
- [ ] 实现类似于 Boot 的支持，使用默认配置，简化配置操作；
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
????"name":?"wdnmd",
????"id":?100
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
????"name":?"zhangsan",
????"id":?999
}
```

# ioc 容器
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