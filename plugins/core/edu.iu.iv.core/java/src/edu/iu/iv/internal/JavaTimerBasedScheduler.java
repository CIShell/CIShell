package edu.iu.iv.internal;

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
import java.util.Map.Entry;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.iu.iv.core.Scheduler;
import edu.iu.iv.core.SchedulerListener;
import edu.iu.iv.core.algorithm.Algorithm;
import edu.iu.iv.internal.AlgorithmTask.STATE;

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
 * @author Team CIShell
 */
// May 8, 2006 7:05:32 PM Shashikant Penumarthy: Initial Implementation
public class JavaTimerBasedScheduler implements Scheduler {

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

	public JavaTimerBasedScheduler() {
		_initialize();
	}

	public JavaTimerBasedScheduler(int maxSimultaneousAlgsLimit) {
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
		return numRunning() > 0;
	}

	public final int numRunning() {
		return _algSchedulerTask.numRunning();
	}

	private boolean _isShutDown = true;

	public final boolean isShutDown() {
		return _isShutDown;
	}

	@Deprecated
	public boolean moveDown(Algorithm algorithm) {
		throw new RuntimeException(
				"Scheduler doesn't care about such orderings."
						+ " Re-schedule the specified algorithm to run before the desired"
						+ "algorithm. Attempted to move: " + algorithm);
	}

	@Deprecated
	public boolean moveUp(Algorithm algorithm) {
		throw new RuntimeException(
				"Scheduler doesn't care about such orderings."
						+ " Re-schedule the specified algorithm to run after the desired"
						+ "algorithm Attempted to move: " + algorithm);
	}

	public boolean reschedule(Algorithm algorithm, Calendar newTime) {
		// Shaky method. Ideally this is done at a higher level. But still, here
		// goes...
		boolean status = false;
		try {
			STATE algState = _algSchedulerTask.getAlgorithmState(algorithm);
			// Cannot reschedule running algs
			switch (algState) {
			case RUNNING:
				status = false;
				break;
			case STOPPED:
				_algSchedulerTask.purgeFinished();
				_algSchedulerTask.schedule(algorithm, newTime);
				status = true;
				break;
			case NEW:
				_algSchedulerTask.cancel(algorithm);
				_algSchedulerTask.schedule(algorithm, newTime);
				break;
			default:
				throw new IllegalStateException(
						"Encountered an invalid state: " + algState);
			}
		} catch (NoSuchElementException nsee) {
			_algSchedulerTask.schedule(algorithm, newTime);
			status = true;
		}
		return status;
	}

	public void runNow(Algorithm algorithm) {
		// There is currently no difference between this one and
		// schedule(Algorithm).
		schedule(algorithm);
	}

	public void schedule(Algorithm algorithm) {
		_algSchedulerTask.schedule(algorithm, Calendar.getInstance());
	}

	public void schedule(Algorithm algorithm, Calendar time) {
		_algSchedulerTask.schedule(algorithm, time);
	}

	@Deprecated
	public void unblock(final Algorithm algorithm) {
		NotImplementedException n = new NotImplementedException();
		n.initCause(new RuntimeException(
				"Cannot block algs reliably so this feature will be removed. "
						+ "Attempted to schedule: " + algorithm));
	}

	@Deprecated
	public void block(final Algorithm algorithm) {
		NotImplementedException n = new NotImplementedException();
		n.initCause(new RuntimeException(
				"Cannot block algs reliably so this feature will be removed. "
						+ "Attempted to schedule: " + algorithm));
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

	private List<SchedulerListener> _schedulerListeners;

	public SchedulerListenerInformer() {
		_schedulerListeners = new ArrayList<SchedulerListener>();
	}

	public void addSchedulerListener(SchedulerListener listener) {
		if (listener == null)
			throw new NullPointerException("Null listeners not allowed!");

		if (!_schedulerListeners.contains(listener))
			_schedulerListeners.add(listener);
	}

	public void removeSchedulerListener(SchedulerListener listener) {
		_schedulerListeners.remove(listener);
	}

	public void algorithmMovedToRunningQueue(Algorithm algorithm, int index) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmMovedToRunningQueue(algorithm, index);
	}

	public void algorithmScheduled(Algorithm algorithm, Calendar time, int index) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmScheduled(algorithm, time, index);
	}

	public void algorithmScheduled(Algorithm algorithm, Calendar time) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmScheduled(algorithm, time);
	}

	public synchronized void algorithmStarted(Algorithm algorithm) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmStarted(algorithm);
	}

	public synchronized void algorithmFinished(Algorithm algorithm) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmFinished(algorithm);
	}

	public void algorithmError(Algorithm algorithm) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmError(algorithm);
	}

	public void algorithmMovedUpInRunningQueue(Algorithm algorithm) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmMovedUpInRunningQueue(algorithm);
	}

	public void algorithmMovedDownInRunningQueue(Algorithm algorithm) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmMovedDownInRunningQueue(algorithm);
	}
}

class AlgSchedulerTask extends TimerTask implements SchedulerListener {

	private Map<Algorithm, AlgorithmTask> _algMap;

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

	public synchronized final boolean isEmpty() {
		return _algMap.size() == 0;
	}

	public synchronized final int numRunning() {
		return _numRunning;
	}

	private SchedulerListener _schedulerListener;

	public AlgSchedulerTask(SchedulerListener listener) {
		_algMap = Collections
				.synchronizedMap(new HashMap<Algorithm, AlgorithmTask>());
		setSchedulerListener(listener);
	}

	public synchronized final void setSchedulerListener(
			SchedulerListener listener) {
		_schedulerListener = listener;
	}

	public synchronized final boolean cancel(Algorithm alg) {
		AlgorithmTask task = this._algMap.get(alg);
		if (task == null)
			return false;
		// The algorithm will run till the end and
		// then stop so there's no real way to cancel running algorithms.
		// Clients should always check the state of an algorithm before trying
		// to reschedule an existing algorithm.
		return task.cancel();
	}

	public synchronized final void schedule(Algorithm alg, Calendar time) {
		AlgorithmTask task = this._algMap.get(alg);
		// If alg already exists, do some checks...
		if (task != null) {
			STATE state = task.getState();
			switch (state) {
			// If its still running, we can't schedule it again.
			case RUNNING:
				throw new RuntimeException(
						"Cannot schedule running algorithm. Check state of algorithm first.");
			// If its new or waiting to run, we refuse to schedule it to force
			// user to explicitly
			// cancel and reschedule.
			case NEW:
				throw new RuntimeException(
						"Algorithm is already scheduled to run. Cancel existing schedule first.");
			case STOPPED:
				// If it was stopped but not cleaned up yet, clean it up
				purgeFinished();
				break;
			default:
				throw new IllegalStateException(
						"State was not one of allowable states: " + state);
			}
		}
		this._algMap.put(alg, new AlgorithmTask(alg, time, this));
	}

	public synchronized final int getMaxSimultaneousAlgs() {
		return this._maxSimultaneousAlgs;
	}

	/**
	 * @param alg
	 *            The algorithm whose state we want to query.
	 * @return State of the specified algorithm.
	 */
	public synchronized final STATE getAlgorithmState(Algorithm alg) {
		AlgorithmTask task = this._algMap.get(alg);
		if (task == null)
			throw new NoSuchElementException("Algorithm doesn't exist.");
		return task.getState();
	}

	/**
	 * Removes all finished algorithms from the queue.
	 */
	public synchronized final void purgeFinished() {
		synchronized (this) {
			Iterator<Entry<Algorithm, AlgorithmTask>> iter = this._algMap
					.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Algorithm, AlgorithmTask> entry = iter.next();
				AlgorithmTask task = entry.getValue();
				if (task.getState() == STATE.STOPPED)
					iter.remove();
			}
		}
	}

	private synchronized final boolean _limitReached() {
		return (_maxSimultaneousAlgs != -1)
				&& (_numRunning >= _maxSimultaneousAlgs);
	}

	@Override
	public void run() {
		synchronized (this) {
			// If we are runing the max allowable, wait until next turn.
			Date now = Calendar.getInstance().getTime();
			// Iterate through algorithms.
			Collection<AlgorithmTask> tasks = this._algMap.values();
			for (AlgorithmTask task : tasks) {
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

	public void algorithmMovedToRunningQueue(Algorithm algorithm, int index) {
		_schedulerListener.algorithmMovedToRunningQueue(algorithm, index);
	}

	public void algorithmScheduled(Algorithm algorithm, Calendar time, int index) {
		_schedulerListener.algorithmScheduled(algorithm, time, index);
	}

	public void algorithmScheduled(Algorithm algorithm, Calendar time) {
		_schedulerListener.algorithmScheduled(algorithm, time);
	}

	private volatile int _numRunning = 0;

	public synchronized void algorithmStarted(Algorithm algorithm) {
		_numRunning++;
		_schedulerListener.algorithmStarted(algorithm);
	}

	public void algorithmFinished(Algorithm algorithm) {
		purgeFinished();
		_numRunning--;
		_schedulerListener.algorithmFinished(algorithm);
	}

	public void algorithmError(Algorithm algorithm) {
		purgeFinished();
		_numRunning--;
		_schedulerListener.algorithmError(algorithm);
	}

	public void algorithmMovedUpInRunningQueue(Algorithm algorithm) {
		_schedulerListener.algorithmMovedUpInRunningQueue(algorithm);
	}

	public void algorithmMovedDownInRunningQueue(Algorithm algorithm) {
		_schedulerListener.algorithmMovedDownInRunningQueue(algorithm);
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
class AlgorithmTask implements Runnable {

	/**
	 * The states in which algorithm tasks can exist.
	 * 
	 * @author Team IVC
	 */
	public static enum STATE {
		/** New algorithms are in this state. */
		NEW,
		/** Running algorithms are in this state. */
		RUNNING,
		/** Algorithms either cancelled or finished are in this state. */
		STOPPED
	}

	private volatile boolean _noRun = false;

	public synchronized final boolean cancel() {
		if (_noRun)
			return true;
		if (_state == STATE.RUNNING)
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

	private volatile STATE _state;

	/**
	 * Execution status of the algorithm (i.e.) return value.
	 */
	private boolean _status;

	/**
	 * Deliberately allow only one listener. Its not the algorithms job to do
	 * all the informing.
	 */
	private SchedulerListener _schedulerListener;

	public AlgorithmTask(Algorithm alg, Calendar scheduledTime,
			SchedulerListener listener) {
		_alg = alg;
		_scheduledTime = scheduledTime;
		_schedulerListener = listener;
		_init();
	}

	public synchronized final Calendar getScheduledTime() {
		// Do a defensive copy cuz we don't want clients changing
		// the time using this reference!
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this._scheduledTime.getTime());
		return calendar;
	}

	private final void _init() {
		_status = false;
		_setState(STATE.NEW);
	}

	public synchronized final boolean getStatus() {
		return _status;
	}

	private synchronized final void _setState(STATE state) {
		this._state = state;
		// Inform listeners
		if (_schedulerListener != null) {
			switch (this._state) {
			case NEW:
				_schedulerListener.algorithmScheduled(_alg, _scheduledTime);
				break;
			case RUNNING:
				_schedulerListener.algorithmStarted(_alg);
				break;
			case STOPPED:
				_noRun = true;
				boolean status = getStatus();
				if (status)
					_schedulerListener.algorithmFinished(_alg);
				else
					_schedulerListener.algorithmError(_alg);
				break;
			default:
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
			_status = _alg.execute();
		} catch (Exception e) {
			// TODO: This is a really bad idea. We should just
			// let this exception bubble up all the way to the top instead
			// of casting a wide net like this.
			_status = false;
		} finally {
			_setState(STATE.STOPPED);
		}
	}
}