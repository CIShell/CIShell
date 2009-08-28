package org.cishell.streaming.prototype;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.streaming.prototype.streamcore.ConsumerAlgorithm;
import org.osgi.service.log.LogService;

public class LoggingConsumerAlgorithm extends ConsumerAlgorithm<Object> {
	private LogService logger;

	
	@SuppressWarnings("unchecked") // TODO
	public LoggingConsumerAlgorithm(
			Data[] data, Dictionary parameters, CIShellContext context) {
		super(data, parameters, context);
		
		this.logger =
			(LogService) context.getService(LogService.class.getName());
	}

	
	@Override
	public void consume(Object value) {
		logger.log(LogService.LOG_INFO, "Consuming " + value);
	}
	
	
	public static class Factory implements AlgorithmFactory {
		@SuppressWarnings("unchecked") // TODO
		public Algorithm createAlgorithm(
				Data[] data, Dictionary parameters, CIShellContext context) {
	        return new LoggingConsumerAlgorithm(data, parameters, context);
	    }
	}
}
