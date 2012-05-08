package org.cishell.reference.gui.log;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

public class LogToConsole implements LogListener {
	private static final String NEWLINE = System.getProperty("line.separator");
	private boolean detailedMessages;

	/**
	 * 
	 * @param detailedMessages
	 *            If {@code false}, only the message will be printed. If
	 *            {@code true} then more details will be given.
	 */
	public LogToConsole(boolean detailedMessages) {
		this.detailedMessages = detailedMessages;
	}

	@Override
	public void logged(LogEntry entry) {
		if (!Utilities.logMessage(entry.getMessage(),
				Utilities.DEFAULT_IGNORED_PREFIXES)) {
			return;
		}

		String level = "";
		switch (entry.getLevel()) {
			case LogService.LOG_DEBUG:
				level = "DEBUG";
				break;
			case LogService.LOG_ERROR:
				level = "ERROR";
				break;
			case LogService.LOG_INFO:
				level = "INFO";
				break;
			case LogService.LOG_WARNING:
				level = "WARNING";
				break;
			default:
				level = "UNKNOWN LEVEL";
				break;
		}

		String logEntry = "";
		if (this.detailedMessages) {
			logEntry += "[" + entry.getBundle().getSymbolicName() + "]: "
					+ level + " " + entry.getMessage();

			if (entry.getException() != null) {
				logEntry += NEWLINE + "Exception: " + NEWLINE
						+ entry.getException();
			}
		} else {
			logEntry += entry.getMessage();
		}

		System.out.println(logEntry);
	}
}
