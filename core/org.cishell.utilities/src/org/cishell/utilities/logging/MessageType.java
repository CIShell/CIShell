package org.cishell.utilities.logging;

import com.google.common.base.Preconditions;

/**
 * A holder for a type of message. It tracks the description of this type of
 * message, how many times the message has been logged, the number of times the
 * message should be logged, and the level at which messages should be logged.
 * 
 * @author David M. Coe - david.coe+CNS@gmail.com
 * 
 */
public class MessageType {
	private final String description;
	private final int logLimit;
	private int loggedCount;
	private final int logLevel;

	MessageType(String description, int logLimit, int logLevel) {
		Preconditions.checkArgument(logLimit >= 0);
		this.description = description;
		this.logLimit = logLimit;
		this.loggedCount = 0;
		this.logLevel = logLevel;
	}

	int getLogLevel() {
		return this.logLevel;
	}

	String getDescription() {
		return this.description;
	}

	int getLogLimit() {
		return this.logLimit;
	}

	/**
	 * Report that a message was received.
	 */
	void reportMessageReceived() {
		this.loggedCount++;
	}

	/**
	 * Report how many messages over the limit this type is
	 * 
	 * @return The number of messages over the limit
	 */
	int messagesOverLimit() {
		int overage = this.loggedCount - this.logLimit;

		if (overage < 0) {
			overage = 0;
		}
		return overage;
	}

	/**
	 * Should messages continue to be logged for this type?
	 * 
	 * @return If the limit for the number of messages to be logged has been
	 *         reached.
	 */
	boolean shouldStillLog() {
		return messagesOverLimit() == 0;
	}
}