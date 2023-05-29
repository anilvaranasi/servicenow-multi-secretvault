// Content of CredentialResolver.java
package com.snc.discovery;

import java.util.*;
import java.io.*;


/**
 * Basic implementation of a CredentialResolver that uses a properties file.
 * This is invoked from CredentialResolver
 */

public class FileCredentialResolver {
	private static String ENV_VAR = "CREDENTIAL_RESOLVER_FILE";
	//private static String DEFAULT_PROP_FILE_PATH = "C:\\dummycredentials.properties";
    private static String DEFAULT_PROP_FILE_PATH = "C:/ServiceNow/API/Java/dummycredentials.properties";
	//private static String DEFAULT_PROP_FILE_PATH = "./dummycredentials.properties";
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
	private Properties fProps;
	private void loadProps() {
		if(fProps == null)
			fProps = new Properties();
		try {
			String propFilePath = System.getenv(ENV_VAR);
			if(propFilePath == null) {
				System.err.println("Environment var "+ENV_VAR+" not found. Using default file: "+DEFAULT_PROP_FILE_PATH);
				propFilePath = DEFAULT_PROP_FILE_PATH;
			}

			File propFile = new File(propFilePath);
			if(!propFile.exists() || !propFile.canRead()) {
				System.err.println("Can't open "+propFile.getAbsolutePath());
			}
			else {
				InputStream propsIn = new FileInputStream(propFile);
				fProps.load(propsIn);
			}
			//fProps.load(CredentialResolver.class.getClassLoader().getResourceAsStream("dummycredentials.properties"));
		} catch (IOException e) {
			System.err.println("Problem loading credentials file:");
			e.printStackTrace();
		}
	}

	/**
	 * Resolve a credential.
	 */
	public Map resolve(Map args) {
		loadProps();
		String id = (String) args.get(ARG_ID);
		String type = (String) args.get(ARG_TYPE);
		String keyPrefix = id+"."+type+".";

		if(id.equalsIgnoreCase("misbehave"))
			throw new RuntimeException("I've been a baaaaaaaaad CredentialResolver!");

		// the resolved credential is returned in a HashMap...
		Map result = new HashMap();
		result.put(VAL_USER, fProps.get(keyPrefix + VAL_USER));
		result.put(VAL_PSWD, fProps.get(keyPrefix + VAL_PSWD));
		result.put(VAL_PKEY, fProps.get(keyPrefix + VAL_PKEY));
		result.put(VAL_PASSPHRASE, fProps.get(keyPrefix + VAL_PASSPHRASE));
		result.put(VAL_AUTHPROTO, fProps.get(keyPrefix + VAL_AUTHPROTO));
		result.put(VAL_AUTHKEY, fProps.get(keyPrefix + VAL_AUTHKEY));
		result.put(VAL_PRIVPROTO, fProps.get(keyPrefix + VAL_PRIVPROTO));
		result.put(VAL_PRIVKEY, fProps.get(keyPrefix + VAL_PRIVKEY));

		System.err.println("Error while resolving credential id/type["+id+"/"+type+"]");

		return result;
	}


	/**
	 * Return the API version supported by this class.
	 */
	public String getVersion() {
		return "1.0";
	}

	public static void main(String[] args) {
		FileCredentialResolver obj = new FileCredentialResolver();
		obj.loadProps();

		System.err.println("I spy the following credentials: ");
		for(Object key: obj.fProps.keySet()) {
			System.err.println(key+": "+obj.fProps.get(key));
		}

	}
}