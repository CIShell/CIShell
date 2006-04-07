/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 25, 2005 at Indiana University.
 */
package edu.iu.iv.preferences;

import java.io.File;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.configuration.AbstractConfigurationPage;

/**
 * 
 * @author Bruce Herr
 */
public class FileSystemPreferences extends AbstractConfigurationPage {

    /**
     * 
     */
    public FileSystemPreferences() {    
        parameterMap.putDirectoryOption(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE,
                "Temporary Files Folder", "",
                new File(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE_DEFAULT), null);
        
        parameterMap.putDirectoryOption(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE,
                "Default Data Folder", "",
                new File(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE_DEFAULT), null);
        
	    Configuration cfg = IVC.getInstance().getConfiguration();
	    
	    parameterMap.get(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE)
	    .setValue(new File(cfg.getString(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE)));
	    
	    parameterMap.get(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE)
    	.setValue(new File(cfg.getString(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE)));
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean save() {
	    Configuration cfg = IVC.getInstance().getConfiguration();
	    
	    cfg.setValue(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE, parameterMap.getTextValue(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE));
	    cfg.setValue(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE, parameterMap.getTextValue(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE));
        
        return true;
    }

}
