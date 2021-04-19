package com.gadgetbadget.payment.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jose4j.base64url.Base64;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import com.gadgetbadget.payment.util.DBHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This is a variant of the original JWTHandler class in the USER SERVICE.
 * This class only contains a subset of the methods in the original class
 * which helps to decode and verify User and Service JWT Authorization Tokens.
 * 
 * @author Ishara_Dissanayake
 */
public class JWTHandler extends DBHandler{
	public boolean validateServiceToken(String jwt) throws MalformedClaimException, JoseException {
		try
		{
			// Retrieve RSA and JW Key Attribute Values from local storage
			JWK jwk = getJWKFromDB("JWK2");

			// Set JWT Claims for Validation
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
					.setAllowedClockSkewInSeconds(2000000000) 
					.setRequireSubject()
					.setExpectedIssuer(jwk.getIssuer())
					.setExpectedAudience(jwk.getAudience())
					.setVerificationKey(jwk.getPublic_key())
					.setJwsAlgorithmConstraints(ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
					.build();


			// Validate the JWT and process it to the Claims
			JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
			System.out.println("JWT validated. JWT Claims: " + jwtClaims);
			return true;
		}
		catch (InvalidJwtException ex)
		{
			System.out.println("Invalid JWT! " + ex);

			if (ex.hasExpired())
			{
				System.out.println("JWT expired at " + ex.getJwtContext().getJwtClaims().getExpirationTime());
			}

			if (ex.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
			{
				System.out.println("JWT had wrong audience: " + ex.getJwtContext().getJwtClaims().getAudience());
			}

			return false;
		}
		catch (Exception ex) {
			System.out.println("Failed to validate the given Service JWT. Exception Details: " + ex.getMessage());
			return false;
		}
	}

	//Validate JWT Token for User Authentication
	public boolean validateToken(String jwt) throws MalformedClaimException, JoseException {
		try
		{
			// Retrieve RSA and JW Key Attribute Values from local storage
			JWK jwk = getJWKFromDB("JWK1");

			// Set JWT Claims for Validation
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
					.setRequireExpirationTime()
					.setAllowedClockSkewInSeconds(jwk.getLifetime())
					.setRequireSubject()
					.setExpectedIssuer(jwk.getIssuer())
					.setExpectedAudience(jwk.getAudience())
					.setVerificationKey(jwk.getPublic_key())
					.setJwsAlgorithmConstraints(ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
					.build();


			// Validate the JWT and process it to the Claims
			JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
			System.out.println("JWT validated. JWT Claims: " + jwtClaims);
			return true;
		}
		catch (InvalidJwtException ex)
		{
			System.out.println("Invalid JWT! " + ex);

			if (ex.hasExpired())
			{
				System.out.println("JWT expired at " + ex.getJwtContext().getJwtClaims().getExpirationTime());
			}

			if (ex.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
			{
				System.out.println("JWT had wrong audience: " + ex.getJwtContext().getJwtClaims().getAudience());
			}

			return false;
		}
		catch (Exception ex) {
			System.out.println("Failed to validate the given JWT. Exception Details: " + ex.getMessage());
			return false;
		}
	}

	//Retrieve RSA keys from Local DB
	public JWK getJWKFromDB(String key_id) {
		JWK jwk = null;
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				jwk = null;
			}

			String query = "SELECT *  FROM `jwt_config` WHERE `jwt_kid`=?;";
			PreparedStatement prstmt = conn.prepareStatement(query);
			prstmt.setString(1, key_id);

			ResultSet rs = prstmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				jwk = null;
			}

			while (rs.next())
			{
				byte[] pubbytes = rs.getBytes("jwt_public");
				byte[] pribytes = rs.getBytes("jwt_private");
				String kid = rs.getString("jwt_kid");
				String algo = rs.getString("jwt_algo");
				int lifetime = rs.getInt("jwt_lifetime");
				String issuer = rs.getString("jwt_issuer");
				String audience = rs.getString("jwt_audience");
				String date_last_updated = rs.getTimestamp("jwt_date_last_updated").toString();

				PublicKey pub = (PublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubbytes));
				PrivateKey pri = (PrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(pribytes));

				jwk = new JWK(kid,pri, pub, lifetime, issuer, audience, algo, date_last_updated);
			}
			conn.close();
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
		}
		return jwk;
	}

	public JsonObject decodeJWTPayload(String jwt) {
		String[] jwtSplitted = jwt.split("\\.");
		String jwtDecoded = new String(Base64.decode(jwtSplitted[1]));
		JsonObject jwtPayload = new JsonParser().parse(jwtDecoded).getAsJsonObject();
		return jwtPayload;
	}
}
