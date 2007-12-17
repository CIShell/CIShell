/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 13, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.server.service.prefs;

import java.util.Vector;

import org.cishell.reference.remoting.event.AbstractEventConsumerProducer;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.util.tracker.ServiceTracker;

//TODO: Finish making remote prefrences class..
public class RemotePreferencesService extends AbstractEventConsumerProducer {
    ServiceTracker localPrefs;
    String[] users;
    
    public RemotePreferencesService(BundleContext bContext, String sessionID) {
        super(bContext, sessionID, "PreferencesService", true);
        
        localPrefs = new ServiceTracker(bContext, PreferencesService.class.getName(), null);
    }

    /**
     * @see org.osgi.service.prefs.PreferencesService#getSystemPreferences()
     */
    public Preferences getSystemPreferences() {
        PreferencesService prefs = (PreferencesService) localPrefs.getService();

        return prefs.getSystemPreferences();
    }

    /**
     * @see org.osgi.service.prefs.PreferencesService#getUserPreferences(java.lang.String)
     */
    public Preferences getUserPreferences(String name) {
        PreferencesService prefs = (PreferencesService) localPrefs.getService();

        return prefs.getUserPreferences(name);
    }

    /**
     * @see org.osgi.service.prefs.PreferencesService#getUsers()
     */
    public String[] getUsers() {
        if (users == null) {
            Event event = createEvent("getUsers", null);
            Event response = postEventAndWait(event);
            
            Vector inUsers = (Vector) response.getProperty("users");
            
            if (inUsers != null) {
                users = (String[]) inUsers.toArray(new String[0]);
            }
        }
        
        return users;
    }
}
