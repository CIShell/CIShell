package org.cishell.reference.service.conversion.util;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;


import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.google.common.collect.ImmutableMap;
import javax.inject.Inject;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.exam.junit.PaxExamParameterized;
/*import org.ops4j.pax.swissbox.tracker.ServiceLookup;*/


import org.cishell.utility.dict.ImmutableDictionary;



@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ImmutableDictionaryTest {

	@Inject
    private BundleContext bc;

/*
   	@Inject
   	@Filter(timeout=100000)
    private ImmutableDictionary<Integer,Integer> oneTwoThree;
*/

	@Rule
	public ExpectedException exception = ExpectedException.none();



	@Configuration
	public Option[] config() {

		return options(
			//regressionDefaults(),
			mavenBundle("org.cishell.libs", "com.google.guava", "11.0.1"),
			mavenBundle("org.cishell.core", "org.cishell.utility.dict", "1.0.0"),
			junitBundles()
			);
	}



	@Test
	public void testIterationOrder() {
		/* Object service = ServiceLookup.getService(bc,
            "org.cishell.utility.dict.ImmutableDictionary");
		 
		 System.out.println("service-------------"+service);
		*/ /*assertThat(service, is(notNullValue()));*/

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
	
	@Test
	public void testFromHashtable() {
		/*
		 * This is different from testFromDictionary because Hashtable is both a Dictionary
		 * and a Map.  You can do things a little more efficiently if you have a Map,
		 * and so this code exercises that efficient way.
		 */
		Dictionary<Integer, Integer> source = new Hashtable<Integer, Integer>();
		source.put(1, 2);
		source.put(3, 4);
		
		ImmutableDictionary<Integer, Integer> dest = ImmutableDictionary.fromDictionary(source);
		
		assertEquals(source, dest);
		assertEquals(dest, source);
	}
	
	@Test
	public void testFromDictionary() {
		/*
		 * This code should exercise the non-optimized version of ID.fromDictionary().
		 */
		Dictionary<Integer, Integer> source = new Hashtable<Integer, Integer>();
		source.put(1, 2);
		source.put(3, 4);
		
		Dictionary<Integer, Integer> wrapped = new ForwardingDictionary<Integer, Integer>(source);
		
		ImmutableDictionary<Integer, Integer> dest = ImmutableDictionary.fromDictionary(wrapped);
		
		assertEquals(source, dest);
		assertEquals(dest, source);
	}
	
}
