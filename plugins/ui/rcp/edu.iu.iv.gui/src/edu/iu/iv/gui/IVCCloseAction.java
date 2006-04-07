/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui;

/**
 * IVCCloseActions provide a means for items external to the core to set
 * the default window closing Action of IVC.  When the main window is about to
 * be closed (via pressing 'x' on the toolbar) the IVCCloseAction will be run
 * and the result will determine whether IVC should be allowed to close or not.
 * 
 * If the run() method returns true, the window will go ahead and close and end
 * the application, if it returns false, it will remain open.
 * 
 * @author Team IVC
 */
public interface IVCCloseAction {
    
    public boolean run();
    
}
