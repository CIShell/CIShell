package org.cishell.utilities.logging;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.osgi.service.log.LogService;

public class TestLogMessageHandler {

	@Test
	/**
	 * Test that the simple version can be constructed.
	 */
	public void testSimpleConstruction() {
		LogMessageHandler lmh = new LogMessageHandler();
	}

	@Test
	/**
	 * Test that the print stream version can be constructed
	 */
	public void testPrintStreamConstruction() {
		LogMessageHandler lmh = new LogMessageHandler(System.out);
	}

	@Test
	/**
	 * Test that the LogService version can be constructed
	 */
	public void testLogServiceConstruction() {
		LogMessageHandler lmh = new LogMessageHandler(new PrintStreamLogger());
	}

	@Test
	/**
	 * Test that adding a message type works.
	 */
	public void testAddMessageType() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LogMessageHandler lmh = new LogMessageHandler(new PrintStream(baos));

		String description = "Description";
		int maximumCount = 10;
		int logLevel = LogService.LOG_DEBUG;

		MessageType mt1 = lmh.addMessageType(description, maximumCount,
				logLevel);
		MessageType mt2 = lmh.addMessageType(description, maximumCount,
				logLevel);
		assertTrue(mt1 != null);
		assertTrue(mt2 != null);
		assertTrue(mt1 != mt2);

		lmh.addMessageType(null, maximumCount, logLevel);
		lmh.addMessageType(null, 0, logLevel);
		try {
			lmh.addMessageType(description, -8, logLevel);
			fail();
		} catch (IllegalArgumentException e) {
			// OK, caught the expected error
		}
	}

	@Test
	/**
	 * Test that handling the messages works correctly
	 */
	public void testMessageHandling() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LogMessageHandler lmh = new LogMessageHandler(new PrintStream(baos));

		int mt1Max = 10;
		MessageType mt1 = lmh.addMessageType("mt1", mt1Max,
				LogService.LOG_DEBUG);
		MessageType mt2 = lmh.addMessageType("mt2", 0, LogService.LOG_ERROR);

		String randomMessage1 = "asdf8ashg";
		lmh.handleMessage(mt2, randomMessage1);
		assertFalse(baos.toString().contains(randomMessage1));

		for (int i = 1; i <= mt1Max; i++) {
			String randomMessage = randomMessage1 + i;
			lmh.handleMessage(mt1, randomMessage);
			assertTrue(baos.toString().contains(randomMessage));
		}

		String randomMessage2 = randomMessage1 + (mt1Max + 1);
		lmh.handleMessage(mt1, randomMessage2);
		assertFalse(baos.toString().contains(randomMessage2));

	}

	@Test
	/**
	 * Test that messages for printing when the limit has been exceded work.
	 */
	public void testOverLimitPrinting() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LogMessageHandler lmh = new LogMessageHandler(new PrintStream(baos));

		int mt1Max = 10;
		MessageType mt1 = lmh.addMessageType("mt1", mt1Max,
				LogService.LOG_DEBUG);
		MessageType mt2 = lmh.addMessageType("mt2", 0, LogService.LOG_ERROR);

		String randomMessage1 = "asdf8ashg";
		lmh.handleMessage(mt1, randomMessage1);
		String output = baos.toString();
		lmh.printOverLimitMessageTypes();
		assertTrue(output.equals(baos.toString()));

		lmh.printOverLimitMessageTypes(LogService.LOG_DEBUG);
		assertTrue(output.equals(baos.toString()));

		lmh.handleMessage(mt2, randomMessage1);
		assertTrue(output.equals(baos.toString()));

		lmh.printOverLimitMessageTypes();
		String overlimitMessage = baos.toString();
		assertFalse(output.equals(overlimitMessage));
	}
}
