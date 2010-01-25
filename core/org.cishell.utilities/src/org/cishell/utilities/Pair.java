package org.cishell.utilities;

public class Pair<S, T> {
	S firstObject;
	T secondObject;

	public Pair(S firstObject, T secondObject) {
		this.firstObject = firstObject;
		this.secondObject = secondObject;
	}

	public S getFirstObject() {
		return this.firstObject;
	}

	public T getSecondObject() {
		return this.secondObject;
	}
}