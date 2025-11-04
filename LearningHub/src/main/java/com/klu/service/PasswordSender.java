package com.klu.service;

public class PasswordSender {
	String password;

	

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "PasswordSender [password=" + password + "]";
	}
	
}
