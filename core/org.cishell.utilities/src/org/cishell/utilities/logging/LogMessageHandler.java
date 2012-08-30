package org.cishell.utilities.logging;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.log.LogService;

import com.google.common.base.Preconditions;

/**
 * A wrapper that will hold several {@link MessageType}s to easily print a
 * certain number of messages to a log, then hold the rest and print a message
 * saying that more messages of each type were suppressed.
 * 
 * @author David M. Coe - david.coe+CNS@gmail.com
 * 
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
public class LogMessageHandler {
	private LogService logger;
	private Set<MessageType> messageTypes = new HashSet<MessageType>();

	/**
	 * A default message handler
	 */
	public LogMessageHandler() {
		this.logger = new PrintStreamLogger();
	}

	/**
	 * A message handler for {@link PrintStream}s
	 * 
	 * @param out
	 *            The PrintStream to be logged to; must not be <code>null</code>
	 */
	public LogMessageHandler(PrintStream out) {
		Preconditions.checkNotNull(out);
		this.logger = new PrintStreamLogger(out);
	}

	/**
	 * A message handler for a {@link LogService}.
	 * 
	 * @param logger
	 *            The {@link LogService} to log to; must not be
	 *            <code>null</code>
	 */
	public LogMessageHandler(LogService logger) {
		Preconditions.checkNotNull(logger);
		this.logger = logger;
	}

	/**
	 * Add a new {@link MessageType} to this logger.
	 * 
	 * @param description
	 *            The description of the {@code MessageType}
	 * @param maximumCount
	 *            The maximum number of messages of this type to print to the
	 *            log; must be at least 0.
	 * @param logLevel
	 *            The {@link LogService} log level to print messages to - should
	 *            probably be one of {@link LogService#LOG_ERROR}
	 *            {@link LogService#LOG_WARNING} {@link LogService#LOG_INFO}
	 *            {@link LogService#LOG_DEBUG}
	 * 
	 * @return A new {@code MessageType} with the parameters specified
	 */
	public MessageType addMessageType(String description, int maximumCount,
			int logLevel) {

		MessageType messageType = new MessageType(description, maximumCount,
				logLevel);
		this.messageTypes.add(messageType);

		return messageType;
	}

	/**
	 * Print the message of messageType if it has not reached the maximum
	 * 
	 * @param messageType
	 *            The messageType
	 * @param message
	 *            The message to be printed
	 */
	public void handleMessage(MessageType messageType, String message) {
		handleMessage(messageType, messageType.getLogLevel(), message);
	}

	// SOMEDAY: It is possible to add message formatting...
//	public void handleMessageF(MessageType messageType, String format,
//			Object... args) {
//		handleMessage(messageType, String.format(format, args),
//				messageType.getLogLevel());
//	}

	/**
	 * @see LogMessageHandler#handleMessage(MessageType, String) but with the
	 *      {@link LogService} level overridden by the value given.
	 * @param messageType
	 *            The message type
	 * @param message
	 *            The message to be printed
	 * @param logLevel
	 *            The overriding log level
	 */
	private void handleMessage(MessageType messageType, int logLevel,
			String message) {
		Preconditions.checkNotNull(messageType);

		messageType.reportMessageReceived();

		if (messageType.shouldStillLog()) {
			this.logger.log(logLevel, message);
		}

	}

	/**
	 * Print the overlimit message for any {@link MessageType} that is over the
	 * limit.
	 */
	public void printOverLimitMessageTypes() {
		for (MessageType messageType : this.messageTypes) {
			printOverLimitMessage(messageType, messageType.getLogLevel());
		}
	}

	/**
	 * Print the over limit message for any {@link MessageType} over the limit
	 * and override the log level with the given value.
	 * 
	 * @param logLevelOverride
	 *            The logLevel at which the message should be logged.
	 */
	public void printOverLimitMessageTypes(int logLevelOverride) {
		for (MessageType messageType : this.messageTypes) {
			printOverLimitMessage(messageType, logLevelOverride);
		}
	}

	/**
	 * Print the overlimit message for a given type and level
	 * 
	 * @param messageType
	 *            The messageType to have the over limit message printed
	 * @param logLevel
	 *            The level at which to log the message
	 */
	private void printOverLimitMessage(MessageType messageType, int logLevel) {
		if (messageType.messagesOverLimit() > 0) {
			this.logger.log(messageType.getLogLevel(),
					getOverLimitMessage(messageType));
		}
	}

	private static String getOverLimitMessage(MessageType messageType) {
		return messageType.messagesOverLimit() + " additional messages about "
				+ messageType.getDescription() + " were filtered.";
	}
}