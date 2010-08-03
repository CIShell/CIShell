/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 19, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.app.service.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.reference.app.service.scheduler.AlgorithmTask.AlgorithmState;
import org.osgi.framework.ServiceReference;

/**
 * A simple scheduler based on {@link java.util.Timer}.
 * 
 * <b>Implementation Notes:</b>
 * <ul>
 * <li> The scheduler starts a TimerTask which runs as long as the scheduler is
 * running. This scheduler TimerTask checks its list of algorithms, removes
 * algorithms that have finished running and sets new ones running if
 * appropriate. It decides whether or not to run an algorithm by checking their
 * state and schedules them to run if their scheduled time is the current time
 * or has already passed. This TimerTask is scheduler on a Timer of its own and
 * is set to run periodically. New algorithms are scheduled by giving them to
 * the scheduler TimerTask task.</li>
 * <li> Algorithms themselves inform the scheduler listener of events. However,
 * only the scheduler TimerTask has access to these tasks and only one scheduler
 * listener is allowed per algorithm. This listener happens to be the scheduler
 * TimerTask, which informs the scheduler of events, which in turn informs all
 * the other listeners. </li>
 * <li>The scheduler task also enforces a limit on the number of algorithms run
 * if so desired. This limit overrides algorithm schedule so that setting a
 * limit too low may cause the algorithm run queue to be full of waiting
 * algorithms. However, this limit can be safely changed at runtime.</li>
 * <li>Running algorithms can neither be paused nor stopped reliably so such
 * methods have either been deprecated and/or they will throw
 * NotImplementedException.</li>
 * <li> By default this, scheduler allows an unlimited number of algorithms to
 * run. </li>
 * <li> This scheduler TimerTask runs continuously even if the algorithm queue
 * is empty. If that's not desired, modify the code such that an empty queue
 * causes the scheduler thread to sleep and to be woken up in response to events
 * such as a new algorithm being scheduled.</li>
 * <li>If more control or more UI integration is required, consider
 * implementing a scheduler based on the Eclipse Jobs API. </li>
 * <li>If more reliability is required (such as making guarantees that an
 * algorithm will absolutely run at the specified time), do not set limits on
 * the number of algorithms to be run. Also if more features are needed in the
 * future such as the ability to serialize schedules, or run algorithms based on
 * combinations of conditions or provide the ability to veto the running of an
 * algorithm, consider using Quartz http://www.opensymphony.com/quartz/ </li>
 * </ul>
 * 
 */
public class SchedulerServiceImpl implements SchedulerService {
    /**
     * This timer runs the algorithm scheduling task.
     */
    private Timer schedulerTimer;

    /**
     * The task which schedules algorithms to run on the _algRunningTimer.
     */
    private AlgorithmSchedulerTask algorithmSchedulerTask;

    /**
     * Convenience object for informing all the schedulers.
     */
    private SchedulerListenerInformer schedulerListenerInformer;

    private boolean isShutDown = true;
    
    public SchedulerServiceImpl() {
        initialize();
    }

    public SchedulerServiceImpl(int maxSimultaneousAlgorithm) {
        this();
        this.algorithmSchedulerTask.setMaxSimultaneousAlgorithms(maxSimultaneousAlgorithm);
        this.isShutDown = false;
    }

    public synchronized final void setMaxSimultaneousAlgorithms(int max) {
        this.algorithmSchedulerTask.setMaxSimultaneousAlgorithms(max);
    }

    private final void initialize() {
        this.schedulerTimer = new Timer(true);
        this.schedulerListenerInformer = new SchedulerListenerInformer();
        this.algorithmSchedulerTask = new AlgorithmSchedulerTask(this.schedulerListenerInformer);
        this.schedulerTimer.schedule(this.algorithmSchedulerTask, 0L, 500L);
    }

    public synchronized final void shutDown() {
        this.algorithmSchedulerTask.cancel();
        this.schedulerTimer.cancel();
        this.isShutDown = true;
    }

    public final boolean isEmpty() {
        return this.algorithmSchedulerTask.isEmpty();
    }

    public final boolean isRunning() {
        return this.algorithmSchedulerTask.isRunning();
    }

    public final int numRunning() {
        return this.algorithmSchedulerTask.numRunning();
    }

    public final boolean isShutDown() {
        return this.isShutDown;
    }

    public boolean reschedule(Algorithm algorithm, Calendar newTime) {
        // Shaky method. Ideally this is done at a higher level. But still, here goes...
        ServiceReference reference = this.algorithmSchedulerTask.getServiceReference(algorithm);
        boolean canReschedule = false;

        try {
            AlgorithmState algorithmState =
            	this.algorithmSchedulerTask.getAlgorithmState(algorithm);
            
            // Cannot reschedule running algorithms.
            if (algorithmState.equals(AlgorithmState.RUNNING)) {
                canReschedule = false;
            } else if (algorithmState.equals(AlgorithmState.STOPPED)) {
                this.algorithmSchedulerTask.purgeFinished();
                this.algorithmSchedulerTask.schedule(algorithm, reference, newTime);
                canReschedule = true;
            } else if (algorithmState.equals(AlgorithmState.NEW)) {
                this.algorithmSchedulerTask.cancel(algorithm);
                this.algorithmSchedulerTask.schedule(algorithm, reference, newTime);
            } else {
                throw new IllegalStateException("Encountered an invalid state: " + algorithmState);
            }
        } catch (NoSuchElementException e) {
            this.algorithmSchedulerTask.schedule(algorithm, reference, newTime);
            canReschedule = true;
        }
        return canReschedule;
    }

    public void runNow(Algorithm algorithm, ServiceReference reference) {
        // There is currently no difference between this one and
        // schedule(Algorithm, reference).
        schedule(algorithm, reference);
    }

    public void schedule(Algorithm algorithm, ServiceReference reference) {
        schedule(algorithm, reference, Calendar.getInstance());
    }

    public void schedule(Algorithm algorithm, ServiceReference reference, Calendar time) {
        this.algorithmSchedulerTask.schedule(algorithm, reference, time);
    }

    public boolean unschedule(Algorithm algorithm) {
        return this.algorithmSchedulerTask.cancel(algorithm);
    }

    public void addSchedulerListener(SchedulerListener listener) {
        this.schedulerListenerInformer.addSchedulerListener(listener);
    }

    public void removeSchedulerListener(SchedulerListener listener) {
        this.schedulerListenerInformer.removeSchedulerListener(listener);
    }

    public synchronized void clearSchedule() {
        this.algorithmSchedulerTask.cancel();
        this.schedulerTimer.cancel();
        
        this.schedulerTimer = new Timer(true);
        this.algorithmSchedulerTask = new AlgorithmSchedulerTask(this.schedulerListenerInformer);
        // TODO: Make constants for these magic numbers.
        this.schedulerTimer.schedule(this.algorithmSchedulerTask, 0L, 500L);
        
        this.schedulerListenerInformer.schedulerCleared();
    }

    public Algorithm[] getScheduledAlgorithms() {
        return this.algorithmSchedulerTask.getScheduledAlgorithms();
    }

    public Calendar getScheduledTime(Algorithm algorithm) {
        return this.algorithmSchedulerTask.getScheduledTime(algorithm);
    }

    public ServiceReference getServiceReference(Algorithm algorithm) {
        return this.algorithmSchedulerTask.getServiceReference(algorithm);
    }

    public void setRunning(boolean isRunning) {
        this.algorithmSchedulerTask.setRunning(isRunning);
        this.schedulerListenerInformer.schedulerRunStateChanged(isRunning);
    }
}

/**
 * Utility class to handle all the listener informing tasks. Strictly speaking,
 * we don't want listener inform methods to hold up the rest of the system. In
 * other words, its possible that a listener starts doing some big operation as
 * a response to some state change of an algorithm, tying up the whole system.
 * So to prevent this, a better solution would be for each listener informer
 * sequence to be run in a separate thread.
 * 
 * @author Team IVC
 */
class SchedulerListenerInformer implements SchedulerListener {
    private List<SchedulerListener> schedulerListeners;

    public SchedulerListenerInformer() {
        this.schedulerListeners = new ArrayList<SchedulerListener>();
    }
    
    public void addSchedulerListener(SchedulerListener listener) {
        this.schedulerListeners.add(listener);
    }
    
    public void removeSchedulerListener(SchedulerListener listener) {
        this.schedulerListeners.remove(listener);
    }
    
    public void algorithmScheduled(Algorithm algorithm, Calendar time) {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.algorithmScheduled(algorithm, time);
        }
    }
    
    public synchronized void algorithmStarted(Algorithm algorithm) {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.algorithmStarted(algorithm);
        }
    }

    public void algorithmError(Algorithm algorithm, Throwable error) {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.algorithmError(algorithm, error);
        }
    }

    public void algorithmFinished(Algorithm algorithm, Data[] createdDM) {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.algorithmFinished(algorithm, createdDM);
        }
    }

    public void algorithmRescheduled(Algorithm algorithm, Calendar time) {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.algorithmRescheduled(algorithm, time);
        }
    }

    public void algorithmUnscheduled(Algorithm algorithm) {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.algorithmUnscheduled(algorithm);
        }
    }

    public void schedulerCleared() {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.schedulerCleared();
        }
    }

    public void schedulerRunStateChanged(boolean isRunning) {
        for (SchedulerListener schedulerListener : this.schedulerListeners) {
            schedulerListener.schedulerRunStateChanged(isRunning);
        }
    }
}

class AlgorithmSchedulerTask extends TimerTask implements SchedulerListener {
	public static final int AS_MANY_SIMULTANEOUS_ALGORITHMS_AS_NEEDED = -1;
    private Map<Algorithm, AlgorithmTask> tasksByAlgorithms;
    private Map<Algorithm, ServiceReference> serviceReferencesByAlgorithms;
    private volatile boolean isRunning = true;
    private volatile int runningTaskCount = 0;
    private SchedulerListener schedulerListener;
    private int maxSimultaneousAlgorithms = AS_MANY_SIMULTANEOUS_ALGORITHMS_AS_NEEDED;

    /**
     * Maximum number of algorithms allowed to run simultaneously. This value
     * can be changed at runtime without any problems. Negative values are
     * interpreted to mean 'no limit'.
     * 
     * @param max
     *            The maximum number of algorithms that can be simultaneously run.
     */
    public synchronized final void setMaxSimultaneousAlgorithms(final int max) {
        if (max < -1) {
            this.maxSimultaneousAlgorithms = AS_MANY_SIMULTANEOUS_ALGORITHMS_AS_NEEDED;
        } else {
            this.maxSimultaneousAlgorithms = max;
        }
    }
    
    public synchronized Algorithm[] getScheduledAlgorithms() {
        return this.tasksByAlgorithms.keySet().toArray(new Algorithm[0]);
    }

    public synchronized final boolean isEmpty() {
        return this.tasksByAlgorithms.size() == 0;
    }

    public synchronized final int numRunning() {
        return this.runningTaskCount;
    }

    public AlgorithmSchedulerTask(SchedulerListener listener) {
        this.tasksByAlgorithms =
        	Collections.synchronizedMap(new HashMap<Algorithm, AlgorithmTask>());
        this.serviceReferencesByAlgorithms = new HashMap<Algorithm, ServiceReference>();
        this.setSchedulerListener(listener);
    }

    public synchronized final void setSchedulerListener(SchedulerListener listener) {
        this.schedulerListener = listener;
    }
    
    public final ServiceReference getServiceReference(Algorithm algorithm) {
    	return this.serviceReferencesByAlgorithms.get(algorithm);
    }
    
    public synchronized final Calendar getScheduledTime(Algorithm algorithm) {
        AlgorithmTask task = this.tasksByAlgorithms.get(algorithm);

        if (task != null) {
            return task.getScheduledTime();
        } else {
            return null;
        }
    }

    public synchronized final boolean cancel(Algorithm algorithm) {
        AlgorithmTask task = this.tasksByAlgorithms.get(algorithm);

        if (task == null) {
            return false;
        }

        // The algorithm will run till the end and
        // then stop so there's no real way to cancel running algorithms.
        // Clients should always check the state of an algorithm before trying
        // to reschedule an existing algorithm.
        return task.cancel();
    }

    public synchronized final void schedule(Algorithm alg, ServiceReference ref, Calendar time) {
        AlgorithmTask task = this.tasksByAlgorithms.get(alg);
        // If alg already exists, do some checks...
        if (task != null) {
            AlgorithmState state = task.getState();
            // If its still running, we can't schedule it again.
            if (state.equals(AlgorithmState.RUNNING)) {
                throw new RuntimeException(
                        "Cannot schedule running algorithm. Check state of algorithm first.");
            }
            // If its new or waiting to run, we refuse to schedule it to force
            // user to explicitly
            // cancel and reschedule.
            else if (state.equals(AlgorithmState.NEW)) {
                throw new RuntimeException(
                        "Algorithm is already scheduled to run. Cancel existing schedule first.");
            }
            else if (state.equals(AlgorithmState.STOPPED)) {
                // If it was stopped but not cleaned up yet, clean it up
                purgeFinished();
            }
            else {
                throw new IllegalStateException(
                        "State was not one of allowable states: " + state);
            }
        }
        //this._algMap.put(alg, new AlgorithmTask(alg, ref, time, this));
        new AlgorithmTask(alg, ref, time, this);
    }

    public synchronized final int getMaxSimultaneousAlgs() {
        return this.maxSimultaneousAlgorithms;
    }
    
    public synchronized final void registerAlgorithmTask(Algorithm algorithm, AlgorithmTask algorithmTask) {
    	this.serviceReferencesByAlgorithms.put(algorithm, algorithmTask.getServiceReference());
        this.tasksByAlgorithms.put(algorithm, algorithmTask);    	
    }

    /**
     * @param algorithm
     *            The algorithm whose state we want to query.
     * @return State of the specified algorithm.
     */
    public synchronized final AlgorithmState getAlgorithmState(Algorithm algorithm) {
        AlgorithmTask task = this.tasksByAlgorithms.get(algorithm);
        if (task == null)
            throw new NoSuchElementException("Algorithm doesn't exist.");
        return task.getState();
    }

    /**
     * Removes all finished algorithms from the queue.
     */
    public synchronized final void purgeFinished() {
        synchronized (this) {
            Iterator<Map.Entry<Algorithm, AlgorithmTask>> entries =
            	this.tasksByAlgorithms.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry<Algorithm, AlgorithmTask> entry = entries.next();
                AlgorithmTask task = entry.getValue();

                if (task.getState() == AlgorithmState.STOPPED) {
                    entries.remove();
                    this.serviceReferencesByAlgorithms.remove(entry.getKey());
                }
            }
        }
    }

    private synchronized final boolean limitReached() {
        return
        	(this.maxSimultaneousAlgorithms != AS_MANY_SIMULTANEOUS_ALGORITHMS_AS_NEEDED) &&
        	(this.runningTaskCount >= this.maxSimultaneousAlgorithms);
    }
    
    
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
    public boolean isRunning() {
        return this.isRunning;
    }
    
    public void run() {
        if (this.isRunning) {
            synchronized (this) {
                // If we are running the max allowable, wait until next turn.
                Date now = Calendar.getInstance().getTime();
                // Iterate through algorithms.
                Collection<AlgorithmTask> tasks = this.tasksByAlgorithms.values();

                for (AlgorithmTask task : tasks) {
                    if (limitReached()) {
                        return;
                    }

                    if ((task.getState() == AlgorithmState.NEW)
                            && now.compareTo(task.getScheduledTime().getTime()) >= 0) {
                        // Run immediately.
                        task.start();
                    }
                }
            }
        }
    }
    
    public synchronized void algorithmScheduled(Algorithm algorithm, Calendar time) {
        this.schedulerListener.algorithmScheduled(algorithm, time);
    }

    public synchronized void algorithmStarted(Algorithm algorithm) {
        this.runningTaskCount++;
        this.schedulerListener.algorithmStarted(algorithm);
    }

    public synchronized void algorithmError(Algorithm algorithm, Throwable error) {
        this.runningTaskCount--;
        this.schedulerListener.algorithmError(algorithm, error);
        purgeFinished();
    }

    public synchronized void algorithmFinished(Algorithm algorithm, Data[] createdDM) {
        this.runningTaskCount--;
        this.schedulerListener.algorithmFinished(algorithm, createdDM);
        purgeFinished();
    }

    public synchronized void algorithmRescheduled(Algorithm algorithm, Calendar time) {
        this.schedulerListener.algorithmRescheduled(algorithm, time);
        
    }

    public synchronized void algorithmUnscheduled(Algorithm algorithm) {
        this.schedulerListener.algorithmUnscheduled(algorithm);
    }

    public synchronized void schedulerCleared() {
        this.schedulerListener.schedulerCleared();
    }

    public synchronized void schedulerRunStateChanged(boolean isRunning) {
        this.schedulerListener.schedulerRunStateChanged(isRunning);
    }
}

/**
 * This task keeps all algorithm related state within itself, thus saving us
 * from using a ton of different data structures to keep track of all state.
 * 
 * It also assumes responsibility for informing scheduler listeners itself.
 * 
 * Clients should never have access to this class directly. Of course, even if
 * they do, there really isn't anyway they can change the state of this thing.
 * 
 * @author Team CIShell
 */
// May 8, 2006 7:19:00 PM Shashikant Penumarthy: Initial implementation.
//July 19, 2006 10:45:00 AM Bruce Herr: Ported to new CIShell
class AlgorithmTask implements Runnable {
    /**
     * The states in which algorithm tasks can exist.
     * 
     * @author Team IVC
     */
	private volatile boolean isCanceled = false;
	private final Algorithm algorithm;

    /* NOTE: TimerTask keeps its own schedule variable which can be retrieved using
     *  scheduledExecutionTime() method. We don't use that here.
     */
    private final Calendar scheduledTime;
    private final ServiceReference serviceReference;
    private volatile AlgorithmState state;

    // Execution status of the algorithm (i.e.) return value.
    private Data[] result;

    // The exception thrown, if an algorithm had one while executing.
    private Exception exceptionThrown;

    // Deliberately allow only one listener. Its not the algorithms job to do all the informing.
    private SchedulerListener schedulerListener;

    public synchronized final boolean cancel() {
        if (this.isCanceled) {
            return true;
    	}

        if (this.state.equals(AlgorithmState.RUNNING)) {
            return false;
        }

        this.state = AlgorithmState.STOPPED;
        this.isCanceled = true;

        return this.isCanceled;
    }

    public synchronized final void start() {
        if (this.isCanceled) {
            return;
        }

        setState(AlgorithmState.RUNNING);
        new Thread(this).start();
    }

    public AlgorithmTask(
    		Algorithm algorithm,
    		ServiceReference serviceReference,
    		Calendar scheduledTime,
    		AlgorithmSchedulerTask algorithmSchedulerTask) {
        this.algorithm = algorithm;
        this.serviceReference = serviceReference;
        this.scheduledTime = scheduledTime;
        this.schedulerListener = algorithmSchedulerTask;

        algorithmSchedulerTask.registerAlgorithmTask(algorithm, this);
        init();
    }

    public synchronized final Calendar getScheduledTime() {
        /* Do a defensive copy because we don't want clients changing the time using
         *  this reference!
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.scheduledTime.getTime());

        return calendar;
    }
    
    public synchronized final ServiceReference getServiceReference() {
        return this.serviceReference;
    }

    private final void init() {
        this.result = null;
        setState(AlgorithmState.NEW);
    }

    public synchronized final Data[] getResult() {
        return this.result;
    }

    private synchronized final void setState(AlgorithmState state) {
        this.state = state;
        // Inform listeners.
        if (this.schedulerListener != null) {
        	this.state.performAction(
        		algorithm,
        		this.schedulerListener,
        		this.scheduledTime,
        		getResult(),
        		this.exceptionThrown);
        	this.isCanceled = this.state.isCanceledNow();
        }
    }

    public synchronized final AlgorithmState getState() {
        return this.state;
    }

    public void run() {
        try {
            this.result = this.algorithm.execute();
        } catch (Exception e) {
            this.exceptionThrown = e;
            setState(AlgorithmState.ERROR);
        } finally {
            setState(AlgorithmState.STOPPED);
        }
    }

    static class AlgorithmState {
    	/** New algorithms are in this state. */
        public static final AlgorithmState NEW = new AlgorithmState("NEW", false) {
        	public void performAction(
        			Algorithm algorithm,
        			SchedulerListener schedulerListener,
        			Calendar scheduledTime,
        			Data[] result,
    				Exception exceptionThrown) {
        		schedulerListener.algorithmScheduled(algorithm, scheduledTime);
        	}
        };

        /** Running algorithms are in this state. */
        public static final AlgorithmState RUNNING = new AlgorithmState("RUNNING", false) {
        	public void performAction(
        			Algorithm algorithm,
        			SchedulerListener schedulerListener,
        			Calendar scheduledTime,
        			Data[] result,
    				Exception exceptionThrown) {
        		schedulerListener.algorithmStarted(algorithm);
        	}
        };
        /** Algorithms either cancelled or finished are in this state. */
        public static final AlgorithmState STOPPED = new AlgorithmState("STOPPED", true) {
        	public void performAction(
        			Algorithm algorithm,
        			SchedulerListener schedulerListener,
        			Calendar scheduledTime,
        			Data[] result,
    				Exception exceptionThrown) {
        		schedulerListener.algorithmFinished(algorithm, result);
        	}
        };
        /** Algorithm had an exceptionThrown while executing */
        public static final AlgorithmState ERROR = new AlgorithmState("ERROR", true) {
        	public void performAction(
        			Algorithm algorithm,
        			SchedulerListener schedulerListener,
        			Calendar scheduledTime,
        			Data[] result,
    				Exception exceptionThrown) {
        		schedulerListener.algorithmError(algorithm, exceptionThrown);
        	}
        };

        private String name;
        private boolean isCanceled;

        public AlgorithmState(String name, boolean isCanceled) {
            this.name = name;
            this.isCanceled = isCanceled;
        }

        public final boolean equals(Object object) {
            if (!(object instanceof AlgorithmState)) {
                return false;
            }

            AlgorithmState state = (AlgorithmState) object;

            return state.name.compareTo(name) == 0;
        }

        public void performAction(
        		Algorithm algorithm,
        		SchedulerListener schedulerListener,
        		Calendar scheduledTime,
    			Data[] result,
    			Exception exceptionThrown) {
        	throw new IllegalStateException("Encountered illegal algorithm state: " + this);
        }

        public boolean isCanceledNow() {
        	return this.isCanceled;
        }
    }
}
