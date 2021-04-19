package com.gadgetbadget.payment.security;

import java.security.Principal;

/**
 * This class is a exact copy of the original class with the same name in the UserService 
 * and used to inject JWT PAYLOAD data to security context of the authenticated user for
 * user/service role authorization process through RoleSecurityContext.java class.
 * 
 * @author Ishara_Dissanayake
 */
public class UserPrincipal implements Principal{
	private String userdata;
	
	public UserPrincipal(String username, String user_id) {
		this.userdata = username+";"+user_id;
	}
	
	@Override
	public String getName() {
		return this.userdata;
	}

}
