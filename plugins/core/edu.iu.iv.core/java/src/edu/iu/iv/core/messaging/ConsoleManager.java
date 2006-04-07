/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;


/**
 * ConsoleManager holds a collection of ConsoleHandlers and also
 * implements ConsoleHandler itself, delegating calls to its ConsoleHandler
 * methods to all of the contained handlers in the collection.  It is used
 * to manage the registry of console devices with IVC and provide an interface
 * to them to the user which does not require they know about each and
 * every one.
 *
 * @author Team IVC
 */
public interface ConsoleManager extends ConsoleHandler {
    /**
     * Add a ConsoleHandler to this ConsoleManager
     *
     * @param handler the ConsoleHandler to add to this ConsoleManager
     */
    public void add(ConsoleHandler handler);

    /**
     * Removes a ConsoleHandler from this ConsoleManager
     *
     * @param handler the ConsoleHandler to remove from this ConsoleManager
     */
    public void remove(ConsoleHandler handler);
}
