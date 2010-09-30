package org.cishell.utilities.dictionary;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class DictionaryUtilities {
	/// Side-effects the provided Dictionary.
	public static <K, V> void addIfNotNull(Dictionary<K, V> dictionary, K key, V value) {
		if ((key != null) && (value != null)) {
			dictionary.put(key, value);
		}
	}

	/// Side-effects the provided Dictionary.
	public static <K, V> void addIfNotNull(
			Dictionary<K, V> dictionary, DictionaryEntry<K, V>... entries) {
		for (DictionaryEntry<K, V> entry : entries) {
			addIfNotNull(dictionary, entry.getKey(), entry.getValue());
		}
	}

	public static <K, V> Dictionary<K, V> copyWithValuesThatAreNotNull(
			Dictionary<K, V> originalDictionary, DictionaryEntry<K, V>... entries) {
		Dictionary<K, V> newDictionary = copy(originalDictionary);

		addIfNotNull(newDictionary, entries);

		return newDictionary;
	}

	/**
	 * Uses Hashtable for the copy.
	 */
	public static<K, V> Dictionary<K, V> copy(Dictionary<K, V> originalDictionary) {
		Hashtable<K, V> newDictionary = new Hashtable<K, V>();
		putAll(newDictionary, originalDictionary);

		return newDictionary;
	}

	public static<K, V> void putAll(Dictionary<K, V> target, Dictionary<K, V> source) {
		for (Enumeration<K> keys = source.keys(); keys.hasMoreElements(); ) {
			K key = keys.nextElement();
			target.put(key, source.get(key));
		}
	}
}