# explore-spring
探索Spring中相关的拓展点

***使用的相关版本***
```xml

<!-- JVM版本 -->
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>

<!-- Spring版本 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>

```

**SpringBoot 启动流程**
```java
/**
 * SpringBoot 启动入口
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```java
/**
 * 启动入口进入后，有以下两个部分
 * 1. 创建 SpringApplication 对象
 * 2. 调用 SpringApplication.run(..)
 */
public class SpringApplication {

    /**
     * Static helper that can be used to run a {@link SpringApplication} from the
     * specified sources using default settings and user supplied arguments.
     * @param primarySources the primary sources to load
     * @param args the application arguments (usually passed from a Java main method)
     * @return the running {@link ApplicationContext}
     */
    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new SpringApplication(primarySources).run(args);
    }
    
}
```

**SpringApplication**
```text
SpringApplication类是一个引导创建启动Spring应用程序的引导类
```

**分析 new SpringApplication(primarySources)**
```java
public class SpringApplication {

    /**
     * @param primarySources
     */
    public SpringApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    /**
     * 在调用run()方法之前，先创建一个SpringApplication实例对象
     * @param resourceLoader
     * @param primarySources
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        
        this.resourceLoader = resourceLoader;
        
        // 通过断言处理，应用程序的主方法不能为空
        Assert.notNull(primarySources, "PrimarySources must not be null");
        
        // 将应用程序主方法以集合的方式存入primarySources里面
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
        
        // 通过类路径来推断当前Web应用程序的类型（NONE、SERVLET、REACTIVE）
        // NONE： 该应用程序不应作为Web应用程序运行，也不应启动嵌入式Web服务器。
        // SERVLET： 该应用程序应作为基于servlet的Web应用程序运行，并应启动嵌入式servlet Web服务器。
        // REACTIVE： 该应用程序应作为反应式Web应用程序运行，并应启动嵌入式反应式Web服务器。
        this.webApplicationType = WebApplicationType.deduceFromClasspath();


        /**
         * getSpringFactoriesInstances()
         * 
         * 此方法的作用是从 MATE-INF/spring.factories 文件中读取到指定接口的实现类
         * 并且通过反射创建实现类的实例
         */

        // 此初始化器的目的在于，创建引导上下文（DefaultBootstrapContext）之后进行回调，提供了自定义增强DefaultBootstrapContext的拓展点
        this.bootstrapRegistryInitializers = new ArrayList<>(getSpringFactoriesInstances(BootstrapRegistryInitializer.class));
        
        // 此初始化器的作用在于，应用程序上下文（ConfigurableApplicationContext）创建之后，刷新之前，提供了自定义增强上下文的拓展点，下文会标注执行初始化器的位置
        setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
        
        // 此应用程序监听者将用于EventPublishingRunListener实例化(也就是获取SpringApplicationRunListener接口的实现类的时候)的时候，订阅 SimpleApplicationEventMulticaster
        setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
        
        // 通过堆栈信息推断出主方法所在类的全限定类名，并通过反射得到Class对象
        this.mainApplicationClass = deduceMainApplicationClass();
    }
    
}
```

**分析 new SpringApplication(primarySources).run(args)**
```java

public class SpringApplication {

    /**
     * 分析SpringApplication.run()的整个执行流程
     * 
     * 整体功能点: 运行Spring应用程序，创建并刷新一个新的ApplicationContext。
     * 
     * @param args
     * @return
     */
    public ConfigurableApplicationContext run(String... args) {
        
        // 整个Spring容器启动的开始时间（此时还在准备阶段）
        long startTime = System.nanoTime();
        
        // 创建一个引导上下文对象，此对象的作用在于创建出Spring容器之前，用来保存一下实例化对象，提供给后续创建Spring容器的过程使用
        DefaultBootstrapContext bootstrapContext = createBootstrapContext();
        ConfigurableApplicationContext context = null;
        
        // 设置该应用程序,即使没有检测到显示器,也允许其启动.
        configureHeadlessProperty();
        
        // 创建SpringApplicationRunListener的集合对象
        // 并且初始化 MATA-INF/spring.factories文件中的SpringApplicationRunListener接口的实现类EventPublishingRunListener
        // 而 EventPublishingRunListener初始化的时候不仅会创建一个SimpleApplicationEventMulticaster对象，而且还会将上面分析的ApplicationListener的相关实现添加到事件监听者集合中，以便与收到消息后通知这些监听者
        SpringApplicationRunListeners listeners = getRunListeners(args);
        
        // 此代码做了以下几件事
        // 第一：创建了一个名为 spring.boot.application.starting 的启动步骤
        // 第二：调用EventPublishingRunListener.starting()方法向所有的ApplicationListener监听者发送一个 ApplicationStartingEvent 消息
        // 第三：给启动步骤设置Tag
        // 第四：启动步骤调用end()方法，表示当前步骤已结束
        listeners.starting(bootstrapContext, this.mainApplicationClass);
        
        try {
            
            
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
            configureIgnoreBeanInfo(environment);
            Banner printedBanner = printBanner(environment);
            context = createApplicationContext();
            context.setApplicationStartup(this.applicationStartup);
            prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
            refreshContext(context);
            afterRefresh(context, applicationArguments);
            Duration timeTakenToStartup = Duration.ofNanos(System.nanoTime() - startTime);
            if (this.logStartupInfo) {
                new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), timeTakenToStartup);
            }
            listeners.started(context, timeTakenToStartup);
            callRunners(context, applicationArguments);
        }
        catch (Throwable ex) {
            handleRunFailure(context, ex, listeners);
            throw new IllegalStateException(ex);
        }
        try {
            Duration timeTakenToReady = Duration.ofNanos(System.nanoTime() - startTime);
            listeners.ready(context, timeTakenToReady);
        }
        catch (Throwable ex) {
            handleRunFailure(context, ex, null);
            throw new IllegalStateException(ex);
        }
        return context;
    }
    
}

```
