/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 14, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.app.service.scheduler;

import java.util.Calendar;

import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;


/**
 * A listener that is notified of events happening in a {@link SchedulerService}
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface SchedulerListener {
	/**
	 * Notification that an Algorithm has been scheduled to be run at a certain
	 * time
	 * 
	 * @param algorithm The scheduled Algorithm
	 * @param time The time is scheduled to be run
	 */
    public void algorithmScheduled(Algorithm algorithm, Calendar time);
    
    /**
     * Notification that an already scheduled Algorithm has been rescheduled 
     * to be run at a different time
     * 
     * @param algorithm The scheduled Algorithm
     * @param time The new time the Algorithm is scheduled to be run
     */
    public void algorithmRescheduled(Algorithm algorithm, Calendar time);
    
    /**
     * Notification that an already scheduled Algorithm has been unscheduled
     * and will therefore not be run
     * 
     * @param algorithm The scheduled Algorithm that was unscheduled
     */
    public void algorithmUnscheduled(Algorithm algorithm);
    
    /**
     * Notification that an Algorithm has started execution
     *  
     * @param algorithm The scheduled algorithm
     */
    public void algorithmStarted(Algorithm algorithm);
    
    /**
     * Notification that an Algorithm has finished executing
     * 
     * @param algorithm The scheduled Algorithm
     * @param createdData The {@link Data} array it returned, or 
     * 					  <code>null</code> if it returned <code>null</code>
     */
    public void algorithmFinished(Algorithm algorithm, Data[] createdData);
    
    /**
     * Notification that an Algorithm had an error while being executed
     * 
     * @param algorithm The scheduled Algorithm
     * @param error The error it threw while executing
     */
    public void algorithmError(Algorithm algorithm, Throwable error);
    
    /**
     * Notification that the scheduler's run state (paused or unpaused) has 
     * changed
     * 
     * @param isRunning <code>true</code> if it is now running, 
     *                  <code>false</code> if it is no longer running (paused)
     */
    public void schedulerRunStateChanged(boolean isRunning);
    
    /**
     * Notification that the scheduler's schedule of Algorithms to be run has 
     * been cleared
     */
    public void schedulerCleared();
}
