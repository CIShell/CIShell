package org.cishell.testing.convertertester.core.tester2.fakelogger;

import org.cishell.framework.CIShellContext;
import org.osgi.service.log.LogService;

public class FakeLogCIShellContext implements CIShellContext {

	private CIShellContext realCContext;

	private FakeLogService fakeLogger;

	public FakeLogCIShellContext(CIShellContext realCContext) {
		this.realCContext = realCContext;
		this.fakeLogger = new FakeLogService((LogService) realCContext
				.getService(LogService.class.getName()));


	}

	public Object getService(String service) {
		if (service.equals(LogService.class.getName())) {
			return this.fakeLogger;	
		} else {
			return this.realCContext.getService(service);
		}
	}


	
	public LogEntry[] getLogEntries() {
		return this.fakeLogger.getLogEntries();
	}
	
	public void clearLogEntires() {
		this.fakeLogger.clearLogEntries();
	}
	
	public boolean hasLogEntries() {
		return this.fakeLogger.hasLogEntries();
	}
}
