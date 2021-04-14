package com.gadgetbadget.user.security;

import java.security.Principal;

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
