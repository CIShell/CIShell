/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 23, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder;

/**
 * Listens for when a widget is selected.
 * 
 * @author Bruce Herr
 */
public interface SelectionListener {
    
    /**
     * Run when the widget being listened to is selected.
     */
    public void widgetSelected();
}
