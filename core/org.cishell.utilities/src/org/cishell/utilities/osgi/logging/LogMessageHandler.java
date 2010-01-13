package org.cishell.utilities.osgi.logging;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.log.LogService;

// TODO: Get reviewed.
// TODO: Make 1.4 compatible and move to cishell utilities?
public class LogMessageHandler {
	private LogService logger;
	private Map<MessageTypeIndicator, MessageType> messageTypes =
		new HashMap<MessageTypeIndicator, MessageType>();

	public LogMessageHandler(LogService logger) {
		this.logger = logger;
	}

	/**
	 * If typeIndicator is already an added message type, its count will be
	 * reset and maximum count overridden.
	 */
	public MessageTypeIndicator addMessageType(
			String description, int maximumCount) {
		MessageTypeIndicator typeIndicator = new MessageTypeIndicator();
		this.messageTypes.put(
			typeIndicator, new MessageType(description, maximumCount));

		return typeIndicator;
	}

	/**
	 * logMessage will always be logged if typeIndicator has not been added
	 * prior to calling this.
	 */
	public void logMessage(
			MessageTypeIndicator typeIndicator,
			int logLevel,
			String logMessage) {
		MessageType messageType = this.messageTypes.get(typeIndicator);

		if (messageType != null) {
			if (messageType.messageLogged()) {
				this.logger.log(logLevel, logMessage);
			}
		} else {
			this.logger.log(logLevel, logMessage);
		}
	}

	public void printOverloadedMessageTypes(int logLevel) {
		for (MessageType messageType : this.messageTypes.values()) {
			if (messageType.wasOverloaded()) {
				this.logger.log(logLevel, messageType.toString());
			}
		}
	}

	public class MessageTypeIndicator {}

	private class MessageType {
		private String description;
		private int maximumCount;
		private int foundCount = 0;
		private int overLoadedCount = 0;

		public MessageType(String description, int maximumCount) {
			this.description = description;
			this.maximumCount = maximumCount;
		}

		public boolean hasAnyLeft() {
			return this.foundCount != this.maximumCount;
		}

		public boolean wasOverloaded() {
			return this.overLoadedCount > 0;
		}

		public boolean messageLogged() {
			if (hasAnyLeft()) {
				this.foundCount++;

				return true;
			} else {
				this.overLoadedCount++;

				return false;
			}
		}

		public String toString() {
			return
				"Found " +
				this.overLoadedCount +
				" more " +
				this.description +
				".";
		}
	}
}