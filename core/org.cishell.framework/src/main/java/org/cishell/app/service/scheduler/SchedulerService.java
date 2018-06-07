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
import org.osgi.framework.ServiceReference;


/**
 * A service for scheduling {@link Algorithm}s to be run. 
 * {@link SchedulerListener}s may be registered to be notified of events.  
 * 
 * Application Developers are encouraged to use this service for scheduling 
 * Algorithms to be run. Algorithm developers are encouraged not to use this 
 * service as it is not guaranteed to be available like the standard CIShell 
 * services are.
 * 
 */
public interface SchedulerService {
	/**
	 * Schedules an Algorithm to be run immediately. If there are simply not
	 * enough resources to run it, it will still have to wait until there are
	 * enough resources to fulfill the request.
	 * 
	 * @param algorithm The algorithm to be run
     * @param reference A reference to the Algorithm's associated service, may be <code>null</code>
	 */
    public void runNow(Algorithm algorithm, ServiceReference reference);
    
    /**
     * Schedules an Algorithm to be run when convenient. This schedules an
     * Algorithm to be run now, but gives no urgent priority to it. Most
     * Algorithms will be scheduled in this way. 
     * 
     * @param algorithm The Algorithm to be scheduled
     * @param reference A reference to the Algorithm's associated service, may be <code>null</code>
     */
    public void schedule(Algorithm algorithm, ServiceReference reference);
    
    /**
     * Schedules an Algorithm to be run at a specific time. The Algorithm will
     * be run at the given time unless there is simply not enough resources
     * at that time. In which case it would wait until there are enough 
     * resources to fulfill the request.
     * 
     * @param algorithm The Algorithm to be scheduled
     * @param reference A reference to the Algorithm's associated service, may be <code>null</code>
     * @param time What time this Algorithm should be run
     */
    public void schedule(Algorithm algorithm, ServiceReference reference, Calendar time);
    
    /**
     * Reschedules an already scheduled Algorithm to be run at a different time.
     * If the Algorithm is not scheduled already, then this method will have no
     * effect and will return <code>false</code>.
     * 
     * @param algorithm The Algorithm already scheduled
     * @param newTime The revised time in which to run the Algorithm
     * @return If the Algorithm was successfully rescheduled
     */
    public boolean reschedule(Algorithm algorithm, Calendar newTime);
    
    /**
     * Unschedules an already scheduled, but not running Algorithm from the 
     * scheduler. Tries to unschedule an Algorithm from the scheduler. If the
     * Algorithm isn't in the scheduler or if the Algorithm is already running
     * then this method returns <code>false</code>.
     * 
     * @param algorithm The Algorithm to remove from the scheduler
     * @return If the Algorithm was successfully unscheduled
     */
    public boolean unschedule(Algorithm algorithm);
    
    /**
     * Adds a listener to be notified of events happening in the scheduler
     * 
     * @param listener The listener to be added
     */
    public void addSchedulerListener(SchedulerListener listener);
    
    /**
     * Removes a {@link SchedulerListener} from the group of listeners listening
     * for scheduler events. This method has no effect if the listener isn't
     * in the group of listeners.
     * 
     * @param listener The listener to be removed
     */
    public void removeSchedulerListener(SchedulerListener listener);

    /**
     * Returns whether the scheduler is running
     * 
     * @return if the scheduler is running
     */
    public boolean isRunning();
    
    /**
     * Pauses or unpauses the running of new {@link Algorithm}s in the 
     * scheduler
     * 
     * @param isRunning <code>true</code> to pause, 
     *                  <code>false</code> to unpause 
     */
    public void setRunning(boolean isRunning);
    
    /**
     * Returns an array of {@link Algorithm}s that the scheduler has scheduled.
     * This includes the Algorithms that are currently running and the ones
     * queued to be run. This also just gives a snapshot of the current set of
     * scheduled Algorithms, so it is not guaranteed to be accurate even 
     * directly after the method returns.
     * 
     * @return The set of Algorithms currently scheduled in the scheduler
     */
    public Algorithm[] getScheduledAlgorithms();
    
    /**
     * Returns the time in which a scheduled Algorithm is scheduled to be run.
     * The time may be in the past if the Algorithm is already running or 
     * <code>null</code> if the Algorithm is not scheduled.
     * 
     * @param algorithm The Algorithm
     * @return The scheduled time for the Algorithm to run or <code>null</code>
     *         if the Algorithm is not scheduled or has completed execution
     */
    public Calendar getScheduledTime(Algorithm algorithm);
    
    /**
     * Returns an Algorithm's associated ServiceReference if one was provided
     * when the Algorithm was scheduled
     * 
     * @param algorithm The Algorithm
     * @return Its associated ServiceReference
     */
    public ServiceReference getServiceReference(Algorithm algorithm);
    
    /**
     * Clears all currently scheduled Algorithms to be run. If an Algorithm is
     * already running, then it will continue to run until finished.
     */
    public void clearSchedule();
    
    /**
     * Returns if there are any Algorithms scheduled
     * 
     * @return Whether there are any Algorithms scheduled
     */
    public boolean isEmpty();
}
