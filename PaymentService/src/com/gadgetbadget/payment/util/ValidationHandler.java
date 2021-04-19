package com.gadgetbadget.payment.util;

import java.math.BigInteger;
import java.sql.Timestamp;

import com.google.gson.JsonObject;

public class ValidationHandler {
	public boolean verifyPaymentMethod(JsonObject paymentMethodDetails) {
		try {
			if (! (paymentMethodDetails.has("creditcard_no") || paymentMethodDetails.has("creditcard_security_no") || paymentMethodDetails.has("exp_date") || paymentMethodDetails.has("creditcard_type"))) {
				return false;
			}

			BigInteger cc_no = new BigInteger(paymentMethodDetails.get("creditcard_no").getAsString());
			System.out.print("CC:::" + cc_no);

			if (paymentMethodDetails.get("creditcard_type").getAsString().equalsIgnoreCase("Master") || paymentMethodDetails.get("creditcard_type").getAsString().equalsIgnoreCase("Visa")) {
				if(! (paymentMethodDetails.get("creditcard_no").getAsString().length() == 13 || (paymentMethodDetails.get("creditcard_no").getAsString().length() == 16))) {
					return false;
				}

				if(! (paymentMethodDetails.get("creditcard_security_no").getAsString().length() == 3 || paymentMethodDetails.get("creditcard_security_no").getAsString().length() == 4)) {
					return false;
				}


			} else {
				if(paymentMethodDetails.get("creditcard_no").getAsString().length() != 16) {
					return false;
				}

				if(paymentMethodDetails.get("creditcard_security_no").getAsString().length() != 4) {
					return false;
				}
			}


			Timestamp expTimestamp = Timestamp.valueOf(paymentMethodDetails.get("exp_date").getAsString());
			Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());

			if(expTimestamp.before(nowTimestamp)) {
				return false;
			}

			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	//validate the type of the user by id
	public boolean validateUserId(String user_id, String user_type) {
		String newUser_type = null;

		try {

			if(user_id.length()!=10) {
				System.out.println("ValidationHandler.class:: ID length is wrong.");
				return false;
			}

			//try converting the body into an integer and see if it contains anything other than numbers
			@SuppressWarnings("unused")
			BigInteger id_without_prefix = new BigInteger(user_id.substring(2,10));

			if(user_id.isEmpty() || user_id == null) {
				System.out.println("ValidationHandler.class:: Null Values received for ID.");
				return false;
			}

			if(user_id.substring(0,2).equalsIgnoreCase("AD")) {
				newUser_type = "ADMIN";
			} else if(user_id.substring(0,2).equalsIgnoreCase("CN")) {
				newUser_type = "CNSMR";
			} else if(user_id.substring(0,2).equalsIgnoreCase("FN")) {
				newUser_type = "FUNDR";
			} else if(user_id.substring(0,2).equalsIgnoreCase("FM")) {
				newUser_type = "FNMGR";
			} else if(user_id.substring(0,2).equalsIgnoreCase("RS")) {
				newUser_type = "RSCHR";
			} else if(user_id.substring(0,2).equalsIgnoreCase("EM")) {
				newUser_type = "EMPLY";
			} else {
				newUser_type = null;
			}

			if(!newUser_type.equals(user_type)) {
				System.out.println("ValidationHandler.class:: User type provided in the payload does not match the needed user type.");
				return false;
			}

			return true;
		} catch (Exception ex) {
			System.out.println("ValidationHandler.class:: ID contains illegal characters.");
			return false;
		}
	}

}