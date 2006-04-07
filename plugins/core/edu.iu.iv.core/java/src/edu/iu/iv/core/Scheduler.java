/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 15, 2004 at Indiana University.
 */
package edu.iu.iv.core;

import java.util.Calendar;

import edu.iu.iv.core.algorithm.Algorithm;

/**
 * Scheduler for scheduling algorithms to be run.
 * 
 * @author Team IVC 
 */
//Created by: Bruce Herr
public interface Scheduler {
	/**
	 * Schedules an algorithm to be run immediately. 
	 * 
	 * @param algorithm the algorithm to run
	 */
	public void runNow(Algorithm algorithm);
	
	/**
	 * Schedules the given algorithm to be run at a later time.
	 * The actual scheduling order is implementation-defined.
	 * 
	 * @param algorithm the algorithm to be scheduled
	 */
	public void schedule(Algorithm algorithm);
	
	/**
	 * Schedules the given algorithm to be run at the given time.
	 * 
	 * @param algorithm the algorithm to be scheduled
	 * @param time the time at which the given algorithm should be run
	 */
	public void schedule(Algorithm algorithm, Calendar time);
	
	
	/**
	 * Unschedules the given Algorithm from the IVC Scheduler. This will
	 * result in this Algorithm not being run.
	 * 
	 * @param algorithm the Algorithm to unschedule
	 * @return true if the Algorithm was unscheduled successfully, false if not
	 */
	public boolean unschedule(Algorithm algorithm);
	
	/**
	 * Reschedules the given Algorithm to run at the given time
	 * 
	 * @param algorithm the Algorithm to reschedule
	 * @param newTime the new time for this Algorithm to run
	 * @return true of the Algorithm was rescheduled successfully, false if not
	 */
	public boolean reschedule(Algorithm algorithm, Calendar newTime);
	
	/**
	 * If the given Algorithm is in the ready list, waiting to be run,
	 * it is moved up one spot in line.
	 * 
	 * @param algorithm the Algorithm to move up in the ready list.
	 * @return true if the item was successfully moved up, false if not
	 */
	public boolean moveUp(Algorithm algorithm);
	
	/**
	 * If the given Algorithm is in the ready list, waiting to run,
	 * it is moved down one spot in line.
	 * 
	 * @param algorithm the Algorithm to move down in the ready list
	 * @return true if the item was successfully moved down, false if not
	 */
	public boolean moveDown(Algorithm algorithm);
	
	/**
	 * Adds the given SchedulerListener to this Scheduler. This listener
	 * will be notified of events that occur with the Scheduler.
	 * 
	 * @param listener the ScheduleListener to add to this Scheduler
	 */
	public void addSchedulerListener(SchedulerListener listener);
	
	/**
	 * Removes the given SchedulerListener from this Scheduler. 
	 * 
	 * @param listener the ScheduleListener to remove from this Scheduler
	 */
	public void removeSchedulerListener(SchedulerListener listener);
	
	/**
	 * Determines if there is currently anything running in the scheduler.
	 * These are items that are actually mid-execution, not just scheduled
	 * for the future.
	 * 
	 * @return true if any Algorithms are running in the scheduler, false
	 * if not.
	 */
	public boolean isRunning();
	
	/**
	 * Determines if there are any Algorithms in the scheduler that
	 * are either running or waiting to run.
	 * 
	 * @return true if there are Algorithms running or waiting to run,
	 * false if not
	 */
	public boolean isEmpty();
	
	/**
	 * Blocks the given Algorithm from running, if it is not already.
	 * This will prevent a scheduled or queued item from starting to run
	 * until unblock(Algorithm) is called.
	 * 
	 * @param algorithm the Algorithm to block from running
	 */
	public void block(Algorithm algorithm);
	
	/**
	 * Unblocks the given Algorithm from running. If block(Algorithm) was
	 * called on the given algorithm, this will unblock it and place it
	 * back in its appropriate position in the queue
	 * 
	 * @param algorithm the blocked Algorithm to unblock
	 */
	public void unblock(Algorithm algorithm);
}