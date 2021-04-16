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
import java.sql.Timestamp;
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

/**
 * This class handles all the JWT related operations including JWT generation, JWT validation, JWT PAYLOAD decoding
 * Generating new RSA Key Pairs, etc. Extends the DBHandler class as there are few methods require local Database
 * Access whenever generating RSA Key pairs or validating JWTs which requires retrieving locally stored JWK values 
 * including Public Key.
 * 
 * @author Ishara_Dissanayake
 */
public class JWTHandler extends DBHandler {
	private static String JWT_SUBJECT = "gadgetbadget.auth."; //subject prefix of the JWT

	/**
	 * This method generates expiring enabled JWTs for user authentication purposes.
	 * 
	 * @param username 			user-name of the logged in(validated) user
	 * @param user_id			corresponding unique user_id
	 * @param role				role id of the validated user
	 * @return					a valid JWT with expiration enabled for 30 minutes
	 * @throws JoseException	Exception is thrown by issues that might occur while generating the JWT by the jose4j package
	 * @throws SQLException		Exception is thrown by issues that might occur while accessing the local SQL database 
	 */
	public String generateToken(String username, String user_id, String role) throws JoseException, SQLException {
		String jwt = null;

		JWT_SUBJECT += username; 			// set a unique subject based on user-name per user
		JWK jwk = getJWKFromDB("JWK1");		// get the JSON Web Key attribute values stored in the local SQL database

		// Generate the JWT Header Claims
		JwtClaims claims = new JwtClaims();
		claims.setIssuer(jwk.getIssuer()); 
		claims.setAudience(jwk.getAudience());
		claims.setExpirationTimeMinutesInTheFuture(jwk.getLifetime());
		claims.setGeneratedJwtId();
		claims.setIssuedAtToNow();
		claims.setNotBeforeMinutesInThePast(2);
		claims.setSubject(JWT_SUBJECT);

		// Generate the JWT PAYLOAD
		claims.setClaim("username",username);
		claims.setClaim("user_id",user_id);
		claims.setClaim("role",role);

		// Create the JsonWebSignature based on Claims and PAYLOAD
		JsonWebSignature jws = new JsonWebSignature();

		// The PAYLOAD of the JWS is JSON content of the JWT Claims
		jws.setPayload(claims.toJson());
		jws.setKey(jwk.getPrivate_key());
		jws.setKeyIdHeaderValue(jwk.getKey_id());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// Sign the JWS and produce the compact serialization or the complete JWT/JWS
		// representation, which is a string consisting of three dot ('.') separated
		// base64url-encoded parts in the form Header.Payload.Signature
		jwt = jws.getCompactSerialization();

		return jwt;
	}

	/**
	 * This method validates JWT Token for User Authentication
	 * 
	 * @param jwt						JWT in the form of a String
	 * @return							Validity of the token as a boolean value
	 * @throws MalformedClaimException	Exception is thrown when there are issues with validation JWT claims
	 * @throws JoseException			Exception is thrown when there are format/encoding issues in the JWT 			
	 */
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

	/**
	 * Generates a new RSA Public Key Private Key pair and Stores it in the local database under the given key id
	 * @param key_id	a unique identifier given for each RSA key pair generated
	 */
	public void generateRSAKeyPair(String key_id) {
		RsaJsonWebKey rsaJsonWebKey;
		try {
			rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
			rsaJsonWebKey.setKeyId(key_id);

			System.out.println("RSA Keys were Generated and Saved: " + (insertJWKToDB(rsaJsonWebKey, key_id)?"Successfully.":"Failed."));
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * This method saves RSA Keys to Local database for future JWT validations
	 * which were generated using these RSA keys. Since RJK is already saved in
	 * the database, an UPDATE query is written instead of an INSERT.
	 * 
	 * @param rsa	RSA JSON KEY with Public and Private Key components
	 * @return		returns a boolean value based on the database operation status
	 */
	public boolean updateJWKInDB(RsaJsonWebKey rsa, String key_id) {
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
			preparedStmt.setString(3, key_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				res = true;
			} else {
				res = false;
			}
		} catch (Exception ex) {
			System.out.println("Exception occured while updating the RSA Keys. Exception Details: " + ex.getMessage());
		}
		return res;
	}

	/**
	 * This method inserts RSA Keys to Local database for future JWT Generating.
	 * Highly useful when a new pair of Keys needed in a special case such as a
	 * security breach or an emergency.
	 * 
	 * @param rsa	RSA JSON KEY with Public and Private Key components
	 * @return		returns a boolean value based on the database operation status
	 */
	public boolean insertJWKToDB(RsaJsonWebKey rsa, String key_id) {
		boolean res = false;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				res = false;
			}

			String query = "INSERT INTO `jwt_config` VALUES(?,?,?,?,?,?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, key_id);
			preparedStmt.setBytes(2, rsa.getPublicKey().getEncoded());
			preparedStmt.setBytes(3, rsa.getPrivateKey().getEncoded());
			preparedStmt.setString(4, "RSA_USING_SHA256");
			preparedStmt.setInt(5, 0);
			preparedStmt.setString(6, "gadgetbadget.user.security.JWTHandler");
			preparedStmt.setString(7, "gadgetbadget.webservices.serviceauth");
			preparedStmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));


			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				res = true;
			} else {
				res = false;
			}
		} catch (Exception ex) {
			System.out.println("Exception occured while inserting the RSA Keys. Exception Details: " + ex.getMessage());
		}
		return res;
	}

	/**
	 * This method retrieves RSA keys from local database preferably when there is a JWT to be validated.
	 * The two Keys Public and Private are retrieved as byte arrays and then converted into preferred types
	 * using java.security package.
	 * 
	 * @param key_id	unique RSA key id in local database
	 * @return			returns the JSON WEB KEY created including the public and private keys stored in the database
	 */
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

	/**
	 * This method decodes the JWT PAYLOAD of a given valid JWT while authenticating.
	 * Typically used by the Authentication Filter implemented to validate JWTs.
	 * 
	 * @param jwt	a valid JWT user/service authentication token
	 * @return		returns a JSON object with decoded PAYLOAD of the given JWT
	 */
	public JsonObject decodeJWTPayload(String jwt) {
		String[] jwtSplitted = jwt.split("\\.");
		String jwtDecoded = new String(Base64.decode(jwtSplitted[1]));
		JsonObject jwtPayload = new JsonParser().parse(jwtDecoded).getAsJsonObject();
		return jwtPayload;
	}

	/** 
	 * This method is used to generate JWT service authentication tokens for authenticating and authorization 
	 * purposes of web services while service-to-service communication establishment. JWTs generated for web
	 * service authentication are time insensitive, which means the time of the expiration of the JWT is not specified
	 * in the list of claims.
	 * 
	 * @param service_name		unique name of the web service which the JWT is to be generated
	 * @param service_id		unique service id given for the web service. usually given manually
	 * @param service_role		3 char prefix given for services which is uniquely mentioned in the ServiceTypes ENUM class
	 * @return					returns a valid non expiring JWT for unique service authentication
	 * @throws JoseException	JWT format/ JWT claim assigning issues
	 * @throws SQLException		SQL issues while retrieving values from the local storage
	 * @throws MalformedClaimException	JWT Verification Exceptions
	 */
	public String generateServiceToken(String service_name, String service_id, String service_role) throws JoseException, SQLException, MalformedClaimException {
		String jwt = null;

		JWT_SUBJECT += service_name;		// set a unique subject based on service-name per service
		JWK jwk = getJWKFromDB("JWK2");		// get the JSON Web Key attribute values stored in the local SQL database

		RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
		rsaJsonWebKey.setKeyId("JWK2");

		// Generate the JWT Header
		JwtClaims claims = new JwtClaims();
		claims.setIssuer(jwk.getIssuer()); 
		claims.setAudience(jwk.getAudience()); 
		claims.setGeneratedJwtId();
		claims.setIssuedAtToNow(); 
		claims.setNotBeforeMinutesInThePast(2); 
		claims.setSubject(JWT_SUBJECT);

		// Generate the JWT PAYLOAD
		claims.setClaim("username",service_name);
		claims.setClaim("user_id",service_id);
		claims.setClaim("role",service_role);

		// Create the JsonWebSignature based on Claims and PAYLOAD
		JsonWebSignature jws = new JsonWebSignature();

		// The PAYLOAD of the JWS is JSON content of the JWT Claims
		jws.setPayload(claims.toJson());
		jws.setKey(jwk.getPrivate_key());
		jws.setKeyIdHeaderValue(jwk.getKey_id());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		// Sign the JWS and produce the compact serialization or the complete JWT/JWS
		// representation, which is a string consisting of three dot ('.') separated
		// base64url-encoded parts in the form Header.Payload.Signature
		jwt = jws.getCompactSerialization();

		if(validateServiceToken(jwt)){
			return jwt;
		} else {
			return null;
		}
	}

	/**
	 * This method validates JWT Token for Service Authentication
	 * 
	 * @param jwt						JWT in the form of a String
	 * @return							Validity of the token as a boolean value
	 * @throws MalformedClaimException	Exception is thrown when there are issues with validation JWT claims
	 * @throws JoseException			Exception is thrown when there are format/encoding issues in the JWT 
	 */
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
}
