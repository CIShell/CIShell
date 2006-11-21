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
import org.cishell.reference.app.service.scheduler.AlgorithmTask.STATE;
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
 * @author Shashikant Penumarthy
 * @author Bruce Herr (bh2@bh2.net)
 */
// May 8, 2006 7:05:32 PM Shashikant Penumarthy: Initial Implementation
// July 19, 2006 10:30:00 AM Bruce Herr: Ported to new CIShell
public class SchedulerServiceImpl implements SchedulerService {
    /**
     * This timer runs the algorithm scheduling task.
     */
    private Timer _schedulerTimer;

    /**
     * The task which schedules algorithms to run on the _algRunningTimer.
     */
    private AlgSchedulerTask _algSchedulerTask;

    /**
     * Convenience object for informing all the schedulers.
     */
    private SchedulerListenerInformer _schedulerListenerInformer;
    
    public SchedulerServiceImpl() {
        _initialize();
    }

    public SchedulerServiceImpl(int maxSimultaneousAlgsLimit) {
        this();
        _algSchedulerTask.setMaxSimultaneousAlgs(maxSimultaneousAlgsLimit);
        _isShutDown = false;
    }

    public synchronized final void setMaxSimultaneousAlgs(int max) {
        _algSchedulerTask.setMaxSimultaneousAlgs(max);
    }

    private final void _initialize() {
        _schedulerTimer = new Timer(true);
        _schedulerListenerInformer = new SchedulerListenerInformer();
        _algSchedulerTask = new AlgSchedulerTask(_schedulerListenerInformer);
        _schedulerTimer.schedule(_algSchedulerTask, 0L, 500L);
    }

    public synchronized final void shutDown() {
        _algSchedulerTask.cancel();
        _schedulerTimer.cancel();
        _isShutDown = true;
    }

    public final boolean isEmpty() {
        return _algSchedulerTask.isEmpty();
    }

    public final boolean isRunning() {
        return _algSchedulerTask.isRunning();
    }

    public final int numRunning() {
        return _algSchedulerTask.numRunning();
    }

    private boolean _isShutDown = true;

    public final boolean isShutDown() {
        return _isShutDown;
    }

    public boolean reschedule(Algorithm algorithm, Calendar newTime) {
        // Shaky method. Ideally this is done at a higher level. But still, here
        // goes...
        ServiceReference ref = _algSchedulerTask.getServiceReference(algorithm);
        boolean status = false;
        try {
            STATE algState = _algSchedulerTask.getAlgorithmState(algorithm);
            
            // Cannot reschedule running algs
            if (algState.equals(STATE.RUNNING)) {
                status = false;
            }
            else if (algState.equals(STATE.STOPPED)) {
                _algSchedulerTask.purgeFinished();
                _algSchedulerTask.schedule(algorithm, ref, newTime);
                status = true;
            }
            else if (algState.equals(STATE.NEW)) {
                _algSchedulerTask.cancel(algorithm);
                _algSchedulerTask.schedule(algorithm, ref, newTime);
            }
            else {
                throw new IllegalStateException(
                        "Encountered an invalid state: " + algState);
            }
        } catch (NoSuchElementException nsee) {
            _algSchedulerTask.schedule(algorithm, ref, newTime);
            status = true;
        }
        return status;
    }

    public void runNow(Algorithm algorithm, ServiceReference ref) {
        // There is currently no difference between this one and
        // schedule(Algorithm, ref).
        schedule(algorithm, ref);
    }

    public void schedule(Algorithm algorithm, ServiceReference ref) {
        schedule(algorithm, ref, Calendar.getInstance());
    }

    public void schedule(Algorithm algorithm, ServiceReference ref, Calendar time) {
        _algSchedulerTask.schedule(algorithm, ref, time);
    }

    public boolean unschedule(Algorithm algorithm) {
        return _algSchedulerTask.cancel(algorithm);
    }

    public void addSchedulerListener(SchedulerListener listener) {
        _schedulerListenerInformer.addSchedulerListener(listener);
    }

    public void removeSchedulerListener(SchedulerListener listener) {
        _schedulerListenerInformer.removeSchedulerListener(listener);
    }

    public synchronized void clearSchedule() {
        _algSchedulerTask.cancel();
        _schedulerTimer.cancel();
        
        _schedulerTimer = new Timer(true);
        _algSchedulerTask = new AlgSchedulerTask(_schedulerListenerInformer);
        _schedulerTimer.schedule(_algSchedulerTask, 0L, 500L);
        
        _schedulerListenerInformer.schedulerCleared();
    }

    public Algorithm[] getScheduledAlgorithms() {
        return _algSchedulerTask.getScheduledAlgorithms();
    }

    public Calendar getScheduledTime(Algorithm algorithm) {
        return _algSchedulerTask.getScheduledTime(algorithm);
    }

    public ServiceReference getServiceReference(Algorithm algorithm) {
        return _algSchedulerTask.getServiceReference(algorithm);
    }

    public void setRunning(boolean isRunning) {
        _algSchedulerTask.setRunning(isRunning);
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

    private List _schedulerListeners;

    public SchedulerListenerInformer() {
        _schedulerListeners = new ArrayList();
    }
    
    public void addSchedulerListener(SchedulerListener listener) {
        _schedulerListeners.add(listener);
    }
    
    public void removeSchedulerListener(SchedulerListener listener) {
        _schedulerListeners.remove(listener);
    }
    
    public void algorithmScheduled(Algorithm algorithm, Calendar time) {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.algorithmScheduled(algorithm, time);
        }
    }
    
    public synchronized void algorithmStarted(Algorithm algorithm) {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.algorithmStarted(algorithm);
        }
    }

    public void algorithmError(Algorithm algorithm, Throwable error) {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.algorithmError(algorithm, error);
        }
    }

    public void algorithmFinished(Algorithm algorithm, Data[] createdDM) {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.algorithmFinished(algorithm, createdDM);
        }
    }

    public void algorithmRescheduled(Algorithm algorithm, Calendar time) {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.algorithmRescheduled(algorithm, time);
        }
    }

    public void algorithmUnscheduled(Algorithm algorithm) {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.algorithmUnscheduled(algorithm);
        }
    }

    public void schedulerCleared() {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.schedulerCleared();
        }
    }

    public void schedulerRunStateChanged(boolean isRunning) {
        for (Iterator iter = _schedulerListeners.iterator() ; iter.hasNext() ; ) {
            SchedulerListener sl = (SchedulerListener) iter.next() ;
            sl.schedulerRunStateChanged(isRunning);
        }
    }
}

class AlgSchedulerTask extends TimerTask implements SchedulerListener {

    private Map _algMap;
    private volatile boolean _running = true;

    // Default allow as many as needed
    private int _maxSimultaneousAlgs = -1;

    /**
     * Maximum number of algorithms allowed to run simultaneously. This value
     * can be changed at runtime without any problems. Negative values are
     * interpreted to mean 'no limit'.
     * 
     * @param max
     *            The maximum number of algorithms that can be simultaneously
     *            run.
     */
    public synchronized final void setMaxSimultaneousAlgs(final int max) {
        if (max < -1)
            this._maxSimultaneousAlgs = -1;
        else
            this._maxSimultaneousAlgs = max;
    }
    
    public synchronized Algorithm[] getScheduledAlgorithms() {
        return (Algorithm[]) _algMap.keySet().toArray(new Algorithm[0]);
    }

    public synchronized final boolean isEmpty() {
        return _algMap.size() == 0;
    }

    public synchronized final int numRunning() {
        return _numRunning;
    }

    private SchedulerListener _schedulerListener;

    public AlgSchedulerTask(SchedulerListener listener) {
        _algMap = Collections.synchronizedMap(new HashMap());
        setSchedulerListener(listener);
    }

    public synchronized final void setSchedulerListener(SchedulerListener listener) {
        _schedulerListener = listener;
    }
    
    public synchronized final ServiceReference getServiceReference(Algorithm algorithm) {
        AlgorithmTask task = (AlgorithmTask)_algMap.get(algorithm);
        if (task != null) {
            return task.getServiceReference();
        } else {
            return null;
        }
    }
    
    public synchronized final Calendar getScheduledTime(Algorithm algorithm) {
        AlgorithmTask task = (AlgorithmTask)_algMap.get(algorithm);
        if (task != null) {
            return task.getScheduledTime();
        } else {
            return null;
        }
    }

    public synchronized final boolean cancel(Algorithm alg) {
        AlgorithmTask task = (AlgorithmTask) this._algMap.get(alg);
        if (task == null)
            return false;
        // The algorithm will run till the end and
        // then stop so there's no real way to cancel running algorithms.
        // Clients should always check the state of an algorithm before trying
        // to reschedule an existing algorithm.
        return task.cancel();
    }

    public synchronized final void schedule(Algorithm alg, ServiceReference ref, Calendar time) {
        AlgorithmTask task = (AlgorithmTask) this._algMap.get(alg);
        // If alg already exists, do some checks...
        if (task != null) {
            STATE state = task.getState();
            // If its still running, we can't schedule it again.
            if (state.equals(STATE.RUNNING)) {
                throw new RuntimeException(
                        "Cannot schedule running algorithm. Check state of algorithm first.");
            }
            // If its new or waiting to run, we refuse to schedule it to force
            // user to explicitly
            // cancel and reschedule.
            else if (state.equals(STATE.NEW)) {
                throw new RuntimeException(
                        "Algorithm is already scheduled to run. Cancel existing schedule first.");
            }
            else if (state.equals(STATE.STOPPED)) {
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
        return this._maxSimultaneousAlgs;
    }
    
    public synchronized final void registerAlgorithmTask(Algorithm algorithm, AlgorithmTask algorithmTask) {
        this._algMap.put(algorithm, algorithmTask);    	
    }

    /**
     * @param alg
     *            The algorithm whose state we want to query.
     * @return State of the specified algorithm.
     */
    public synchronized final STATE getAlgorithmState(Algorithm alg) {
        AlgorithmTask task = (AlgorithmTask) this._algMap.get(alg);
        if (task == null)
            throw new NoSuchElementException("Algorithm doesn't exist.");
        return task.getState();
    }

    /**
     * Removes all finished algorithms from the queue.
     */
    public synchronized final void purgeFinished() {
        synchronized (this) {
            Iterator iter = this._algMap
                    .entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                AlgorithmTask task = (AlgorithmTask) entry.getValue();
                if (task.getState() == STATE.STOPPED)
                    iter.remove();
            }
        }
    }

    private synchronized final boolean _limitReached() {
        return (_maxSimultaneousAlgs != -1)
                && (_numRunning >= _maxSimultaneousAlgs);
    }
    
    
    public void setRunning(boolean isRunning) {
        _running = isRunning;
    }
    
    public boolean isRunning() {
        return _running;
    }
    
    public void run() {
        if (_running) {
            synchronized (this) {
                // If we are running the max allowable, wait until next turn.
                Date now = Calendar.getInstance().getTime();
                // Iterate through algorithms.
                Collection tasks = this._algMap.values();
                for (Iterator iter = tasks.iterator() ; iter.hasNext() ;) {
                    AlgorithmTask task = (AlgorithmTask) iter.next() ;
                    if (_limitReached())
                        return;
                    if ((task.getState() == STATE.NEW)
                            && now.compareTo(task.getScheduledTime().getTime()) >= 0) {
                        // Run immediately
                        task.start();
                    }
                }
            }
        }
    }

    private volatile int _numRunning = 0;
    
    public synchronized void algorithmScheduled(Algorithm algorithm, Calendar time) {
        _schedulerListener.algorithmScheduled(algorithm, time);
    }

    public synchronized void algorithmStarted(Algorithm algorithm) {
        _numRunning++;
        _schedulerListener.algorithmStarted(algorithm);
    }

    public synchronized void algorithmError(Algorithm algorithm, Throwable error) {
        _numRunning--;
        _schedulerListener.algorithmError(algorithm, error);
        purgeFinished();
    }

    public synchronized void algorithmFinished(Algorithm algorithm, Data[] createdDM) {
        _numRunning--;
        _schedulerListener.algorithmFinished(algorithm, createdDM);
        purgeFinished();
    }

    public synchronized void algorithmRescheduled(Algorithm algorithm, Calendar time) {
        _schedulerListener.algorithmRescheduled(algorithm, time);
        
    }

    public synchronized void algorithmUnscheduled(Algorithm algorithm) {
        _schedulerListener.algorithmUnscheduled(algorithm);
    }

    public synchronized void schedulerCleared() {
        _schedulerListener.schedulerCleared();
    }

    public synchronized void schedulerRunStateChanged(boolean isRunning) {
        _schedulerListener.schedulerRunStateChanged(isRunning);
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
    static final class STATE {
        private String _name ;
        public STATE(String name) {
            this._name = name ;
        }
        public final boolean equals(Object object) {
            if (! (object instanceof STATE))
                return false ;
            STATE state = (STATE) object ;
            return state._name.compareTo(_name) == 0;
        }
        /** New algorithms are in this state. */
        public static final STATE NEW = new STATE("NEW") ;
        /** Running algorithms are in this state. */
        public static final STATE RUNNING = new STATE("RUNNING") ;
        /** Algorithms either cancelled or finished are in this state. */
        public static final STATE STOPPED = new STATE("STOPPED") ;
        /** Algorithm had an error while executing */
        public static final STATE ERROR = new STATE("ERROR");
    }

    private volatile boolean _noRun = false;

    public synchronized final boolean cancel() {
        if (_noRun)
            return true;
        if (_state.equals(STATE.RUNNING))
            return false;
        _state = STATE.STOPPED;
        _noRun = true;
        return _noRun;
    }

    public synchronized final void start() {
        if (_noRun)
            return;
        _setState(STATE.RUNNING);
        new Thread(this).start();
    }

    private final Algorithm _alg;

    // NOTE: TimerTask keeps its own schedule variable which can be retrieved
    // using scheduledExecutionTime() method. We don't use that here.
    private final Calendar _scheduledTime;
    
    private final ServiceReference _ref;

    private volatile STATE _state;

    /**
     * Execution status of the algorithm (i.e.) return value.
     */
    private Data[] _result;
    
    /**
     * The error, if an algorithm had one while executing
     */
    private Exception _error;

    /**
     * Deliberately allow only one listener. Its not the algorithms job to do
     * all the informing.
     */
    private SchedulerListener _schedulerListener;

    public AlgorithmTask(Algorithm alg, ServiceReference ref, Calendar scheduledTime,
            //SchedulerListener listener) {
    		AlgSchedulerTask algSchedulerTask) {
        _alg = alg;
        _ref = ref;
        _scheduledTime = scheduledTime;
        _schedulerListener = algSchedulerTask;
        algSchedulerTask.registerAlgorithmTask(alg, this);
        _init();
    }

    public synchronized final Calendar getScheduledTime() {
        // Do a defensive copy cuz we don't want clients changing
        // the time using this reference!
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this._scheduledTime.getTime());
        return calendar;
    }
    
    public synchronized final ServiceReference getServiceReference() {
        return _ref;
    }

    private final void _init() {
        _result = null;
        _setState(STATE.NEW);
    }

    public synchronized final Data[] getResult() {
        return _result;
    }

    private synchronized final void _setState(STATE state) {
        this._state = state;
        // Inform listeners
        if (_schedulerListener != null) {
            if (this._state.equals(STATE.NEW)) {
                _schedulerListener.algorithmScheduled(_alg, _scheduledTime);
            }
            else if (this._state.equals(STATE.RUNNING)) {
                _schedulerListener.algorithmStarted(_alg);
            }
            else if (this._state.equals(STATE.STOPPED)) {
                _noRun = true;
                _schedulerListener.algorithmFinished(_alg, getResult());
            } 
            else if (this._state.equals(STATE.ERROR)) {
                _noRun = true;
                _schedulerListener.algorithmError(_alg, _error);
            }
            else {
                throw new IllegalStateException(
                        "Encountered illegal algorithm state: " + _state);
            }
        }
    }

    public synchronized final STATE getState() {
        return this._state;
    }

    public void run() {
        try {
            _result = _alg.execute();
        } catch (Exception e) {
            _error = e;
            _setState(STATE.ERROR);
        } finally {
            _setState(STATE.STOPPED);
        }
    }
}
