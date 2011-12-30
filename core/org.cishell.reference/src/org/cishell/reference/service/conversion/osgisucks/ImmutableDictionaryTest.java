package org.cishell.reference.service.conversion.osgisucks;

import static org.junit.Assert.*;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ImmutableDictionaryTest {

	@Test
	public void testIterationOrder() {
		ImmutableDictionary<Integer, Integer> oneTwoThree = ImmutableDictionary.of(1, 1, 2, 2, 3, 3);
		Iterator<Integer> keys = oneTwoThree.keySet().iterator();
		assertEquals(Integer.valueOf(1), keys.next());
		assertEquals(Integer.valueOf(2), keys.next());
		assertEquals(Integer.valueOf(3), keys.next());
	}

	@Test
	public void testEqualsMaps() {
		ImmutableDictionary<Integer, Integer> oneTwo = ImmutableDictionary.of(1, 1, 2, 2);
		
		assertEquals(oneTwo, ImmutableMap.of(1,1,2,2));
		assertEquals(oneTwo, ImmutableMap.of(2,2,1,1));
		assertFalse(oneTwo.equals(ImmutableMap.of(2,1,1,2)));
		
		assertEquals(ImmutableMap.of(1,1,2,2), oneTwo);
		assertEquals(ImmutableMap.of(2,2,1,1), oneTwo);
	}
	
	@Test
	public void testEqualsDictonaries() {
		Dictionary<Integer, Integer> dict = new Hashtable<Integer, Integer>();
		dict.put(1, 1);
		dict.put(2, 2);
		
		ImmutableDictionary<Integer, Integer> oneTwo = ImmutableDictionary.of(1, 1, 2, 2);
		
		assertTrue(oneTwo.equals(dict));
		assertTrue(dict.equals(oneTwo));
	}
	
	@Test
	public void testEqualsProperties() {
		Dictionary<Object,Object> dict = new Properties();
		dict.put("hi", "there");
		dict.put("bye", "bye");
		
		ImmutableDictionary<Object, Object> oneTwo = ImmutableDictionary.<Object,Object>of("hi", "there", "bye", "bye");
		
		assertTrue(oneTwo.equals(dict));
		assertTrue(dict.equals(oneTwo));
	}
}
