package org.cishell.utilities;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * FrequencyMap is a map of key with its emergence frequency.
 * It supports two type of mapping structures: null key and 
 * non-null key; by providing boolean value to enable 
 * allowNullValue through constructor call. Add() method will
 * add a new key if the key is not exist. Else it will increase
 * the frequency by 1. sum() will return the count that 
 * represents sum of all the frequency values in the map
 * @author kongch
 *
 * @param <E>
 */
public class FrequencyMap <E> {
	private Map<E, Frequency> itemToFrequencyMap;
	private int count;
	
	public FrequencyMap(boolean allowNullValue) {
		count = 0;
		if (allowNullValue) {
			itemToFrequencyMap = new HashMap<E, Frequency>();
		} else {
			itemToFrequencyMap = new Hashtable<E, Frequency>();
		}
	}
	
	/**
	 * Add the given key to the map and increase the frequency by 1.
	 * @param key - key to be added
	 */
	public void add(E key) {
		Frequency frequency;
		
		/* 
		 * Get the Frequency object for the given key if key already exist. 
		 * Else create a new Frequency object and add it to the map
		 */
		if (itemToFrequencyMap.containsKey(key)) {
			frequency = itemToFrequencyMap.get(key);
		} else {
			frequency = new Frequency();
			try {
				itemToFrequencyMap.put(key, frequency);
			} catch (NullPointerException  e) {
				/* It is a non-null map. Throw a runtime exception */
				String message = "FrequencyMap.add(E key) was called with a null key."
						+ " If null support is desired, "
						+ " construct FrequencyMap with allowNullValue = true";
				throw new NullValueSupportException(message, e);
			}
		}
		
		/* increase frequency by 1 and also sum of all frequencies by 1 */
		count++;
		frequency.increase();
	}
	
	/**
	 * Retrieve a set of keys stored in this map. 
	 */
	public Set<E> keySet() {
		return itemToFrequencyMap.keySet();
	}
	
	/**
	 * Get the emergence frequency for the given key.
	 * @param key - item to be lookup
	 * @return Return number of times the key exists
	 * if lookup success. Return zero if key not exist
	 */
	public int getFrequency(E key) {
		if (itemToFrequencyMap.containsKey(key)) {
			return itemToFrequencyMap.get(key).getValue();
		} else {
			return 0;
		}
	}
	
	public int sum() {
		return count;
	}
	
	public boolean isEmpty() {
		return itemToFrequencyMap.isEmpty();
	}
	
	private class Frequency {
		private int value;
		public Frequency() {
			value = 0;
		}
		
		public void increase() {
			value++;
		}
		
		public int getValue() {
			return value;
		}
	}
}
