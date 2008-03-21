package org.cishell.reference.prefs.admin.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.cishell.framework.preference.PreferenceProperty;
import org.cishell.reference.prefs.admin.PrefAdmin;
import org.cishell.reference.prefs.admin.PrefPage;
import org.cishell.reference.prefs.admin.PrefsByService;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeService;

public class PrefAdminImpl implements PrefAdmin, ConfigurationPlugin, ConfigurationListener {

	private LogService log;
	private MetaTypeService mts;
	private ConfigurationAdmin ca;
	
	private PrefInfoGrabber prefInfoGrabber;
	private PrefReferenceProcessor prefProcessor;
	
	private List prefReferencesToBeProcessed = new ArrayList();
	private List prefHolderReferences = new ArrayList();
	
	private boolean hasBeenActivated = false;

	//PrefAdmin interface 
	
	public PrefPage[] getLocalPrefPages() {
		return prefProcessor.getAllLocalPrefPages();
	}
	

	public PrefPage[] getGlobalPrefPages() {
		return prefProcessor.getAllGlobalPrefPages();
	}

	public PrefPage[] getParamPrefPages() {
		return prefProcessor.getAllParamPrefPages();
	}

	public PrefsByService[] getPrefsByService() {
		return prefProcessor.getAllPrefsByService();
	}
	
	//Service Component Interface 
	
    protected void activate(ComponentContext ctxt) {
    	
    	this.log = (LogService) ctxt.locateService("LOG");
    	this.mts = (MetaTypeService) ctxt.locateService("MTS");
    	this.ca = (ConfigurationAdmin) ctxt.locateService("CS");
    	
    	this.prefInfoGrabber = new PrefInfoGrabber(log, mts, ca);
    	this.prefProcessor = new PrefReferenceProcessor(log, prefInfoGrabber);
    	
    	this.hasBeenActivated = true;
    	
    	//takes care of any prefHolders that may have been registered
    	//before the rest of these services were registered.
    	this.prefProcessor.processPrefReferences(
    			(ServiceReference[]) prefReferencesToBeProcessed.toArray(new ServiceReference[0]));
    	this.prefReferencesToBeProcessed.clear();
    }
    
    protected void deactivate(ComponentContext ctxt) {
    }
    
    /**
     * This method is called whenever a Service which potentially has preference 
     * information is registered. 
     * 
     * @param prefHolder The service reference for the service with preference information
     */
    protected void prefHolderRegistered(ServiceReference prefHolder) {
    	this.prefReferencesToBeProcessed.add(prefHolder);	
    	//(we must wait until this service is activated before we can properly process the preference holders)
    	if (this.hasBeenActivated == true) {
    		this.prefProcessor.processPrefReferences(
    				(ServiceReference[]) this.prefReferencesToBeProcessed.toArray(new ServiceReference[0]));
    		this.prefReferencesToBeProcessed.clear();
    	}
    
    	this.prefHolderReferences.add(prefHolder);
    }
    
    protected void prefHolderUnregistered(ServiceReference prefHolder) {
    	this.prefHolderReferences.remove(prefHolder);
    }

    // ConfigurationPlugin interface
    
    //injects global preferences into local preferences as they go from configuration service to the managed service
	public void modifyConfiguration(ServiceReference reference,
			Dictionary properties) {
		PrefPage[] globalPrefPages = getGlobalPrefPages();
		for (int ii = 0; ii < globalPrefPages.length; ii++) {
			PrefPage globalPrefPage = globalPrefPages[ii];
			Configuration globalPrefConf = globalPrefPage.getPrefConf();
			
			String namespace = globalPrefConf.getPid();
			
			Dictionary globalPrefDict = globalPrefConf.getProperties();
			
			//the keys of each dictionary are the ids of global preference OCDs.
			Enumeration ids = globalPrefDict.keys();
			while (ids.hasMoreElements()) {
				String id = (String) ids.nextElement();
				String value = (String) globalPrefDict.get(id);
			
				String keyForConfiguration = namespace + "." + id;
				
				properties.put(keyForConfiguration, value);
			}
		}
	}

	//ConfigurationListener interface
	
	public void configurationEvent(ConfigurationEvent event) {
		if (event.getType() == ConfigurationEvent.CM_UPDATED) {
			if (isFromGlobalConf(event.getPid())) {
				sendGlobalPreferences();
			} else {
			}
		} else if (event.getType() == ConfigurationEvent.CM_DELETED) {
		}
	}
	
	/**
	 * Call when a global preference object is created or updated.
	 * Necessary because changes to global preference do not
	 * cause an update event for every ManagedService.
	 */
	private void sendGlobalPreferences() {
		try {
		for (int ii = 0; ii < this.prefHolderReferences.size(); ii++) {
			ServiceReference prefHolder = (ServiceReference) this.prefHolderReferences.get(ii);
			if (prefHolder.getProperty(PreferenceProperty.RECEIVE_PREFS_KEY) != null &&
				prefHolder.getProperty(PreferenceProperty.RECEIVE_PREFS_KEY).equals("true")) {
			Configuration localPrefConf = ca.getConfiguration((String) prefHolder.getProperty(Constants.SERVICE_PID));
			try {
			localPrefConf.update();
			} catch (IOException e) {
				this.log.log(LogService.LOG_ERROR, "Unable to update configuration for " + localPrefConf.getPid(), e);
			}
		}
		}
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, "Unable to obtain all configuration objects", e);
		}
	}
	
	private boolean isFromGlobalConf(String pid) {
		return (pid.substring(0, pid.length() - 1).endsWith(PreferenceProperty.GLOBAL_PREFS_CONF_SUFFIX)
				|| pid.endsWith(PreferenceProperty.GLOBAL_PREFS_CONF_SUFFIX));
	}

	
}