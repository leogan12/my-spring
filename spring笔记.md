### getSingleton方法 ###
<ol>
<li>从缓存中取数据
<pre>public Object getSingleton(String beanName) {
	return getSingleton(beanName, true);
}
</pre>
</li>
<li>从缓存中取数据，被一方法调用
<pre>protected Object getSingleton(String beanName, boolean allowEarlyReference) {}</pre></li>
<li>当缓存中取不到时，再次调用getSingleton的重载方法，将这个类的工厂方法放到三级缓存中去。如果放进去了，下次需要该类时，从一方法中可获取到。
<pre>// Create bean instance.创建实例对象（普通对象非代理），调用addSingletonFactory放入缓存
getSingleton(beanName, () -> {return createBean(beanName, mbd, args);});
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {}
</pre>
<pre>addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
// 可以看到 addSingletonFactory 和 getSingleton 的入参singletonFactory，虽然都是factory但是功能不同getSingleton是创建实例，
// 但是这个实例并不会直接放入缓存，addSingletonFactory放入缓存的是一个工厂方法，后续实现时会判断是否需要获取代理对象
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
	if (!this.singletonObjects.containsKey(beanName)) {
		this.singletonFactories.put(beanName, singletonFactory);
		this.earlySingletonObjects.remove(beanName);
		this.registeredSingletons.add(beanName);
	}
}
</pre>
</li>
</ol>

###BeanFactory 和 FactoryBean
<ol><li>BeanFactory是bean的工厂，是springIOC容器管理bean的一些规范，其实现类有ApplicationContext，主要方法为getBean，其内部会调用AbstractBeanFactory#doGetBean方法
	<pre>protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
			throws BeansException {
		String beanName = transformedBeanName(name);
		Object bean;
		// Eagerly check singleton cache for manually registered singletons.
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null && args == null) {
			...
			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
		}
		else {...}</pre>
		getObjectForBeanInstance内部会调用getObjectFromFactoryBean再调用doGetObjectFromFactoryBean，其内部会调用factory.getObject()，
		这里就是BeanFactory和FactoryBean的关联之处
		<pre>private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
		Object object;
		try {
			if (System.getSecurityManager() != null) {
				AccessControlContext acc = getAccessControlContext();
				try {
					object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
				}
				catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}
			else {
				object = factory.getObject();
			}
		}</pre>
	</li>
	<li>FactoryBean决定了BeanFactory最终要得到的实例对象是什么，也是我们能在spring中用到的对象实例
	<pre>protected Object getObjectForBeanInstance(
			Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

		// Don't let calling code try to dereference the factory if the bean isn't a factory.
		// 判断名称是否以&开头 如果要获取到FactoryBean本身，beanName必然是以&开头的
		if (BeanFactoryUtils.isFactoryDereference(name)) {
			if (beanInstance instanceof NullBean) {//null的话直接返回
				return beanInstance;
			}
			if (!(beanInstance instanceof FactoryBean)) {// 如果不是FactoryBean 就报错
				throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
			}
			if (mbd != null) {
				mbd.isFactoryBean = true;
			}
			return beanInstance;
		}

		// Now we have the bean instance, which may be a normal bean or a FactoryBean.
		// If it's a FactoryBean, we use it to create a bean instance, unless the
		// caller actually wants a reference to the factory.
		if (!(beanInstance instanceof FactoryBean)) {// 是FactoryBean就返回
			return beanInstance;
		}

		Object object = null;
		if (mbd != null) {
			mbd.isFactoryBean = true;
		}
		else {
			object = getCachedObjectForFactoryBean(beanName);//从缓存中获取 缓存是factoryBeanObjectCache
		}
		if (object == null) {
			// Return bean instance from factory.
			FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
			// Caches object obtained from FactoryBean if it is a singleton.
			if (mbd == null && containsBeanDefinition(beanName)) {
				mbd = getMergedLocalBeanDefinition(beanName);
			}
			boolean synthetic = (mbd != null && mbd.isSynthetic());
			object = getObjectFromFactoryBean(factory, beanName, !synthetic);//获取对象 内部再调用doGetObjectFromFactoryBean 内部再调用factory.getObject();
		}
		return object;
	}</pre>
	<pre></pre>
	</li></ol>
