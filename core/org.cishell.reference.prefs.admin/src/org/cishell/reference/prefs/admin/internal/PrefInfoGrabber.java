package org.cishell.reference.prefs.admin.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.userprefs.UserPrefsProperty;
import org.cishell.reference.prefs.admin.PreferenceOCD;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

//NOTE: methods that get configuration objects don't really need the ObjectClassDefinitions. We just use them (hackishly) to know how many configuration objects we should get 
//Since, there are possibly an infinite number of Configuration objects for a service, we need the ObjectClassDefinitions to tell us when to stop trying to grab configuration objects
//(If we try to grab one that didn't previously exist, it will just make an empty one, sometimes we want this behaviour and sometimes we don't).
public class PrefInfoGrabber implements UserPrefsProperty, AlgorithmProperty {
	
	private LogService log;
	private MetaTypeService mts;
	private ConfigurationAdmin ca;
	
	public PrefInfoGrabber(LogService log, MetaTypeService mts, ConfigurationAdmin ca) {
		this.log = log;
		this.mts = mts;
		this.ca = ca;
	}
	
	public PreferenceOCD[] getLocalPrefOCDs(ServiceReference prefHolder) {
		String localPrefOCDID = getLocalPrefOCDID(prefHolder);
		return extractOCDs(prefHolder, localPrefOCDID);
	}
	
	public PreferenceOCD[] getGlobalPrefOCDs(ServiceReference prefHolder) {
		String globalPrefOCDID = getGlobalPrefOCDID(prefHolder);
		return extractOCDs(prefHolder, globalPrefOCDID);
	}
	
	public PreferenceOCD[] getParamPrefOCDs(ServiceReference prefHolder) {
		String paramPrefOCDID = getParamPrefOCDID(prefHolder);
		return extractParamOCDs(prefHolder, paramPrefOCDID);
	}
	
	private String getLocalPrefOCDID(ServiceReference prefHolder) {
		String localPrefOCDID = (String) prefHolder.getProperty(UserPrefsProperty.LOCAL_PREFS_PID);
		if (localPrefOCDID != null) {
			return localPrefOCDID;
		} else {
			//no name defined. Use default name.
			String defaultLocalPrefOCDID = (String) prefHolder.getProperty(Constants.SERVICE_PID)
				+ UserPrefsProperty.LOCAL_PREFS_OCD_SUFFIX;
			return defaultLocalPrefOCDID;
		} 
	}
	
	private String getGlobalPrefOCDID(ServiceReference prefHolder) {
		String globalPrefOCDID = (String) prefHolder.getProperty(UserPrefsProperty.GLOBAL_PREFS_PID);
		if (globalPrefOCDID != null) {
		return globalPrefOCDID;
		} else {
			//no names defined. Use default names.
			String defaultGlobalPrefOCDID =  (String) prefHolder.getProperty(Constants.SERVICE_PID) 
				+ UserPrefsProperty.GLOBAL_PREFS_OCD_SUFFIX;
			return defaultGlobalPrefOCDID;
		}
	}
	
	private String getParamPrefOCDID(ServiceReference prefHolder) {
		String paramPrefOCDID = (String) prefHolder.getProperty(AlgorithmProperty.PARAMETERS_PID);
		if ( paramPrefOCDID != null) {
		return  paramPrefOCDID;
		} else {
			//no names defined. Use default names.
			String defaultParamPrefOCDID =  (String) prefHolder.getProperty(Constants.SERVICE_PID) + UserPrefsProperty.PARAM_PREFS_OCD_SUFFIX;
			return defaultParamPrefOCDID;
		}
	}
	
	private PreferenceOCD[] extractOCDs(ServiceReference prefHolder, String ocdID) {
		Bundle bundle = prefHolder.getBundle();
		MetaTypeInformation bundleMetaTypeInfo = mts.getMetaTypeInformation(bundle);
		List extractedOCDList = new ArrayList();
		//go through ocdID, ocdID + "2", ocdID + "3", etc..., until we try to get an OCD that doesn't exist.
		PreferenceOCD extractedOCD = extractOCD(bundleMetaTypeInfo, ocdID);
		int ii = 2;
		while (extractedOCD != null) {
			extractedOCDList.add(extractedOCD);
			extractedOCD = extractOCD(bundleMetaTypeInfo, ocdID + ii);
			ii++;
		}
		return (PreferenceOCD[]) extractedOCDList.toArray(new PreferenceOCD[extractedOCDList.size()]);
	}
	
	//returns null if specified OCD does not exist.
	private PreferenceOCD extractOCD(MetaTypeInformation bundleMetaTypeInfo, String ocdID) {
		try {
			ObjectClassDefinition requestedOCD = bundleMetaTypeInfo.getObjectClassDefinition(ocdID, null);
			if (requestedOCD == null) return null;
			PreferenceOCD wrappedOCD = new PreferenceOCDImpl(this.log, requestedOCD);
			return wrappedOCD;
		} catch (IllegalArgumentException e) {
			//requested OCD does not exist
			return null;
		}
	}
	
	//returning multiple OCDs just for the sake of symmetry, actually can only return 1 (or 0 on error)
	private PreferenceOCD[] extractParamOCDs(ServiceReference prefHolder, String ocdID) {
		Bundle bundle = prefHolder.getBundle();
		MetaTypeInformation bundleMetaTypeInfo = mts.getMetaTypeInformation(bundle);
		PreferenceOCD paramOCD = extractOCD(bundleMetaTypeInfo, ocdID);
		if (paramOCD != null) {
			return new PreferenceOCD[]{paramOCD};
		} else {
			return new PreferenceOCD[0];
		}
	}

	public Configuration[] getLocalPrefConfs(ServiceReference prefHolder, PreferenceOCD[] prefOCDs) {
		String localPrefConfID = getLocalPrefConfID(prefHolder);
		return extractConfs(localPrefConfID, prefOCDs);
	}

	public Configuration[] getGlobalPrefConfs(ServiceReference prefHolder, PreferenceOCD[] prefOCDs) {
		String globalPrefConfID = getGlobalPrefConfID(prefHolder);
		return extractConfs(globalPrefConfID, prefOCDs);
	}

	public Configuration[] getParamPrefConfs(ServiceReference prefHolder, PreferenceOCD[] prefOCDs) {
		String paramPrefConfID = getParamPrefConfID(prefHolder);
		return extractConfs(paramPrefConfID, prefOCDs);
	}
	
	public void ensurePrefsCanBeSentTo(ServiceReference serviceRef) {
		try {
			Configuration conf = ca.getConfiguration((String) serviceRef.getProperty(Constants.SERVICE_PID), null);

			Dictionary properties = conf.getProperties();
		if (properties == null) {
			conf.update(new Hashtable());
		}
		} catch (IOException e) {
			return;
		}
	}
	

	/**
	 * Gets all the configuration objects that correspond to the preference OCDs provided.
	 * @param confPID the PID that all the configuration objects start with, (confPID, confPID + "2", etc...)
	 * @param prefOCDs the OCDs associated with the configuration objects we are trying to get (puts a limit on # of Conf objects we try to extract)
	 * @return All the Configuration objects associated with the OCDs.
	 */
	private Configuration[] extractConfs(String confPID, PreferenceOCD[] prefOCDs) {
		if (prefOCDs.length == 0) {
			return new Configuration[0];
		}
		List extractedConfList = new ArrayList();
		Configuration prefConf = extractConf(confPID);
		if (prefConf != null) {
			extractedConfList.add(prefConf);
			for (int ii =2; ii < prefOCDs.length + 1; ii++) {
				prefConf = extractConf(confPID + ii);
				if (prefConf != null) {
					extractedConfList.add(prefConf);
				} else {
					break;
				}
			}
		} 
		
		return (Configuration[]) extractedConfList.toArray(new Configuration[extractedConfList.size()]);
	}
	
	//will return null on exception
	private Configuration extractConf(String confPID) {
		try {
			return ca.getConfiguration(confPID, null);
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, "Unable to load configuration for " + confPID + " due to IO Error");
			return null;
		}
	}
	
	
	private String getLocalPrefConfID(ServiceReference prefHolder) {
		return (String) prefHolder.getProperty(Constants.SERVICE_PID); //has to be service.pid for configuration service to deliver updates to the right service 
	}
	
	private String getGlobalPrefConfID(ServiceReference prefHolder) {
		String globalPrefOCDID = (String) prefHolder.getProperty(UserPrefsProperty.GLOBAL_PREFS_PID);
		if (globalPrefOCDID != null) {
		return globalPrefOCDID;
		} else {
			//no names defined. Use default names.
			String defaultGlobalPrefOCDID =  (String) prefHolder.getProperty(Constants.SERVICE_PID) + UserPrefsProperty.GLOBAL_PREFS_CONF_SUFFIX;
			return defaultGlobalPrefOCDID;
		}
	}
	
	private String getParamPrefConfID(ServiceReference prefHolder) {
		String paramPrefOCDID = (String) prefHolder.getProperty(AlgorithmProperty.PARAMETERS_PID);
		if ( paramPrefOCDID != null) {
		return  paramPrefOCDID;
		} else {
			//no names defined. Use default names.
			String defaultGlobalPrefOCDID =  (String) prefHolder.getProperty(Constants.SERVICE_PID) + UserPrefsProperty.PARAM_PREFS_CONF_SUFFIX;
			return defaultGlobalPrefOCDID;
		}
	}
	

}
