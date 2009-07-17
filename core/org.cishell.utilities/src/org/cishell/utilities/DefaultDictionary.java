package org.cishell.utilities;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class DefaultDictionary extends Dictionary {
	Object defaultValue;
	Dictionary wrappedDictionary;
	
	public DefaultDictionary
		(Object defaultValue, Dictionary wrappedDictionary)
	{
		this.defaultValue = defaultValue;
		this.wrappedDictionary = wrappedDictionary;
	}
	
	public DefaultDictionary(Object defaultValue) {
		this(defaultValue, new Hashtable());
	}
	
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public Enumeration elements() {
		return this.wrappedDictionary.elements();
	}

	public Object get(Object key) {
		Object wrappedDictionaryGetResult = this.wrappedDictionary.get(key);
		
		if (wrappedDictionaryGetResult == null)
			return this.defaultValue;
		
		return wrappedDictionaryGetResult;
	}

	public boolean isEmpty() {
		return this.wrappedDictionary.isEmpty();
	}

	public Enumeration keys() {
		return this.wrappedDictionary.keys();
	}

	public Object put(Object key, Object value) {
		return this.wrappedDictionary.put(key, value);
	}

	public Object remove(Object key) {
		return this.wrappedDictionary.remove(key);
	}

	public int size() {
		return this.wrappedDictionary.size();
	}
}