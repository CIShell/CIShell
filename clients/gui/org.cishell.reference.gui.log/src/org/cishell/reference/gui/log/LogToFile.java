package org.cishell.reference.gui.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

/**
 * This is a basic implementation. It writes log records to files
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 * @author Felix Terkhorn (terkhorn@gmail.com)
 */
public class LogToFile implements LogListener {
	//default log directory
	private static final String DEFAULT_LOG_DIR = 
		System.getProperty("user.dir") 
		+ File.separator + "logs";
	private static final String FILE_NAME_PREFIX = "user";
	
	private File currentDir;
	private Logger logger;
	
	//Specify the default size of each log file
	private static final int LIMIT = 100000; // 100 kb
	
	private static final int MAX_NUM_LOG_FILES  = 10;

    /**
     * Constructor
     */
    public LogToFile() {
    	
    	try {
            // Create an appending file handler
    		currentDir = getLogDirectory();
    		if (currentDir != null){
    			boolean append = true;
    			String logFileName = currentDir+File.separator+
								generateUniqueFile(FILE_NAME_PREFIX)+
								".%g.log";
    			
    			FileHandler handler = new FileHandler(logFileName, 
            			LIMIT, MAX_NUM_LOG_FILES, append);
    			
    			handler.setFormatter(new SimpleFormatter());
        
    			// Add to the desired logger
    			logger = Logger.getLogger("edu.iu.iv.logger");
    			logger.addHandler(handler);
    			logger.setUseParentHandlers(false); // Edited by Felix Terkhorn.  terkhorn@gmail.com May-9-2007
    		}
        } catch (IOException e) {
        	e.printStackTrace();
        }

    }
	
    public void logged(final LogEntry entry) {
    	String message = entry.getMessage();
    	int osgiLogLevel = entry.getLevel();
    	Level javaLogLevel;
/*
    	// Correspondence between OSGI log levels and Java.util.logging log levels:
    	// JAVA				OSGI
    	// ----				----
    	// SEVERE=1000		LOG_ERROR=1
    	// WARNING=900		LOG_WARNING=2
    	// INFO=800			LOG_INFO=3
    	// FINEST=300		LOG_DEBUG=4
  */  	
    	if (osgiLogLevel == 1) { // OSGI level = LOG_ERROR
    		javaLogLevel = Level.SEVERE;
    	} else if (osgiLogLevel == 2) {  // OSGI level = LOG_WARNING
    		javaLogLevel = Level.WARNING;
    	} else if (osgiLogLevel == 3) {  // OSGI level = LOG_INFO
    		javaLogLevel = Level.INFO;
    	} else if (osgiLogLevel == 4) {  // OSGI level = LOG_DEBUG
    		javaLogLevel = Level.FINEST;
    	} else { // if these don't match, set it to INFO
    		     // maybe not the best way to do this
    		javaLogLevel = Level.INFO;
    	}  // edited by Felix Terkhorn.  terkhorn@gmail.com   May-9-2007
    	
    	if (goodMessage(message)) {
    		// stdout printing happens here, despite having 1 handler only.
    		logger.log(javaLogLevel, message + "\r\n", entry.getException());
    	}
    }
    
    private boolean goodMessage(String msg) {
        if (msg == null || 
                msg.startsWith("ServiceEvent ") || 
                msg.startsWith("BundleEvent ") || 
                msg.startsWith("FrameworkEvent ")) {
            return false;
        } else {
            return true;   
        }
    }

    private static File getLogDirectory(){    	
    	//later, we should get the log directory from preference service
    	File logDir = new File(DEFAULT_LOG_DIR);
    	if (!logDir.exists() || !logDir.isDirectory()){  
    		try{
    			if (logDir.mkdir()){
    				return logDir;
    			} else {
    				return new File (DEFAULT_LOG_DIR);
    			}
    		}catch (Exception e){
    			e.printStackTrace();
    			return new File (DEFAULT_LOG_DIR);
    		}
    		
    	}else
    		return logDir;		
            
    }
    
    /*
     * create log file with given name plus unique timestamp
     */
    private String generateUniqueFile(String fileNamePrefix) {
        
/*		
 * 		We can set any date time format we want.
 *     	For the legend on different formats check http://java.sun.com/j2se/1.4.2/docs/api/java/text/SimpleDateFormat.html
*/    	
/*		TODO Make sure that 2 files with same filename are not generated. 
 * 		Current granularity is set at till milliseconds.
*/
    	
        String dateFormat = "MM-dd-yyyy-hh-mm-a-SSS";
        Calendar currentTemporalSnapshot = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String timestamp = "-" + simpleDateFormat.format(currentTemporalSnapshot.getTime());
        
        return fileNamePrefix + timestamp; 
    }

}
