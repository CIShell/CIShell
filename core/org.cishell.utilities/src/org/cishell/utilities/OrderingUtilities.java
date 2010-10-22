package org.cishell.utilities;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class OrderingUtilities {
	/**
	 * Normally Ordering.explicit() gives an Ordering that throws a ClassCastException for
	 * unspecified values.  This method gives an explicit ordering that puts unspecified values
	 * before all others.
	 * <br/><br/>
	 * Until {@link http://code.google.com/p/guava-libraries/issues/detail?id=332} is addressed.
	 */
	public static <T> Ordering<T> explicitWithUnknownsFirst(List<T> valuesInOrder) {
		Function<T, T> unknownToNullFunction = new IdentityIndicatorFunction<T>(valuesInOrder);

		return Ordering.explicit(valuesInOrder).nullsFirst().onResultOf(unknownToNullFunction);
	}
	
	/**
	 * {@link org.cishell.utilities.OrderingUtilities.explicitWithUnknownsFirst(List<T>)}
	 */
	public static <T> Ordering<T> explicitWithUnknownsFirst(
			T leastValue, T... remainingValuesInOrder) {
		return explicitWithUnknownsFirst(Lists.asList(leastValue, remainingValuesInOrder));
	}
	
	
	/**
	 * Normally Ordering.explicit() gives an Ordering that throws a ClassCastException for
	 * unspecified values.  This method gives an explicit ordering that puts unspecified values
	 * after all others.
	 * <br/><br/>
	 * Until {@link http://code.google.com/p/guava-libraries/issues/detail?id=332} is addressed.
	 */
	public static <T> Ordering<T> explicitWithUnknownsLast(List<T> valuesInOrder) {
		Function<T, T> unknownToNullFunction = new IdentityIndicatorFunction<T>(valuesInOrder);

		return Ordering.explicit(valuesInOrder).nullsLast().onResultOf(unknownToNullFunction);
	}
	
	/**
	 * {@link org.cishell.utilities.OrderingUtilities.explicitWithUnknownsLast(List<T>)}
	 */
	public static <T> Ordering<T> explicitWithUnknownsLast(
			T leastValue, T... remainingValuesInOrder) {
		return explicitWithUnknownsLast(Lists.asList(leastValue, remainingValuesInOrder));
	}
	
	/**
	 * For values in the given collection, this is the identity function.
	 * For other values we return null.
	 */
	public static class IdentityIndicatorFunction<T> implements Function<T, T> {
		private Collection<? extends T> values;

		public IdentityIndicatorFunction(Collection<? extends T> values) {
			this.values = values;
		}
		
		public T apply(T candidateValue) {
			if (values.contains(candidateValue)) {
				return candidateValue;
			} else {
				return null;
			}
		}
	}
}
