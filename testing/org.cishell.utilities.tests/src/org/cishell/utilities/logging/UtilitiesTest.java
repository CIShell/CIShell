package org.cishell.utilities.logging;

import static org.junit.Assert.*;
import org.cishell.utilities.logging.Utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.junit.Test;
import org.osgi.service.log.LogService;

/**
 * Tests for the {@link Utilities} class.
 * 
 * @author David M. Coe - david.coe+CNS@gmail.com
 * 
 */
public class UtilitiesTest {
	@Test
	/**
	 * Test that all osgi levels are mapped to the correct java level
	 */
	public void testOSGIToJavaConversion() {
		Map<Integer, Level> translator = new HashMap<Integer, Level>();
		translator.put(LogService.LOG_DEBUG, Level.FINEST);
		translator.put(LogService.LOG_ERROR, Level.SEVERE);
		translator.put(LogService.LOG_INFO, Level.INFO);
		translator.put(LogService.LOG_WARNING, Level.WARNING);

		for (Entry<Integer, Level> entry : translator.entrySet()) {
			assertTrue(
					"The osgi level was not translated to java as "
							+ "expected.",
					entry.getValue().equals(
							Utilities.osgiLevelToJavaLevel(entry.getKey())));
		}
	}

	@Test
	/**
	 * Test that an unknown osgi level is mapped correctly
	 */
	public void testUnknownOSGILevel() {
		assertTrue("The unknown osgi level was "
				+ "not translated to java level correctly",
				Level.SEVERE.equals(Utilities
						.osgiLevelToJavaLevel(Integer.MAX_VALUE)));
	}
}
