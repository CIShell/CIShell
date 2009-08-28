package org.cishell.streaming.prototype.streamcore;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;

public abstract class ConsumerAlgorithm<T> implements Algorithm {	
	private Stream<T> stream;
	
    
    @SuppressWarnings("unchecked") // TODO
	public ConsumerAlgorithm(Data[] data, Dictionary parameters,
			CIShellContext context) {
    	this.stream = (Stream<T>) data[0].getData();
    }

    
    public Data[] execute() throws AlgorithmExecutionException {    	
    	int nextToPrint = 0;
    	
    	while (!stream.isFinalEndpoint(nextToPrint)) {
    		int endpoint = stream.getCurrentEndpoint();
    		
    		for (int ii = nextToPrint; ii < endpoint; ii++) {
    			// TODO Debug only
    			System.out.println("About to consume "
    					+ stream.getValueAtTimestep(ii)
    					+ " from timestep "
    					+ ii);
    			consume(stream.getValueAtTimestep(ii));
    		}
    		
    		nextToPrint = endpoint;
    		
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
    	}
    	
    	return null;
    }
    
    public abstract void consume(T value);
}