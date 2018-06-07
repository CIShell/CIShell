package org.cishell.tests.ProgressTrackableAlgorithm;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;

public class AlgorithmTest implements Algorithm, ProgressTrackable {
	public static final int TOTAL_WORK_UNITS = 100;
	
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    ProgressMonitor monitor;
    
    public AlgorithmTest(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
        this.monitor = ProgressMonitor.NULL_MONITOR;
    }

    public Data[] execute() throws AlgorithmExecutionException {
    	monitor.start(ProgressMonitor.CANCELLABLE | 
			  ProgressMonitor.PAUSEABLE | 
			  ProgressMonitor.WORK_TRACKABLE, TOTAL_WORK_UNITS);
    	
		for (int i = 0; i < TOTAL_WORK_UNITS; ++i) {
			if (monitor.isCanceled()) {
				break;
			}
			else if (monitor.isPaused()) {
				--i;
			}
			monitor.worked(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new AlgorithmExecutionException(e);
			}
		}
		
		monitor.done();
	    return null;
    }

	public ProgressMonitor getProgressMonitor() {
		return this.monitor;
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}
}