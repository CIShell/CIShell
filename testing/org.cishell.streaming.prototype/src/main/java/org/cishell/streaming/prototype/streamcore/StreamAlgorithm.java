package org.cishell.streaming.prototype.streamcore;

import java.util.Calendar;
import java.util.Dictionary;

import org.cishell.app.service.datamanager.DataManagerListener;
import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;

public abstract class StreamAlgorithm<T>
		implements Algorithm, DataManagerListener, SchedulerListener {
	private Data outStreamContainer;
	private Stream<T> stream;
	private DataManagerService dataManager;
	private SchedulerService scheduler;
		
    @SuppressWarnings("unchecked")
	public StreamAlgorithm(
    		Data[] data, Dictionary parameters, CIShellContext context) {
    	this.dataManager =
    		(DataManagerService) context.getService(
    				DataManagerService.class.getName());
		dataManager.addDataManagerListener(this);
		
		this.scheduler =
    		(SchedulerService) context.getService(
    				SchedulerService.class.getName());
		scheduler.addSchedulerListener(this);
    }

    
    public Data[] execute() throws AlgorithmExecutionException {
    	this.stream = createStream();
    	this.outStreamContainer =
    		new BasicData(stream, Stream.class.getName());
        return new Data[]{ outStreamContainer };
    }
    
    protected abstract Stream<T> createStream();    

    
    // *** DataManagerListener (TODO: Implement as appropriate)
    public void dataAdded(Data data, String label) {}
	public void dataLabelChanged(Data data, String label) {}
	public void dataSelected(Data[] data) {}
	public void dataRemoved(Data data) {
		if (outStreamContainer.equals(data)) {
			stream.stop();
			dataManager.removeDataManagerListener(this);
			scheduler.removeSchedulerListener(this);
		}
	}
	// *** DataManagerListener (TODO: Implement as appropriate)
	
	
	// *** SchedulerListener (TODO: Implement as appropriate)
	public void algorithmScheduled(Algorithm algorithm, Calendar time) {}
    public void algorithmRescheduled(Algorithm algorithm, Calendar time) {}
    public void algorithmUnscheduled(Algorithm algorithm) {}
    public void algorithmStarted(Algorithm algorithm) {}
    public void algorithmFinished(Algorithm algorithm, Data[] createdData) {}
    public void algorithmError(Algorithm algorithm, Throwable error) {}
    public void schedulerRunStateChanged(boolean isRunning) {
    	System.out.println("Scheduler run state changed!");
    	if (isRunning) {
    		stream.unpause();
    	} else {
    		stream.pause();
    	}
    }    
	public void schedulerCleared() {}
	// *** SchedulerListener (TODO: Implement as appropriate)
}