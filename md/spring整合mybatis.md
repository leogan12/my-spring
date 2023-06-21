1. Spring 整合mybaits 底层实现原理 https://blog.csdn.net/dandanforgetlove/article/details/105842378
2. 剑指spring源码(二)补充篇之ImportBeanDefinitionRegistrar https://blog.csdn.net/liurenyou/article/details/97025292

1. 我们写的mapper类都是接口，如果用spring的方式去生产对应的代理类，应该是jdk动态代理，生成的代理类不是我们想要的，我们需要生成的类是能关联数据库并进行数据库的操作；
2. 所以我们要定义自己的解析类，首先需要找到对应的mapper接口所在的位置，定义一个扫描类MapperScan注解，同时需要引入一个解析mapper的beanDefinition的类MapperScannerRegistrar，
这个类需要继承 ImportBeanDefinitionRegistrar ；
3. 我们扫描包路径，得到对应的mapper类，进行遍历，注册自己的BeanDefinition，注意这里最主要注册的信息是beanName，beanClass=MapperFactoryBean.class；
```
    String beanClassName = definition.getBeanClassName();
    // the mapper interface is the original class of the bean  // Mapper接口是bean的原始类
    // but, the actual class of the bean is MapperFactoryBean  // 但是，bean 的实际类是 MapperFactoryBean
    definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
    definition.setBeanClass(this.mapperFactoryBeanClass);
```
4. MapperMethodProxy
