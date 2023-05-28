package com.leo.test;

import com.leo.controller.UserController;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {

	@Test
	public void test() {
		// 获取 Spring 容器对象
		// 执行这行代码相当于启动了 Spring 容器，解析 spring.xml 文件，并且实例化所有的 bean 对象放到 spring 容器中
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
		// 获取 UserController 对象
		UserController userController = applicationContext.getBean("userController", UserController.class);
		// 执行方法
		String allUsers = userController.findAll();
		System.out.println("allUsers = " + allUsers);
	}
}
