package org.cishell.reference.service.conversion.util;

import java.util.Dictionary;
import java.util.Enumeration;

public class ForwardingDictionary<K,V> extends Dictionary<K,V> {
	private final Dictionary<K,V> delegate;
	
	public ForwardingDictionary(Dictionary<K,V> delegate) {
		this.delegate = delegate;
	}

	public Enumeration<V> elements() {
		return delegate.elements();
	}

	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}

	public V get(Object arg0) {
		return delegate.get(arg0);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public Enumeration<K> keys() {
		return delegate.keys();
	}

	public V put(K arg0, V arg1) {
		return delegate.put(arg0, arg1);
	}

	public V remove(Object arg0) {
		return delegate.remove(arg0);
	}

	public int size() {
		return delegate.size();
	}

	public String toString() {
		return delegate.toString();
	}
}
