/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 28, 2005 at Indiana University.
 */
package edu.iu.iv.core;

import edu.iu.iv.core.algorithm.Algorithm;

import java.util.Calendar;


/**
 * Interface for SchedulerListeners to conform to.  This will allow things
 * to be notified when a new item is scheduled in the IVC Scheduler.
 *
 * @author Team IVC
 */
public interface SchedulerListener {
    /**
     * Notify that the given Algorithm has been moved into the running queue
     * at the given index in the queue
     *
     * @param algorithm the Algorithm that was moved into the running queue
     * @param index the index in the running queue of the given Algorithm
     */
    public void algorithmMovedToRunningQueue(Algorithm algorithm, int index);

    /**
     * Notify that the given Algorithm was scheduled at the given time, with
     * the given index in the running queue. This method should be used for
     * items that are scheduled to run immediatly, rather than at a specific
     * time, as opposed to <code>algorithmScheduled(Algorithm, Calendar)</code>.
     *
     * @param algorithm the Algorithm that was scheduled
     * @param time the date/time at which this Algorithm was scheduled
     * @param index the index in the running queue of this newly scheduled item
     */
    public void algorithmScheduled(Algorithm algorithm, Calendar time, int index);

    /**
     * Notify that the given Algorithm was scheduled at the given time. 
     * This method should be used for items that are scheduled to run at a
     * given time, rather than immediately, as opposed to 
     * <code>algorithmScheduled(Algorithm, Calendar, int)</code>.
     *
     * @param algorithm the Algorithm that was scheduled
     * @param time the date/time at which this Algorithm was scheduled
     */
    public void algorithmScheduled(Algorithm algorithm, Calendar time);

    /**
     * Notify that the given Algorithm has started execution.
     *
     * @param algorithm the Algorithm that has started execution.
     */
    public void algorithmStarted(Algorithm algorithm);

    /**
     * Notify that the given Algorith has finished execution.
     *
     * @param algorithm the Algorithm that has finished execution
     */
    public void algorithmFinished(Algorithm algorithm);

    /**
     * Notify that the given Algorithm has had its execution halted due
     * to error.
     *
     * @param algorithm the Algorithm that has had its execution halted due to
     * error.
     */
    public void algorithmError(Algorithm algorithm);

    /**
     * Notify that the given Algorithm has moved up one index in the
     * running queue of Algorithms.
     *
     * @param algorithm the Algorithm that has moved up one index
     */
    public void algorithmMovedUpInRunningQueue(Algorithm algorithm);

    /**
     * Notify that the given Algorithm has moved down one index in the
     * running queue of Algorithms.
     *
     * @param algorithm the Algorithm that has moved down one index
     */
    public void algorithmMovedDownInRunningQueue(Algorithm algorithm);
}
