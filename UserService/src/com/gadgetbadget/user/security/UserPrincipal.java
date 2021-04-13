package com.gadgetbadget.user.security;

import java.security.Principal;

public class UserPrincipal implements Principal{
	private String username;
	private String user_id;
	
	public UserPrincipal(String username, String user_id) {
		this.username = username;
		this.user_id = user_id;
	}
	
	@Override
	public String getName() {
		return this.username;
	}
	
	public String getUserId() {
		return this.user_id;
	}

}
