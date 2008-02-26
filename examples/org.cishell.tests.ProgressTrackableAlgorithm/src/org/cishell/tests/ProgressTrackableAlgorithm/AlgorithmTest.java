package org.cishell.tests.ProgressTrackableAlgorithm;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;

public class AlgorithmTest implements Algorithm, ProgressTrackable {
//public class AlgorithmTest implements Algorithm {
	public static final int TOTAL_WORK_UNITS = 100;
	
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    ProgressMonitor monitor;
    
    public AlgorithmTest(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
    }

    public Data[] execute() {
    	if (monitor != null) {
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
				e.printStackTrace();
			}
    	}
    	monitor.done();
        return null;
    	}
    	else {
        	for (int i = 0; i < TOTAL_WORK_UNITS; ++i) {
        		try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}        		
        	}
    		return null;
    	}
    }

	public ProgressMonitor getProgressMonitor() {
		// TODO Auto-generated method stub
		return this.monitor;
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}
}