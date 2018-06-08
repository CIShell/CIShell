package org.cishell.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ArrayListUtilitiesTests {

	@Test
	/**
	 * Test the makePreview method
	 */
	public void testMakePreview() {
		String separator = ", ";
		String ellipsis = "...";
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
		int requestedPrefixSize = 2;
		int requestedSuffixSize = 1;
		String result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("1, 2, ..., 6".equals(result));
		
		requestedPrefixSize = 7;
		requestedSuffixSize = 7;
		result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("1, 2, 3, 4, 5, 6".equals(result));
		
		requestedPrefixSize = 0;
		requestedSuffixSize = 2;
		result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("..., 5, 6".equals(result));
		
		requestedPrefixSize = 2;
		requestedSuffixSize = 0;
		result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("1, 2, ...".equals(result));
		
		requestedPrefixSize = 6;
		requestedSuffixSize = 0;
		result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("1, 2, 3, 4, 5, 6".equals(result));
		
		requestedPrefixSize = 0;
		requestedSuffixSize = 6;
		result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("1, 2, 3, 4, 5, 6".equals(result));
		
		requestedPrefixSize = 5;
		requestedSuffixSize = 0;
		result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("1, 2, 3, 4, 5, ...".equals(result));
		
		requestedPrefixSize = 7;
		requestedSuffixSize = 7;
		result = ArrayListUtilities.makePreview(Arrays.asList(1), requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("1".equals(result));
		
		requestedPrefixSize = 2;
		requestedSuffixSize = 2;
		result = ArrayListUtilities.makePreview(Arrays.asList(list, list ,list, list, list ,list, list, list ,list, list, list ,list, list), requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
		assertTrue("[1, 2, 3, 4, 5, 6], [1, 2, 3, 4, 5, 6], ..., [1, 2, 3, 4, 5, 6], [1, 2, 3, 4, 5, 6]".equals(result));
		
		requestedPrefixSize = -1;
		requestedSuffixSize = -1;
		try {
			result = ArrayListUtilities.makePreview(list, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
			System.out.println(requestedPrefixSize + "," + requestedSuffixSize + ": "  + result);
			fail();
		} catch (IllegalArgumentException e) {
			// Ok, an error was expected
		}

		requestedPrefixSize = 7;
		requestedSuffixSize = 7;
		try {
			result = ArrayListUtilities.makePreview(null, requestedPrefixSize, requestedSuffixSize, separator, ellipsis);
			System.out.println("null = " + result);
			fail();
		} catch (NullPointerException e) {
			// Ok, an error was expected.
		}
	}
	
	/**
	 * Test the {@link ArrayListUtilities#unionCollections(java.util.Collection, java.util.Collection, java.util.Collection)} method.
	 */
	@Test
	public void testUnionCollections() {
		Set<Integer> s1 = new HashSet<Integer>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5}));
		Set<Integer> s2 = new HashSet<Integer>(Arrays.asList(new Integer[]{6, 7, 8, 9, 10}));
		Set<Integer> skip = new HashSet<Integer>();
		
		Collection<Integer> union = ArrayListUtilities.unionCollections(s1, s2, skip);
		assertTrue(union.containsAll(s1));
		assertTrue(union.containsAll(s2));
		
		skip = new HashSet<Integer>(Arrays.asList(new Integer[]{2, 9}));
		union = ArrayListUtilities.unionCollections(s1, s2, skip);
		assertFalse(union.containsAll(s1));
		assertFalse(union.containsAll(s2));
		for (Integer i : skip) {
			assertTrue(!union.contains(i));
		}
	}
}
