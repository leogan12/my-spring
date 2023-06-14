### getSingleton方法 ###
<ol><li>从缓存中取数据
<pre>public Object getSingleton(String beanName) {
		return getSingleton(beanName, true);
	}</pre></li>
	<li>从缓存中取数据，被一方法调用
	<pre>protected Object getSingleton(String beanName, boolean allowEarlyReference) {}</pre></li>
	<li>当缓存中取不到时，会将这个类的工厂方法放到三级缓存中去。如果放进去了，下次需要该类时，从一方法中可获取到。
	<pre>public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {}</pre></li></ol>

