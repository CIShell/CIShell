/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 22, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import edu.iu.iv.common.configuration.BasicConfiguration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.messaging.ConsoleHandler;
import edu.iu.iv.core.messaging.ConsoleLevel;
import edu.iu.iv.internal.BasicConsoleManager;

/**
 *
 * @author Team IVC
 */
public class BasicConsoleManagerTester extends TestCase {

    private BasicConsoleManager manager;
    private ConsoleHandler handler1;
    private ConsoleHandler handler2;
    private ConsoleHandler handler3;
    private int printed;
    
    private boolean userActivity;
    private boolean systemInformation;
    private boolean algorithmInformation;    
    private boolean systemWarning;
    private boolean systemError;
    
    
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
                    return "";
                }});
            
           
        } catch (IOException e) {
            assertTrue(false);
        }
        
        IVC.getDelegate().setDefaultSettings();
        
        manager = new BasicConsoleManager();
        handler1 = new DummyConsoleHandler();
        handler2 = new DummyConsoleHandler();
        handler3 = new DummyConsoleHandler();        
        printed = 0;
        userActivity = false;
        systemInformation = false;
        algorithmInformation = false;    
        systemWarning = false;
        systemError = false;    
    }

    public void testAddAndRemove() {
        //ensure no items are contained
        printed = 0;
        manager.print("hello");
        assertEquals(printed, 0);
        
        manager.add(handler1);
        manager.add(handler2);
        manager.add(handler3);
        manager.print("hello");
        assertEquals(printed, 3);
        
        printed = 0;
        manager.remove(handler1);
        manager.print("hello");
        assertEquals(printed, 2);
        
        printed = 0;
        manager.remove(handler2);
        manager.remove(handler3);
        manager.print("hello");
        assertEquals(printed, 0);        
    }

    public void testGetAndSetDefaultLevel() {
        manager.add(handler1);
        manager.add(handler2);
        manager.add(handler3);
        
        assertEquals(handler1.getDefaultLevel(), ConsoleLevel.ALGORITHM_INFORMATION);
        assertEquals(handler2.getDefaultLevel(), ConsoleLevel.ALGORITHM_INFORMATION);
        assertEquals(handler3.getDefaultLevel(), ConsoleLevel.ALGORITHM_INFORMATION);        
        
        manager.setDefault(ConsoleLevel.SYSTEM_WARNING);
        
        assertEquals(manager.getDefaultLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler1.getDefaultLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler2.getDefaultLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler3.getDefaultLevel(), ConsoleLevel.SYSTEM_WARNING);
        
        manager.remove(handler1);
        manager.setDefault(ConsoleLevel.USER_ACTIVITY);
        assertEquals(manager.getDefaultLevel(), ConsoleLevel.USER_ACTIVITY);
        assertNotSame(handler1.getDefaultLevel(), ConsoleLevel.USER_ACTIVITY);
        assertEquals(handler1.getDefaultLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler2.getDefaultLevel(), ConsoleLevel.USER_ACTIVITY);
        assertEquals(handler3.getDefaultLevel(), ConsoleLevel.USER_ACTIVITY);
    }

    public void testGetAndSetMaximumLevel() {
        manager.add(handler1);
        manager.add(handler2);
        manager.add(handler3);
        
        assertEquals(handler1.getMaximumLevel(), ConsoleLevel.SYSTEM_ERROR);
        assertEquals(handler2.getMaximumLevel(), ConsoleLevel.SYSTEM_ERROR);
        assertEquals(handler3.getMaximumLevel(), ConsoleLevel.SYSTEM_ERROR);        
        
        manager.setMaximumLevel(ConsoleLevel.SYSTEM_WARNING);
        
        assertEquals(manager.getMaximumLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler1.getMaximumLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler2.getMaximumLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler3.getMaximumLevel(), ConsoleLevel.SYSTEM_WARNING);
        
        manager.remove(handler1);
        manager.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        assertEquals(manager.getMaximumLevel(), ConsoleLevel.USER_ACTIVITY);
        assertNotSame(handler1.getMaximumLevel(), ConsoleLevel.USER_ACTIVITY);
        assertEquals(handler1.getMaximumLevel(), ConsoleLevel.SYSTEM_WARNING);
        assertEquals(handler2.getMaximumLevel(), ConsoleLevel.USER_ACTIVITY);
        assertEquals(handler3.getMaximumLevel(), ConsoleLevel.USER_ACTIVITY);
        
    }    

    /*
     * Class under test for void print(String)
     */
    public void testPrintString() {
        manager.add(handler1);
        manager.add(handler2);
        
        manager.setDefault(ConsoleLevel.SYSTEM_WARNING);
        manager.setMaximumLevel(ConsoleLevel.ALGORITHM_INFORMATION);
        printed = 0;
        systemWarning = false;
        manager.print("hello");
        //nothing should print at those levels
        assertEquals(printed, 0);
        assertFalse(systemWarning);
        
        manager.setMaximumLevel(ConsoleLevel.SYSTEM_WARNING);
        printed = 0;
        systemWarning = false;
        manager.print("hello");
        assertEquals(printed, 2);
        assertTrue(systemWarning);

        manager.setMaximumLevel(ConsoleLevel.ALGORITHM_INFORMATION);
        manager.setDefault(ConsoleLevel.SYSTEM_INFORMATION);
        printed = 0;
        systemInformation = false;
        manager.print("hello");
        assertEquals(printed, 2);
        assertTrue(systemInformation);
        
        handler1.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        printed = 0;
        systemInformation = false;
        manager.print("hello");
        //only one should print now since user activity level will not get
        //sys info messages
        assertEquals(printed, 1);
        assertTrue(systemInformation);        
    }

    /*
     * Class under test for void print(String, ConsoleLevel)
     */
    public void testPrintStringConsoleLevel() {
        manager.add(handler1);
        manager.add(handler2);
        
        manager.setMaximumLevel(ConsoleLevel.ALGORITHM_INFORMATION);
        printed = 0;
        systemWarning = false;
        manager.print("hello", ConsoleLevel.SYSTEM_WARNING);
        //nothing should print at those levels
        assertEquals(printed, 0);
        assertFalse(systemWarning);
        
        manager.setMaximumLevel(ConsoleLevel.SYSTEM_WARNING);
        printed = 0;
        systemWarning = false;
        manager.print("hello", ConsoleLevel.SYSTEM_WARNING);
        assertEquals(printed, 2);
        assertTrue(systemWarning);

        manager.setMaximumLevel(ConsoleLevel.ALGORITHM_INFORMATION);
        printed = 0;
        systemInformation = false;
        manager.print("hello", ConsoleLevel.SYSTEM_INFORMATION);
        assertEquals(printed, 2);
        assertTrue(systemInformation);
        
        handler1.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        printed = 0;
        systemInformation = false;
        manager.print("hello", ConsoleLevel.SYSTEM_INFORMATION);
        //only one should print now since user activity level will not get
        //sys info messages
        assertEquals(printed, 1);
        assertTrue(systemInformation);       
    }

    public void testPrintUserActivity() {
        manager.add(handler1);
        
        printed = 0;
        userActivity = false;
        manager.printUserActivity("hello");
        assertTrue(userActivity);
        assertEquals(printed, 1);
        
        manager.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        printed = 0;
        userActivity = false;
        manager.printUserActivity("hello");
        assertTrue(userActivity);
        assertEquals(printed, 1);
        
    }

    public void testPrintSystemInformation() {
        manager.add(handler1);
        
        printed = 0;
        systemInformation = false;
        manager.printSystemInformation("hello");
        assertTrue(systemInformation);
        assertEquals(printed, 1);
        
        manager.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        printed = 0;
        systemInformation = false;
        manager.printSystemInformation("hello");
        assertFalse(systemInformation);
        assertEquals(printed, 0);
    
    }

    public void testPrintSystemWarning() {
        manager.add(handler1);
        
        manager.setMaximumLevel(ConsoleLevel.SYSTEM_WARNING);
        printed = 0;
        systemWarning = false;
        manager.printSystemWarning("hello");
        assertTrue(systemWarning);
        assertEquals(printed, 1);
        
        manager.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        printed = 0;
        systemWarning = false;
        manager.printSystemWarning("hello");
        assertFalse(systemWarning);
        assertEquals(printed, 0);
    }

    public void testPrintSystemError() {
        manager.add(handler1);
        
        manager.setMaximumLevel(ConsoleLevel.SYSTEM_ERROR);
        printed = 0;
        systemError = false;
        manager.printSystemError("hello");
        assertTrue(systemError);
        assertEquals(printed, 1);
        
        manager.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        printed = 0;
        systemError = false;
        manager.printSystemError("hello");
        assertFalse(systemError);
        assertEquals(printed, 0);
    }

    public void testPrintAlgorithmInformation() {
        manager.add(handler1);
        
        printed = 0;
        algorithmInformation = false;
        manager.printAlgorithmInformation("hello");
        assertTrue(algorithmInformation);
        assertEquals(printed, 1);
        
        manager.setMaximumLevel(ConsoleLevel.USER_ACTIVITY);
        printed = 0;
        algorithmInformation = false;
        manager.printAlgorithmInformation("hello");
        assertFalse(algorithmInformation);
        assertEquals(printed, 0);
    }
    
    private class DummyConsoleHandler implements ConsoleHandler {

        private ConsoleLevel defaultLevel = ConsoleLevel.ALGORITHM_INFORMATION;
        private ConsoleLevel maxLevel = ConsoleLevel.SYSTEM_ERROR;
        
        public void setDefault(ConsoleLevel level) {
            defaultLevel = level;
        }

        public ConsoleLevel getDefaultLevel() {
            return defaultLevel;
        }

        public void setMaximumLevel(ConsoleLevel level) {
            maxLevel = level;
        }

        public ConsoleLevel getMaximumLevel() {
            return maxLevel;
        }

        public void print(String message) {
            print(message, defaultLevel);
        }

        public void print(String message, ConsoleLevel level) {
            printed++;
            if(level == ConsoleLevel.USER_ACTIVITY)
                userActivity = true;
            if(level == ConsoleLevel.SYSTEM_INFORMATION)
                systemInformation = true;
            if(level == ConsoleLevel.ALGORITHM_INFORMATION){
                algorithmInformation = true;               
            }
            if(level == ConsoleLevel.SYSTEM_WARNING){
                systemWarning = true;
            }
            if(level == ConsoleLevel.SYSTEM_ERROR){
                systemError = true;
            }
        }

        public void printUserActivity(String message) {
            userActivity = true;
            printed++;
        }

        public void printSystemInformation(String message) {
            systemInformation = true;
            printed++;
        }

        public void printSystemWarning(String message) {
            systemWarning = true;
            printed++;
        }

        public void printSystemError(String message) {
            systemError = true;
            printed++;
        }

        public void printAlgorithmInformation(String message) {
            algorithmInformation = true;
            printed++;            
        }
        
    }

}
