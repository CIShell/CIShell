/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;


/**
 * A ConsoleHandler provides convenience methods to interface to some Console
 * implementation, such as a GUI Text area or just a command line implementation.
 * ConsoleHandlers can be registered with IVC's ConsoleManager to be notified
 * of writes to the Console and respond appropriately.
 *
 * @author Team IVC
 */
public interface ConsoleHandler {
    
    /**
     * Sets the default ConsoleLevel for the IVCConsole. This determines what
     * ConsoleLevel is used when none is specified. By Default this is
     * ALGORITHM_INFORMATION.
     *
     * @param level the ConsoleLevel to use when none is specified.
     */
    public void setDefault(ConsoleLevel level);
    
    /**
     * Returns the default level of this IVCConsole. This is the level
     * which is used for printing when no level is specified.  By default this
     * is ALGORITHM_INFORMATION.
     * 
     * @return the default level of this IVCConsole
     */
    public ConsoleLevel getDefaultLevel();

    /**
     * Sets the maximum ConsoleLevel for the IVCConsole. This determines whether
     * a given message will be allowed to be displayed on the console.  By default,
     * this is ALGORITHM_INFORMATION, meaning things like SYSTEM_WARNING and 
     * SYSTEM_ERROR do not show up on the console.
     *
     * @param level the ConsoleLevel of messages to allow to be displayed.
     */
    public void setMaximumLevel(ConsoleLevel level);
    
    /**
     * Returns the maximum ConsoleLevel for the IVCConsole.  This is the
     * highest level at which a message will be allowed to be displayed
     * on the console.
     * 
     * @return the maximum ConsoleLevel for the IVCConsole
     */
    public ConsoleLevel getMaximumLevel();
    
    /**
     * Prints the given message to the IVCConsole at the default level
     * (ALGORITHM_INFORMATION by default).
     * 
     * @param message the message to print at the default level.
     */
    public void print(String message);
    
    /**
     * Prints the given message at the given ConsoleLevel, if that level
     * is not of greater priority than the maximum level of the IVCConsole. For
     * example, if the IVCConsole's maximum level is ALGORITHM_INFORMATION, then
     * SYSTEM_ERROR messages will not be displayed.
     *
     * @param message the message to display in the IVCConsole
     * @param level the level of the given message
     */
    public void print(String message, ConsoleLevel level);
    
    /**
     * Prints the given message at the USER_ACTIVITY ConsoleLevel.
     *
     * @param message the message to print at the USER_ACTIVITY ConsoleLevel.
     */
    public void printUserActivity(String message);

    /**
     * Prints the given message at the SYSTEM_INFORMATION ConsoleLevel.
     *
     * @param message the message to print at the SYSTEM_INFORMATION ConsoleLevel.
     */
    public void printSystemInformation(String message);
    
    /**
     * Prints the given message at the SYSTEM_WARNING ConsoleLevel.
     *
     * @param message the message to print at the SYSTEM_WARNING ConsoleLevel.
     */
    public void printSystemWarning(String message);       

    /**
     * Prints the given message at the SYSTEM_ERROR ConsoleLevel.
     *
     * @param message the message to print at the SYSTEM_ERROR ConsoleLevel.
     */
    public void printSystemError(String message);

    /**
     * Prints the given message at the ALGORITHM_INFORMATION ConsoleLevel.
     *
     * @param message the message to print at the ALGORITHM_INFORMATION ConsoleLevel.
     */
    public void printAlgorithmInformation(String message);

}
