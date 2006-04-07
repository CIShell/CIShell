/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 21, 2005 at Indiana University.
 */
package edu.iu.iv.core;

import java.io.File;

/**
 * This class contains all the IVC Preferences that are used by the core.
 * The fields are keys to be used when getting data from the configuration
 * object from IVC.getConfiguration() or in the case of the fields that 
 * end in _DEFAULT are default values for the preference.
 * 
 * @author Bruce Herr
 */
public class IVCPreferences {
    /** the preference for getting the error log directory **/
    public static final String ERROR_LOG_PREFERENCE = "errorlog";
    public static final String ERROR_LOG_PREFERENCE_DEFAULT = 
        System.getProperty("user.dir") + File.separator + "logs"  + File.separator;
    
    /** the preference for getting the user log directory **/
    public static final String USER_LOG_PREFERENCE = "userlog";
    public static final String USER_LOG_PREFERENCE_DEFAULT = 
        System.getProperty("user.dir") + File.separator + "logs" + File.separator;
    
    /** the preference for the maximum error log size **/
    public static final String ERROR_LOG_SIZE_PREFERENCE = "errorlogsize";
    public static final int ERROR_LOG_SIZE_PREFERENCE_DEFAULT = 100;
    
    /** the preference for the maximum user log size **/
    public static final String USER_LOG_SIZE_PREFERENCE = "userlogsize";
    public static final int USER_LOG_SIZE_PREFERENCE_DEFAULT = 100;
    
    /** the preference for whether to show all errors on the console **/
    public static final String SHOW_ALL_ERRORS_PREFERENCE = "showall";
    public static final boolean SHOW_ALL_ERRORS_PREFERENCE_DEFAULT = false;
    
    /** the preference for whether to showing critical errors on the console **/
    public static final String SHOW_CRITICAL_ERRORS_PREFERENCE = "showcritical";
    public static final boolean SHOW_CRITICAL_ERRORS_PREFERENCE_DEFAULT = false;
    
    /** the preference for getting the temporary files folder **/
    public static final String TEMPORARY_FILES_FOLDER_PREFERENCE = "temporaryfilesfolder";
    public static final String TEMPORARY_FILES_FOLDER_PREFERENCE_DEFAULT = getTempFolder();
    
    /** the preference for getting the defatault data folder **/
    public static final String DEFAULT_DATA_FOLDER_PREFERERNCE = "defaultdatafolder";
    public static final String DEFAULT_DATA_FOLDER_PREFERERNCE_DEFAULT = 
        System.getProperty("user.dir") + File.separator + "sampledata";
    
    /** the preference for getting the max simultaneous algorithms to be run **/
    public static final String MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE = "maxalgorithms";
    public static final int MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE_DEFAULT = 4;
    
    /** the preference for confirming the user's exit on close **/
    public static final String EXIT_WITHOUT_PROMPT = "ivcconfirmexit";
    public static final boolean EXIT_WITHOUT_PROMPT_DEFAULT = false;
    
    /** the preference for getting the max number of error log files **/
    public static final String MAX_NUMBER_OF_ERROR_LOGFILES = "maxnumberoferrorlogfiles";
    public static final int MAX_NUMBER_OF_ERROR_LOGFILES_DEFAULT = 20;
    
    /** the preference for getting the max number of user log files **/
    public static final String MAX_NUMBER_OF_USER_LOGFILES = "maxnumberofuserlogfiles";
    public static final int MAX_NUMBER_OF_USER_LOGFILES_DEFAULT = 20;
    
    private IVCPreferences() {}
	
	//NOTE: Do we want to handle with any more finer grain control here?
	private static String getTempFolder(){
		String tempFolder = "";
		if(System.getProperty("os.name").startsWith("Windows"))
		    tempFolder = "C:" + File.separator + "windows" + File.separator + "temp";
		else
		    tempFolder = File.separator + "tmp";
		return tempFolder;
	}
}
