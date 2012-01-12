package org.cishell.utility.dict;

import java.util.Enumeration;
import java.util.Iterator;

class EnumerationIterator<E> implements Iterator<E> {
	private Enumeration<? extends E> delegate;

	EnumerationIterator(Enumeration<? extends E> enumeration) {
		super();
		this.delegate = enumeration;
	}

	public boolean hasNext() {
		return delegate.hasMoreElements();
	}

	public E next() {
		return delegate.nextElement();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
