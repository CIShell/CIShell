/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 3, 2005 at Indiana University.
 */
package edu.iu.iv.schedulerview;

/**
 * This interface defines the operation refreshView() which is called on
 * all SchedulerContentModelListeners when the model changes.  This is used
 * by the SchedulerContentModel to keep the SchedulerView updated.
 *
 *
 * @author Team IVC
 */
public interface SchedulerContentModelListener {

    /**
     * Refreshes the view of this listener to respond to the notification
     * of change in the SchedulerContentModel.
     */
    public void refreshView();
    
}
