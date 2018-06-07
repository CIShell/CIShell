package org.cishell.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class StringUtilitiesTests {

	@Test
	/**
	 * Test the stripSurroundingQuotes method
	 */
	public void testStripSurroundingQuotes() {
		Map<String, String> quotedAndAnswer = new HashMap<String, String>();
		quotedAndAnswer.put("\"", "\"");
		quotedAndAnswer.put("\"\"", "");
		quotedAndAnswer.put("\"\"\"", "\"");
		quotedAndAnswer.put("\"Hello!\"", "Hello!");
		quotedAndAnswer.put("\"\"\"\"", "\"\"");
		quotedAndAnswer.put("\"\"Hello!\"\"", "\"Hello!\"");

		for (Entry<String, String> entry : quotedAndAnswer.entrySet()) {

			assertTrue(entry.getValue().equals(
					StringUtilities.stripSurroundingQuotes(entry.getKey())));

		}

		try {
			StringUtilities.stripSurroundingQuotes(null);
			fail();
		} catch (NullPointerException e) {
			// OK, expected exception
		}
	}

	@Test
	/**
	 * Test the countOccurrencesOfChar method
	 */
	public void testCountOccurrencesOfChar() {
		Map<Pair<CharSequence, String>, Integer> inputAndOutput = new HashMap<Pair<CharSequence, String>, Integer>();
		inputAndOutput.put(new Pair<CharSequence, String>("", "1"), 0);
		inputAndOutput.put(new Pair<CharSequence, String>(
				"#128ahf-a-dgfba-d-f!", "@"), 0);
		inputAndOutput.put(new Pair<CharSequence, String>(
				"#128ahf-a-dgfba-d-f!", "1"), 1);
		inputAndOutput.put(new Pair<CharSequence, String>(
				"#128ahf-a-dgfba-d-f!", "#"), 1);
		inputAndOutput.put(new Pair<CharSequence, String>(
				"David is very nice!", "i"), 3);
		inputAndOutput.put(new Pair<CharSequence, String>("(((((((._))", ")"),
				2);

		for (Entry<Pair<CharSequence, String>, Integer> entry : inputAndOutput
				.entrySet()) {

			int answer = StringUtilities.countOccurrencesOfChar(entry.getKey()
					.getFirstObject(), entry.getKey().getSecondObject()
					.toCharArray()[0]);
			assertTrue(answer == entry.getValue());
		}

		try {
			StringUtilities.countOccurrencesOfChar(null, '0');
			fail();
		} catch (NullPointerException e) {
			// OK, expected exception
		}
	}

	@Test
	/**
	 * Test the {@link StringUtilities#multiply(String, int)} method.
	 */
	public void testMultipy() {
		try {
			StringUtilities.multiply(null, 0);
			fail();
		} catch (NullPointerException e) {
			// OK, expected exception
		}

		try {
			StringUtilities.multiply("target", -5);
			fail();
		} catch (IllegalArgumentException e) {
			// Ok, expected exception was caught
		}
		
		Map<Pair<String, Integer>, String> inputOutput = new HashMap<Pair<String,Integer>, String>();
		inputOutput.put(new Pair<String, Integer>("####", 5), "####################");
		inputOutput.put(new Pair<String, Integer>("####", 0), "");
		inputOutput.put(new Pair<String, Integer>("abcde", 2), "abcdeabcde");
		inputOutput.put(new Pair<String, Integer>("", 2), "");
		for (Entry<Pair<String, Integer>, String> entry : inputOutput.entrySet()) {
			String answer = StringUtilities.multiply(entry.getKey().getFirstObject(), entry.getKey().getSecondObject());
			assertTrue(answer + ": " + entry.getValue(), entry.getValue().equals(answer));
		}
	}
	
	/**
	 * Test the {@link StringUtilities#filterEmptyStrings(String[])}
	 * 
	 */
	@Test
	public void testFilterEmptyStrings() {
		List<String> emptyStrings = Arrays.asList(new String[] {""});
		List<String> notEmptyStrings = Arrays.asList(new String[] {"1", " 2 ", "3\n", " "});
		List<String> stringsToFilter = new ArrayList<String>(emptyStrings);
		stringsToFilter.addAll(notEmptyStrings);
		
		String[] filtered = StringUtilities.filterEmptyStrings(stringsToFilter.toArray(new String[0]));
		assertTrue(Arrays.asList(filtered).containsAll(notEmptyStrings));
		for (String empty : emptyStrings) {
			assertFalse(Arrays.asList(filtered) + " contains '" + empty + "'.", Arrays.asList(filtered).contains(empty));
		}
		
	}
}
