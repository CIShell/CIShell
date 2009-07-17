package edu.iu.scipolicy.utilities;

import static org.junit.Assert.fail;

import java.util.Enumeration;

import org.cishell.utilities.DefaultDictionary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultDictionaryTest {
	private Shezam bamBam;
	
	@Before
	public void setUp() throws Exception {
		this.bamBam = new Shezam();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSize() {
		DefaultDictionary testDictionary = new DefaultDictionary(this.bamBam);
		
		// "Get" a value and make sure the size is still 0 afterwards.
		// (NOTE: This test may not really be valid because subclasses may wish to
		// actually store such keys.)
		testDictionary.get(new Shezam());
		
		if (testDictionary.size() != 0)
			fail();
	}

	@Test
	public void testIsEmpty() {
		DefaultDictionary testDictionary = new DefaultDictionary(this.bamBam);
		
		// "Get" a value and make sure the size is still 0 afterwards.
		// (NOTE: This test may not really be valid because subclasses may wish to
		// actually store such keys.)
		testDictionary.get(new Shezam());
		
		if (!testDictionary.isEmpty())
			fail();
	}

	@Test
	public void testElements() {
		DefaultDictionary testDictionary = new DefaultDictionary(this.bamBam);
		
		// "Get" a value and make sure the size is still 0 afterwards.
		// (NOTE: This test may not really be valid because subclasses may wish to
		// actually store such keys.)
		testDictionary.get(new Shezam());
		
		Enumeration elements = testDictionary.elements();
		
		if (elements.hasMoreElements())
			fail();
	}

	@Test
	public void testGetObject() {
		DefaultDictionary testDictionary = new DefaultDictionary(this.bamBam);
		
		// "Get" a value and make sure the size is still 0 afterwards.
		// (NOTE: This test may not really be valid because subclasses may wish to
		// actually store such keys.)
		if (testDictionary.get("Shezam!") != this.bamBam)
			fail();
	}

	@Test
	public void testKeys() {
		DefaultDictionary testDictionary = new DefaultDictionary(this.bamBam);
		
		// "Get" a value and make sure the size is still 0 afterwards.
		// (NOTE: This test may not really be valid because subclasses may wish to
		// actually store such keys.)
		testDictionary.get(new Shezam());
		
		Enumeration keys = testDictionary.keys();
		
		if (keys.hasMoreElements())
			fail();
	}

	@Test
	public void testPutObjectObject() {
		DefaultDictionary testDictionary = new DefaultDictionary(this.bamBam);
		
		// "Get" a value and make sure the size is still 0 afterwards.
		// (NOTE: This test may not really be valid because subclasses may wish to
		// actually store such keys.)
		testDictionary.put(this.bamBam, new Shezam());
		
		if (testDictionary.get(this.bamBam) == this.bamBam)
			fail();
	}

	@Test
	public void testRemoveObject() {
		// Nothing happens if an object that's not in a Dictionary is removed.
	}
	
	private class Shezam {
	};
}
