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
        
        // 通过断言处理，应用程序的main方法不能为空
        Assert.notNull(primarySources, "PrimarySources must not be null");
        
        // 将应用程序main方法以集合的方式存入primarySources里面
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
        
        // 通过堆栈信息推断出main方法所在类的全限定类名，并通过反射得到Class对象
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

            /**
             * 拓展: 
             * 
             * VM options: 程序中需要的运行时环境变量，它需要以-D或-X或-XX开头，每个参数使用空格分隔;
             * Program arguments: 传入main方法的字符串数组args[]，它通常以--开头;
             * Environment variables: 没有前缀，优先级低于VM options
             * 
             * 优先级： Program arguments > VM options > Environment variables
             */
            
            // 用来解析main方法中的入参【public static void main(String[] args) 中的 args】
            // 此参数可以在 Program arguments 中配置
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            
            // 启动Spring容器之前，准备并配置环境
            // 第一步：创建环境变量对象，并且解析配置main方法参数到环境对象中
            // 第二步：发送一个准备环境的事件给相关的监听者
            // 第三步：移动defaultProperties使其成为环境对象中的最后一个属性源
            // 第四步：将环境对象绑定到SpringApplication中
            // 第五步：如果不是自定义的环境，则创建一个环境转换器，将环境对象转换成标准环境（StandardEnvironment），如果已经是标准环境
            ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
            
            // 配置忽略Bean信息
            configureIgnoreBeanInfo(environment);
            
            // 打印Spring Banner，可自定义banner
            Banner printedBanner = printBanner(environment);
            
            // 根据Web应用类型 创建Spring容器（这里是 AnnotationConfigServletWebServerApplicationContext ）
            // 创建 DefaultListableBeanFactory Spring容器的核心 （初始化父类GenericApplicationContext时创建）
            context = createApplicationContext();
            
            // 第一步：应用程序上下文 注入 启动步骤对象
            // 第二步：DefaultListableBeanFactory 注入 程序启动步骤对象
            context.setApplicationStartup(this.applicationStartup);
            
            // 启动Spring容器之前，准备环境
            // 第一步：给Spring容器注入环境对象
            // 第二步：向BeanFactory注册 BeanNameGenerator、ConversionService（如果存在、如果需要添加转换服务）；向Spring容器注入资源加载器、资源加载器的类加载器（如果资源加载器存在的话）；
            // 第三步：执行在刷新Spring容器之前（创建之后），回调应用程序初始化器的实现（此操作用于增强应用程序上下文，ApplicationContextInitializer.class 上文说的地方）
            // 第四步：发布 contextPrepared 事件
            // 第五步：关闭引导上下文（DefaultBootstrapContext），并且发布 BootstrapContextClosedEvent 事件（此处暂无消费者）
            // 第六步：向BeanFactory注册单例Bean springApplicationArguments、springBootBanner，设置是否允许循环依赖、是否允许BeanDefinition重写
            // 第七步：如果存在延迟加载的类，则给Spring容器添加一个 LazyInitializationBeanFactoryPostProcessor 的BeanFactory后置处理器
            // 第八步：增加一个PropertySourceOrderingBeanFactoryPostProcessor BeanFactory后置处理器
            // 第九步：加载启动类，并且将启动类注册到beanDefinitionMap中
            // 第十步：发布 contextLoaded 事件，容器准备就绪
            
            // 注意：在执行 applyInitializers() 与 listeners.contextLoaded(context) 这两步时会向Spring容器中注册ApplicationListener的监听者，在刷新容器时会用到
            prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
            
            // 容器准备就绪，刷新Spring容器，此步骤为Spring的核心部分，下文单独做分析
            refreshContext(context);
            
            // Spring容器刷新之后执行（当前暂无拓展内容）
            afterRefresh(context, applicationArguments);
            
            // 计算启动所需时间，打印时间
            Duration timeTakenToStartup = Duration.ofNanos(System.nanoTime() - startTime);
            if (this.logStartupInfo) {
                new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), timeTakenToStartup);
            }
            
            // 发布 started 事件，Spring容器启动成功 
            listeners.started(context, timeTakenToStartup);
            
            // 调用拓展类 （ApplicationRunner 与 CommandLineRunner）的实现类
            callRunners(context, applicationArguments);
        }
        catch (Throwable ex) {
            handleRunFailure(context, ex, listeners);
            throw new IllegalStateException(ex);
        }
        try {
            // 整个Spring容器启动 + 执行拓展实现 所需时间，并且发布 ready 事件
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

**分析刷新Spring容器的过程**
```java

public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext {

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        synchronized (this.startupShutdownMonitor) {
            
            // 创建一个启动步骤（spring.context.refresh），applicationStartup 的注入位置在上文已有说明
            // 标志着开始刷新Spring的上下文
            StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");

            // 刷新上下文之前的准备
            // 1. 计算刷新上下文的启动时间
            // 2. 将此上下文切换至活动状态
            // 3. 初始化上下文中所有的占位符属性源,初始化 servlet 上下文参数（servletContextInitParams）与配置参数（servletConfigInitParams）
            // 4. 验证环境变量中的必填属性，如果必填属性不存在，抛出 MissingRequiredPropertiesException 异常
            // 5. 将应用程序监听器（在准备Spring容器期间加入监听者列表的）保存至earlyApplicationListeners，并创建存储早期的应用程序事件集合对象earlyApplicationEvents
            prepareRefresh();

            // 获取BeanFactory对象
            // 第一步：将应用上下文状态改为可刷新状态，并且设置序列化ID
            // 第二步：通过一个公有方法返回BeanFactory对象
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            // 准备此上下文中要使用的BeanFactory
            // 1. 向BeanFactory注入类加载器（classLoader）
            // 2. 如果支持spel表达式则向BeanFactory设置一个 StandardBeanExpressionResolver 解析器,用以支持解析spel
            // 3. 向BeanFactory增加一个ResourceEditorRegistrar，主要用于对Bean属性编辑的工具
            // 4. 向BeanFactory增加一个 ApplicationContextAwareProcessor Bean后置处理器,在Bean实例化之后注入ApplicationContext对象
            // 5. 向BeanFactory增加一批忽略依赖接口（EnvironmentAware、EmbeddedValueResolverAware、ResourceLoaderAware、ApplicationEventPublisherAware、MessageSourceAware、ApplicationContextAware、ApplicationStartupAware）
            // 6. 给指定的依赖类型设置自动自动注入的值（BeanFactory、ResourceLoader、ApplicationEventPublisher、ApplicationContext），白话举例：只要 xXX instanceof BeanFactory 成立，那么这个对象就会被注入指定的值
            // 7. 向BeanFactory增加一个 ApplicationListenerDetector Bean后置处理器，用于早期的Bean可靠性检查
            // 8. 如果发现LoadTimeWeaver，则增加一个LoadTimeWeaverAwareProcessor Bean后置处理器，并且为类型匹配设置一个临时的类加载器
            // 9. 注册默认的环境Bean (environment、systemProperties、systemEnvironment、applicationStartup)
            prepareBeanFactory(beanFactory);

            try {
                // Allows post-processing of the bean factory in context subclasses.
                postProcessBeanFactory(beanFactory);

                StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");
                // Invoke factory processors registered as beans in the context.
                invokeBeanFactoryPostProcessors(beanFactory);

                // Register bean processors that intercept bean creation.
                registerBeanPostProcessors(beanFactory);
                beanPostProcess.end();

                // Initialize message source for this context.
                initMessageSource();

                // Initialize event multicaster for this context.
                initApplicationEventMulticaster();

                // Initialize other special beans in specific context subclasses.
                onRefresh();

                // Check for listener beans and register them.
                registerListeners();

                // Instantiate all remaining (non-lazy-init) singletons.
                finishBeanFactoryInitialization(beanFactory);

                // Last step: publish corresponding event.
                finishRefresh();
            }

            catch (BeansException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
                }

                // Destroy already created singletons to avoid dangling resources.
                destroyBeans();

                // Reset 'active' flag.
                cancelRefresh(ex);

                // Propagate exception to caller.
                throw ex;
            }

            finally {
                // Reset common introspection caches in Spring's core, since we
                // might not ever need metadata for singleton beans anymore...
                resetCommonCaches();
                contextRefresh.end();
            }
        }
    }
    
}

```
