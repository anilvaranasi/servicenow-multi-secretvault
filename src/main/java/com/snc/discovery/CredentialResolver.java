package com.snc.discovery;
import java.util.*;
import org.json.*;
/**
 * Basic implementation of a CredentialResolver that uses a properties file.
 */

public class CredentialResolver {
	// These are the permissible names of arguments passed INTO the resolve()
	// method.

	// the string identifier as configured on the ServiceNow instance...
	public static final String ARG_ID = "id";

	// a dotted-form string IPv4 address (like "10.22.231.12") of the target
	// system...
	public static final String ARG_IP = "ip";

	// the string type (ssh, snmp, etc.) of credential as configured on the
	// instance...
	public static final String ARG_TYPE = "type";

	// the string MID server making the request, as configured on the
	// instance...
	public static final String ARG_MID = "mid";

	// These are the permissible names of values returned FROM the resolve()
	// method.

	// the string user name for the credential, if needed...
	public static final String VAL_USER = "user";

	// the string password for the credential, if needed...
	public static final String VAL_PSWD = "pswd";

	// the string pass phrase for the credential if needed:
	public static final String VAL_PASSPHRASE = "passphrase";

	// the string private key for the credential, if needed...
	public static final String VAL_PKEY = "pkey";

	// the string authentication protocol for the credential, if needed...
	public static final String VAL_AUTHPROTO = "authprotocol";

	// the string authentication key for the credential, if needed...
	public static final String VAL_AUTHKEY = "authkey";

	// the string privacy protocol for the credential, if needed...
	public static final String VAL_PRIVPROTO = "privprotocol";

	// the string privacy key for the credential, if needed...
	public static final String VAL_PRIVKEY = "privkey";

	public CredentialResolver() {
	}


	/**
	 * Resolve a credential.
	 */
	public Map resolve(Map args) {
		Map result = new HashMap();
		
		AWSCredentialResolver AwsObject = new AWSCredentialResolver();
		String id = (String) args.get(ARG_ID);
		String type = (String) args.get(ARG_TYPE);
		System.out.println("InsideResolve " + id +  " "+ type);
		String awsuser = "";
		String awspswd = "";
		if(id.startsWith("aws")) {
			System.out.println("InsideResolve " + id +  " "+ type);
			String awsPrefix = "aws" + ".";
			String secretName = id+"."+type;
			JSONObject AwsSecret = AwsObject.returnCredentials(secretName);
			System.out.println("secretName=" + secretName);
			awspswd = AwsSecret.getString(secretName);
			System.out.println("secretName=" + secretName + " awspswd=" + awspswd);
			secretName = secretName.replace(awsPrefix, "");
			awsuser = secretName.replace("." + type , "");
			System.out.println("secretName=" + secretName  + " " + "awsuser=" + awsuser);
			//awsuser = secretName.replace(keyPrefix, ""); // replacing keyPrefix in the secret name to get the actual user name.
			if(id.equalsIgnoreCase("misbehave"))
				throw new RuntimeException("I've been a baaaaaaaaad CredentialResolver!");
			// the resolved credential is returned in a HashMap...
			result.put(VAL_USER, awsuser);
			result.put(VAL_PSWD, awspswd);
			return result;
		}
		if(id.startsWith("mydev")) {
			FileCredentialResolver FileObject = new FileCredentialResolver();
		result = FileObject.resolve(args);	
		return result;
		}
		return result;
	}


	/**
	 * Return the API version supported by this class.
	 */
	public String getVersion() {
		return "1.0";
	}

	public static void main(String[] args) {
		CredentialResolver obj = new CredentialResolver();
		Map inputs = new HashMap();
		Map result = new HashMap();
		inputs.put(ARG_ID, "aws.adminanil");
		inputs.put(ARG_TYPE, "basic_auth");
		System.out.println(inputs.get(ARG_ID) + " " + inputs.get(ARG_TYPE));
		result =obj.resolve(inputs);
		System.out.println(result.get(VAL_USER) + " " + result.get(VAL_PSWD));
		inputs.put(ARG_ID, "mydev");
		inputs.put(ARG_TYPE, "basic_auth");
		System.out.println(inputs.get(ARG_ID) + " " + inputs.get(ARG_TYPE));
		result =obj.resolve(inputs);
		System.out.println(result.get(VAL_USER) + " " + result.get(VAL_PSWD));
	}
}