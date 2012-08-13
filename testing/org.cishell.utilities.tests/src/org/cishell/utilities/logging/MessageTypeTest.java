package org.cishell.utilities.logging;

import static org.junit.Assert.*;

import org.junit.Test;
import org.osgi.service.log.LogService;

public class MessageTypeTest {

	@Test
	/**
	 * Test that the parameters are set correctly
	 */
	public void checkParameters() {
		String description = "description";
		int maximumCount = 10;
		int logDebug = LogService.LOG_DEBUG;
		MessageType mt = new MessageType(description, maximumCount, logDebug);
		assertTrue(description.equals(mt.getDescription()));
		assertTrue(maximumCount == mt.getLogLimit());
		assertTrue(logDebug == mt.getLogLevel());
	}

	@Test
	/**
	 * Test that invalid parameters are handled correctly
	 */
	public void testInvalidParameters() {
		String description = "description";
		int maximumCount = -1;
		int logDebug = LogService.LOG_DEBUG;
		try {
			MessageType mt = new MessageType(description, maximumCount,
					logDebug);
			fail();
		} catch (IllegalArgumentException e) {
			// Ok, caught the expected exception
		}
	}

	@Test
	/**
	 * Test that the funtionality of the maximum count works correctly
	 */
	public void testFunctionality() {
		String description = "description";
		int maximumCount = 10;
		int logDebug = LogService.LOG_DEBUG;
		MessageType mt = new MessageType(description, maximumCount, logDebug);

		for (int i = 1; i <= maximumCount; i++) {
			mt.reportMessageReceived();
			assertTrue(mt.shouldStillLog());
			assertTrue(mt.messagesOverLimit() == 0);
		}

		mt.reportMessageReceived();
		assertFalse(mt.shouldStillLog());
		assertTrue(mt.messagesOverLimit() == 1);

	}
}
