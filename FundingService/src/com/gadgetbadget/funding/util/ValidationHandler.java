package com.gadgetbadget.funding.util;

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
				if(paymentMethodDetails.get("creditcard_no").getAsString().length() != 13 || (paymentMethodDetails.get("creditcard_no").getAsString().length() != 16)) {
					return false;
				}

				if(paymentMethodDetails.get("creditcard_security_no").getAsString().length() != 3 || paymentMethodDetails.get("creditcard_security_no").getAsString().length() != 4) {
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
				
			}
			
			return true;

		} catch (Exception ex) {
			return false;
		}
	}
}
