package com.leo.controller;


import com.leo.service.UserService;

public class UserController {
	private UserService userService;

	/**
	 * DI：set 方法注入
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String findAll() {
		return userService.findAll();
	}
}
