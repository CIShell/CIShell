/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 9, 2004 at Indiana University.
 */
package edu.iu.iv.internal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.Logger;

/**
 * 
 * @author Team IVC 
 */
//Created by: Bruce Herr
public class IVCLogManager {
	private BasicLogger errorLogger;
	private BasicLogger userLogger;
    private static final String ERROR_LOG_FORMAT = File.separator + "error.log";
    private static final String USER_LOG_FORMAT = File.separator + "user.log";
	
	public IVCLogManager() {
	    configureLogging();
	}
	
	private void configureLogging() {
	    String errorLogFile = null;
	    String userLogFile = null;
	    int errorLogMaxSize = 0;
	    int userLogMaxSize = 0;
	    
	    Configuration cfg = IVC.getInstance().getConfiguration();
	    errorLogFile = cfg.getString(IVCPreferences.ERROR_LOG_PREFERENCE);
	    userLogFile = cfg.getString(IVCPreferences.USER_LOG_PREFERENCE);
	    errorLogMaxSize = cfg.getInt(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE);
	    userLogMaxSize = cfg.getInt(IVCPreferences.USER_LOG_SIZE_PREFERENCE);
	    
	    if(errorLogMaxSize <= 0) errorLogMaxSize = 100;
	    if(userLogMaxSize <= 0) userLogMaxSize = 100;
						
		//use default if pref isnt set
		if (errorLogFile == null || errorLogFile.equals("")) {
		    errorLogFile = IVCPreferences.ERROR_LOG_PREFERENCE_DEFAULT;			   
		}
        //add the error.log part used for formatting
        errorLogFile += ERROR_LOG_FORMAT;
        errorLogger = createLogger(errorLogFile, errorLogMaxSize);
        
        //use default if pref isnt set
        if (userLogFile == null || userLogFile.equals("")) {
            userLogFile = IVCPreferences.USER_LOG_PREFERENCE_DEFAULT;
        }
        //add the user.log part used for formatting
        userLogFile += USER_LOG_FORMAT;
        userLogger = createLogger(userLogFile, userLogMaxSize);
		
		cleanLogDirectory(userLogFile, errorLogFile);
	}
    
    private BasicLogger createLogger(String logFile, int maxLogSize) {
        File userLog = new File(logFile);
        if(!userLog.getParentFile().exists()){
            userLog.getParentFile().mkdirs();
            try {
                userLog.createNewFile();
            } catch (IOException e) {}
        }

        //create logger
        return new BasicLogger(logFile, maxLogSize);
    }
	
	/*
	 * gets rid of the oldest log files in the directory to keep only the
	 * maximum number specified in the preferences of ivc
	 */
	private void cleanLogDirectory(String userLog, String errorLog){
	    Configuration cfg = IVC.getInstance().getConfiguration();
	    
	    int maxUserLogs = cfg.getInt(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES);
	    int maxErrorLogs = cfg.getInt(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES);
	    
	    File userLogFile = new File(userLog);
	    File errorLogFile = new File(errorLog);
	    
	    String userLogParent = userLogFile.getParent();
	    String errorLogParent = errorLogFile.getParent();
	    
	    int index = userLogFile.getName().lastIndexOf(".");
	    final String userLogName = userLogFile.getName().substring(0, index);
	    final String userExtension = userLogFile.getName().substring(index);
	    index = errorLogFile.getName().lastIndexOf(".");
	    final String errorLogName = errorLogFile.getName().substring(0, index);
	    final String errorExtension = errorLogFile.getName().substring(index);
	    
	    if(userLogParent != null){
	        File userLogDir = new File(userLogParent);
	    	FileFilter userLogFilter = new FileFilter() {
				public boolean accept(File file) {			    
					return file.isFile() && 
						file.getName().startsWith(userLogName) &&
						file.getName().endsWith(userExtension);
				}
			};
			File[] userLogs = userLogDir.listFiles(userLogFilter);
			if(userLogs.length > maxUserLogs){
			    Arrays.sort(userLogs);
				for(int i = 0; i < userLogs.length - maxUserLogs; i++){
				    userLogs[i].delete();
				}			    
			}
	    }
	    
	    if(errorLogParent != null){
	        File errorLogDir = new File(errorLogParent);
	    	FileFilter errorLogFilter = new FileFilter() {
				public boolean accept(File file) {			    
					return file.isFile() && 
						file.getName().startsWith(errorLogName) &&
						file.getName().endsWith(errorExtension);
				}
			};
			File[] errorLogs = errorLogDir.listFiles(errorLogFilter);
			if(errorLogs.length > maxErrorLogs){
			    Arrays.sort(errorLogs);
				for(int i = 0; i < errorLogs.length - maxErrorLogs; i++){
				    errorLogs[i].delete();
				}			    
			}

	    }
	    
	}
	
	public Logger getErrorLogger() {
		return errorLogger;
	}

	public Logger getUserLogger() {
		return userLogger;
	}
}
