

1. 我们写的mapper类都是接口，如果用spring的方式去生产对应的代理类，应该是jdk动态代理，生成的代理类不是我们想要的，我们需要生成的类是能关联数据库并进行数据库的操作；
2. 所以我们要定义自己的解析类，首先需要找到对应的mapper接口所在的位置，定义一个扫描类MapperScan注解，同时需要引入一个解析mapper的beanDefinition的类MapperScannerRegistrar，
这个类需要继承 ImportBeanDefinitionRegistrar ；
>2.1 对于用配置文件，我们的入口是MapperScannerConfigurer继承了BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor；
    最后都是要周doScan来扫描对应的路径获取BeanDefinitionHolder，这里调用spring的ClassPathBeanDefinitionScanner来获取BeanDefinition，得到的beanClass是Mapper接口对象，
    不是我们想要的mybatis对象，我们要的beanClass是MapperFactoryBean，所以接着运行了processBeanDefinitions()方法，如3中所示代码，修改对应的beanClass
https://gitee.com/leogan/forsave/blob/master/picture/MapperScannerConfigurer.png
3. 我们扫描包路径，得到对应的mapper类，进行遍历，注册自己的BeanDefinition，注意这里最主要注册的信息是beanName，beanClass=MapperFactoryBean.class；
```
    String beanClassName = definition.getBeanClassName();
    // the mapper interface is the original class of the bean  // Mapper接口是bean的原始类
    // but, the actual class of the bean is MapperFactoryBean  // 但是，bean 的实际类是 MapperFactoryBean
    definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
    definition.setBeanClass(this.mapperFactoryBeanClass);
```
4. MapperFactoryBean的getObject获取的实例是mybatis生成实例 MapperProxy；
```
public T getObject() throws Exception {
    return getSqlSession().getMapper(this.mapperInterface);
  }
```
5. MapperProxy实现了InvocationHandler？
1. Spring 整合mybaits 底层实现原理 https://blog.csdn.net/dandanforgetlove/article/details/105842378
2. 剑指spring源码(二)补充篇之ImportBeanDefinitionRegistrar https://blog.csdn.net/liurenyou/article/details/97025292
