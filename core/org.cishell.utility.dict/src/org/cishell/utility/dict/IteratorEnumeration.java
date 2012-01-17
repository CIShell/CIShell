package org.cishell.utility.dict;

import java.util.Enumeration;
import java.util.Iterator;

class IteratorEnumeration<E> implements Enumeration<E> {

	private final Iterator<E> delegate;
	
	
	IteratorEnumeration(Iterator<E> delegate) {
		this.delegate = delegate;
	}

	public boolean hasMoreElements() {
		return delegate.hasNext();
	}

	public E nextElement() {
		return delegate.next();
	}

}
