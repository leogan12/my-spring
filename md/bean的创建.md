getSingleton用于获取和创建bean，首先调用getSingleton方法从缓存获取，没有获取到，再次调用getSingleton的重载方法，放入ObjectFactory，并执行singletonFactory.getObject();来创建对象

1. 从一二三级缓存中查询，这个步骤是从getBean开始，到doGetBean方法中， DefaultSingletonBeanRegistry#getSingleton(beanName, true)：
这是查询缓存的方法，如果三级缓存中有的话，会执行singletonFactory.getObject()，一般出现在循环依赖的情况下，
导致执行2.1.1.2中的getEarlyBeanReference方法
``` Object sharedInstance = getSingleton(beanName); ```

2. doGetBean：缓存中没有就创建
```
sharedInstance = getSingleton(beanName, () -> {
		return createBean(beanName, mbd, args);
});
```

	2.1 DefaultSingletonBeanRegistry#getSingleton(String beanName, ObjectFactory<?> singletonFactory)：导致2中的createBean方法会执行
	``` singletonObject = singletonFactory.getObject(); ```
	
		2.1.1 调用createBean方法，返回beanInstance
		``` Object beanInstance = doCreateBean(beanName, mbdToUse, args); ```
	
			2.1.1.1  doCreateBean方法 实例化bean
			``` 
   			if (instanceWrapper == null) {
				instanceWrapper = createBeanInstance(beanName, mbd, args);
			}
			Object bean = instanceWrapper.getWrappedInstance(); ``` 
			
			2.1.1.2  doCreateBean 缓存到三级缓存，解决循环依赖，如果没有循环依赖，getEarlyBeanReference不会执行
			``` 
			if (earlySingletonExposure) {
				addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
			} ``` 
			
				2.1.1.2.1 addSingletonFactory方法 ObjectFactory<?> singletonFactory
				this.singletonFactories.put(beanName, singletonFactory);
				this.earlySingletonObjects.remove(beanName);
			
			2.1.1.3  doCreateBean  填充属性
			populateBean(beanName, mbd, instanceWrapper);
			2.1.1.4  doCreateBean  初始化
			exposedObject = initializeBean(beanName, exposedObject, mbd);
			
				2.1.1.4.1  initializeBean方法，并return wrappedBean;
				invokeAwareMethods(beanName, bean);
				wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
				invokeInitMethods(beanName, wrappedBean, mbd);
				//这里进行aop，AbstractAutoProxyCreator#wrapIfNecessary
				wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
				return wrappedBean;
			
			2.1.1.5  doCreateBean  返回	
			return exposedObject;
			
		2.1.2 createBean：返回
		return beanInstance;
	
	2.2 getSingleton： 已经拿到对象了
	newSingleton = true;

	2.3 getSingleton： 放入一级缓存（二级缓存可能是空的） 删除二三级缓存
	if (newSingleton) {
		addSingleton(beanName, singletonObject);
	}
	
 	2.4 getSingleton：返回
	return singletonObject;
				
4. doGetBean：这里会判断是否是FactoryBean 如mybatis中的MapperFactoryBean
bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
