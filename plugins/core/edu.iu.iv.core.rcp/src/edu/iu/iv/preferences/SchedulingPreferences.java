/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 25, 2005 at Indiana University.
 */
package edu.iu.iv.preferences;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.configuration.AbstractConfigurationPage;

/**
 * 
 * @author Bruce Herr
 */
public class SchedulingPreferences extends AbstractConfigurationPage {

    /**
     * 
     */
    public SchedulingPreferences() {
        parameterMap.putIntOption(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE, 
                "Maximum Simultaneous Algorithms (requires restart):", "",
                IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE_DEFAULT, null);               

        Configuration cfg = IVC.getInstance().getConfiguration();
	    
	    parameterMap.get(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE)
	    .setValue(new Integer(cfg.getInt(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE)));
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean save() {
        Configuration cfg = IVC.getInstance().getConfiguration();
	    
        cfg.setValue(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE, 
                parameterMap.getIntValue(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE));
        
        
        return true;
    }

}
