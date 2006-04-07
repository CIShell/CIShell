/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 28, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.Scheduler;
import edu.iu.iv.core.SchedulerListener;
import edu.iu.iv.core.algorithm.Algorithm;

/**
 *
 * @author Team IVC
 */
public class BasicScheduler implements Scheduler {
    //sheduler for undated algorithms
    private SchedulerThread schedulerThread = new SchedulerThread();
    //thread to monitor scheduled items to find those that are ready
    private Thread monitorThread;     
    //list of algorithms scheduled with a date (Vector for threadsafety)
    private Vector scheduledAlgorithms;    
    private Vector schedulerListeners;
    private Vector blockedAlgorithms;
    
    private Map algorithmToAlgorithmSchedule = new HashMap();    
    private static int currentlyRunning = 0;
        
    public BasicScheduler(){
        scheduledAlgorithms = new Vector();
        schedulerListeners = new Vector();
        blockedAlgorithms = new Vector();
        monitorThread = new Thread(){
            public void run(){
                while(true){
                    try {
                        Calendar currentTime = Calendar.getInstance();
                        boolean done = false;
                        while(!done && !scheduledAlgorithms.isEmpty()){                            
                            AlgorithmSchedule item = (AlgorithmSchedule)scheduledAlgorithms.get(0);	                            
                            //if it is before or equal to now, run it
                            if(item.getTime().before(currentTime) || item.getTime().equals(currentTime)){
                                scheduledAlgorithms.remove(0);
                                Algorithm algorithm = item.getAlgorithm();
                                algorithmToAlgorithmSchedule.remove(algorithm);
                                schedulerThread.scheduleFirst( algorithm );
                                notifyListenersOfMoveToRunningQueue(algorithm, schedulerThread.schedule.indexOf(algorithm));
                            }
                            else{
                                done = true;
                            }
                        }                        
                        sleep(500);                                               
                    } catch (InterruptedException e) {}                    
                }
            }
        };
        monitorThread.start();        
                
    }
    
    /**
	 * @see edu.iu.iv.core.Scheduler#isRunning()
	 */
    public boolean isRunning() {
        return currentlyRunning > 0;
    }

    /**
	 * @see edu.iu.iv.core.Scheduler#isEmpty()
	 */
    public boolean isEmpty() {
        return schedulerThread.schedule.isEmpty() && scheduledAlgorithms.isEmpty();
    }

    /**
	 * @see edu.iu.iv.core.Scheduler#block(Algorithm)
	 */
    public void block(Algorithm algorithm) {
        blockedAlgorithms.add(algorithm);
    }

    /**
	 * @see edu.iu.iv.core.Scheduler#unblock(Algorithm)
	 */
    public void unblock(Algorithm algorithm) {
        blockedAlgorithms.remove(algorithm);
    }
    
    /**
	 * @see edu.iu.iv.core.Scheduler#addSchedulerListener(SchedulerListener)
	 */
    public void addSchedulerListener(SchedulerListener listener) {
        schedulerListeners.add(listener);
    }

    /**
	 * @see edu.iu.iv.core.Scheduler#removeSchedulerListener(SchedulerListener)
	 */
    public void removeSchedulerListener(SchedulerListener listener) {
        schedulerListeners.remove(listener);
    }
    
	/**
	 * @see edu.iu.iv.core.Scheduler#runNow(edu.iu.iv.core.Algorithm)
	 */
	public synchronized void runNow( Algorithm algorithm ) {
		if ( algorithm != null ){
			schedulerThread.scheduleFirst( algorithm );
			notifyListenersOfSchedule(algorithm, Calendar.getInstance(), schedulerThread.schedule.indexOf(algorithm));
		}
	}
	
	/**
	 * @see edu.iu.iv.core.Scheduler#reschedule(Algorithm, Calendar)
	 */
    public boolean reschedule(Algorithm algorithm, Calendar newTime) {        
        boolean removed = unschedule(algorithm);
        if(removed){
            schedule(algorithm, newTime);
        }
        
        return removed;
    }
	
	/**
	 * @see edu.iu.iv.core.Scheduler#schedule(Algorithm, Calendar)
	 */
    public void schedule(Algorithm algorithm, Calendar time) {
        //if the schedule time is in the past, schedule it now
        if(time.before(Calendar.getInstance())){
            runNow(algorithm);
        }
        else{
            //find the correct location in the list and insert the new item
            
            AlgorithmSchedule alg = new AlgorithmSchedule(algorithm, time);
            if(scheduledAlgorithms.isEmpty()){
                //empty list, so tack on the front
                scheduledAlgorithms.add(alg);
            }
            else{
                boolean found = false;
                int i = 0;
                while(!found && i < scheduledAlgorithms.size()){
	                AlgorithmSchedule item = (AlgorithmSchedule)scheduledAlgorithms.get(i);
	                if(item.getTime().after(time)){
	                    //insert in position
	                    found = true;
	                    scheduledAlgorithms.add(i, alg);
	                }
	                i++;
	            }
                if(!found){
                    //add on the end
                    scheduledAlgorithms.add(alg);
                }
            }
            algorithmToAlgorithmSchedule.put(algorithm, alg);
            notifyListenersOfSchedule(algorithm, time, -1);
        }
            
    }
	
	/**
	 * @see edu.iu.iv.core.Scheduler#schedule(edu.iu.iv.core.Algorithm)
	 */
	public void schedule( Algorithm algorithm ) {
		if ( algorithm != null ){
			schedulerThread.scheduleLast( algorithm );
			notifyListenersOfSchedule(algorithm, Calendar.getInstance(),  schedulerThread.schedule.indexOf(algorithm));
		}
	}
	
	private void notifyListenersOfSchedule(Algorithm algorithm, Calendar time, int index){
	    for(int i = 0; i < schedulerListeners.size(); i++){
	        if(index != -1){
	            ((SchedulerListener)schedulerListeners.get(i)).algorithmScheduled(algorithm, time, index);
	        }
	        else{
	            ((SchedulerListener)schedulerListeners.get(i)).algorithmScheduled(algorithm, time);
	        }
	    }
	}
	
	private void notifyListenersOfMoveToRunningQueue(Algorithm algorithm, int index){
	    for(int i = 0; i < schedulerListeners.size(); i++){	        
	        ((SchedulerListener)schedulerListeners.get(i)).algorithmMovedToRunningQueue(algorithm, schedulerThread.schedule.indexOf(algorithm));	        
	    }
	}
	
	private void notifyListenersOfStart(Algorithm algorithm){
	    for(int i = 0; i < schedulerListeners.size(); i++){
	        ((SchedulerListener)schedulerListeners.get(i)).algorithmStarted(algorithm);
	    }
	}
	
	private void notifyListenersOfFinish(Algorithm algorithm){
	    for(int i = 0; i < schedulerListeners.size(); i++){
	        ((SchedulerListener)schedulerListeners.get(i)).algorithmFinished(algorithm);
	    }
	}
	
	
	private void notifyListenersOfError(Algorithm algorithm){
	    for(int i = 0; i < schedulerListeners.size(); i++){
	        ((SchedulerListener)schedulerListeners.get(i)).algorithmError(algorithm);
	    }
	}
	
	private void notifyListenersOfMoveUp(Algorithm algorithm){
	    for(int i = 0; i < schedulerListeners.size(); i++){
	        ((SchedulerListener)schedulerListeners.get(i)).algorithmMovedUpInRunningQueue(algorithm);
	    }
	}
	
	private void notifyListenersOfMoveDown(Algorithm algorithm){
	    for(int i = 0; i < schedulerListeners.size(); i++){
	        ((SchedulerListener)schedulerListeners.get(i)).algorithmMovedDownInRunningQueue(algorithm);
	    }
	}
	
	/**
	 * @see edu.iu.iv.core.Scheduler#unschedule(Algorithm)
	 */
    public boolean unschedule(Algorithm algorithm) {
        //see if it is in the schedule list, not yet in the running queue if its
        //time has not come yet
        AlgorithmSchedule alg = (AlgorithmSchedule)algorithmToAlgorithmSchedule.get(algorithm);
        if(alg != null && scheduledAlgorithms.contains(alg)){
            scheduledAlgorithms.remove(alg);
            return true;
        }
        
        //if not, remove it from the running queue
        else if (schedulerThread.schedule.contains(algorithm)){
            schedulerThread.schedule.remove(algorithm);
            return true;
        }
        
        return false;
    
    }
    
	/**
	 * @see edu.iu.iv.core.Scheduler#moveUp(Algorithm)
	 */
    public boolean moveUp(Algorithm algorithm) {
        boolean success = schedulerThread.moveUp(algorithm);
        if(success)
            notifyListenersOfMoveUp(algorithm);
        return success;
    }

	/**
	 * @see edu.iu.iv.core.Scheduler#moveDown(Algorithm)
	 */
    public boolean moveDown(Algorithm algorithm) {
        boolean success = schedulerThread.moveDown(algorithm);
        if(success)
            notifyListenersOfMoveDown(algorithm);
        return success;
    }
    
	private class SchedulerThread extends Thread  {
		
		// must use a vector since this could be accessed by multiple threads
		private List schedule = new Vector();
		private int maxSimultaneous;		
		
		public SchedulerThread(){
		    maxSimultaneous = IVC.getInstance().getConfiguration().getInt(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE);
		    
		    if (maxSimultaneous == 0) {
		        //for unit testing
		        maxSimultaneous = 4;
		    }
		}
		
		/**
		 * Schedules an algorithm at the front of the run queue.
		 * 
		 * @param algorithm the algorithm to schedule
		 */
		public synchronized void scheduleFirst( Algorithm algorithm ) {
			schedule.add( 0, algorithm );
			// start the scheduler thread if it isn't already running
			if ( !isAlive() ) start();
		}
		
		/**
		 * Schedules an algorithm at the end of the run queue.
		 * 
		 * @param algorithm the algorithm to run
		 */
		public synchronized void scheduleLast( Algorithm algorithm ) {
			schedule.add( algorithm );
			// start the scheduler thread if it isn't already running
			if ( !isAlive() ) start();
		}
		
		public synchronized boolean moveUp(Algorithm algorithm){
		    int index = schedule.indexOf(algorithm);
		    int newIndex = index - 1;
		    if(newIndex < 0)
		        return false; //cant be less than zero
		    schedule.remove(index);
		    schedule.add(newIndex, algorithm);
		    return true;
		}
		
		public synchronized boolean moveDown(Algorithm algorithm){
		    int index = schedule.indexOf(algorithm);
		    if(index == -1) return false; //not in list
		    
		    int newIndex = index + 1;
		    if(newIndex > schedule.size() - 1)
		        return false; //cant be larger than list size
		    
		    schedule.remove(index);
		    schedule.add(newIndex, algorithm);
		    return true;
		}
		
		/**
		 * Executes each algorithm in the queue in order, then dies. 
		 */
		public void run() {		    
			while(!schedule.isEmpty())  {								
			    //wait until there arent too many algs running
			    while(currentlyRunning >= maxSimultaneous){
			        try {
                        sleep(500);
                    } catch (InterruptedException e) {}				       
			    }
			    
			    //make sure its still empty after the wait, things may
			    //have been deleted
			    if(!schedule.isEmpty()){
				    final Algorithm algorithm = ((Algorithm) schedule.remove( 0 ));
				    
				    if(blockedAlgorithms.contains(algorithm)){
				        //put it on the back of the queue
				        schedule.add(schedule.size(), algorithm);
				    }
				    else{				    
				        //go ahead and run it
					    currentlyRunning++;
					    
					    //run new algorithm in a new thread
					    new Thread(){
					        public void run(){
					            notifyListenersOfStart(algorithm);
					            try{
					                algorithm.execute();
					                currentlyRunning--;
						            notifyListenersOfFinish(algorithm);
					            } catch (Exception e) {
					                currentlyRunning--;	
					                notifyListenersOfError(algorithm);
					                String message = "Algorithm: " + algorithm.getClass().getName() + " had an error\n";
					                IVC.showError("Error!", message, getStackTrace(e));
					                IVC.getInstance().getConsole().printAlgorithmInformation(message);
					                
					                message = e.getMessage();
					                if (message == null) message = "";
					                IVC.getInstance().getConsole().printSystemError(message);
					                IVC.getInstance().getErrorLogger().error(message);
					            }				            
					        }
					        
					        private String getStackTrace(Exception e) {
					            String trace = "Error: " + e.getClass() + " (" + e.getMessage() + ")\n";
					            
					            StackTraceElement[] stack = e.getStackTrace();
					            for (int i=0; i < stack.length; i++) {
					                trace += stack[i].toString() + "\n";
					            }
					            
					            return trace;
					        }
					    }.start();
				    }
			    }
			}
			
			// threads can't be restarted once they die, so we need
			// a new instance in the enclosing class
			schedulerThread = new SchedulerThread();
		}
	}

	private class AlgorithmSchedule {
	    private Algorithm algorithm;
	    private Calendar time;
	    
	    public AlgorithmSchedule(Algorithm algorithm, Calendar time){
	        this.algorithm = algorithm;
	        this.time = time;
	    }
	    
	    public Calendar getTime(){
	        return time;
	    }
	    
	    public void setTime(Calendar time){
	        this.time = time;
	    }
	    
	    public Algorithm getAlgorithm(){
	        return algorithm;
	    }
	}

}
