package org.cishell.reference.service.conversion.osgisucks;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * An immutable implementation of the Dictionary abstract class.  Also 
 * implements the Map interface, so it can be used interchangeably!
 * <p>
 * Backed by an {@link ImmutableMap}, this implementation uses
 * {@link ImmutableMap#copyOf(Map)} when constructed using
 * {@code ImmutableDictionary.fromMap(Map)}.  So there will likely
 * not be a spurious copy operation if you pass in an ImmutableMap.
 * <p>
 * The iteration order of the Dictionary will be the same as that
 * of the Map or Dictionary that is passed in, or of the arguments
 * to the {@code .of(...)} factories.
 * <p>
 * As a note, it is possible that some weird implementation of Dictionary
 * is out there that does not also implement Map.  In that case, this
 * class is not guaranteed to perform correctly with respect to
 * {@code .equals()} with that class.  {@link Map#equals(Object)} is carefully
 * specified, and anything that implements {@code Map} will behave correctly
 * (transitive, reflexive, etc.).  {@code Hashtable} does implement Map
 * and behaves correctly.
 * @author Thomas Smith
 *
 */

// The Google Collections implementation of ImmutableMap is interesting to look at.
// http://code.google.com/p/guava-libraries/source/browse/guava/src/com/google/common/collect/ImmutableMap.java
public class ImmutableDictionary<K, V> extends Dictionary<K, V> implements Map<K,V> {
	private final ImmutableMap<K,V> delegate;
	
	private ImmutableDictionary(Map<? extends K,? extends V> source) {
		this.delegate = ImmutableMap.copyOf(source);
	}
	
	/**
	 * Construct an ImmutableDictionary from a Map.
	 * <p>
	 * Internally, {@code ImmutableMap.copyOf(Map)} is used, so if you pass in an
	 * ImmutableMap, this should be quite fast as no copy is made.
	 * <p>
	 * Subsequent modifications to the source Map will not affect the returned
	 * ImmutableDictionary. 
	 * @param map the Map to copy
	 * @return an immutable version of the provided Map
	 */
	public static <K,V> ImmutableDictionary<K,V> fromMap(Map<? extends K, ? extends V> map) {
		return new ImmutableDictionary<K,V>(map);
	}
	
	/**
	 * Construct an ImmutableDictionary from a Dictionary.
	 * <p>
	 * Subsequent modifications to the source Dictionary will not affect the returned
	 * ImmutableDictionary
	 * @param dict the Dictionary to copy
	 * @return an immutable version of the provided Dictionary
	 */
	public static <K,V> ImmutableDictionary<K,V> fromDictionary(Dictionary<? extends K, ? extends V> dict) {
		// Hashtable, for instance, is also a Map.
		// We can leave the copying of the entries to ImmutableMap.copyOf, which probably
		// uses .entrySet(), which is faster than iterating over keys as we have to do here.
		if (dict instanceof Map) {
			return ImmutableDictionary.fromMap((Map<? extends K, ? extends V>) dict);
		}
		ImmutableMap.Builder<K,V> builder = ImmutableMap.builder();
		Enumeration<? extends K> keys = dict.keys();
		while (keys.hasMoreElements()) {
			K key = keys.nextElement();
			builder.put(key, dict.get(key));
		}
		
		return new ImmutableDictionary<K,V>(builder.build());
	}

	/**
	 * Returns an empty, immutable Dictionary.
	 *
	 * @throws IllegalArgumentException if duplicate keys are added
	 */
	// Casting to any key and value types is safe because the dictionary will never hold any elements.
	@SuppressWarnings("unchecked")
	public static <K,V> ImmutableDictionary<K,V> of() {
		return (ImmutableDictionary<K,V>) ImmutableDictionary.fromMap(ImmutableMap.of());
	}

	/**
	 * Returns an immutable Dictionary containing only the key and value specified.
	 *
	 * @throws IllegalArgumentException if duplicate keys are added
	 */
	public static <K,V> ImmutableDictionary<K,V> of(K k1, V v1) {
		return ImmutableDictionary.fromMap(ImmutableMap.of(k1, v1));
	}
	
	/**
	 * Returns an immutable Dictionary containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException if duplicate keys are added
	 */
	public static <K,V> ImmutableDictionary<K,V> of(K k1, V v1, K k2, V v2) {
		return ImmutableDictionary.fromMap(ImmutableMap.of(k1, v1, k2, v2));
	}
	
	/**
	 * Returns an immutable Dictionary containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException if duplicate keys are added
	 */
	public static <K,V> ImmutableDictionary<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
		return ImmutableDictionary.fromMap(ImmutableMap.of(k1, v1, k2, v2, k3, v3));
	}
	
	/**
	 * Returns an immutable Dictionary containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException if duplicate keys are added
	 */
	public static <K,V> ImmutableDictionary<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		return ImmutableDictionary.fromMap(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4));
	}
	
	/**
	 * Returns an immutable Dictionary containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException if duplicate keys are added
	 */
	public static <K,V> ImmutableDictionary<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
		return ImmutableDictionary.fromMap(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
	}
	
	/**
	 * Guaranteed to throw an exception and leave the Dictionary unmodified.
	 * @throws UnsupportedOperationException always
	 */
	public final V put(K k, V v) {
		return delegate.put(k, v);
	}

	/**
	 * Guaranteed to throw an exception and leave the Dictionary unmodified.
	 * @throws UnsupportedOperationException always
	 */
	public final V remove(Object o) {
		return delegate.remove(o);
	}

	/**
	 * Guaranteed to throw an exception and leave the Dictionary unmodified.
	 * @throws UnsupportedOperationException always
	 */
	public final void putAll(Map<? extends K, ? extends V> map) {
		delegate.putAll(map);
	}

	/**
	 * Guaranteed to throw an exception and leave the Dictionary unmodified.
	 * @throws UnsupportedOperationException always
	 */
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

	// TODO test with Dictionaries...
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
