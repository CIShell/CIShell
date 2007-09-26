package org.cishell.reference.gui.scheduler;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;


/**
 * Listens for notification from the scheduler and notifies all registered objects
 */
public class SchedulerContentModel implements SchedulerListener {
    private static final SchedulerContentModel INSTANCE = new SchedulerContentModel();
    
	private SchedulerService schedulerService;
    private List             schedulerListenerList;
    private Map              classNameToPersistentMap;
    
    private boolean          isRunning;

    private SchedulerContentModel(){
		schedulerService = Activator.getSchedulerService();
		if (schedulerService != null) {
			schedulerService.addSchedulerListener(this);
		}
    	schedulerListenerList = new Vector();
    	
    	classNameToPersistentMap = new Hashtable();
    }

    public static SchedulerContentModel getInstance(){
        return INSTANCE;
    }

    public void register(SchedulerListener listener) {
    	schedulerListenerList.add(listener);
    }
    
    public void deregister(SchedulerListener listener) {
    	schedulerListenerList.remove(listener);
    }
    
    public void persistObject(String className, Object o) {
    	classNameToPersistentMap.put(className, o);
    }
    
    public Object getPersistedObject(String className) {
    	return classNameToPersistentMap.get(className);
    }
    
    public boolean isRunning() {
    	return schedulerService.isRunning();
    }

	public void algorithmError(Algorithm algorithm, Throwable error) {
		for (int i = 0; i < schedulerListenerList.size(); ++i) {
			SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
			schedulerListener.algorithmError(algorithm, error);
		}		
	}

	public void algorithmFinished(Algorithm algorithm, Data[] createdData) {
		for (int i = 0; i < schedulerListenerList.size(); ++i) {
			SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
			schedulerListener.algorithmFinished(algorithm, createdData);
		}		
	}

	public void algorithmRescheduled(Algorithm algorithm, Calendar time) {
		for (int i = 0; i < schedulerListenerList.size(); ++i) {
			SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
			schedulerListener.algorithmRescheduled(algorithm, time);
		}		
	}

	public void algorithmScheduled(Algorithm algorithm, Calendar time) {
		for (int i = 0; i < schedulerListenerList.size(); ++i) {
			SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
			schedulerListener.algorithmScheduled(algorithm, time);
		}		
	}

	public void algorithmStarted(Algorithm algorithm) {
		for (int i = 0; i < schedulerListenerList.size(); ++i) {
			SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
			schedulerListener.algorithmStarted(algorithm);
		}		
	}

	public void algorithmUnscheduled(Algorithm algorithm) {
		for (int i = 0; i < schedulerListenerList.size(); ++i) {
			SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
			schedulerListener.algorithmUnscheduled(algorithm);
		}		
	}

	public void schedulerCleared() {
		for (int i = 0; i < schedulerListenerList.size(); ++i) {
			SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
			schedulerListener.schedulerCleared();
		}		
	}

	public void schedulerRunStateChanged(boolean isRunning) {
		if (this.isRunning != isRunning) {
			this.isRunning = isRunning;
			schedulerService.setRunning(isRunning);
		}
		else {
			for (int i = 0; i < schedulerListenerList.size(); ++i) {
				SchedulerListener schedulerListener = (SchedulerListener)schedulerListenerList.get(i);
				schedulerListener.schedulerRunStateChanged(this.isRunning);
			}		
		}
	}
}
