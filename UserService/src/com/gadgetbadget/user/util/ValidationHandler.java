package com.gadgetbadget.user.util;

public class ValidationHandler {
	
	//validate user type
	public boolean validateUserType(String user_id, UserType user_type) {
		UserType newUser_type = UserType.INVLD;
		if(user_id.isEmpty() | user_id == null) {
			return false;
		}
		
		if(user_id.substring(0,2).equalsIgnoreCase("AD")) {
			newUser_type = UserType.ADMIN;
		} else if(user_id.substring(0,2).equalsIgnoreCase("CN")) {
			newUser_type = UserType.CNSMR;
		} else if(user_id.substring(0,2).equalsIgnoreCase("FN")) {
			newUser_type = UserType.FUNDR;
		} else if(user_id.substring(0,2).equalsIgnoreCase("FM")) {
			newUser_type = UserType.FNMGR;
		} else if(user_id.substring(0,2).equalsIgnoreCase("RS")) {
			newUser_type = UserType.RSCHR;
		} else if(user_id.substring(0,2).equalsIgnoreCase("EM")) {
			newUser_type = UserType.EMPLY;
		} else {
			newUser_type = UserType.INVLD;
		}
		
		if(newUser_type != user_type) {
			return false;
		}
		
		return true;		
	}
}
