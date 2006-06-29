/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 29, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.algorithm;

/**
 * A class to monitor the progress of an algorithm. It allows for notification
 * of progress, notification of cancellation, and description of current work 
 * during execution. These methods are generally only called by the algorithm
 * with the CIShell client providing the progress monitor implementation.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface ProgressMonitor {
    /**
     * Notifies the start of execution of the algorithm in addition to 
     * revealing how many work units will be used.  
     * 
     * @param totalWorkUnits The number of work units, may be -1 if the  
     *                       algorithm does not provide progress information.
     */
    public void start(int totalWorkUnits);
    
    /**
     * Notifies that a certain number of units of work has been completed
     * 
     * @param work The number of units of work completed 
     *             since last notification.
     */
    public void worked(int work);
    
    /**
     * The algorithm is finished executing.
     */
    public void done();
    
    /**
     * Sets or clears a flag for cancellation of this algorithm's execution.
     * An algorithm writer can ignore or clear this flag if it cannot stop 
     * midstream. This is the only method that can be called by someone other
     * than the algorithm.
     * 
     * @param value Set or clear the cancellation of algorithm execution
     */
    public void setCanceled(boolean value);
    
    /**
     * Returns whether cancellation of algorithm execution is requested. An
     * algorithm that can be cancelled should poll this method when convenient
     * to see if it should cancel.
     *
     * @return Whether cancellation of algorithm execution is requested
     */
    public boolean isCanceled();
    
    /**
     * Method to describe what the algorithm is currently doing for the benefit
     * of the users of the algorithm as it progresses during execution.
     * 
     * @param currentWork A short description of the current work the algorithm
     *                    is doing
     */
    public void describeWork(String currentWork);
}
