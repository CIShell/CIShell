package org.cishell.utilities;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SetUtilitiesTest {
	@Test
	public void testGetKeysOfMapEntrySetWithValue() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("Hello", 1);
		map.put("Goodbye", 2);
		map.put(null, 2);
		
		Collection<String> oneResult = SetUtilities.getKeysOfMapEntrySetWithValue(map.entrySet(), 1);
		assertTrue(oneResult.size() == 1);
		assertTrue(oneResult.contains("Hello"));
		assertFalse(oneResult.contains("Goodbye"));
		assertFalse(oneResult.contains(null));
		
		Collection<String> twoResult = SetUtilities.getKeysOfMapEntrySetWithValue(map.entrySet(), 2);
		assertTrue(twoResult.size() == 2);
		assertTrue(twoResult.contains("Goodbye"));
		assertTrue(twoResult.contains(null));
		assertFalse(twoResult.contains("Hello"));

	}
}
