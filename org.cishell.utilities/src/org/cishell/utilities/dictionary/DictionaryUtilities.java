package org.cishell.utilities.dictionary;

import java.util.Dictionary;
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

	public static <K, V> Dictionary<K, V> copy(Dictionary<K, V> originalDictionary) {
		Dictionary<K, V> newDictionary = new Hashtable<K, V>();

		for (DictionaryEntry<K, V> originalEntry :
				new DictionaryIterator<K, V>(originalDictionary)) {
			newDictionary.put(originalEntry.getKey(), originalEntry.getValue());
		}

		return newDictionary;
	}
}