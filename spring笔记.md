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
// 可以看到 addSingletonFactory 和 getSingleton 的入参singletonFactory，虽然都是factory但是功能不同getSingleton是创建实例，但是这个实例并不会直接放入缓存，addSingletonFactory放入缓存的是一个工厂方法，后续实现时会判断是否需要获取代理对象
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

