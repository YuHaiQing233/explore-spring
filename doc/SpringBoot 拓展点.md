
### BeanFactoryPostProcessor

**它是干什么的**
```java
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    /**
     * 译文：
     * 在标准初始化之后修改应用程序上下文的内部bean工厂。
     * 所有的bean定义都已加载，但还没有实例化任何bean。这允许覆盖或添加属性，甚至是对急于初始化的bean。
     * 
     * 白话：
     * 所有的beanDefinition都已经注册到了beanFactory（DefaultListableBeanFactory）中，但是此时尚未实例化Bean的时候;
     * 允许自定义修改应用程序上下文的bean定义（beanDefinition），调整上下文的底层bean工厂的bean属性值
     */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
```

**自定义BeanFactoryPostProcessor**
```java


package com.explore.service;

import org.springframework.stereotype.Component;

/**
 * @Author HaiQing.Yu
 * @Date 2023/5/6 18:48
 */
@Component
public class ModifyBeanDefinitionService {

    /**
     * 姓名
     */
    private String name = "Lee";

    /**
     * 年龄
     */
    private Integer age = 10;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "ModifyBeanDefinitionService{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
```
```java
package com.explore.expand;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @Author HaiQing.Yu
 * @Date 2023/5/6 17:56
 */
@Slf4j
@Component
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("开始执行CustomBeanFactoryPostProcessor#postProcessBeanFactory");

        // 1. 获取Dog类的BeanDefinition
        BeanDefinition definition = beanFactory.getBeanDefinition("modifyBeanDefinitionService");

        // 2. 获取Dog类的属性集合
        MutablePropertyValues propertyValues = definition.getPropertyValues();

        // 3. 给color属性赋值
        propertyValues.addPropertyValue("name", "jay");
        propertyValues.addPropertyValue("age", 20);

        log.info("打印属性: {}", definition.getPropertyValues());
    }
}

```
```java
package com.explore;

import com.explore.service.ModifyBeanDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author HaiQing.Yu
 * @Date 2023/3/7 16:52
 */
@Slf4j
@SpringBootApplication
public class SecurityApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SecurityApplication.class, args);

        ModifyBeanDefinitionService bean = run.getBean(ModifyBeanDefinitionService.class);
        log.info("modifyBeanDefinitionService:{}", bean);
    }

}
```
使用自定义BeanFactoryPostProcessor的执行结果
```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.2)

2023-05-06 19:01:55.302  INFO 4841 --- [           main] com.explore.SecurityApplication          : Starting SecurityApplication using Java 11.0.14 on yuhaiqingdeMacBook-Pro.local with PID 4841 (/Users/yuhaiqing/project/local/explore-springsecurity/target/classes started by yuhaiqing in /Users/yuhaiqing/project/local/explore-springsecurity)
2023-05-06 19:01:55.373  INFO 4841 --- [           main] com.explore.SecurityApplication          : No active profile set, falling back to 1 default profile: "default"
2023-05-06 19:01:56.168  INFO 4841 --- [           main] c.e.e.CustomBeanFactoryPostProcessor     : 开始执行CustomBeanFactoryPostProcessor#postProcessBeanFactory
2023-05-06 19:01:56.169  INFO 4841 --- [           main] c.e.e.CustomBeanFactoryPostProcessor     : 打印属性: PropertyValues: length=2; bean property 'name'; bean property 'age'
2023-05-06 19:01:56.788  INFO 4841 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 9001 (http)
2023-05-06 19:01:56.796  INFO 4841 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-05-06 19:01:56.796  INFO 4841 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.65]
2023-05-06 19:01:56.883  INFO 4841 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-05-06 19:01:56.884  INFO 4841 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1413 ms
2023-05-06 19:01:57.695  INFO 4841 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9001 (http) with context path ''
2023-05-06 19:01:57.712  INFO 4841 --- [           main] com.explore.SecurityApplication          : Started SecurityApplication in 2.851 seconds (JVM running for 3.638)
2023-05-06 19:01:57.715  INFO 4841 --- [           main] com.explore.SecurityApplication          : modifyBeanDefinitionService:ModifyBeanDefinitionService{name='jay', age=20}
```
未使用自定义BeanFactoryPostProcessor的执行结果
```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.2)

2023-05-06 19:03:09.276  INFO 4848 --- [           main] com.explore.SecurityApplication          : Starting SecurityApplication using Java 11.0.14 on yuhaiqingdeMacBook-Pro.local with PID 4848 (/Users/yuhaiqing/project/local/explore-springsecurity/target/classes started by yuhaiqing in /Users/yuhaiqing/project/local/explore-springsecurity)
2023-05-06 19:03:09.342  INFO 4848 --- [           main] com.explore.SecurityApplication          : No active profile set, falling back to 1 default profile: "default"
2023-05-06 19:03:10.712  INFO 4848 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 9001 (http)
2023-05-06 19:03:10.719  INFO 4848 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-05-06 19:03:10.720  INFO 4848 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.65]
2023-05-06 19:03:10.797  INFO 4848 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-05-06 19:03:10.797  INFO 4848 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1351 ms
2023-05-06 19:03:11.581  INFO 4848 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9001 (http) with context path ''
2023-05-06 19:03:11.596  INFO 4848 --- [           main] com.explore.SecurityApplication          : Started SecurityApplication in 2.772 seconds (JVM running for 3.546)
2023-05-06 19:03:11.599  INFO 4848 --- [           main] com.explore.SecurityApplication          : modifyBeanDefinitionService:ModifyBeanDefinitionService{name='Lee', age=10}
```

分析 BeanFactoryPostProcessor(Bean工厂后置处理器)
```text
实例化时机:
    0. 源码位置：AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors())
    1. 实例化步骤：根据类型 BeanDefinitionRegistryPostProcessor 进行匹配，并且按照 PriorityOrdered、Ordered和其他 这三种类型进行分组，并且按照 PriorityOrdered > Ordered > 其他 的顺序进行实例化；
    2. 实例化步骤：匹配 BeanFactoryPostProcessor 在BeanFactory中的BeanDefinition，按照 PriorityOrdered > Ordered > 其他 的顺序进行实例化；
    白话: 从源码位置开始进行beanFactoryPostProcessor实例化，实例化的过程分为两个部分，先实例化 BeanDefinitionRegistryPostProcessor接口的子类，再实例BeanFactoryPostProcessor接口的其他子类，两个部分实例化时均有执行的优先级；
    
调用时机：
    0. 源码位置：AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors())
    1. 在实例化之后，就开始调用beanFactory的后置处理器；
    
作用：
    0. 作用于beanDefinition加载之后，bean实例化之前；
    1. 允许修改beanFactory中的bean的definition（上面的的例子）；
    2. 手动向BeanDefinitionRegistry中注册bean definition 或者 替换；

应用场景：
    PropertyOverrideConfigurer：
        此后置处理器用于将 datasource.properties 中的数据加载到 bean的definition中；
        
    PropertySourcesPlaceholderConfigurer： 
        用于将 ${...} 占位符替换为实际的值
    
    ServletComponentRegisteringPostProcessor：
        用来扫描@WebServlet、@WebFilter以及@WebListener，将这些注解修饰的类，注册到BeanDefinitionRegistry中
        
    ConfigurationClassPostProcessor（implements BeanDefinitionRegistryPostProcessor）：
        将 @Configuration、@Component、@PropertySource、@ComponentScan、@Import、@ImportResource、@Bean 修饰的类解析为 ConfigurationClass，存到集合中，最后将 ConfigurationClass 包装成beanDefinition注册到BeanDefinitionRegistry中

```



**BeanPostProcessor的作用**
```java

public interface BeanPostProcessor {

    /**
     * 译文：
     * 在任何bean初始化回调(如InitializingBean的afterPropertiesSet或自定义初始化方法)之前，将此BeanPostProcessor应用于给定的新bean实例。这个bean已经被属性值填充了。返回的bean实例可能是原始bean实例的包装器。
     * 
     * 白话：
     * 在创建Bean实例的过程中，属性填充之后，调用初始化方法之前，调用此beanPostProcessor，用于拓展Bean信息
     * 
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

    /**
     * 译文：
     * 在任何bean初始化回调(如InitializingBean的afterPropertiesSet或自定义初始化方法)之后，将此BeanPostProcessor应用于给定的新bean实例。这个bean已经被属性值填充了。返回的bean实例可能是原始bean实例的包装器。
     * 对于FactoryBean，将为FactoryBean实例和由FactoryBean创建的对象调用这个回调(从Spring 2.0开始)。后处理器可以通过相应的FactoryBean instanceof检查来决定是应用于FactoryBean还是已创建的对象，或者两者都应用。
     * 
     * 白话：
     * 在创建Bean实例的过程中，调用初始化方法之后，调用此beanPostProcessor，用于拓展Bean信息（可以对bean实例进行代理封装）；
     * 
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}

```

**分析BeanPostProcessor**
```text

实例化时机： 
    源码位置：AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> registerBeanPostProcessors(beanFactory); BeanPostProcessor子实现类就是在此时注册到BeanFactory的
    实例化步骤：先根据BeanPostProcessor.class类型获取所有的bean后置处理器的beanName,然后通过beanName获取到beanDefinition然后实例化对象
    
调用时机：
    0. 实例化Bean过程的前后会调用 postProcessBeforeInitialization 与 postProcessAfterInitialization
    1. AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> onRefresh(); 创建Tomcat服务器时会调用
    2. AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> finishBeanFactoryInitialization(beanFactory) 初始化所有非懒加载的Bean,实例化前后调用bean的后置处理器;
    
作用：
    1. 在bean实例化的时候，依赖注入完成，在调用初始化方法之前 回调 postProcessBeforeInitialization；
    2. 在bean实例化的时候，在调用初始化方法之后 回调 postProcessAfterInitialization；


应用场景：
    SimpleServletPostProcessor： 
        对实现Servlet接口的bean应用 初始化（Servlet.init(..)）和销毁（Servlet.destroy(..)）回调。
    
    ServletContextAwareProcessor:
        1. 如果bean实现了ServletContextAware，那么则注入 ServletContext；
        2. 如果bean实现了ServletConfigAware，那么则注入 ServletConfig；

```

**InstantiationAwareBeanPostProcessor & DestructionAwareBeanPostProcessor**
```java
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 译文：
     * 在目标bean实例化之前应用这个BeanPostProcessor。返回的bean对象可能是要使用的代理而不是目标bean，从而有效地抑制目标bean的默认实例化。
     * 
     * 白话：
     * 目标bean在实例化之前会调用此方法，如果此接口返回非空对象，则目标bean正常的实例化流程不在执行
     * 如果返回值不为空，则表示目标bean返回的代理对象，不再继续执行实例化流程，如果为空则需要继续目标bean的实例化流程
     * 
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
	@Nullable
	default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

    /**
     * 译文：
     * 在bean通过构造函数或工厂方法实例化之后，但在Spring属性填充(来自显式属性或自动装配)发生之前执行操作。
     * 这是在Spring自动装配开始之前对给定bean实例执行自定义字段注入的理想回调。
     * 
     * 白话：
     * 目标bean实例化之后，Spring属性填充（显式赋值或者自动装配）之前，调用此方法；
     * 如果返回值为true的话，则表示此目标bean可以继续设置属性，否则跳过设置属性
     * 
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
	default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}


    /**
     * 译文：
     * 在工厂将给定的属性值应用到给定的bean之前，对它们进行后处理，不需要任何属性描述符。
     * 如果实现提供自定义postProcessPropertyValues实现，则应该返回null(默认值)，否则则返回pvs。在该接口的未来版本中(删除了postProcessPropertyValues)，默认实现将直接返回给定的pv。
     * 
     * 白话：
     * Spring属性填充（显式赋值或者自动装配）之前，调用此方法进行后置处理
     * 
     * @param pvs
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
	@Nullable
	default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
			throws BeansException {

		return null;
	}


    /**
     * 译文：
     * 在工厂将给定属性值应用到给定bean之前，对它们进行后处理。允许检查是否满足所有依赖项，例如基于bean属性设置器上的“Required”注释。
     * 还允许替换要应用的属性值，通常通过基于原始PropertyValues创建新的MutablePropertyValues实例，添加或删除特定值。
     * 默认实现按原样返回给定的pv。
     * 
     * @param pvs
     * @param pds
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
	@Deprecated
	@Nullable
	default PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		return pvs;
	}

}
```

**分析InstantiationAwareBeanPostProcessor**
```text

实例化时机：
    0. 同BeanPostProcessor的实例化时机一致；
    1. 源码位置：AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> registerBeanPostProcessors(beanFactory); BeanPostProcessor子实现类就是在此时注册到BeanFactory的
    2. 实例化步骤：先根据BeanPostProcessor.class类型获取所有的bean后置处理器的beanName,然后通过beanName获取到beanDefinition然后实例化对象

调用时机：
    1. postProcessBeforeInstantiation()  -->  此方法在bean实例化之前（bean实例化源码： Object beanInstance = doCreateBean(beanName, mbdToUse, args);）调用
    2. postProcessAfterInstantiation()  -->  populateBean(beanName, mbd, instanceWrapper);  在bean实例化之后，且在填充属性之前；
    3. postProcessProperties() & postProcessPropertyValues()  -->  先获取BeanDefinition中的属性，然后自动注入(byName、byType),然后先调用postProcessProperties() ，如果返回值为空，则继续调用 postProcessPropertyValues() ，将最后得到的属性应用到 bean的包装对象中

作用：
    此接口类提供的方法主要用于 bean实例化前后的增强处理，postProcessBeforeInstantiation()方法可以返回一个代理对象，打断后续的属性应用与初始化方法调用的流程；
    postProcessAfterInstantiation()方法在实例化之后，填充属性之前调用，自定义属性填充之后可以通过返回值来决定是否需要继续后续的属性填充流程；
    postProcessProperties() & postProcessPropertyValues() 用于自定义属性填充处理；

应用场景：
    CommonAnnotationBeanPostProcessor：
        0. @PostConstruct @PreDestroy （InitDestroyAnnotationBeanPostProcessor 提供默认实现）
        1. @Resource 解决此注解的注入问题
        2. @PostConstruct 调用此初始化方法的调用（反射）
        3. @PreDestroy bean实例销毁前调用（反射）
    
    AutowiredAnnotationBeanPostProcessor：
        0. @Autowired @Value
        1. @Autowired 实现此注解的注入逻辑；
        2. @Value 实现此注解的修饰的属性填充逻辑；
    
```

```java
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 
     * 译文：
     * 在给定的bean实例销毁之前应用这个BeanPostProcessor，例如调用自定义销毁回调。
     * 像DisposableBean的destroy和自定义destroy方法一样，这个回调只适用于容器完全管理其生命周期的bean。这通常是单例和作用域bean的情况。
     * 
     * 
     * 
     * @param bean
     * @param beanName
     * @throws BeansException
     */
	void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

    /**
     * 
     * 译文：
     * 确定给定的bean实例是否需要这个后处理器销毁。
     * 默认实现返回true。如果5之前的DestructionAwareBeanPostProcessor的实现没有提供该方法的具体实现，Spring也会默认为true。
     * 
     * 
     * 
     * @param bean
     * @return
     */
	default boolean requiresDestruction(Object bean) {
		return true;
	}

}
```
**分析DestructionAwareBeanPostProcessor**
```text

实例化时机：
    0. 同BeanPostProcessor的实例化时机一致；
    1. 源码位置：AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> registerBeanPostProcessors(beanFactory); BeanPostProcessor子实现类就是在此时注册到BeanFactory的
    2. 实例化步骤：先根据BeanPostProcessor.class类型获取所有的bean后置处理器的beanName,然后通过beanName获取到beanDefinition然后实例化对象

调用时机：
    1. 在Bean实例化和填充完属性之后，执行 AbstractAutowireCapableBeanFactory#initializeBean#invokeInitMethods(beanName, wrappedBean, mbd);  --->  wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);  此处回调@PostConstruct方法；
    2. 创建bean实例结束之后，会执行 AbstractAutowireCapableBeanFactory#doCreateBean(..)#registerDisposableBeanIfNecessary(beanName, bean, mbd); 这段代码，将有@PreDestroy注解修饰的方法及相关bean定义信息存入 DisposableBean 并封装成DisposableBeanAdapter,最后在执行 applicationContext.close()方法时调用postProcessBeforeDestruction()；
    
作用：
    此方法可以在bean实例销毁之前，进行一下自定义拓展，比如释放连接等；

应用场景：
    CommonAnnotationBeanPostProcessor：
        1. 此bean后置处理器，在bean初始化之前执行了@PostConstruct注解修饰的方法，执行了实现了InitializingBean接口的afterPropertiesSet(), 且执行顺序为 先执行 @PostConstruct 方法，后执行afterPropertiesSet();
        2. 调用时机处描述了 @PreDestroy 的执行过程；

```

**探索 MergedBeanDefinitionPostProcessor**
```java
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

    /**
     * 译文：
     * 对指定bean的给定合并bean定义进行后处理
     * 
     * 白话：
     * 在bean实例化之后，填补属性之前，修改beanDefinition
     * 
     * @param beanDefinition
     * @param beanType
     * @param beanName
     */
	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

    /**
     * 译文：
     * 通知指定名称的bean定义已被重置，并且此后处理器应清除受影响bean的所有元数据。
     * 默认实现为空。
     * 
     * @param beanName
     */
	default void resetBeanDefinition(String beanName) {
	}

}
```
**分析MergedBeanDefinitionPostProcessor**
```text

实例化时机：
    0. 同BeanPostProcessor的实例化时机一致；
    1. 源码位置：AbstractApplicationContext#refresh()#invokeBeanFactoryPostProcessors(beanFactory) --> registerBeanPostProcessors(beanFactory); BeanPostProcessor子实现类就是在此时注册到BeanFactory的
    2. 实例化步骤：先根据BeanPostProcessor.class类型获取所有的bean后置处理器的beanName,然后通过beanName获取到beanDefinition然后实例化对象


调用时机：
    1. 在源码  AbstractAutowireCapableBeanFactory#doCreateBean(..) ---> applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName); 此处回调 postProcessMergedBeanDefinition();

作用：
    1. 支持一些外部注解修饰的字段，修改beanDefinition；

应用场景：
    CommonAnnotationBeanPostProcessor：
        1. CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition(..)  --->  super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);  此处就是支持@PostConstruct @PreDestroy注解修饰的方法，以便在合适的时机回调这些方法；
        2. CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition(..)  --->  InjectionMetadata metadata = findResourceMetadata(beanName, beanType, null); 此处是查找@Resource 修饰的属性，以便在合适的时候进行注入；
    
    AbstractAutoProxyCreator（AOP实现原理）:
        0. 此类为实现AOP的具体实现类;  源码调用步骤： AbstractApplicationContext.refresh()  --->  finishBeanFactoryInitialization(beanFactory);  ---> beanFactory.preInstantiateSingletons();  ---> DefaultListableBeanFactory.preInstantiateSingletons()  --->  AbstractBeanFactory.getBean(String name)  ---> doGetBean(...) ---> AbstractAutowireCapableBeanFactory.createBean(...)  ---> doCreateBean(...)   --->  initializeBean(...)  --->  wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);  初始化完对象实例之后，调用 BeanPostProcessor中的 postProcessAfterInitialization() 方法来对实例化后的对象创建代理；                                                                                                   

    ApplicationContextAwareProcessor：
        1. 此bean的后置处理器是用来完成 相关对象的注入支持一下注解接口（EnvironmentAware、MessageSourceAware、ResourceLoaderAware、ApplicationContextAware、EmbeddedValueResolverAware、ApplicationEventPublisherAware）;
        2. 以上功能完成的接口是 postProcessBeforeInitialization(...) 方法内完成；

```

### ApplicationContextInitializer

**ApplicationContextInitializer 作用**
```java

/**
 * 此接口用于在Spring容器刷新之前执行的一个回调函数，通常用于想Spring容器注入属性
 * @param <C>
 */
@FunctionalInterface
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
    
    /**
     * 初始化给定的应用程序上下文。
     * @param applicationContext
     */
	void initialize(C applicationContext);

}

```

**分析ApplicationContextInitializer**
```text

实例化时机：
    1. 源码位置：SpringApplication.run(..)  --->  new SpringApplication(primarySources).run(args)  --->  setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class)); ApplicationContextInitializer子实现类就是在此时注册到BeanFactory的
    2. 实例化步骤：先根据ApplicationContextInitializer.class类型获取所有的bean后置处理器的beanName,然后通过beanName获取到beanDefinition然后实例化对象

调用时机：
    1. 在源码的 SpringApplication.run(String... args)  --->  prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);  --->  applyInitializers(context); 此处回调ApplicationContextInitializer.initialize(C applicationContext) 方法；

作用：
    1. 在Spring容器刷新之前进行一些准备；

应用场景：
    DelegatingApplicationContextInitializer：
        1. 获取配置 context.initializer.classes 中的其他初始化器，进行实例化，并且完成回调；
    PropertySourceBootstrapConfiguration：
        1. 此初始化器是用来在Spring容器刷新之前，加载配置信息并同步到ConfigurableEnvironment中；
        2. 使用Nacos作为注册中心就是使用了这种方式

```

### @PostConstruct @PreDestroy & InitializingBean DisposableBean

**Spring Bean 初始化时与销毁时的回调**
```java

package com.explore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author: YuHaiQing
 * @time: 2023/5/11 17:19
 */
@Slf4j
@RestController
public class UserController implements InitializingBean, DisposableBean {

    @PostConstruct
    public void initMethod(){
        log.info("初始化 ---> UserController类初始化时调用了initMethod");
    }

    @PreDestroy
    public void destroyMethod(){
        log.info("销毁时  --->  UserController类销毁时调用了destroyMethod");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("实例化  --->  afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        log.info("销毁时 ---> destroy");
    }
}

```
**执行结果**
```text

Connected to the target VM, address: '127.0.0.1:53623', transport: 'socket'

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.2)

2023-05-14 16:53:57.759  INFO 4332 --- [           main] c.explore.BeanPostProcessorApplication   : Starting BeanPostProcessorApplication using Java 11.0.14 on YuHaiQing with PID 4332 (E:\project\github\explore-spring\explore-bean-post-processor\target\classes started by YuHaiQing in E:\project\github\explore-spring)
2023-05-14 16:53:57.759  INFO 4332 --- [           main] c.explore.BeanPostProcessorApplication   : No active profile set, falling back to 1 default profile: "default"
2023-05-14 16:53:58.279  INFO 4332 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2023-05-14 16:53:58.279  INFO 4332 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2023-05-14 16:53:58.279  INFO 4332 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.65]
2023-05-14 16:53:58.349  INFO 4332 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2023-05-14 16:53:58.349  INFO 4332 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 550 ms
2023-05-14 16:53:58.369  INFO 4332 --- [           main] com.explore.controller.UserController    : 初始化 ---> UserController类初始化时调用了initMethod
2023-05-14 16:53:58.369  INFO 4332 --- [           main] com.explore.controller.UserController    : 实例化  --->  afterPropertiesSet
2023-05-14 16:53:58.529  INFO 4332 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-05-14 16:53:58.539  INFO 4332 --- [           main] c.explore.BeanPostProcessorApplication   : Started BeanPostProcessorApplication in 1.009 seconds (JVM running for 1.899)
```
```text
Disconnected from the target VM, address: '127.0.0.1:53623', transport: 'socket'
2023-05-14 16:54:08.655  INFO 4332 --- [ionShutdownHook] com.explore.controller.UserController    : 销毁时  --->  UserController类销毁时调用了destroyMethod
2023-05-14 16:54:08.655  INFO 4332 --- [ionShutdownHook] com.explore.controller.UserController    : 销毁时 ---> destroy
```

**区别与联系**
```text
区别：
    1. @PostContract 和 @PreDestroy @Bean(initMethod = "") 是JDK自带的注解，Spring对其提供了支持；
    2. InitializingBean 和 DisposableBean 这两个接口为Spring提供的接口； 

联系：
    1. @PostContract 与 InitializingBean 都提供了Bean初始化时，调用初始化方法，以便在初始化时进行调用
    2. @PreDestroy 与 DisposableBean 都提供了Bean销毁时，回调的方法，在销毁Bean之前进行回调；

```

### CommandLineRunner & ApplicationRunner
```java
@FunctionalInterface
public interface CommandLineRunner {

	/**
	 * Callback used to run the bean.
	 * @param args incoming main method arguments
	 * @throws Exception on error
	 */
	void run(String... args) throws Exception;

}
```
```java
@FunctionalInterface
public interface ApplicationRunner {

	/**
	 * Callback used to run the bean.
	 * @param args incoming application arguments
	 * @throws Exception on error
	 */
	void run(ApplicationArguments args) throws Exception;

}
```

```text

调用时机：
    0. 源码： SpringApplication.run(String... args) ---> callRunners(context, applicationArguments); 
    1. 都是在Spring容器启动成功后调用；
    2. 可以用来初始化线程池、加载热数据；

区别：
    1. 两个接口的入参是不同的 ；
    
相同点：
    1. 接口中的方法名都是run方法；
    2. 在 run 方法内部抛出异常时, 应用都将无法正常启动；
    3. 都可以获取到启动时指定的外部参数。


执行顺序：
    1. ApplicationRunner > CommandLineRunner;
```

### FactoryBean

```text

1. FactoryBean包装一个对象，使用getObject()方法来获取真正的实例化对象;
2. FactoryBean包装的对象一般来自第三方，无法添加@Component等注解，就无法直接被Spring管理;
3. 这些对象是已经被实例化了的，有统一的来源，例如来自Mybatis;
4. 当然也可以自定义一个bean，使用FactoryBean来包装，但这好像是多余的。@Bean注解可以很好的解决需要自定义实例化、初始化过程的bean;

```


### BeanDefinition

```text
BeanDefinition 是定义 Bean 的配置元信息接口，包含：
    1. Bean 的类名
    2. 设置父 bean 名称、是否为 primary、
    3. Bean 行为配置信息，作用域、自动绑定模式、生命周期回调、延迟加载、初始方法、销毁方法等
    4. Bean 之间的依赖设置，dependencies
    5. 构造参数、属性设置
```


### AOP 

**初始化时机**
```text

1. AbstractAutowireCapableBeanFactory#createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
2. Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
3. bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
4. Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
5. AbstractAutoProxyCreator#postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
6. shouldSkip(beanClass, beanName)
7. AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors()
8. advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
9. BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors()
10. List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
11. ReflectiveAspectJAdvisorFactory#getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory)
12. Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, 0, aspectName);
13. return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod, this, aspectInstanceFactory, declarationOrderInAspect, aspectName);

```