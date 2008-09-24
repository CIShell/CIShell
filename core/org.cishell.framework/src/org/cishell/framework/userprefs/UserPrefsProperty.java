/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Feb 8, 2008 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.framework.userprefs;

/**
 * A standard set of properties and values to be placed in a service's
 * metadata Dictionary when registering a service with the OSGi service registry 
 * for the purpose of publishing and receiving user-adjustable preferences.
 * 
 * See the <a href="http://cishell.org/dev/docs/spec/cishell-spec-1.0.pdf">
 * CIShell Specification 1.0</a> for information on publishing user-adjustable
 * preferences. 
 */
public interface UserPrefsProperty {

	/**
	 * The suffix to add to the service's PID for generating a local preferences
	 * PID when using the standard naming convention
	 */
	public static final String LOCAL_PREFS_OCD_SUFFIX = ".prefs.local";
	
	/**
	 * The suffix to add to the service's PID for generating a global preferences
	 * PID when using the standard naming convention
	 */
	public static final String GLOBAL_PREFS_OCD_SUFFIX = ".prefs.global";
	
	/**
	 * The suffix to add to the service's PID for an {@link Algorithm}'s 
	 * user-entered input parameters PID when using the standard naming convention
	 */
	public static final String PARAM_PREFS_OCD_SUFFIX = "";
	
	/** 
	 * The key for specifying a local preferences PID.
	 * Only use this when not following the standard naming convention. 
	 */
	public static final String LOCAL_PREFS_PID = "local_prefs_pid";
	
	/** 
	 * The key for specifying a global preferences PID.
	 * Only use this when not following the standard naming convention. 
	 */
	public static final String GLOBAL_PREFS_PID = "global_prefs_pid";



	/**
	 * The key for specifying what types of preferences are published
	 */
	public static final String PREFS_PUBLISHED_KEY = "prefs_published";
	
	/**
	 * The value for specifying that local preferences are to be published
	 */
	public static final String PUBLISH_LOCAL_PREFS_VALUE = "local";
	
	/**
	 * The value for specifying that global preferences are to be published
	 */
	public static final String PUBLISH_GLOBAL_PREFS_VALUE = "global";
	
	/**
	 * The value for specifying that an {@link Algorithm}'s user-entered input
	 * parameter defaults may be adjusted by the user
	 */
	public static final String PUBLISH_PARAM_DEFAULT_PREFS_VALUE = "param-defaults";
	
	/**
	 * The key for declaring a need to receive preferences. "true" and "false" 
	 * are the possible associated values.
	 */
	public static final String RECEIVE_PREFS_KEY = "receive_prefs";
		

	
	/**
	 * The suffix to add to the service's PID for getting the local preferences
	 * directly from the ConfigurationAdmin (not recommended)
	 */
	public static final String LOCAL_PREFS_CONF_SUFFIX = "";

	/**
	 * The suffix to add to the service's PID for getting the global preferences
	 * directly from the ConfigurationAdmin (not recommended)
	 */
	public static final String GLOBAL_PREFS_CONF_SUFFIX = GLOBAL_PREFS_OCD_SUFFIX;
	
	/**
	 * The suffix to add to the service's PID for getting an {@link Algorithm}'s
	 * user-entered input parameter defaults that have been user-adjusted directly
	 * from the ConfigurationAdmin (not recommended)
	 */
	public static final String PARAM_PREFS_CONF_SUFFIX = ".prefs.params";
	
	/**
	 * A key set in each configuration object which states the Bundle-Version of
	 * the service when it was last updated
	 */
	public static final String BUNDLE_VERSION_KEY = "Bundle-Version";
}
