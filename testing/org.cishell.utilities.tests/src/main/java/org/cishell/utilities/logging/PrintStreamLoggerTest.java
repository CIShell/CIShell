package org.cishell.utilities.logging;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Tests for the Logger class.
 * 
 * @author David M. Coe - david.coe+CNS@gmail.com
 * 
 */
public class PrintStreamLoggerTest {
	@Test
	/**
	 * Test creating the default logger
	 */
	public void createSimpleLogger() {
		PrintStreamLogger l = new PrintStreamLogger();
	}

	@Test
	/**
	 * Test creating a bad, null printstream logger
	 */
	public void createNullPrintStreamLogger() {
		PrintStream ps = null;
		try {
			PrintStreamLogger l = new PrintStreamLogger(ps);
			fail("Null did not throw an exception for the PrintStream");
		} catch (NullPointerException e) {
			// Ok, got the expected exception
		}
	}

	@Test
	/**
	 * Test creating a valid print stream version of the logger
	 */
	public void createPrintStreamLogger() {
		PrintStreamLogger l = new PrintStreamLogger(System.out);
	}

	@Test
	/**
	 * Test actually logging the output
	 */
	public void testLogging() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStreamLogger l = new PrintStreamLogger(new PrintStream(baos));
		String random = "01345hg-0-0s-dfg";
		l.log(1, random);
		String logged = baos.toString();

		assertTrue("The random string was not logged!", logged.contains(random));
		Throwable throwable = new IllegalAccessError();
		l.log(1, random, throwable);
		ServiceReference sr = new ServiceReference<Float>() {

			@Override
			public int compareTo(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Bundle getBundle() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getProperty(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String[] getPropertyKeys() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Bundle[] getUsingBundles() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isAssignableTo(Bundle arg0, String arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		l.log(sr, 1, random);
		l.log(sr, 1, random, throwable);

	}

	@Test
	/**
	 * Test actually logging the output with the same input gets the same results
	 */
	public void testReproducableLogging() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStreamLogger l = new PrintStreamLogger(new PrintStream(baos));
		String random = "01345hg-0-0s-dfg";
		l.log(1, random);

		String logged1 = baos.toString();

		baos = new ByteArrayOutputStream();
		l = new PrintStreamLogger(new PrintStream(baos));
		l.log(1, random);

		String logged2 = baos.toString();

		assertTrue("The output differed for the same input!",
				logged1.equals(logged2));

	}

	@Test
	/**
	 * Test that different log levels do not have the same message
	 */
	public void testLogLevels() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStreamLogger l = new PrintStreamLogger(new PrintStream(baos));
		String random = "01345hg-0-0s-dfg";
		l.log(LogService.LOG_ERROR, random);

		String logged1 = baos.toString();

		baos = new ByteArrayOutputStream();
		l = new PrintStreamLogger(new PrintStream(baos));
		l.log(LogService.LOG_INFO, random);

		String logged2 = baos.toString();

		assertTrue("The output was the same for different!",
				!logged1.equals(logged2));

	}
}
