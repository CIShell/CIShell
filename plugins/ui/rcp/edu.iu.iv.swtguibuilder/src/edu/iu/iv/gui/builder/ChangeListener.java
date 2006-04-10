/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 28, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder;

/**
 * A listener to be notified when something has changed.
 * 
 * @author Bruce Herr
 */
public interface ChangeListener {
    /**
     * Notifies the class that a change has occured.
     */
    public void changeOccured();
}
