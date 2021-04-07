package com.gadgetbadget.user.model;

import com.gadgetbadget.user.util.DBHandler;

public class User extends DBHandler{
	private String user_id;
	private String username;
	private String password;
	private String role_id;
	private String first_name;
	private String last_name;
	private char gender;
	private String date_joined;
	private String primary_email;
	private String primary_phone;

	public User() {
		
	}	
	
	public User(String user_id, String username, String password, String role_id, String first_name, String last_name,
			char gender, String date_joined, String primary_email, String primary_phone) {
		super();
		this.user_id = user_id;
		this.username = username;
		this.password = password;
		this.role_id = role_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.gender = gender;
		this.date_joined = date_joined;
		this.primary_email = primary_email;
		this.primary_phone = primary_phone;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole_id() {
		return role_id;
	}

	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public String getDate_joined() {
		return date_joined;
	}

	public void setDate_joined(String date_joined) {
		this.date_joined = date_joined;
	}

	public String getPrimary_email() {
		return primary_email;
	}

	public void setPrimary_email(String primary_email) {
		this.primary_email = primary_email;
	}

	public String getPrimary_phone() {
		return primary_phone;
	}

	public void setPrimary_phone(String primary_phone) {
		this.primary_phone = primary_phone;
	}
	
}
