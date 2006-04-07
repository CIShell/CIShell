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
public class GeneralIVCPreferences extends AbstractConfigurationPage {

    /**
     * 
     */
    public GeneralIVCPreferences() {
        parameterMap.putBooleanOption(IVCPreferences.EXIT_WITHOUT_PROMPT, 
                "Exit IVC without prompt", "", 
                IVCPreferences.EXIT_WITHOUT_PROMPT_DEFAULT, null);
        
	    Configuration cfg = IVC.getInstance().getConfiguration();
	    
	    parameterMap.get(IVCPreferences.EXIT_WITHOUT_PROMPT)
	    .setValue(Boolean.valueOf(cfg.getBoolean(IVCPreferences.EXIT_WITHOUT_PROMPT)));
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean save() {
        Configuration cfg = IVC.getInstance().getConfiguration();
        	    	    
		cfg.setValue(IVCPreferences.EXIT_WITHOUT_PROMPT, parameterMap.getBooleanValue(IVCPreferences.EXIT_WITHOUT_PROMPT));
        
        return true;
    }
}
