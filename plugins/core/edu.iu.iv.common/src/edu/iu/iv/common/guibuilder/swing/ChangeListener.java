/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing;

/**
 * Listener that listens for changes.
 * 
 * @author Bruce Herr
 */
public interface ChangeListener {
    
    /**
     * Run when a change occurs in the class being listened to.
     */
    public void changeOccured();
}
