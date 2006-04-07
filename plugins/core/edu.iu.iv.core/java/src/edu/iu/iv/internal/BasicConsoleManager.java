/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.messaging.ConsoleHandler;
import edu.iu.iv.core.messaging.ConsoleLevel;
import edu.iu.iv.core.messaging.ConsoleManager;

/**
 * Basic implementation of ConsoleManager interface.  This class keeps track
 * of the consoles in use by IVC and provides the interface to print 
 * messages to them at various ConsoleLevels.
 *
 * @author Team IVC
 */
public class BasicConsoleManager implements ConsoleManager {
    private Set consoles = new HashSet();
    private ConsoleLevel defaultLevel;
    private ConsoleLevel currentLevel;
    
    public BasicConsoleManager(){
        defaultLevel = ConsoleLevel.ALGORITHM_INFORMATION;
        
        Configuration cfg = IVC.getInstance().getConfiguration();
        boolean showAll = cfg.getBoolean(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE);
        boolean showCritical = cfg.getBoolean(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE);
        
        if(showAll || showCritical){
            currentLevel = ConsoleLevel.SYSTEM_ERROR;
        }
        else{
            currentLevel = ConsoleLevel.ALGORITHM_INFORMATION;
        }
    }
    
    /**
     * Add a ConsoleHandler to this ConsoleManager
     *
     * @param handler the ConsoleHandler to add to this ConsoleManager
     */
    public void add(ConsoleHandler handler){
        consoles.add(handler);
    }
    
    /**
     * Removes a ConsoleHandler from this ConsoleManager
     *
     * @param handler the ConsoleHandler to remove from this ConsoleManager
     */
    public void remove(ConsoleHandler handler){
        consoles.remove(handler);
    }

    /**
     * Returns the default level of this ConsoleManager. This is the level
     * which is used for printing when no level is specified.  By default this
     * is ALGORITHM_INFORMATION.
     * 
     * @return the default level of this IVCConsole
     */
    public ConsoleLevel getDefaultLevel() {
        return defaultLevel;
    }

    /**
     * Returns the maximum ConsoleLevel for the ConsoleManager.  This is the
     * highest level at which a message will be allowed to be displayed
     * on the console.
     * 
     * @return the maximum ConsoleLevel for the IVCConsole
     */
    public ConsoleLevel getMaximumLevel() {
        return currentLevel;
    }
    
    /**
     * Sets the default ConsoleLevel for the ConsoleManager and all contained
     * ConsoleHandlers. This determines what
     * ConsoleLevel is used when none is specified. By Default this is
     * ALGORITHM_INFORMATION.
     *
     * @param level the ConsoleLevel to use when none is specified.
     */
    public void setDefault(ConsoleLevel level) {
        defaultLevel = level;
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            next.setDefault(level);
        }
    }

    /**
     * Sets the maximum ConsoleLevel for the ConsoleManager and all contained
     * ConsoleHandlers .This determines whether
     * a given message will be allowed to be displayed on the console.  By default,
     * this is ALGORITHM_INFORMATION, meaning things like SYSTEM_WARNING and 
     * SYSTEM_ERROR do not show up on the console.
     *
     * @param level the ConsoleLevel of messages to allow to be displayed.
     */
    public void setMaximumLevel(ConsoleLevel level) {
        currentLevel = level;
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            next.setMaximumLevel(level);
        }
    }

    /**
     * Prints the given message at the default level in all contained
     * ConsoleHandlers (ALGORITHM_INFORMATION by default).
     * 
     * @param message the message to print at the default level.
     */
    public void print(String message) {
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            if (next.getDefaultLevel().compareTo(next.getMaximumLevel()) <= 0) {
                next.print(message);
            }
        }
        
        log(message, defaultLevel);
    }

    /**
     * Prints the given message at the given ConsoleLevel in all contained
     * ConsoleHandlers, if that level
     * is not of greater priority than the maximum level of the ConsoleHandler. For
     * example, if the maximum level is ALGORITHM_INFORMATION, then
     * SYSTEM_ERROR messages will not be displayed.
     *
     * @param message the message to display
     * @param level the level of the given message
     */
    public void print(String message, ConsoleLevel level) {
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            if (level.compareTo(next.getMaximumLevel()) <= 0) {
                next.print(message, level);
            }
        }
        
        log(message, level);
    }

    /**
     * Prints the given message at the USER_ACTIVITY ConsoleLevel in
     * all contained ConsoleHandlers.
     *
     * @param message the message to print at the USER_ACTIVITY ConsoleLevel.
     */
    public void printUserActivity(String message) {
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            if (ConsoleLevel.USER_ACTIVITY.compareTo(next.getMaximumLevel()) <= 0) {
                next.printUserActivity(message);
            }
        }
        
        log(message, ConsoleLevel.USER_ACTIVITY);
    }

    /**
     * Prints the given message at the SYSTEM_INFORMATION ConsoleLevel
     * in all contained ConsoleHandlers.
     *
     * @param message the message to print at the SYSTEM_INFORMATION ConsoleLevel.
     */
    public void printSystemInformation(String message) {
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            if (ConsoleLevel.SYSTEM_INFORMATION.compareTo(next.getMaximumLevel()) <= 0) {
                next.printSystemInformation(message);
            }
        }
        
        log(message, ConsoleLevel.SYSTEM_INFORMATION);
    }

    /**
     * Prints the given message at the SYSTEM_WARNING ConsoleLevel
     * in all contained ConsoleHandlers.
     *
     * @param message the message to print at the SYSTEM_WARNING ConsoleLevel.
     */
    public void printSystemWarning(String message) {
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            if (ConsoleLevel.SYSTEM_WARNING.compareTo(next.getMaximumLevel()) <= 0) {
                next.printSystemWarning(message);
            }
        }
    }

    /**
     * Prints the given message at the SYSTEM_ERROR ConsoleLevel
     * in all contained ConsoleHandlers.
     *
     * @param message the message to print at the SYSTEM_ERROR ConsoleLevel.
     */
    public void printSystemError(String message) {
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            if (ConsoleLevel.SYSTEM_ERROR.compareTo(next.getMaximumLevel()) <= 0) {
                next.printSystemError(message);
            }
        }
    }

    /**
     * Prints the given message at the ALGORITHM_INFORMATION ConsoleLevel
     * in all contained ConsoleHandlers.
     *
     * @param message the message to print at the ALGORITHM_INFORMATION ConsoleLevel.
     */
    public void printAlgorithmInformation(String message) {
        Iterator consoleIterator = consoles.iterator();
        while(consoleIterator.hasNext()){
            ConsoleHandler next = (ConsoleHandler)consoleIterator.next();
            if (ConsoleLevel.ALGORITHM_INFORMATION.compareTo(next.getMaximumLevel()) <= 0) {
                next.printAlgorithmInformation(message);
            }
        }
        
        log(message, ConsoleLevel.ALGORITHM_INFORMATION);
    }
    

    /*
     * if the given message is below SYSTEM_WARNING, log it in the user log
     * (error log is populated directly at the source of the error)
     */
    private static void log(String message, ConsoleLevel level) {
        if (level.compareTo(ConsoleLevel.SYSTEM_WARNING) < 0) {
            IVC.getInstance().getUserLogger().info(message);
        }
    }


}
