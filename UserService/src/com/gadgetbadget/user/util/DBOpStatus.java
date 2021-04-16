package com.gadgetbadget.user.util;

/**
 * This ENUM class defines all the valid types of a response in case a database operation
 * or an end-point call is not replying with a collection of data or when a status message is required.
 * Status types defined in this class are utilized by almost in every class and specially in JsonResponseBuilder
 * class while producing JSON objects as responses.
 * 
 * @author Ishara_Dissanayake
 */
public enum DBOpStatus {
	UNSUCCESSFUL,
	SUCCESSFUL,
	EXCEPTION,
	ERROR,
	UNAUTHORIZED,
	UNKNOWN,
	AUTHENTICATED,
	PROHIBITED
}
