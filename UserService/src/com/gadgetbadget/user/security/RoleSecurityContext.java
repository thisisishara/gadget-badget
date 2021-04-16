package com.gadgetbadget.user.security;

import java.security.Principal;
import java.util.Set;

import javax.ws.rs.core.SecurityContext;

/**
 * This class is used to Inject new user data which were obtained from the JWT PAYLOAD
 * to the security context and the user/service principal of the request after authentication
 * process is succeeded. Plays a major role in user/service authorization at end-points.
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
