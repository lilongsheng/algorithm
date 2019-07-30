1.beanFactory初始化流程
2.BeanPostProcessor 的使用
在容器里面任意bean的初始化前、初始化后对其做一些操作，如修改成员、调用某些方法（切换DB连接等）
3.Spring的BeanPostProcessor和BeanFactoryPostProcessor区别
BeanPostProcessor：(发生在Spring容器的实例化和依赖注入之后)
bean级别的处理，针对某个具体的bean进行处理，接口提供了两个方法，分别是初始化前和初始化后执行方法
在init方法前后执行，需要注意一点，我们定义一个类实现了BeanPostProcessor，默认是会对整个Spring容器中所有的bean进行处理。
可以利用方法中的两个参数。类型分别为Object和String，第一个参数是每个bean的实例，第二个参数是每个bean的name或者id属性的值。定位到我们处理的bean

BeanFactoryPostProcessor：BeanFactory级别的处理，是针对整个Bean的工厂进行处理
利用getBeanDefinition的方法，找到我们定义bean的BeanDefinition对象，然后我们可以对定义的属性进行修改，
当我们在xml中定义了bean标签时，Spring会把这些bean标签解析成一个javabean，这个BeanDefinition就是bean标签对应的javabean。
Spring容器初始化bean大致过程
1.定义bean标签
2.将bean标签解析成BeanDefinition
    BeanFactoryPostProcessor(发生)
3.调用构造方法实例化(IOC)
4.属性值得依赖注入(DI)
    BeanPostProcessor(发生)

5.beanFactory 和 FactoryBean区别 联系

6.BeanFactoryAware、 BeanNameAware、ApplicationContextAware
让bean了解spring容器对其管理的细节信息，如让bean知道在容器中是以哪个名称被管理的，或者让bean知道beanFactory或这applicationcontext的存在，
也就是让该bean可以获取到beanfactory或applicationcontext的实例
BeanNameAware
如果某个bean需要访问配置文件中本身bean的id属性，这个bean类通过实现BeanNameAware接口，在依赖关系确定之后，初始化方法之前，提供回调自身的能力，从而获得本身bean的id属性BeanFactoryAware
实现了BeanFactoryAware接口的bean，可以直接通过beanfactory来访问spring的容器，当该bean被容器创建之后，会有一个相应的beanfactory的实例引
缺点：实例bean本身会依赖spring容器，增加了耦合度

7.spring xml bean 加载顺序
  1.执行构造方法
  2.BeanNameAware执行 （让bean有意识自己bean的名字）
  3.BeanFactoryAware执行 (让bean有意识自己的beanFactory是谁)
  4.InitializingBean执行 (初始化前执行)
  5.init-method执行 (初始化方法)
  6.BeanFactoryPostProcessor (初始化完beanFactory后执行)
  7.BeanPostProcessor (初始化前后执行 可以拿到具体实例对象) 当初了实现该接口的类，还有其它bean
时才会调用，意思是只有自己一个bean不需要执行，没啥意义