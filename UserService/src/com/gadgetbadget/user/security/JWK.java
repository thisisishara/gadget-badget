package com.gadgetbadget.user.security;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * The class JWK is distributed among all the services as a part of the authentication 
 * process implemented by the USER SERVICE. JWK represents the JSON Web Key and used to
 * save and retrieve JWK data to and from the Local Database in each Web Service. JWK
 * data is configured beforehand manually. 
 * 
 * @author Ishara_Dissanayake
 */
public class JWK {
	private String key_id;
	private PrivateKey private_key;
	private PublicKey public_key;
	private int lifetime;
	private String issuer;
	private String audience;
	private String algorithm;
	private String date_last_updated;
	
	public JWK(String key_id, PrivateKey private_key, PublicKey public_key, int lifetime, String issuer,
			String audience, String algorithm, String date_last_updated) {
		super();
		this.key_id = key_id;
		this.private_key = private_key;
		this.public_key = public_key;
		this.lifetime = lifetime;
		this.issuer = issuer;
		this.audience = audience;
		this.algorithm = algorithm;
		this.date_last_updated = date_last_updated;
	}

	public String getKey_id() {
		return key_id;
	}

	public void setKey_id(String key_id) {
		this.key_id = key_id;
	}

	public PrivateKey getPrivate_key() {
		return private_key;
	}

	public void setPrivate_key(PrivateKey private_key) {
		this.private_key = private_key;
	}

	public PublicKey getPublic_key() {
		return public_key;
	}

	public void setPublic_key(PublicKey public_key) {
		this.public_key = public_key;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getDate_last_updated() {
		return date_last_updated;
	}

	public void setDate_last_updated(String date_last_updated) {
		this.date_last_updated = date_last_updated;
	}

	@Override
	public String toString() {
		return "JWK [key_id=" + key_id + ", private_key=" + private_key + ", public_key=" + public_key + ", lifetime="
				+ lifetime + ", issuer=" + issuer + ", audience=" + audience + ", algorithm=" + algorithm
				+ ", date_last_updated=" + date_last_updated + "]";
	}
	
	
		
}
