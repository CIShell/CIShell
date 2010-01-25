package org.cishell.utilities.dictionary;

public class DictionaryEntry<K, V> {
	private K key;
	private V value;

	public DictionaryEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return this.key;
	}

	public V getValue() {
		return this.value;
	}
}