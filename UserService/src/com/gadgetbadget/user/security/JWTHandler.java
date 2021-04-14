package com.gadgetbadget.user.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jose4j.base64url.Base64;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import com.gadgetbadget.user.util.DBHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JWTHandler extends DBHandler {
	private static String JWT_SUBJECT = "gadgetbadget.auth.";

	//Generate JWT for User Authentication
	//Expiration is set to 30 minutes
	public String generateToken(String username, String user_id, String role) throws JoseException, SQLException {
		String jwt = null;

		JWT_SUBJECT += username;
		JWK jwk = getJWKFromDB();

		RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
		rsaJsonWebKey.setKeyId("JWK1");

		//Generate the JWT Header
		JwtClaims claims = new JwtClaims();
		claims.setIssuer(jwk.getIssuer());  // who creates the token and signs it
		claims.setAudience(jwk.getAudience()); // to whom the token is intended to be sent
		claims.setExpirationTimeMinutesInTheFuture(jwk.getLifetime()); // time when the token will expire (10 minutes from now)
		claims.setGeneratedJwtId(); // a unique identifier for the token
		claims.setIssuedAtToNow();  // when the token was issued/created (now)
		claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		claims.setSubject(JWT_SUBJECT); // the subject/principal is whom the token is about

		//Generate the JWT PAYLOAD
		claims.setClaim("username",username); // additional claims/attributes about the subject can be added
		claims.setClaim("user_id",user_id);
		claims.setClaim("role",role);

		// A JWT is a JWS and/or a JWE with JSON claims as the PAYLOAD.
		// In this example it is a JWS so we create a JsonWebSignature object.
		JsonWebSignature jws = new JsonWebSignature();

		// The PAYLOAD of the JWS is JSON content of the JWT Claims
		jws.setPayload(claims.toJson());

		// The JWT is signed using the private key
		jws.setKey(jwk.getPrivate_key());

		// Set the Key ID (kid) header because it's just the polite thing to do.
		// We only have one key in this example but a using a Key ID helps
		// facilitate a smooth key roll-over process
		jws.setKeyIdHeaderValue(jwk.getKey_id());

		// Set the signature algorithm on the JWT/JWS that will integrity protect the claims
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// Sign the JWS and produce the compact serialization or the complete JWT/JWS
		// representation, which is a string consisting of three dot ('.') separated
		// base64url-encoded parts in the form Header.Payload.Signature
		// If you wanted to encrypt it, you can simply set this JWT as the PAYLOAD
		// of a JsonWebEncryption object and set the CTY (Content Type) header to "JWT".
		jwt = jws.getCompactSerialization();


		// Now you can do something with the JWT. Like send it to some other party
		// over the clouds and through the interwebs.
//		System.out.println("Private Key B64: " + Base64.encode(rsaJsonWebKey.getPrivateKey().getEncoded())+"\nPublic Key B64: " + Base64.encode(rsaJsonWebKey.getPublicKey().getEncoded())+ "\nJWToken: " + jwt);
//		System.out.println("Private Key Encoded: " + rsaJsonWebKey.getPrivateKey().getEncoded()+"\nPublic Key Encoded: " + rsaJsonWebKey.getPublicKey().getEncoded());
//		System.out.println("Private Key RAW: " + rsaJsonWebKey.getPrivateKey()+"\nPublic Key RAW: " + rsaJsonWebKey.getPublicKey());

		//validateToken(jwt, Base64.encode(rsaJsonWebKey.getPrivateKey().getEncoded()), Base64.encode(rsaJsonWebKey.getPublicKey().getEncoded()));
//		System.out.println("isGenerated and saved: " + saveToDB(rsaJsonWebKey));

		return jwt;
	}
	
	//Validate JWT Token for User Authentication
	public boolean validateToken(String jwt) throws MalformedClaimException, JoseException {
		try
		{
			JWK jwk = getJWKFromDB();

			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
					.setRequireExpirationTime() // the JWT must have an expiration time
					.setAllowedClockSkewInSeconds(jwk.getLifetime()) // allow some leeway in validating time based claims to account for clock skew
					.setRequireSubject() // the JWT must have a subject claim
					.setExpectedIssuer(jwk.getIssuer()) // whom the JWT needs to have been issued by
					.setExpectedAudience(jwk.getAudience()) // to whom the JWT is intended for
					.setVerificationKey(jwk.getPublic_key()) // verify the signature with the public key
					.setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
							ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256) // which is only RS256 here
					.build(); // create the JwtConsumer instance


			//  Validate the JWT and process it to the Claims
			JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
			System.out.println("JWT validation succeeded! " + jwtClaims);
			return true;
		}
		catch (InvalidJwtException ex)
		{
			// InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
			// Hopefully with meaningful explanations(s) about what went wrong.
			System.out.println("Invalid JWT! " + ex);

			// Programmatic access to (some) specific reasons for JWT invalidity is also possible
			// should you want different error handling behavior for certain conditions.

			// Whether or not the JWT has expired being one common reason for invalidity
			if (ex.hasExpired())
			{
				System.out.println("JWT expired at " + ex.getJwtContext().getJwtClaims().getExpirationTime());
			}

			// Or maybe the audience was invalid
			if (ex.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
			{
				System.out.println("JWT had wrong audience: " + ex.getJwtContext().getJwtClaims().getAudience());
			}

			return false;
		}
		catch (Exception ex) {
			System.out.println("Invalid JWT?? " + ex);
			return false;
		}
	}

	//Save RSA Keys to Local DB
	public boolean saveJWKToDB(RsaJsonWebKey rsa) {
		boolean res = false;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				res = false;
			}

			String query = "UPDATE `jwt_config` SET `jwt_public`=?, `jwt_private`=? WHERE `jwt_kid`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setBytes(1, rsa.getPublicKey().getEncoded());
			preparedStmt.setBytes(2, rsa.getPrivateKey().getEncoded());
			preparedStmt.setString(3, "JWK1");

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				res = true;
			} else {
				res = false;
			}
		} catch (Exception ex) {
			System.out.println("exception in token save: " + ex.getMessage());
		}
		return res;
	}

	//Retrieve RSA keys from Local DB
	public JWK getJWKFromDB() {
		JWK jwk = null;
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				jwk = null;
			}

			String query = "SELECT *  FROM `jwt_config` WHERE `jwt_kid`=?;";
			PreparedStatement prstmt = conn.prepareStatement(query);
			prstmt.setString(1, "JWK1");

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
	

	//SERVICE Token Verification
	//Expiration is not set
	public String generateServiceToken(String service_name, String service_id, String service_role) throws JoseException, SQLException {
		String jwt = null;

		JWT_SUBJECT += service_name;
		JWK jwk = getJWKFromDB();

		RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
		rsaJsonWebKey.setKeyId("JWK1");

		//Generate the JWT Header
		JwtClaims claims = new JwtClaims();
		claims.setIssuer(jwk.getIssuer());  // who creates the token and signs it
		claims.setAudience(jwk.getAudience()); // to whom the token is intended to be sent
		claims.setGeneratedJwtId(); // a unique identifier for the token
		claims.setIssuedAtToNow();  // when the token was issued/created (now)
		claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		claims.setSubject(JWT_SUBJECT); // the subject/principal is whom the token is about

		//Generate the JWT PAYLOAD
		claims.setClaim("username",service_name); // additional claims/attributes about the subject can be added
		claims.setClaim("user_id",service_id);
		claims.setClaim("role",service_role);

		// A JWT is a JWS and/or a JWE with JSON claims as the PAYLOAD.
		// In this example it is a JWS so we create a JsonWebSignature object.
		JsonWebSignature jws = new JsonWebSignature();

		// The PAYLOAD of the JWS is JSON content of the JWT Claims
		jws.setPayload(claims.toJson());

		// The JWT is signed using the private key
		jws.setKey(jwk.getPrivate_key());

		// Set the Key ID (kid) header because it's just the polite thing to do.
		// We only have one key in this example but a using a Key ID helps
		// facilitate a smooth key roll-over process
		jws.setKeyIdHeaderValue(jwk.getKey_id());

		// Set the signature algorithm on the JWT/JWS that will integrity protect the claims
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// Sign the JWS and produce the compact serialization or the complete JWT/JWS
		// representation, which is a string consisting of three dot ('.') separated
		// base64url-encoded parts in the form Header.Payload.Signature
		// If you wanted to encrypt it, you can simply set this JWT as the PAYLOAD
		// of a JsonWebEncryption object and set the CTY (Content Type) header to "JWT".
		jwt = jws.getCompactSerialization();


		// Now you can do something with the JWT. Like send it to some other party
		// over the clouds and through the interwebs.
//		System.out.println("Private Key B64: " + Base64.encode(rsaJsonWebKey.getPrivateKey().getEncoded())+"\nPublic Key B64: " + Base64.encode(rsaJsonWebKey.getPublicKey().getEncoded())+ "\nJWToken: " + jwt);
//		System.out.println("Private Key Encoded: " + rsaJsonWebKey.getPrivateKey().getEncoded()+"\nPublic Key Encoded: " + rsaJsonWebKey.getPublicKey().getEncoded());
//		System.out.println("Private Key RAW: " + rsaJsonWebKey.getPrivateKey()+"\nPublic Key RAW: " + rsaJsonWebKey.getPublicKey());

		//validateToken(jwt, Base64.encode(rsaJsonWebKey.getPrivateKey().getEncoded()), Base64.encode(rsaJsonWebKey.getPublicKey().getEncoded()));
//		System.out.println("isGenerated and saved: " + saveToDB(rsaJsonWebKey));
		try {
			validateServiceToken(jwt);
		} catch (MalformedClaimException | JoseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jwt;
	}
	
	
	public boolean validateServiceToken(String jwt) throws MalformedClaimException, JoseException {
		try
		{
			JWK jwk = getJWKFromDB();

			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
					.setAllowedClockSkewInSeconds(2000000000) // allow some leeway in validating time based claims to account for clock skew
					.setRequireSubject() // the JWT must have a subject claim
					.setExpectedIssuer(jwk.getIssuer()) // whom the JWT needs to have been issued by
					.setExpectedAudience(jwk.getAudience()) // to whom the JWT is intended for
					.setVerificationKey(jwk.getPublic_key()) // verify the signature with the public key
					.setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
							ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256) // which is only RS256 here
					.build(); // create the JwtConsumer instance


			//  Validate the JWT and process it to the Claims
			JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
			System.out.println("JWT validation succeeded! " + jwtClaims);
			return true;
		}
		catch (InvalidJwtException ex)
		{
			// InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
			// Hopefully with meaningful explanations(s) about what went wrong.
			System.out.println("Invalid JWT! " + ex);

			// Programmatic access to (some) specific reasons for JWT invalidity is also possible
			// should you want different error handling behavior for certain conditions.

			// Whether or not the JWT has expired being one common reason for invalidity
			if (ex.hasExpired())
			{
				System.out.println("JWT expired at " + ex.getJwtContext().getJwtClaims().getExpirationTime());
			}

			// Or maybe the audience was invalid
			if (ex.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
			{
				System.out.println("JWT had wrong audience: " + ex.getJwtContext().getJwtClaims().getAudience());
			}

			return false;
		}
		catch (Exception ex) {
			System.out.println("Invalid JWT?? " + ex);
			return false;
		}
	}
}
