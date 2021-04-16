package com.gadgetbadget.user.security;

import java.security.Principal;

/**
 * This class is used to Inject new user principal to the security context of the request after authentication
 * process is succeeded.
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
