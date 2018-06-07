package org.cishell.streaming.prototype;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.streaming.prototype.streamlib.Stream;
import org.osgi.service.log.LogService;

public class Printer implements Algorithm {
	
	private Stream<String> stream;
	private LogService logger;
	
    
    public Printer(Data[] data, Dictionary parameters, CIShellContext context) {
    	this.stream = (Stream<String>) data[0].getData();
        this.logger = (LogService) context.getService(LogService.class.getName());
    }

    public Data[] execute() throws AlgorithmExecutionException {
    	
    	int nextToPrint = 0;
    	while(!stream.isFinalEndpoint(nextToPrint)) {
    		int endpoint = stream.getCurrentEndpoint();
    		for(int ii = nextToPrint; ii < endpoint; ii++) {
    			logger.log(LogService.LOG_INFO, "Received: " + stream.getValueAtTimestep(ii));
    		}
    		nextToPrint = endpoint;
    		try {
					Thread.sleep(100);
			} catch (InterruptedException e) {}
    	}
    	return null;
    }   
}