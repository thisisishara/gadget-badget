package com.gadgetbadget.payment.security;

import java.security.Principal;
import java.util.Set;

import javax.ws.rs.core.SecurityContext;

/**
 * This class is a exact copy of the original class with the same name in the UserService and
 * used to inject JWT PAYLOAD data to security context of the authenticated user for
 * user/service role authorization process by extending the SecurityContext Interface
 * 
 * @author Ishara_Dissanayake
 */
public class RoleSecurityContext implements SecurityContext{

	private Set<String> roles;
	private String username;
	private String user_id;
	private boolean isSecure;
	
	public RoleSecurityContext(Set<String> roles, String username, String user_id, boolean isSecure) {
		this.roles = roles;
		this.username = username;
		this.user_id = user_id;
		this.isSecure = isSecure;
	}
	
	@Override
	public Principal getUserPrincipal() {
		return new UserPrincipal(username, user_id);
	}

	@Override
	public boolean isUserInRole(String role) {
		return roles.contains(role);
	}

	@Override
	public boolean isSecure() {
		return isSecure;
	}

	@Override
	public String getAuthenticationScheme() {
		return "Token-Based Authentication";
	}

}
