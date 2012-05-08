package org.cishell.reference.gui.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

/**
 * This is a basic implementation. It writes log records to files
 * 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 * @author Felix Terkhorn (terkhorn@gmail.com)
 * @author David M. Coe (david.coe@gmail.com)
 */
public class LogToFile implements LogListener {
	private static final String NEWLINE = System.getProperty("line.separator");

	// Specify file handler properties
	private static final int LIMIT = 100000; // 100 kb
	private static final int MAX_NUM_LOG_FILES = 10;
	private static final boolean APPEND = true;

	// Specify whether or not this logger should send its output to its parent
	// Logger
	private static final boolean SEND_TO_PARENT_LOGGER = false;

	private static final String LOGGER_NAME = "org.cishell.logger.file_logger";

	// Put logs in "User working directory"/logs
	private static final String LOG_DIRECTORY = System.getProperty("user.dir")
			+ File.separator + "logs";
	private static final String LOG_PREFIX = "cishell-user";

	private Logger logger;

	/**
	 * 
	 * @throws LogToFileCreationException
	 *             if there is a problem creating the LogToFile
	 */
	public LogToFile() throws LogToFileCreationException {
		try {
			// Create an appending file handler
			validateDirectory(LOG_DIRECTORY);

			// For more pattern info, see
			// http://docs.oracle.com/javase/6/docs/api/java/util/logging/FileHandler.html
			String logPattern = LOG_DIRECTORY + File.separator + LOG_PREFIX
					+ "-" + getTimestamp() + ".%u.%g.log";

			FileHandler handler = new FileHandler(logPattern, LIMIT,
					MAX_NUM_LOG_FILES, APPEND);

			handler.setFormatter(new SimpleFormatter());

			this.logger = Logger.getLogger(LOGGER_NAME);
			this.logger.addHandler(handler);
			this.logger.setUseParentHandlers(SEND_TO_PARENT_LOGGER);
			this.logger.setLevel(Level.ALL);
		} catch (InvalidDirectoryException e) {
			throw new LogToFileCreationException(
					"The LogToFile logger could not be created.", e);
		} catch (SecurityException e) {
			throw new LogToFileCreationException(
					"The LogToFile logger could not be created.", e);
		} catch (IOException e) {
			throw new LogToFileCreationException(
					"The LogToFile logger could not be created.", e);
		}
	}

	/**
	 * Represents the failure to instantiate LogToFile.
	 * 
	 * @author David M. Coe (david.coe@gmail.com)
	 * 
	 */
	public static class LogToFileCreationException extends Exception {
		private static final long serialVersionUID = -5837126430378342519L;

		public LogToFileCreationException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Creates or verifies that a directory exists at the
	 * {@code directoryPathname} location.
	 * 
	 * @param directoryPathname
	 *            the pathname of the directory to be created/tested
	 * @throws InvalidDirectoryException
	 *             If a directory does not exist and cannot be created at the
	 *             {@code directoryPathname} location.
	 */
	private static void validateDirectory(String directoryPathname)
			throws InvalidDirectoryException {
		File file = new File(directoryPathname);
		if (file.exists()) {
			if (file.isDirectory()) {
				return;
			}

			throw new InvalidDirectoryException(
					"There is a file, not a directory at '" + directoryPathname
							+ "'.");
		}

		if (!file.mkdirs()) {
			throw new InvalidDirectoryException(
					"The file directory did not exist, but could not be made at '"
							+ directoryPathname + "'.");
		}
	}

	/**
	 * Represents a failure to validate the directory.
	 * 
	 * @author David M. Coe (david.coe@gmail.com)
	 * 
	 */
	private static class InvalidDirectoryException extends Exception {
		private static final long serialVersionUID = 6314769551833577561L;

		protected InvalidDirectoryException(String message) {
			super(message);
		}
	}

	@Override
	public void logged(LogEntry entry) {
		String message = entry.getMessage();
		if (!Utilities.logMessage(message, Utilities.DEFAULT_IGNORED_PREFIXES)) {
			return;
		}
		
		Level level;

		switch (entry.getLevel()) {
			case LogService.LOG_DEBUG:
				level = Level.FINEST;
				break;
			case LogService.LOG_ERROR:
				level = Level.SEVERE;
				break;
			case LogService.LOG_INFO:
				level = Level.INFO;
				break;
			case LogService.LOG_WARNING:
				level = Level.WARNING;
				break;
			default:
				level = Level.SEVERE;
				break;
		}
		
		String logEntry = "";
		logEntry += message + NEWLINE;
		
		if (entry.getException() != null) {
			logEntry += "Exception: " + NEWLINE + entry.getException();
		}
		
		this.logger.log(level, logEntry);
	}

	private static String getTimestamp() {
		/*
		 * We can set any date time format we want. For the legend on different
		 * formats check
		 * http://java.sun.com/j2se/1.4.2/docs/api/java/text/SimpleDateFormat
		 * .html
		 */

		String dateFormat = "MM-dd-yyyy-hh-mm-a";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		String timestamp = simpleDateFormat.format(Calendar.getInstance()
				.getTime());

		return timestamp;
	}

}
