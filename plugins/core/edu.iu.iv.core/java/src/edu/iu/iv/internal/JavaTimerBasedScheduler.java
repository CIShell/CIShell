package edu.iu.iv.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
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
 * The scheduler starts a timer which runs as long as the scheduler is running.
 * New algorithms are scheduled by giving them to an algorithm scheduler task
 * which is itself scheduled on a different timer and called periodically. The
 * scheduler task scans its list of algorithms, checks their state and schedules
 * them to run if their scheduled time is the current time or has already
 * passed. The algorithms themselves are scheduled on a timer other than the one
 * on which the scheduler task is running. The scheduler task also enforces a
 * limit on the number of algorithms run if so desired. This limit overrides
 * algorithm schedule so that setting a limit too low may cause the algorithm
 * run queue to be full of waiting algorithms.
 * 
 * NOTE: By default this, scheduler allows an unlimited number of algorithms to
 * run.
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
	 * The timer that actually runs the algorithms.
	 */
	private Timer _algRunningTimer;

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
	}

	private final void _initialize() {
		_schedulerTimer = new Timer();
		_algRunningTimer = new Timer();
		_schedulerListenerInformer = new SchedulerListenerInformer();
		_algSchedulerTask = new AlgSchedulerTask(_algRunningTimer,
				_schedulerListenerInformer);
		_schedulerTimer.schedule(_algSchedulerTask, 0L, 1000L);
	}

	public synchronized final void shutDown() {
		_algSchedulerTask.cancel();
		_algRunningTimer.cancel();
		_schedulerTimer.cancel();
	}

	public boolean isEmpty() {
		return (_algSchedulerTask.getNumAlgsInState(STATE.RUNNING) > 0)
				|| (_algSchedulerTask.getNumAlgsInState(STATE.NEW) > 0);
	}

	public boolean isRunning() {
		return _algSchedulerTask.getNumAlgsInState(STATE.RUNNING) > 0;
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
				status = _algSchedulerTask.cancel(algorithm);
				if (status)
					_algSchedulerTask.schedule(algorithm, newTime);
				break;
			default:
				throw new RuntimeException("WTF!!");
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

	public void algorithmStarted(Algorithm algorithm) {
		for (SchedulerListener sl : _schedulerListeners)
			sl.algorithmStarted(algorithm);
	}

	public void algorithmFinished(Algorithm algorithm) {
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

class AlgSchedulerTask extends TimerTask {

	private Map<Algorithm, AlgorithmTask> _algMap;

	// Default allow as many as needed
	private int _maxSimultaneousAlgs = -1;

	private Timer _timer;

	/**
	 * Maximum number of algorithms allowed to run simultaneously. This value
	 * can be changed at runtime without any problems. Allowable values are -1
	 * (for no limit) and values > 0. If unacceptable values are given, they are
	 * ignored.
	 * 
	 * @param max
	 *            The maximum number of algorithms that can be simultaneously
	 *            run.
	 */
	public synchronized final void setMaxSimultaneousAlgs(final int max) {
		if (max < -1 || max == 0)
			return;
		this._maxSimultaneousAlgs = max;
	}

	private SchedulerListener _schedulerListener;

	public AlgSchedulerTask(final Timer timer, SchedulerListener listener) {
		// Timer must not be null. Miserably fail otherwise.
		assert (timer != null) : "Set timer running first.";
		_timer = timer;
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
		// If the task doesn't exist, it is equivalent to have been
		// cancelled.
		if (task == null)
			return true;
		// If this is already stopped and is waiting to be cleaned up, clean it
		// up first.
		if (task.getState() == STATE.STOPPED) {
			purgeFinished();
			return true;
		}
		// Note that getting a return value of true here does not mean the task
		// is no longer running. It just means it has been scheduled for
		// successful cancellation. The algorithm will run till the end and
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
			// If its new, we refuse to schedule it to force user to explicitly
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
		this._algMap.put(alg, new AlgorithmTask(alg, time, _schedulerListener));
	}

	public synchronized final int getMaxSimultaneousAlgs() {
		return this._maxSimultaneousAlgs;
	}

	/**
	 * @param alg
	 *            The algorithm whose state we want to query.
	 * @return State of the specified algorithm.
	 */
	public final STATE getAlgorithmState(Algorithm alg) {
		synchronized (_algMap) {
			AlgorithmTask task = this._algMap.get(alg);
			if (task == null)
				throw new NoSuchElementException("Algorithm doesn't exist.");
			return task.getState();
		}
	}

	/**
	 * @param state
	 *            The state we are interested in.
	 * @return The number of algorithms in the specified state.
	 */
	public final int getNumAlgsInState(STATE state) {
		int count = 0;
		for (AlgorithmTask task : this._algMap.values())
			if (task != null && task.getState() == state)
				count++;
		return count;
	}

	/**
	 * @return true if we have reached the limit of how many algs we can run,
	 *         false otherwise. If limit is set to -1, always returns false,
	 *         i.e. no limit.
	 */
	private final boolean _limitReached() {
		int max = getMaxSimultaneousAlgs();
		return (max != -1) && (getNumAlgsInState(STATE.RUNNING) >= max);
	}

	/**
	 * Removes all finished algorithms from the queue.
	 */
	public synchronized final void purgeFinished() {
		synchronized (this._algMap) {
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

	@Override
	public void run() {
		purgeFinished();
		synchronized (_algMap) {
			// If we are runing the max allowable, wait until next turn.
			if (_limitReached())
				return;
			// Iterate through algorithms.
			Set<Entry<Algorithm, AlgorithmTask>> entrySet = this._algMap
					.entrySet();
			for (Entry<Algorithm, AlgorithmTask> entry : entrySet) {
				// Check this at every point.
				if (_limitReached())
					break;
				AlgorithmTask task = entry.getValue();
				Date now = Calendar.getInstance().getTime();
				// Only schedule algorithms that are fit to be scheduled.
				if ((task.getState() == STATE.NEW)
						&& now.compareTo(task.getScheduledTime().getTime()) <= 0) {
					// Run immediately
					_timer.schedule(task, 0L);
				}
			}
		}
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
class AlgorithmTask extends TimerTask {

	/**
	 * The states in which algorithm tasks can exist.
	 * 
	 * @author Team IVC
	 */
	public static enum STATE {
		/** Newly scheduled algorithms are in this state. */
		NEW,
		/** Running algorithms are in this state. */
		RUNNING,
		/** Algorithms either cancelled or finished are in this state. */
		STOPPED
	}

	private final Algorithm _alg;

	// NOTE: TimerTask keeps its own schedule variable which can be retrieved
	// using scheduledExecutionTime() method. We don't use that here.
	private final Calendar _scheduledTime;

	private STATE _state;

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
		// Inform listener appropriately
		if (_schedulerListener != null) {
			switch (this._state) {
			case NEW:
				_schedulerListener.algorithmScheduled(_alg, _scheduledTime);
				break;
			case RUNNING:
				_schedulerListener.algorithmStarted(_alg);
				break;
			case STOPPED:
				boolean status = getStatus();
				if (status)
					_schedulerListener.algorithmFinished(_alg);
				else
					_schedulerListener.algorithmError(_alg);
			}
		}
	}

	public synchronized final STATE getState() {
		return this._state;
	}

	@Override
	public void run() {
		try {
			_setState(STATE.RUNNING);
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