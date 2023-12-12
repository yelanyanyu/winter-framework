# winter-framework
Դ��ֿ��ַ��[yelanyanyu/winter-framework (github.com)](https://github.com/yelanyanyu/winter-framework)
# ��������
�����ĿԤ�����һ��С�͵��� spring ����Ŀ��Ҫʵ�ֵĹ����ǣ�
+ ʵ�� DispatcherServlet �ķַ����ܣ�
+ ʵ�� HandlerMapping �� HandlerAdapter ƥ�� url �Ĺ��ܣ�
+ ʵ�� PathVariable �����Ĺ��ܣ�
+ ʵ�� Get �� Post �ַ�����Ĺ��ܣ�
+ ʵ�� ViewResolver ��ͼ�Ľ������ܣ�
+ ֧�� jsp �Ľ�����
## �����Լ����Ի���
Java 17 + tomcat 8 + servlet-api 3.1 + maven 3.9.4
IDE��IntelliJ IDEA 2023.2 + postman

## δ�������滮
- [ ] JdbcTemplate �Լ�����֧�֣����ڼ����ݿ�Ĳ�����
- [ ] AOP ֧�֣�
- [ ] ʵ�������� Boot ��֧�֣�ʹ��Ĭ�����ã������ò�����
- [ ] �����ع���򻯣�
- [ ] ʵ���ļ��ϴ������ص� api �򻯣�
- [ ] �����Ż���ʵ�־�̬��Դ�� cache ���湦�ܣ�
- [ ] ֧���첽��

## Ŀ¼�ṹ
```text
����.github
��  ����ISSUE_TEMPLATE
����.idea
��  ����artifacts
��  ����inspectionProfiles
����framework-test # ���Գ���
��  ����src
��      ����main
��          ����java
��          ��  ����com
��          ��      ����yelanyanyu
��          ��          ����bean
��          ��          ����config
��          ��          ����controller
��          ����resources
��          ����webapp
��              ����WEB-INF # ��ԴĿ¼
��                  ����views
����mvc-context # ����ʵ��
��  ����logs
��  ����src
��      ����main
��      ��  ����java
��      ��  ��  ����com
��      ��  ��      ����yelanyanyu
��      ��  ��          ����annotation # ע��
��      ��  ��          ����context # ����ʵ��
��      ��  ��          ����exception # �Զ����쳣
��      ��  ��          ����io # ��ȡ bean �Լ� �����ļ����
��      ��  ��          ����util # ������
��      ��  ����resources
��      ����test
��          ����java
��          ��  ����com
��          ��      ����bean
��          ��      ����yelanyanyu
��          ����resources
����mvc-webmvc # web mvcʵ��
    ����logs
    ����src
        ����main
        ��  ����java
        ��  ��  ����com
        ��  ��      ����yelanyanyu
        ��  ��          ����webmvc
        ��  ��              ����annotation # ע��
        ��  ��              ����bean 
        ��  ��              ����exception # �Զ���ע��
        ��  ��              ����util # ������
        ��  ��              ����view # ��ͼ����ͼ������
        ��  ����resources
        ����test
            ����java
            ��  ����com
            ��      ����yelanyanyu
            ��          ����bean
            ����resources

```


# ���ٿ�ʼ
>����ּ�ڿ������ָÿ�ܡ�
## JSP ����
### ��Ŀ����
��������Ҫ����һ�� jsp ��ǰ��˰������Ŀ��
1. ��ԴĿ¼���½����� `.../config/WebMvcConfiguration.java`��������ע�� `@Configuration` ���Σ���ʾ������һ�������ࣻ
```java
@Configuration
public class WebConfiguration {
}
```

2. ������ͼ������ InternalResourceViewResolver��
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
>**�����**
>+ `irv.setPrefix("/WEB-INF/views/");` ��ʾ��Դ��ǰ׺����ǰ׺�������� `@GetMapping` �� `@PostMapping` ����ʹ�ã�
>+ `irv.setSuffix(".jsp");` ��ʶ��Դ�ĺ�׺������ת������Դ����ú�׺ƴ�ӣ�����Դ��Ϊ `success`���򽫻�ƴ�ӳ� `success.jsp`��
>+ `@Bean` �ǹ���ע�⣬���з����� `@Bean` ���Σ���÷����ķ���ֵ�������� ioc �������ڱ����У��Ὣ��ͼ������ irv ע�ᵽ ioc��

3. �ڶ���Ŀ¼�½�һ���������� `WebConfig`��Ҫ�������඼�Ǹ��������ͬ���������Ӽ�������˵��
   ![image.png](https://yelanyanyu-img-bed.oss-cn-hangzhou.aliyuncs.com/img/20231120211504.png)
```java
@ComponentScan
@Configuration
@Import(WebMvcConfiguration.class)
@Order(1)
public class WebConfig {
}
```

>**�����**
> + `@Configuration` ��ʾ�����������ࣻ
> + `@ComponentScan` ����ָ����Ҫɨ��İ���ָ����Щ��Ӧ�ü�������ά������
> + `@Import(WebMvcConfiguration.class)` ��ʾ����ոն���������ࣻ
> + `@Order(1)` ��ʾ������������ʱ�������ȼ�������ࣻ

***
> #### ע��
> + ����ð� jsp ����Դ������Ŀ¼ `/WEB-INF/` �У���ȷ����Դ����ȷ�ı��м�������� Tomcat�����أ�
> + ��Դ����ֻ֧�ֺ�׺ `.jsp`���벻Ҫ���ģ�
### ���ٱ�д Controller
1. ͨ��������ʹ��ע�� `@Controller` ���� `@RestController` ʹ�ø����Ϊ controller��
   ���磺
```java
@Controller
public class TestController {
}
```

2. ͨ���ڸ�������һ�����������Ҽ���ע�� `@GetMapping` ʹ������һ�������� get Ϊ�������͵� Handler ������ͨ�� `@PostMapping` ���� Post �͵��������磺
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
>�����
>+ ���ǿ���ͨ���޸�ע��� value ֵ�����ƶ� uri��ʹ��ÿһ������ url ���Ե�����Ӧ�ķ������д���
>+ �������ǵ� context-path Ϊ `/test`����ô����Ҫ���� `t2()`����Ӧ������ url ��Ϊ `http://your-ip-address:port/test/t2`��

3. ���ǿ��Դ��� `ModelAndView` �������� Request ������ӱ��������ҽ� request ����ת�����̶���Դ�������ض���
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
>**�����**
>+ ������ `t1()`�������ú󣬽���ת������Դ `[prefix]success.jsp`������ prefix �ǳ�ʼ�������趨��ֵ��
>+ ������ `t2()` �����ú󣬽��Ὣҳ���ض��� `http://ip:port/success`��ע�⣬�ض��򽫲�����֮ǰ�趨�ĺ�׺ `.jsp` ����ƴ�ӣ�

## ���� JSON ��ǰ��˷���
����Ŀ����Ҫ֧�ֵ��� json ���ݵķ��أ��Ӷ�ʵ��ǰ��˵���ȫ���롣�����Է��ض��������������ļ����ϴ������ء�

���� json ���ݵĺ��������¼���ע�⣺
+ ʹ�� `@RestController` ע����ָ�� Controller��
+ ʹ�� `@ResponseBody` ��ע�ͷ�����
+ ʹ�� `@RequestBody` ��ע�� handler ������

### RestController
#### ���л�����
���һ���౻ `@RestController` ע�ͣ���ô�����е��κη����ķ���ֵ���ᱻ���л�Ϊ JSON ��ʽ�����ݣ����ء��ٸ����ӣ�����������һ������ Member����������ڷ����з��� Member ������ô�ײ�ͻὫ�����л���Ϊ json ���ݣ�
```java
@RestController
public class DemoController {
    @GetMapping("/t3")
    public Member t3() {
        return new Member("asdfasf", 100);
    }
}
```
���ص����ݣ�
```json
{"name":"asdfasf","id":100}
```
#### ���л��ַ���
����������ص��� String ���͵��ַ�������ôҲ�� json ����ʽ���н������أ�
```java
@RestController
public class DemoController {
    @GetMapping("/t4")
    public String t4() {
        return "{\"name\":\"asdfasf\",\"id\":100}";
    }
}
```
���ص����ݣ�
```json
{"name":"asdfasf","id":100}
```

#### ���ض�������
�������ļ��ϴ������ص��������Է��ط� json ��ʽ�Ķ�������Ҳ�Ǳ���ġ����ǿ���ͨ������һ�� byte ����Ҳ���ض���������
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
����һ���ಢû�б� `@RestController` ע�ͣ���ô������Ȼ����ͨ��ע�� `@ResponseBody` ������ json ��ʽ�����ݻ��߶���������

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
> **�����**
>  + ���������Ч���ͱ�ע�� `@RestController` ע�͵�Ч������ͬ�ģ�

### RequestBody
�����һ�������е�ĳ�������� `@RequestBody` ע�ͣ���ô�ò����ͻ��װ json ��ʽ�� request ������

**������**
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

**���������**
```text
body: {"name":"wdnmd","id":100}
url: http://localhost:8080/mvc/t6?id=200
```

**������أ�**
```json
{
????"name":?"wdnmd",
????"id":?100
}
```
```text
console: id: 200
```

> **�����**
>  + ���� `member` �� `@RequestBody` ע��ʱ��request ���е� json ��ʽ�Ĳ����ͻᱻ�Զ���װ��һ�����󣬲��Ҳ�Ӱ�����������Ľ��ܣ�

## ��������
### PathVariable
�ÿ�ܻ�֧��·������������ֱ����ʹ�ð�����
```java
@RestController
public class DemoController {
    @GetMapping("/t7/{name}/{id}")
    public Member t7(@PathVariable("name") String name, @PathVariable("id") Integer id) {
        return new Member(name, id);
    }
}
```

���ǲ��Ե� url Ϊ��`http://localhost:8080/mvc/t7/zhangsan/999`��
���صĽ��Ϊ��
```json
{
????"name":?"zhangsan",
????"id":?999
}
```

# ioc ����
ʵ�ָ���Ŀ����Ҫ������� ioc ������ioc ���������������ӳ�񡪡�bean ʵ������������Ե�������ʱ��������Ӧ�� controller����ʵ�����û����Ը߶��Զ��������ѡ���֤�˿���ԭ��

## �����ܹ�
![AnnotationConfigApplicationContext.png](https://yelanyanyu-img-bed.oss-cn-hangzhou.aliyuncs.com/img/AnnotationConfigApplicationContext.png)
> **�����**
>  + ���� AnnotationConfigApplicationContext �Ǿ����ʵ�֣��������������Ҫ���ܣ��������֮����ܣ�
>  + ConfigurableApplicationContext �� AnnotationConfigApplicationContext �ĳ��������ṩ�������߽�����չ�Ľӿڣ����ǿ���ͨ���������ж��ο������ṩ��������� bean �������ط��������ڶ��������ж��ο�����
>  + ApplicationContext �����ṩ���û���Ľ�һ�����󣬶��⿪���˻�ȡ�����ڵ� bean ����ط������� web-mvc ģ���У�����ʹ�����������

## ���� bean �Ļ�������
> + ���� BeanDefinition��
> + ���� Bean ʵ����
> + ��ʼ�� Bean �����ǿ����ע�����⣻
> + ���� BeanPostProcessor��
> + ���������ע�����⣻

### ���� BeanDefinition
һ�� bean ͨ���Ƚϸ��ӣ��������ֻ���� bean ʵ�� Object ������ô��Ϣ��̫�٣���Ժ���ķ��������ע�������ɼ�����鷳�����ԣ�����������Ҫ�����һ�� Bean �Ķ��塣

```java
@Data
public class BeanDefinition implements Comparable<BeanDefinition> {
    /**
     * beanʵ��������
     */
    private final String name;
    /**
     * bean ��class����
     */
    private final Class<?> beanClass;
    /**
     * bean ����ʵ��
     */
    private Object instance = null;
    /**
     * bean ������
     */
    private final Constructor<?> constructor;
    /**
     * ������������
     */
    private final String factoryName;
    /**
     * ������������
     */
    private final Method factoryMethod;
    /**
     * bean ��˳��
     */
    private final int order;
    /**
     * �Ƿ� @Primary ע��
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

ΪʲôҪ���ù�����������Ϊ��Bean ��ע����Ϊ�����֣�һ��ֱ��ע���� `@Component` ע��� bean����һ���������ࣨ�� `@Configuration` ע�͵ģ��У��� `@Bean` ע�͵ķ������÷���ҲӦ�÷���һ�� bean���� `@Bean` ע�͵ķ������ǹ����������ù����������ڷ��� bean ʵ�����������������ڲ�������

�ʶ���ɨ��� bean �Ϳ��Դ��·�Ϊ���������
+ �� `@Bean` ע�͵� bean ��Ҫ�Թ�������ע�� ioc��
+ �� `@Component` ע�͵� bean ��Ҫ�Թ��췽��ע�ᵽ ioc���������Թ�����������ʽ��
+ �� `@Configuration` ע�͵� bean ����ͨ����ע�ᣬ�������б� `@Bean` ע�͵ķ���Ҫ�Ե�һ�ַ�ʽע�룻

���ɨ�� bean �أ���Ҫ�ṩ���¼�����Ϣ��
+ ��׼����Ҳ�������Ŀ¼�µ������඼��Ҫ�����ǵ���
+ ��Ҫ�������ļ���һЩ���ĳЩ��������Ҫͨ�������ļ�����ע��ģ����ԣ������ṩһ�������ļ��� Bean ��

ɨ��ʲô��Դ���� `.class` Ϊ��β����Դ������Ϊʲô�������漰�� jvm ��֪ʶ�Ͳ��ٹ���׸���ˡ����ɨ��������Դ�أ���ͳ�ķ�����дһ�� dfs ����������������� java 8 �� stream �������Լ���ļ���һ���̡�

�����е���Դ��ɨ����Ϻ󣬻���ȫ�������棬�Ա�֮��������ʵ��ʹ�á�

### ��ʼ�� Bean
�����е� BeanDefinition ���Ѿ�������ϣ���ô����Ҫ���Դ��� Bean ʵ���ˡ�������׶�Ҫ�����������Ҫ��ѭ�����������⡣

�������⣬�漰����ע���У� `@Value`�� `@Autowired`��ʲô������������Ͳ��ٽ����ˡ�

�����Թ�������ע��� Bean ���Թ���������ע��� bean ����Ҫ����ǿ����ѭ�������⡣������������ע��� Bean����Ҫ���������ע������⡣

Ҫ����������⣬��õİ취���������ߣ�һ������ʵ��������ע�롣��ǿ�������޷��ֳ������ģ����ԣ��� bean ��ʼ����ʱ�򣬾�Ҫȥ�ж��Ƿ����ǿ����ע�룬���������������Ӧ��ֱ�ӱ���������ѭ���������Ϳ���ʹ�����ַ��������

�����Ϊʲô��һ����λ��˵�ǡ����� bean������һ��Ҫ˵��ʼ�� bean �ˡ�

�ܶ���֮����ʼ�� Bean �Ĺ������ǣ�
+ ��鲢�ų�ǿ����ע�����⣻
+ ͨ�������������߹���������ǿ����ע�룻
+ ����һЩ�� Bean ʵ����

### ���� Bean ʵ��
������������������Ҫ�������һ��Ҫ��ɵĹ����Ǵ������е� Bean��

�ʶ�����׶ε������ǣ�
+ ע�� bean ����������
+ ���� Bean ʵ����
+ ���� Bean �� init �������� `@PostConstruct` ע�͵ķ�������

### BeanPostProcessor ��ʵ��
�����������ʵ��������滻��ʵ�ʻ����У����ǻὫԭ�������滻��һ�������ࡣ

�����͵���Ĵ���ʱ������Ҫ��ͨ��Ӧ�÷��� `@Configuration` ��ĺ��汻ע�ᵽ ioc �����С���Ϊ�ں���� bean ������ע���У���Ҫʹ�� BeanPostProcessor ���������������͵��滻��

�ʶ����ڴ��� Bean ʵ���󣬽����ţ���Ӧ�õ��� BeanPostProcessor �ķ���������������滻��

����һ����Ҫ�������ǣ�ʲôʱ��Ҫ�õ�ԭʼ�Ķ���ʲôʱ��Ӧ���õ������������ֱ��˵���ۣ�
+ �� A ���� B����ô A ��ע�� B ����ʱ��Ӧ��ע�� B �� proxy ����ֻҪ���� Bean ��ɺ����̵��� `BeanPostProcessor` ��ʵ�����滻���������� Bean ���õĿ϶����� Proxy �ˣ�
+ �� A ���� B����ô��Ҫ�� B ע�����ʱ��Ӧ��ע�뵽 B ��ԭʼ����

��Ҫ��ȷ����������һ�����Ǳ�����취Ҫ�������ķ����õ����ã����ǣ�������Ҫ������������е�ԭʼ����ķ���ʱ����Ҫ����ע��Ķ�����Ч�������磬A �����д������ AP������ A ������ B����ô�� AP �����ض����� `m1()` ��ʹ�ã��ض���һ���ǻ�ִ�е� A ����ķ��� `m1()` �ģ����� A �ķ��� `m1()` ��Ҫʹ�õ����� B�������� B ��û��ע��ͻ���ֿ�ָ���쳣��