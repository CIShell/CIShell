package org.cishell.testing.convertertester.core.tester2.fakelogger;

import java.util.ArrayList;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import java.util.List;

public class FakeLogService implements LogService {

	
	private List logMessages;
	private LogService realLog;
	
	public FakeLogService(LogService realLogService) {
		this.realLog = realLogService;
		
		this.logMessages = new ArrayList();
	}
	
	public void log(int level, String message) {
		if (level == LOG_ERROR | level == LOG_WARNING) {
			this.logMessages.add(new LogEntry(message));
		} else {
			this.realLog.log(level, message);
		}
	}

	public void log(int level, String message, Throwable exception) {
		if (level == LOG_ERROR | level == LOG_WARNING) {
			this.logMessages.add(new LogEntry(message, exception));
		} else {
			this.realLog.log(level, message, exception);
		}
	}

	public void log(ServiceReference sr, int level, String message) {
		if (level == LOG_ERROR | level == LOG_WARNING) {
			this.logMessages.add(new LogEntry(message));
		} else {
			this.realLog.log(sr, level, message);
		}
	}

	public void log(ServiceReference sr, int level, String message,
			Throwable exception) {
		if (level == LOG_ERROR | level == LOG_WARNING) {
			this.logMessages.add(new LogEntry(message, exception));
		} else {
			this.realLog.log(sr, level, message, exception);
		}
	}
	
	public LogEntry[] getLogEntries() {
		return (LogEntry[])
		this.logMessages.toArray(new LogEntry[0]);
	}
	
	public void clearLogEntries() {
		this.logMessages = new ArrayList();
	}
	
	public boolean hasLogEntries() {
		return this.logMessages.size() > 0;
	}
	

}
