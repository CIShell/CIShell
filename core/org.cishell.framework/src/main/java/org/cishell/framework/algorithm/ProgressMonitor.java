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
 * of progress, notification of cancellation, notification of pausing, and 
 * description of current work during execution. Except for the setter methods, 
 * the methods are generally only called by the algorithm with the CIShell 
 * application providing the progress monitor implementation.
 */
public interface ProgressMonitor {
	/**
	 * A monitor with empty methods for use by algorithms when no 
	 * ProgressMonitor has been given to it. This helps to eliminate spurious
	 * <code>null</code> checks to ensure the progress monitor is not 
	 * <code>null</code>. 
	 */
	public static final ProgressMonitor NULL_MONITOR = new ProgressMonitor() {
		public void describeWork(String currentWork) {}
		public void done() {}
		public boolean isCanceled() {return false;}
		public boolean isPaused() {return false;}
		public void setCanceled(boolean value) {}
		public void setPaused(boolean value) {}
		public void start(int capabilities, int totalWorkUnits) {}
		public void start(int capabilities, double totalWorkUnits) {}
		public void worked(int work) {}
		public void worked(double work) {}
	};
		
	/**
	 * Capability constant specifying that this algorithm can 
	 * update its work progress (value is 1&lt;&lt;1)
	 */
	public static final int WORK_TRACKABLE = 1 << 1;
	
	/**
	 * Capability constant specifying that this algorithm can 
	 * be cancelled (value is 1&lt;&lt;2)
	 */
	public static final int CANCELLABLE = 1 << 2;
	
	/**
	 * Capability constant specifying that this algorithm can 
	 * be paused (value is 1&lt;&lt;3)
	 */
	public static final int PAUSEABLE = 1 << 3;
	
    /**
     * Notifies the start of execution of the algorithm in addition to 
     * revealing how many work units will be used 
     * 
     * @param capabilities   An OR'ed int that tells the monitor what the 
     *                       algorithm is capable of with respect to the 
     *                       monitor. The OR'ed values are taken from the int
     *                       constants specified in this interface.
     * @param totalWorkUnits The number of work units, -1 if the  
     *                       algorithm does not provide progress information
     */
    public void start(int capabilities, int totalWorkUnits);
    public void start(int capabilities, double totalWorkUnits);
    
    /**
     * Notifies that a certain number of units of work has been completed
     * 
     * @param work The number of units of work completed, total.
     *             
     */
    public void worked(int work);
    public void worked(double work);
    
    /**
     * The algorithm is finished executing
     */
    public void done();
    
    /**
     * Sets or clears a flag for cancellation of this algorithm's execution.
     * An algorithm developer can ignore or clear this flag if it cannot stop 
     * midstream. This is one of the methods that can be called by something 
     * other than the algorithm.
     * 
     * @param value Set or clear the cancellation request
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
     * Sets or clears a flag for pausing of this algorithm's execution. An
     * algorithm developer can ignore or clear this flag if it cannot pause 
     * midstream. This is one of the methods that can be called by something 
     * other than the algorithm.
     * 
     * @param value Set or clear the pause request
     */
    public void setPaused(boolean value);
    
    /**
     * Returns whether pausing of algorithm execution is requested. An
     * algorithm that can be paused should poll this method when convenient
     * to see if it should pause.
     *
     * @return Whether pausing of algorithm execution is requested
     */
    public boolean isPaused();
    
    /**
     * Method to describe what the algorithm is currently doing for the benefit
     * of the users of the algorithm as it progresses during execution
     * 
     * @param currentWork A short description of the current work the algorithm
     *                    is doing
     */
    public void describeWork(String currentWork);
}
