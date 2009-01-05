package org.cishell.reference.gui.log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
	private static String default_log_dir = 
		System.getProperty("user.dir") 
		+ File.separator + "logs";
	private static String prefix_file_name = "user";
	
	private File currentDir;
	private Logger logger;
	
	//Specify the default size of each log file
	private int limit = 100000; // 100 kb
	
	//Specify the default numbers of log files
	private int max_Number_Of_LogFiles  = 10;

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
								generateUniqueFile(prefix_file_name)+
								".%g.log";
    			
    			FileHandler handler = new FileHandler(logFileName, 
            			limit, max_Number_Of_LogFiles, append);
    			
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
    	
    	// Log the exception?
    	Throwable throwableToBeLogged = entry.getException();
    	
    	// Log the exception's stack trace?
    	if (throwableToBeLogged != null) {
    		// TODO: Log to a different file?  This would possibly be where we'd
    		// check the preferences/etc.
    		StringWriter stackTraceStringWriter = new StringWriter();
    		
    		throwableToBeLogged.printStackTrace(new PrintWriter(stackTraceStringWriter));
    		logger.log(javaLogLevel, "Stace Trace: " + stackTraceStringWriter.toString() + "\r\n");
    	}
    	
    	if (goodMessage(message)) {
    		logger.log(javaLogLevel, message + "\r\n"); // stdout printing happens here, despite having 1 handler only
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
    	File logDir = new File(default_log_dir);
    	if (!logDir.exists() || !logDir.isDirectory()){  
    		try{
    			if (logDir.mkdir()){
    				return logDir;
    			} else {
    				return new File (default_log_dir);
    			}
    		}catch (Exception e){
    			e.printStackTrace();
    			return new File (default_log_dir);
    		}
    		
    	}else
    		return logDir;		
            
    }
    
    /*
     * create log file with given name plus unique timestamp
     */
    private String generateUniqueFile(String prefixFN) {
 
        Calendar now = Calendar.getInstance();
        String month = (now.get(Calendar.MONTH) + 1) + ""; //zero based

        if (month.length() == 1) {
            month = "0" + month;
        }

        String day = now.get(Calendar.DAY_OF_MONTH) + "";

        if (day.length() == 1) {
            day = "0" + day;
        }

        String year = now.get(Calendar.YEAR) + "";
        
        String timestamp = "-" + month + "-" + day + "-" + year + "-"+
        		System.currentTimeMillis();
        return prefixFN+timestamp;        
        
    }

}
