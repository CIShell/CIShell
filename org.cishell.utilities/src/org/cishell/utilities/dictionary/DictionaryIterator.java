package org.cishell.utilities.dictionary;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;

public class DictionaryIterator<K, V>
		implements Iterator<DictionaryEntry<K, V>>, Iterable<DictionaryEntry<K, V>> {
	private Dictionary<K, V> dictionary;
	Enumeration<K> keys;

	public DictionaryIterator(Dictionary<K, V> dictionary) {
		this.dictionary = dictionary;
		this.keys = dictionary.keys();
	}

	public boolean hasNext() {
		return this.keys.hasMoreElements();
	}

	public DictionaryEntry<K, V> next() {
		K nextKey = this.keys.nextElement();
		V nextValue = this.dictionary.get(nextKey);

		return new DictionaryEntry<K, V>(nextKey, nextValue);
	}

	public void remove() {
		String exceptionMessage = "remove() cannot be called on a DictionaryIterator.";

		throw new UnsupportedOperationException(exceptionMessage);
	}

	public Iterator<DictionaryEntry<K, V>> iterator() {
		return this;
	}
}