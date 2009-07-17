package edu.iu.scipolicy.utilities;

import static org.junit.Assert.*;

import org.cishell.utilities.StringUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringUtilitiesTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testImplodeStringArray() {
		try {
			String[] stringArray = new String[] { "test1", "test2" };
			
			String implodedStringArray =
				StringUtilities.implodeStringArray(stringArray, "!");
			
			if (!implodedStringArray.equals("test1!test2"))
				fail();
		}
		catch (Exception e) {
			fail();
		}
	}
}
