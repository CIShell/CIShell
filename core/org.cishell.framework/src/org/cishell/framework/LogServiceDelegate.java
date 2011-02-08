package org.cishell.framework;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class LogServiceDelegate implements LogService {
	private ServiceReference uniqueServiceReference;
	private LogService actualLogService;

	public LogServiceDelegate(
			ServiceReference uniqueServiceReference, LogService actualLogService) {
		this.uniqueServiceReference = uniqueServiceReference;
		this.actualLogService = actualLogService;
	}

	public void log(int level, String message) {
		this.actualLogService.log(this.uniqueServiceReference, level, message);
	}

	public void log(int level, String message, Throwable exception) {
		this.actualLogService.log(this.uniqueServiceReference, level, message, exception);
	}

	public void log(ServiceReference serviceReference, int level, String message) {
		this.actualLogService.log(serviceReference, level, message);
	}

	public void log(
			ServiceReference serviceReference, int level, String message, Throwable exception) {
		this.actualLogService.log(serviceReference, level, message, exception);
	}
}