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
package org.cishell.framework.preference;

public interface PreferenceProperty {

    //used to override the default id of the local preference OCD
	public static final String LOCAL_PREF_PID = "local_pref_pid";
	//used to override the default ids of the global preference OCDs
	public static final String GLOBAL_PREF_PID = "global_pref_pid";
	
//	//set to true in a service's properties if you want the CIShell preference service to look for local preferences in your METADATA.XML file
//	//In addition to the described behavior, setting this to true will also cause the same behavior as setting 'receive prefs' to true.
//	public static final String PUBLISH_LOCAL_PREFS_KEY = "publish_local_prefs";
//	//set to true in a service's properties if you want the CIShell preference service to look for global preferences in your METADATA.XML file
//	public static final String PUBLISH_GLOBAL_PREF_KEY = "publish_global_prefs";
//	//set to true in a service's properties if you want to are not publishing local preferences, but still want to receive global preference info 
//	public static final String RECEIVE_PREFS_KEY = "receive_prefs";
//	//set to true if you want the default values of your parameters to be changeable in the preferences menu
//	public static final String PUBLISH_PARAM_DEFAULT_PREFS_KEY = "publish_param_prefs";
	
	public static final String PREFS_PUBLISHED_KEY = "prefs_published";
	public static final String PUBLISH_LOCAL_PREF_VALUE = "local";
	public static final String PUBLISH_GLOBAL_PREF_VALUE = "global";
	public static final String PUBLISH_PARAM_DEFAULT_PREF_VALUE = "param-defaults";
	
	public static final String RECEIVE_PREFS_KEY = "receive_prefs";
	
	//by default, OCDs with the ID `service.pid` + `one of these suffixes` will be treated as the OCD for the OCD type that corresponds to the suffix.
	
	public static final String LOCAL_PREFS_OCD_SUFFIX = ".prefs.local"; //with or without digit (>=2) on end
	public static final String GLOBAL_PREFS_OCD_SUFFIX = ".prefs.global"; //with or without digit ( >= 2) on end
	public static final String PARAM_PREFS_OCD_SUFFIX = "";
	
	public static final String LOCAL_PREFS_CONF_SUFFIX = "";
	public static final String GLOBAL_PREFS_CONF_SUFFIX = GLOBAL_PREFS_OCD_SUFFIX;
	public static final String PARAM_PREFS_CONF_SUFFIX = ".prefs.params";
	
	//used to remember what bundle version a preference configuration object corresponds to
	//the same as OSGi one.
	public static final String BUNDLE_VERSION_KEY = "Bundle-Version";
}
