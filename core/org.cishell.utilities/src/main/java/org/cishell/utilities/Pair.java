package org.cishell.utilities;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.firstObject == null) ? 0 : this.firstObject.hashCode());
		result = prime
				* result
				+ ((this.secondObject == null) ? 0 : this.secondObject
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (this.firstObject == null) {
			if (other.firstObject != null)
				return false;
		} else if (!this.firstObject.equals(other.firstObject))
			return false;
		if (this.secondObject == null) {
			if (other.secondObject != null)
				return false;
		} else if (!this.secondObject.equals(other.secondObject))
			return false;
		return true;
	}

}