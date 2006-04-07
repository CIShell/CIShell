/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 22, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.io.File;
import java.io.IOException;

import edu.iu.iv.common.configuration.BasicConfiguration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.internal.BasicLogger;

import junit.framework.TestCase;

/**
 *
 * @author Team IVC
 */
public class BasicLoggerTester extends TestCase {

    private BasicLogger logger;
    private String filePath = System.getProperty("user.dir") + File.separator + "tmp.log";
    
    protected void setUp() {
        try {
            IVC.setDelegate(new AbstractIVCDelegate(new BasicConfiguration(new File("config.xml"),"")) {

                public String getPluginPath(String pluginID) {
                    return "";
                }

                public String getPluginPath(Object plugin) {
                    return "";
                }

                public String getDefaultDataFolder() {
                    return "";
                }

                public String getTemporaryFilesFolder() {
                    return System.getProperty("user.dir");
                }});
            
           
        } catch (IOException e) {
            assertTrue(false);
        }
        
        logger = new BasicLogger(filePath, 5);        
    }
    
    protected void tearDown(){
        //get rid of tmp file
        //logger.getFile().delete();    
    }

    public void testGetAndSetMaxSize() {
        assertEquals(logger.getMaxSize(), 5);
        logger.setMaxSize(10);
        assertEquals(logger.getMaxSize(), 10);
        logger.setMaxSize(405);
        assertEquals(logger.getMaxSize(), 405);
    }
    
    public void testLogging(){
        File log = logger.getFile();
        int size = logger.getMaxSize();
        assertEquals(size, 5);
        
        assertTrue(log.length() < size);
        
        //log some stuff - this is far more than 5k of data, so the log will have to
        //keep its size managed to that limit
        for(int i  = 0; i < 20; i++){
	        String logMessage = 
	                "log log log log log log log log log log log log log log log log log log " +
	        		"log log log log log log log log log log log log log log log log log log " +
	        		"log log log log log log log log log log log log log log log log log log " +
	        		"log log log log log log log log log log log log log log log log log log " +
	        		"log log log log log log log log log log log log log log log log log log " +
	        		"log log log log log log log log log log log log log log log log log log " +
	        		"log log log log log log log log log log log log log log log log log log " +
	        		"log log log log log log log log log log log log log log log log log log ";
	        logger.info(logMessage);	     
	        //ensure the log is still under the limit
	        assertTrue(log.length() / 1024 < logger.getMaxSize());
        }        
    }

}
