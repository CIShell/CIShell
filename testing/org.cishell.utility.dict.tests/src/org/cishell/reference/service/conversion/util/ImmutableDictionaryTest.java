package org.cishell.reference.service.conversion.util;

import static org.junit.Assert.*;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.cishell.utility.dict.ImmutableDictionary;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;

public class ImmutableDictionaryTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();

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
	
	@Test
	public void testNoPut() {
		Dictionary<Integer, Integer> dict = ImmutableDictionary.of(1, 2);
		
		exception.expect(UnsupportedOperationException.class);
		dict.put(3, 4);
	}
	
	@Test
	public void testNoRemove() {
		Dictionary<Integer, Integer> dict = ImmutableDictionary.of(1, 2);
		
		exception.expect(UnsupportedOperationException.class);
		dict.remove(1);
	}
	
	@Test
	public void testNoClear() {
		ImmutableDictionary<Integer, Integer> dict = ImmutableDictionary.of(1, 2);
		
		exception.expect(UnsupportedOperationException.class);
		dict.clear();
	}
	
	@Test
	public void testNoPutAll() {
		ImmutableDictionary<Integer, Integer> dict = ImmutableDictionary.of(1, 2);
		
		exception.expect(UnsupportedOperationException.class);
		dict.putAll(ImmutableMap.of(3,4));
	}
	

}
