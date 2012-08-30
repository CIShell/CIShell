package org.cishell.utilities.arrayutilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.cishell.utilities.ArrayUtilities;
import org.junit.Test;

public class IndexOfTests extends TestCase {
	Object[] array;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		array = new Object[]{1, 2, 3, 4, true, 'a', "ABC", null, true};
	}
	
	@Test
	public void testIndexOfTestNullTarget() {
		ArrayUtilities.indexOf(array, null);
	}
	
	@Test
	public void testIndexOfTestNullArray() {
		ArrayUtilities.indexOf(null, 'a');
	}
	
	@Test
	public void testIndexOfTestParamsNull() {
		ArrayUtilities.indexOf(null, null);
	}
	
	@Test
	public void testIndexOfTestCorrectIndex() {
		assertTrue(ArrayUtilities.indexOf(array, 1) == Arrays.asList(array).indexOf(1));
		assertTrue(ArrayUtilities.indexOf(array, 2) == Arrays.asList(array).indexOf(2));
		assertTrue(ArrayUtilities.indexOf(array, 3) == Arrays.asList(array).indexOf(3));
		assertTrue(ArrayUtilities.indexOf(array, 4) == Arrays.asList(array).indexOf(4));
		assertTrue(ArrayUtilities.indexOf(array, true) == Arrays.asList(array).indexOf(true));
		assertTrue(ArrayUtilities.indexOf(array, 'a') == Arrays.asList(array).indexOf('a'));
		assertTrue(ArrayUtilities.indexOf(array, "ABC") == Arrays.asList(array).indexOf("ABC"));
		assertTrue(ArrayUtilities.indexOf(array, null) == Arrays.asList(array).indexOf(null));
	}
	
	@Test
	public void testIndexOfWithDoubleOccuringObjects() {
		assertTrue(ArrayUtilities.indexOf(array, true) == Arrays.asList(array).indexOf(true));
		List<Object> list = new ArrayList(Arrays.asList(array));
		list.remove(true);
		array = list.toArray();
		int correct = Arrays.asList(array).indexOf(true);
		assertTrue(ArrayUtilities.indexOf(array, true) == correct);
	}
	
	@Test
	public void testObjectNotInArray() {
		assertTrue(ArrayUtilities.indexOf(array, "My Name.") == -1);
	}
}
