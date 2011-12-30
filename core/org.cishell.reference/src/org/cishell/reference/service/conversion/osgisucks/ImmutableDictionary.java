package org.cishell.reference.service.conversion.osgisucks;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ImmutableDictionary<K, V> extends Dictionary<K, V> implements Map<K,V> {
	private final ImmutableMap<K,V> delegate;
	
	private ImmutableDictionary(Map<? extends K,? extends V> source) {
		this.delegate = ImmutableMap.copyOf(source);
	}
	
	public static <K,V> ImmutableDictionary<K,V> fromMap(Map<? extends K, ? extends V> map) {
		return new ImmutableDictionary<K,V>(map);
	}
	
	public static <K,V> ImmutableDictionary<K,V> fromDictionary(Dictionary<? extends K, ? extends V> dict) {
		ImmutableMap.Builder<K,V> builder = ImmutableMap.builder();
		Enumeration<? extends K> keys = dict.keys();
		while (keys.hasMoreElements()) {
			K key = keys.nextElement();
			builder.put(key, dict.get(key));
		}
		
		return new ImmutableDictionary<K,V>(builder.build());
	}


	public final V put(K k, V v) {
		return delegate.put(k, v);
	}

	public final V remove(Object o) {
		return delegate.remove(o);
	}

	public final void putAll(Map<? extends K, ? extends V> map) {
		delegate.putAll(map);
	}

	public final void clear() {
		delegate.clear();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	public V get(Object key) {
		return delegate.get(key);
	}

	public ImmutableSet<java.util.Map.Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	public ImmutableSet<K> keySet() {
		return delegate.keySet();
	}

	public ImmutableCollection<V> values() {
		return delegate.values();
	}

	public boolean equals(Object object) {
		return delegate.equals(object);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public int size() {
		return delegate.size();
	}

	public String toString() {
		return delegate.toString();
	}

	@Override
	public Enumeration<V> elements() {
		return new IteratorEnumeration<V>(delegate.values().iterator());
	}

	@Override
	public Enumeration<K> keys() {
		return new IteratorEnumeration<K>(delegate.keySet().iterator());
	}
}
