/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 21, 2005 at Indiana University.
 */
package edu.iu.iv.preferences;

import java.io.File;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.common.parameter.Validator;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.configuration.AbstractConfigurationPage;
import edu.iu.iv.core.messaging.ConsoleLevel;
import edu.iu.iv.core.messaging.ConsoleManager;

/**
 * 
 * @author Bruce Herr
 */
public class ErrorsAndLoggingPreferences extends AbstractConfigurationPage {

    /**
     * 
     */
    public ErrorsAndLoggingPreferences() {        
        
        parameterMap.putBooleanOption(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE,
                "Show Critical Errors on Console", "",
                IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE_DEFAULT,
                new Validator() {
                	public boolean isValid(Object value) {
                	    if (value == Boolean.FALSE && 
                	            parameterMap.getBooleanValue(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE)) {
                	        parameterMap.get(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE).setValue(Boolean.TRUE);
                	    }
                	    
                	    return (value != null);
                	}
            	});
        
        parameterMap.putBooleanOption(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE,
                "Show All Errors on Console", "",
                IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE_DEFAULT,
                new Validator() {
                	public boolean isValid(Object value) {
                	    if (value == Boolean.TRUE) {
                	        parameterMap.get(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE)
                	        	.setValue(Boolean.TRUE);
                	    }
                	    
                	    return (value != null);
                	}
            	});

        parameterMap.putDirectoryOption(IVCPreferences.USER_LOG_PREFERENCE,
                "User Logfile Directory:", "",
                new File(IVCPreferences.USER_LOG_PREFERENCE_DEFAULT),
                new Validator() {
                	public boolean isValid(Object value) {
                	    return (value != null);
                	}
            	});
        
        parameterMap.putDirectoryOption(IVCPreferences.ERROR_LOG_PREFERENCE,
                "Error Logfile Directory:", "",
                new File(IVCPreferences.ERROR_LOG_PREFERENCE_DEFAULT),
                new Validator() {
                	public boolean isValid(Object value) {
                	    return (value != null);
                	}
            	});
        
        parameterMap.putIntOption(IVCPreferences.USER_LOG_SIZE_PREFERENCE,
                "User Logfile Size Limit (kb):", "",
                IVCPreferences.USER_LOG_SIZE_PREFERENCE_DEFAULT,
                new Validator() {
                	public boolean isValid(Object value) {
                	    return (value != null);
                	}
            	});
        
        parameterMap.putIntOption(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE,
                "Error Logfile Size Limit (kb):", "",
                IVCPreferences.ERROR_LOG_SIZE_PREFERENCE_DEFAULT,
                    new Validator() {
                    	public boolean isValid(Object value) {
                    	    return (value != null);
                    	}
                	});
        
        parameterMap.putIntOption(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES,
                "Maximum number of user log files to keep:", "",
                IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES_DEFAULT,
                new Validator() {
                	public boolean isValid(Object value) {
                	    return (value != null);
                	}
            	});
        
        parameterMap.putIntOption(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES,
                "Maximum number of error log files to keep:", "",
                IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES_DEFAULT,
                new Validator() {
                	public boolean isValid(Object value) {
                	    return (value != null);
                	}
            	});
        
	    Configuration cfg = IVC.getInstance().getConfiguration();
	    
	    parameterMap.get(IVCPreferences.USER_LOG_PREFERENCE)
	    .setValue(new File(cfg.getString(IVCPreferences.USER_LOG_PREFERENCE)));
	    
	    parameterMap.get(IVCPreferences.ERROR_LOG_PREFERENCE)
    	.setValue(new File(cfg.getString(IVCPreferences.ERROR_LOG_PREFERENCE)));
	    
	    parameterMap.get(IVCPreferences.USER_LOG_SIZE_PREFERENCE)
    	.setValue(new Integer(cfg.getInt(IVCPreferences.USER_LOG_SIZE_PREFERENCE)));
	    
	    parameterMap.get(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE)
		.setValue(new Integer(cfg.getInt(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE)));
	    
	    parameterMap.get(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE)
    	.setValue(Boolean.valueOf(cfg.getBoolean(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE)));
	    
	    parameterMap.get(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE)
		.setValue(Boolean.valueOf(cfg.getBoolean(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE)));	    
	    
	    parameterMap.get(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES)
		.setValue(new Integer(cfg.getInt(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES)));	    
	    
	    parameterMap.get(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES)
		.setValue(new Integer(cfg.getInt(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES)));
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean save() {
	    ConsoleManager console = IVC.getInstance().getConsole();
	    
	    boolean showAll = parameterMap.getBooleanValue(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE);
	    boolean showCritical = parameterMap.getBooleanValue(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE);
	    
	    if(showAll){
	        console.setMaximumLevel(ConsoleLevel.SYSTEM_ERROR);
	        ConsoleLevel.setShowOnlyCriticalErrors(false);
	    }
	    else if (showCritical){
	        console.setMaximumLevel(ConsoleLevel.SYSTEM_ERROR);
	        ConsoleLevel.setShowOnlyCriticalErrors(true);
	    }
	    else{
	        console.setMaximumLevel(ConsoleLevel.ALGORITHM_INFORMATION);
	        ConsoleLevel.setShowOnlyCriticalErrors(false);
	    }
	    
	    Configuration cfg = IVC.getInstance().getConfiguration();
	    
	    cfg.setValue(IVCPreferences.USER_LOG_PREFERENCE, parameterMap.getTextValue(IVCPreferences.USER_LOG_PREFERENCE));
		cfg.setValue(IVCPreferences.ERROR_LOG_PREFERENCE, parameterMap.getTextValue(IVCPreferences.ERROR_LOG_PREFERENCE));
		cfg.setValue(IVCPreferences.USER_LOG_SIZE_PREFERENCE, parameterMap.getIntValue(IVCPreferences.USER_LOG_SIZE_PREFERENCE));
		cfg.setValue(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE, parameterMap.getIntValue(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE));
		cfg.setValue(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE, showAll);
		cfg.setValue(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE, showCritical);
		cfg.setValue(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES, parameterMap.getIntValue(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES));
		cfg.setValue(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES, parameterMap.getIntValue(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES));
        
        return isValid();
    }
}
