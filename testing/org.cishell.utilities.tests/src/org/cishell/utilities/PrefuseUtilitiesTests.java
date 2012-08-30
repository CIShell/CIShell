package org.cishell.utilities;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cishell.utilities.PrefuseUtilities.UninterpretableObjectException;
import org.junit.Test;

public class PrefuseUtilitiesTests {

	@Test
	/**
	 * Test Objects that might be from the Prefuse wrapping issue.
	 */
	public void testValidWrappedObjects() {
		List<Pair<?, ?>> wrappedAndAnswers = new ArrayList<Pair<?, ?>>();

		// integer
		wrappedAndAnswers.add(new Pair(Integer.valueOf(1), Integer.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new Integer[] { Integer.valueOf(1) },
				Integer.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new Integer[0], null));

		// long
		wrappedAndAnswers.add(new Pair(Long.valueOf(1L), Long.valueOf(1L)));
		wrappedAndAnswers.add(new Pair(new Long[] { Long.valueOf(1L) }, Long
				.valueOf(1L)));
		wrappedAndAnswers.add(new Pair(new Long[0], null));

		// string
		wrappedAndAnswers.add(new Pair(String.valueOf(1), String.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new String[] { String.valueOf(1) },
				String.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new String[0], null));

		// boolean
		wrappedAndAnswers.add(new Pair(Boolean.valueOf(true), Boolean
				.valueOf(true)));
		wrappedAndAnswers.add(new Pair(new Boolean[] { Boolean.valueOf(true) },
				Boolean.valueOf(true)));
		wrappedAndAnswers.add(new Pair(new Boolean[0], null));

		// double
		wrappedAndAnswers.add(new Pair(Double.valueOf(1), Double.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new Double[] { Double.valueOf(1) },
				Double.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new Double[0], null));

		// float
		wrappedAndAnswers.add(new Pair(Float.valueOf(1), Float.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new Float[] { Float.valueOf(1) }, Float
				.valueOf(1)));
		wrappedAndAnswers.add(new Pair(new Float[0], null));

		for (Pair<?, ?> wrappedAndAnswer : wrappedAndAnswers) {
			Object result = PrefuseUtilities
					.removePrefuseArrayWrapper(wrappedAndAnswer
							.getFirstObject());
			Object answer = wrappedAndAnswer.getSecondObject();
			if (result != null && wrappedAndAnswer.getSecondObject() != null) {
				assertTrue(result + " did not match the expected: " + answer,
						result.equals(answer));
			} else {
				assertTrue(result == answer);
			}
		}
	}

	@Test
	/**
	 * Test objects that are NOT from a prefuse wrapping issue.
	 */
	public void testInvalidObjects() {
		int[] actualArray = new int[] { 1, 2, 3 };
		assertTrue(actualArray == PrefuseUtilities
				.removePrefuseArrayWrapper(actualArray));

		try {
			PrefuseUtilities.removePrefuseArrayWrapper(null);
			fail();
		} catch (NullPointerException e) {
			// null was invalid input
		}
	}
}
