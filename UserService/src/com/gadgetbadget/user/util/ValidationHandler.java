package com.gadgetbadget.user.util;

/**
 * This class is mainly used to do verifications need to be performed in various classes.
 * All verifications and data validations can be directed to the appropriate methods in 
 * this class and majority of the methods will possibly return a boolean value based on 
 * the validation done.
 * 
 * @author Ishara_Dissanayake
 */
public class ValidationHandler {

	/**
	 * This method is used to validate if a given user type of a consumed JSON object/s
	 * by the end-points are exactly a valid type defined in the UserType ENUM class. 
	 * 
	 * @param user_id		user_id given in the consumed JSON object or input parameters
	 * @param user_type		defined accurate user types hard-coded in the UserType ENUM class
	 * @return				returns a boolean true value when user type consumed is a valid user type
	 */
	public boolean validateUserType(String user_id, UserType user_type) {
		UserType newUser_type = UserType.INVLD;
		if(user_id.isEmpty() | user_id == null) {
			return false;
		}

		if(user_id.length()!=10) {
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
