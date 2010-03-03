package org.cishell.utilities.osgi.logging;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.log.LogService;

public class LogMessageHandler {
	private LogService logger;
	private Map<MessageTypeDescriptor, MessageType> messageTypes =
		new HashMap<MessageTypeDescriptor, MessageType>();

	public LogMessageHandler(LogService logger) {
		this.logger = logger;
	}

	/**
	 * If typeIndicator is already an added message type, its count will be
	 * reset and maximum count overridden.
	 */
	public MessageTypeDescriptor addMessageType(String description, int maximumCount) {
		MessageTypeDescriptor typeIndicator = new MessageTypeDescriptor();
		this.messageTypes.put(typeIndicator, new MessageType(description, maximumCount));

		return typeIndicator;
	}

	/// message will always be logged if typeIndicator has not been added prior to calling this.
	public void handleMessage(MessageTypeDescriptor typeIndicator, int logLevel, String message) {
		MessageType messageType = this.messageTypes.get(typeIndicator);

		if (messageType != null) {
			messageType.logMessage(logLevel, message, this.logger);
		} else {
			this.logger.log(logLevel, message);
		}
	}

	public void printOverloadedMessageTypes(int logLevel) {
		for (MessageType messageType : this.messageTypes.values()) {
			if (messageType.isOverloaded()) {
				this.logger.log(logLevel, messageType.reportOverloads());
			}
		}
	}

	// TODO: Javadoc what this is all about.  (I will later.)
	public class MessageTypeDescriptor {}

	private static class MessageType {
		private String description;
		private int maximumCount;
		private int foundCount = 0;
		private int overloadedCount = 0;

		public MessageType(String description, int maximumCount) {
			this.description = description;
			this.maximumCount = maximumCount;
		}

		public void logMessage(int logLevel, String message, LogService logger) {
			if (shouldStillLog()) {
				logger.log(logLevel, message);
				this.foundCount++;
			} else {
				this.overloadedCount++;
			}
		}

		public String reportOverloads() {
			return "Found " + this.overloadedCount + " more " + this.description + ".";
		}

		private boolean shouldStillLog() {
			return this.foundCount < this.maximumCount;
		}

		private boolean isOverloaded() {
			return this.overloadedCount > 0;
		}
	}
}