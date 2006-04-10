/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 2, 2005 at Indiana University.
 */
package edu.iu.iv.schedulerview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.iu.iv.core.SchedulerListener;
import edu.iu.iv.core.algorithm.Algorithm;

/**
 * SchedulerContentModel maintains all the state information about the
 * IVC scheduler that is needed by the SchedulerView to properly display
 * itself. It has various methods for providing information to the
 * SchedulerView and simplifying certain tasks. It should be noted that
 * methods on the model do not affect the underlying scheduler in IVC
 * (such as adding items or moving them in the queue), but rather respond
 * to changes in the scheduler (as a SchedulerListener) and update the
 * model used by the SchedulerView appropriately.
 *
 * @author Team IVC
 */
public class SchedulerContentModel implements SchedulerListener {
    
    /* singleton instance */
    private static final SchedulerContentModel INSTANCE = new SchedulerContentModel();
    
    //maps a SchedulerItem to what Vector (below) it is currently in for
    //convenience lookup
    private Map itemToVectorMap;    
    
    private Vector running; //currently running items
    private Vector runningQueue; //things waiting to run
    private Vector scheduledItems; //items scheduled for the future
    private Vector finishedItems; //completed tasks
    
    //maintains list of algorithms to monitor progress for
    private Vector progressiveAlgorithms = new Vector();
    
    //provides lookup of SchedulerItem based on its algorithm
    private Map algorithmToSchedulerItem = new HashMap();
    
    //keeps track of the last item scheduled. this is currently used
    //by the SchedulerView when things are rescheduled to set the 
    //name back to the original name appropriately (since a new SchedulerItem
    //is actually made with a new unique name)
    private SchedulerItem mostRecentItemScheduled;
    
    //keeps track of SchedulerContentModelListeners
    private List listeners;
    
    //whether or not completed algorithms should be removed automatically
    private boolean removeAutomatically;
    
    private Object lock = new Object();
    
    //private constructor for singleton pattern
    private SchedulerContentModel(){
        itemToVectorMap = new HashMap();
        finishedItems = new Vector();
        running = new Vector();
        runningQueue = new Vector();
        scheduledItems = new Vector();
        listeners = new ArrayList();
        removeAutomatically = false;
        setupMonitorThread();
    }    
    
    /**
     * Returns the single instance of SchedulerContentModel.
     * 
     * @return the single instance of SchedulerContentModel
     */
    public static SchedulerContentModel getInstance(){
        return INSTANCE;
    }
    
    /**
     * Returns the SchedulerItem most recently added to this model
     * 
     * @return the SchedulerItem most recently added to this model
     */
    public SchedulerItem getMostRecentAddition(){
        return mostRecentItemScheduled;
    }
    
    /**
     * Adds the given SchedulerContentModelListener to this 
     * SchedulerContentModel, to be notified of changes.
     * 
     * @param listener the SchedulerContentModelListener to add
     */
    public void addListner(SchedulerContentModelListener listener){
        listeners.add(listener);
    }

    /**
     * Removes the given SchedulerContentModelListener from this 
     * SchedulerContentModel.
     * 
     * @param listener the SchedulerContentModelListener to remove
     */
    public void removeListner(SchedulerContentModelListener listener){
        listeners.remove(listener);
    }
    
    
    /**
     * Sets the policy for removal of completed Algorithms. If true,
     * completed Algorithms will automatically be removed from the model,
     * and views of it.
     * 
     * @param removeAutomatically true if completed Algorithms should
     * be removed automatically, false if not
     */
    public void setRemoveAutomatically(boolean removeAutomatically){
        this.removeAutomatically = removeAutomatically;
    }
    

	
	/**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an item is moved into the running queue.
	 * 
	 * @param algorithm the Algorithm that was moved into the running queue
	 * @param index the index in the running queue of the given Algorithm
	 */
    public void algorithmMovedToRunningQueue(Algorithm algorithm, int index) {
        synchronized(lock){
            SchedulerItem item = (SchedulerItem)algorithmToSchedulerItem.get(algorithm);
            itemMovedToRunningQueue(item, index);
        }
        notifyListeners();
    }

	/**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an Algorithm is scheduled to run immediatly, with the given
	 * index in the running queue.
	 * 
	 * @param algorithm the Algorithm that was scheduled
	 * @param time the time at which this Algorithm was scheduled
	 * @param index the index in the running queue of the given Algorithm
	 */
    public void algorithmScheduled(Algorithm algorithm, Calendar time, int index) {
        synchronized(lock){
	        mostRecentItemScheduled = new SchedulerItem(algorithm, time);
	        algorithmToSchedulerItem.put(algorithm, mostRecentItemScheduled);
	        add(mostRecentItemScheduled, index);     
        }
        notifyListeners();
    }        
	
	/**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an item is scheduled at a particular time
	 * 
	 * @param algorithm the Algorithm that was scheduled
	 * @param time the time at which this Algorithm is scheduled to run
	 */
    public void algorithmScheduled(final Algorithm algorithm, final Calendar time) {
        synchronized(lock){
	        mostRecentItemScheduled = new SchedulerItem(algorithm, time);
	        algorithmToSchedulerItem.put(algorithm, mostRecentItemScheduled);
	        add(mostRecentItemScheduled);
        }
        notifyListeners();
    }
    
	/**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an Algorithm is started
	 * 
	 * @param algorithm the Algorithm that was started
	 */    
    public void algorithmStarted(Algorithm algorithm) {        
        synchronized(lock){
            SchedulerItem schedulerItem = (SchedulerItem)algorithmToSchedulerItem.get(algorithm);       
	        schedulerItem.start();        
	        if(schedulerItem.isProgressive()){
	            progressiveAlgorithms.add(schedulerItem);
	        }
	        itemStarted(schedulerItem);
        }
                
        notifyListeners();
    }
    
    /**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an Algorithm is halted due to error
	 * 
	 * @param algorithm the Algorithm that was halted due to error
	 */    
    public void algorithmError(Algorithm algorithm) {       
        synchronized(lock){
            SchedulerItem schedulerItem = (SchedulerItem)algorithmToSchedulerItem.get(algorithm);      
	        schedulerItem.signalError();
	        if(schedulerItem.isProgressive())
	            progressiveAlgorithms.remove(algorithm);
	        itemFinished(schedulerItem);
        }
        
        notifyListeners();                
    }

    /**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an Algorithm is completed
	 * 
	 * @param algorithm the Algorithm that was completed
	 */    
    public void algorithmFinished(Algorithm algorithm) {
        SchedulerItem schedulerItem;
        synchronized(lock){
            schedulerItem = (SchedulerItem)algorithmToSchedulerItem.get(algorithm);
	        schedulerItem.finish();
	        if(schedulerItem.isProgressive())
	            progressiveAlgorithms.remove(algorithm);
	        itemFinished(schedulerItem);
        }
        if(removeAutomatically){
            remove(schedulerItem);
        }
        notifyListeners();        
    }
    
    /**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an Algorithm is moved up in the running queue
	 * 
	 * @param algorithm the Algorithm that was moved up in the running queue
	 */    
    public void algorithmMovedUpInRunningQueue(Algorithm algorithm) {        
        synchronized(lock){
            SchedulerItem schedulerItem = (SchedulerItem)algorithmToSchedulerItem.get(algorithm);
            moveUp(schedulerItem);
        }
        notifyListeners();
    }

    /**
	 * Implementation of method defined on SchedulerListener to update the
	 * model when an Algorithm is moved down in the running queue
	 * 
	 * @param algorithm the Algorithm that was completed
	 */
    public void algorithmMovedDownInRunningQueue(Algorithm algorithm) {        
        synchronized(lock){
            SchedulerItem schedulerItem = (SchedulerItem)algorithmToSchedulerItem.get(algorithm);
            moveDown(schedulerItem);
        }
        notifyListeners();
    }  
    
    /**
     * Removes the given item from this model.
     * 
     * @param item the SchedulerItem to remove from this model.
     */
    public void remove(SchedulerItem item){
        synchronized(lock){
	        Vector vector = (Vector)itemToVectorMap.get(item);
	        //cant remove from running
	        if(!vector.equals(running)){         
	            vector.remove(item);
	            item.kill();
	            itemToVectorMap.remove(item);
	        }
        }
        notifyListeners();
    }
    
    /**
     * Removes all completed items from this model.
     */
    public void removeCompleted(){
        synchronized(lock){
	        for(int i=0; i < finishedItems.size(); i++){
	            SchedulerItem item = (SchedulerItem)finishedItems.get(i);
	            itemToVectorMap.remove(item);
	            item.kill();
	        }        
	        finishedItems.clear();
        }
        notifyListeners();
    }
    

    
    /**
     * Determines if the given SchedulerItem can move up in the running queue,
     * meaning that it is not already at the top position.
     * 
     * @param item the SchedulerItem to determine if it can move up in the running
     * queue
     * @return true if the item can move up, false if not
     */
    public boolean canMoveUp(SchedulerItem item){
        return runningQueue.indexOf(item) > 0;
    }
    
    /**
     * Determines if the given SchedulerItem can move down in the running queue,
     * meaning that it is not already at the bottom position
     * 
     * @param item the SchedulerItem to determine if it can move down in the running
     * queue
     * @return true if the item can move down, false if not
     */
    public boolean canMoveDown(SchedulerItem item){
        int index = runningQueue.indexOf(item);
        return index >= 0 && index < runningQueue.size() - 1;
    }
        
    
    /**
     * Returns an ordered array of all the SchedulerItems in the model to be used
     * for displaying views.  This array contains the items in the following order:
     * finished items, running items, items in the running queue, and scheduled items.
     * 
     * @return an ordered array of all the SchedulerItems in the model
     */
    public SchedulerItem[] getItems(){
        int finishedSize = finishedItems.size();
        int runningSize = running.size();
        int runningQueueSize = runningQueue.size();
        int scheduledItemsSize = scheduledItems.size();
        
        int size = finishedSize + runningSize + runningQueueSize + scheduledItemsSize;        
        SchedulerItem[] items = new SchedulerItem[size];
        
        int i = 0;
        int j = 0;
        int length = finishedSize;
        while(i < finishedSize){
            items[i] = (SchedulerItem)finishedItems.get(j);
            i++;
            j++;
        }
        
        j = 0;
        length = length + runningSize;
        while(i < length){
            items[i] = (SchedulerItem)running.get(j);
            i++;
            j++;
        }
        
        j=0;
        length = length + runningQueueSize;
        while(i < length){
            items[i] = (SchedulerItem)runningQueue.get(j);
            i++;
            j++;
        }
        
        j = 0;
        length = length + scheduledItemsSize;
        while(i < length){
            items[i] = (SchedulerItem)scheduledItems.get(j);
            i++;
            j++;
        }
        
        return items;
    }     
    
    /*
     * notifies listeners that a change in the model has occured and
     * they should refresh based upon it
     */
    private void notifyListeners(){
        Iterator iter = listeners.iterator();
        while(iter.hasNext()){
            SchedulerContentModelListener listener = (SchedulerContentModelListener)iter.next();
            listener.refreshView();
        }
    }
    
    /*
     * sets up and starts the thread that monitors ProgressiveAlgorithms
     * for their progress and updates their views
     */
	private void setupMonitorThread(){
	    Thread monitor = new Thread(){
	        public void run(){
	            while(true){
	                if(!progressiveAlgorithms.isEmpty()){
	                    //query each for progress and update the progress bar
	                    for(int i = 0; i < progressiveAlgorithms.size(); i++){
	                        SchedulerItem item = (SchedulerItem)progressiveAlgorithms.get(i);
	                        item.monitorProgress();    	                        
	                    }
	                }
	                try {
                        sleep(500);
                    } catch (InterruptedException e) {}
	            }
	        }
	    };
	    monitor.start();
	}
	
    /*
     * Adds the given item into the running queue at the given index.
     * 
     * @param item the SchedulerItem to move into the running queue
     * @param index the index in the running queue of the given item
     */
    private void add(SchedulerItem item, int index){
        runningQueue.add(index, item);
        item.setDate("queued");
        item.setTime("queued");
        itemToVectorMap.put(item, runningQueue);
    }
    
    /*
     * Adds the given SchedulerItem into the scheduled items list.  This is
     * to be used for items that are scheduled at a particular time, as opposed
     * to items that are run immediately, which should use 
     * <code>add(SchedulerItem item, int index)</code>.
     * 
     * @param item the SchedulerItem to add into the scheduled items list
     */
    private void add(SchedulerItem item){
        boolean found = false;
        int i = 0;
        while(!found && i < scheduledItems.size()){
            SchedulerItem schedulerItem = (SchedulerItem)scheduledItems.get(i);
            if(schedulerItem.getCalendar().after(item.getCalendar())){
                found = true;
            }
            i++;
        }
        scheduledItems.add(i, item);
        itemToVectorMap.put(item, scheduledItems);
    }
    
    /*
     * Moves an item out of the scheduled items list and into the running queue
     * when its time has come to execute. In keeping with the semantics of the IVC
     * scheduler, this index is treated as either zero or non-zero, meaning that
     * the item will either be placed at the beginning of the running queue or
     * at the end, even if the non-zero index given is not at the end.
     * 
     * @param item the SchedulerItem to move into the running queue
     * @param index the index in the running queue of the given item
     */
    private void itemMovedToRunningQueue(SchedulerItem item, int index){
        scheduledItems.remove(item);
        item.setDate("queued");
        item.setTime("queued");
        //if the index is zero, it was added to the front
        if(index == 0)
            runningQueue.add(index, item);
        
        //otherwise it was added to the end. to be safe, since things
        //could have been removed, dont use the index, but rather put it
        //on the end
        else
            runningQueue.add(item);
        
        itemToVectorMap.put(item, runningQueue);
    }
    
    /*
     * Signals that the given SchedulerItem has started execution and should
     * be moved into the running list.
     * 
     * @param item the SchedulerItem that has started execution
     */
    private void itemStarted(SchedulerItem item){
        //either in running queue or scheduled items
        Vector vector = (Vector)itemToVectorMap.get(item);        
        vector.remove(item);
	    item.setDate("running");
	    item.setTime("running");
	    running.add(item);
	    itemToVectorMap.put(item, running);    
    }
    
    /*
     * Signals that the given SchedulerItem has finished execution and
     * should be moved into the finished list
     * 
     * @param item the SchedulerItem that has finished execution
     */
    private void itemFinished(SchedulerItem item){
        running.remove(item);
        finishedItems.add(item);
        itemToVectorMap.put(item, finishedItems);
    }
    
    /*
     * Moves the given item up one position in the running queue, signifying an
     * increase in the priority of the item.
     * 
     * @param item the SchedulerItem to move up in the running queue
     */
    private void moveUp(SchedulerItem item){
        if(canMoveUp(item)){
            int index = runningQueue.indexOf(item);
            runningQueue.remove(index);
            runningQueue.add(index - 1, item);
        }
    }

    /*
     * Moves the given item down one position in the running queue, signifying an
     * decrease in the priority of the item.
     * 
     * @param item the SchedulerItem to move down in the running queue
     */
    private void moveDown(SchedulerItem item){
        if(canMoveDown(item)){
            int index = runningQueue.indexOf(item);
            runningQueue.remove(index);
            runningQueue.add(index + 1, item);
        } 
    }

}
