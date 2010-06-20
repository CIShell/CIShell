package org.cishell.utilities;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class DefaultDictionary<K, V> extends Dictionary<K, V> {
	V defaultValue;
	Dictionary<K, V> wrappedDictionary;
	
	public DefaultDictionary(V defaultValue, Dictionary<K, V> wrappedDictionary) {
		this.defaultValue = defaultValue;
		this.wrappedDictionary = wrappedDictionary;
	}
	
	public DefaultDictionary(V defaultValue) {
		this(defaultValue, new Hashtable<K, V>());
	}
	
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public Enumeration<V> elements() {
		return this.wrappedDictionary.elements();
	}

	public V get(Object key) {
		V wrappedDictionaryGetResult = this.wrappedDictionary.get(key);
		
		if (wrappedDictionaryGetResult == null)
			return this.defaultValue;
		
		return wrappedDictionaryGetResult;
	}

	public boolean isEmpty() {
		return this.wrappedDictionary.isEmpty();
	}

	public Enumeration<K> keys() {
		return this.wrappedDictionary.keys();
	}

	public V put(K key, V value) {
		return this.wrappedDictionary.put(key, value);
	}

	public V remove(Object key) {
		return this.wrappedDictionary.remove(key);
	}

	public int size() {
		return this.wrappedDictionary.size();
	}
}