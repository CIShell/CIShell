package org.cishell.utilities;

import static org.cishell.utilities.NumberUtilities.EMPTY_CELL_MESSAGE;

import java.lang.reflect.Array;
import java.text.ParseException;

import com.google.common.base.Preconditions;

/**
 * A collection of utilities for Prefuse.
 * 
 * <p>
 * There are several functions that help deal with an issue where if one value
 * in a column was empty, then Prefuse wraps all the other values in an array
 * and the empty value is the empty array and all the other values are the first
 * element of an array.
 * </p>
 * 
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class PrefuseUtilities {

	// Suppress default constructor for noninstantiability
	private PrefuseUtilities() {
		throw new AssertionError();
	}

	/**
	 * This fixes a issue where if a column has an empty value, Prefuse will
	 * wrap that value in an appropriate array. If the value was empty, there is
	 * an empty array, otherwise the value is the first element in that array.
	 * 
	 * @param object
	 *            The value of the column given by Prefuse. The object must not
	 *            be {@code null}.
	 * @return The Number value that the object represents.
	 * @throws EmptyInterpretedObjectException
	 *             If the object represents an empty cell in the table.
	 * @throws UninterpretableObjectException
	 *             If the object cannot be interpreted as a number.
	 * @throws NullPointerException
	 *             if object is null
	 */
	public static Number interpretObjectAsNumber(Object object)
			throws EmptyInterpretedObjectException,
			UninterpretableObjectException {
		Preconditions.checkNotNull(object, "The object must not be null.");

		try {
			return NumberUtilities.interpretObjectAsNumber(object);
		} catch (NumberFormatException e) {
			// XXX Uses exception message to control flow!
			if (NumberUtilities.EMPTY_CELL_MESSAGE.equals(e.getMessage())) {
				throw new EmptyInterpretedObjectException(e);
			}

			throw new UninterpretableObjectException(e);
		} catch (ParseException e) {
			throw new UninterpretableObjectException(e);
		}
	}

	/**
	 * This fixes a issue where if a column has an empty value, Prefuse will
	 * wrap that value in an appropriate array. If the value was empty, there is
	 * an empty array, otherwise the value is the first element in that array.
	 * 
	 * @param object
	 *            The value of the column given by Prefuse. The object must not
	 *            be {@code null}.
	 * @return The Integer value that the object represents.
	 * @throws EmptyInterpretedObjectException
	 *             If the object represents an empty cell in the table.
	 * @throws UninterpretableObjectException
	 *             If the object cannot be interpreted as a number.
	 * @throws NullPointerException
	 *             if object is null
	 * @throws IllegalArgumentException
	 *             if {@code object} is a long (long[], Long[], Long) value
	 *             greater than {@link Integer#MAX_VALUE} or less than
	 *             {@link Integer#MIN_VALUE}
	 */
	public static Integer interpretObjectAsInteger(Object object)
			throws EmptyInterpretedObjectException,
			UninterpretableObjectException {
		Preconditions.checkNotNull(object, "The object must not be null.");

		if (object instanceof Number) {
			Number number = (Number) object;
			return Integer.valueOf(number.intValue());
		} else if (object instanceof short[]) {
			short[] objectAsShortArray = (short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return Integer.valueOf(objectAsShortArray[0]);
		} else if (object instanceof Short[]) {
			Short[] objectAsShortArray = (Short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return Integer.valueOf(objectAsShortArray[0].intValue());
		} else if (object instanceof int[]) {
			int[] objectAsIntArray = (int[]) object;

			if (objectAsIntArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return Integer.valueOf(objectAsIntArray[0]);
		} else if (object instanceof Integer[]) {
			Integer[] objectAsIntegerArray = (Integer[]) object;

			if (objectAsIntegerArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return objectAsIntegerArray[0];
		} else if (object instanceof long[]) {
			long[] objectAsLongArray = (long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return checkedCast(objectAsLongArray[0]);
		} else if (object instanceof Long[]) {
			Long[] objectAsLongArray = (Long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return checkedCast(objectAsLongArray[0]);
		} else if (object instanceof float[]) {
			float[] objectAsFloatArray = (float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return Integer.valueOf(new Float(objectAsFloatArray[0]).intValue());
		} else if (object instanceof Float[]) {
			Float[] objectAsFloatArray = (Float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return Integer.valueOf(objectAsFloatArray[0].intValue());
		} else if (object instanceof double[]) {
			double[] objectAsDoubleArray = (double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return Integer.valueOf(new Double(objectAsDoubleArray[0])
					.intValue());
		} else if (object instanceof Double[]) {
			Double[] objectAsDoubleArray = (Double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return Integer.valueOf(objectAsDoubleArray[0].intValue());
		}

		String objectAsString = object.toString();
		try {
			return Integer.valueOf(objectAsString);
		} catch (NumberFormatException e) {
			throw new UninterpretableObjectException(e);
		}
	}

	/**
	 * This fixes a issue where if a column has an empty value, Prefuse will
	 * wrap that value in an appropriate array. If the value was empty, there is
	 * an empty array, otherwise the value is the first element in that array.
	 * 
	 * @param object
	 *            The value of the column given by Prefuse. The object must not
	 *            be {@code null}.
	 * @return The Double value that the object represents.
	 * @throws EmptyInterpretedObjectException
	 *             If the object represents an empty cell in the table.
	 * @throws UninterpretableObjectException
	 *             If the object cannot be interpreted as a number.
	 * @throws NullPointerException
	 *             if object is null
	 */
	public static Double interpretObjectAsDouble(Object object)
			throws EmptyInterpretedObjectException,
			UninterpretableObjectException {
		Preconditions.checkNotNull(object, "The object must not be null.");

		try {
			return NumberUtilities.interpretObjectAsDouble(object);
		} catch (NumberFormatException e) {
			// XXX Uses exception message to control flow!
			if (NumberUtilities.EMPTY_CELL_MESSAGE.equals(e.getMessage())) {
				throw new EmptyInterpretedObjectException(e);
			}

			throw new UninterpretableObjectException(e);
		}
	}

	/**
	 * This fixes a issue where if a column has an empty value, Prefuse will
	 * wrap that value in an appropriate array. If the value was empty, there is
	 * an empty array, otherwise the value is the first element in that array.
	 * 
	 * @param object
	 *            The value of the column given by Prefuse. The object must not
	 *            be {@code null}.
	 * @return The Float value that the object represents.
	 * @throws EmptyInterpretedObjectException
	 *             If the object represents an empty cell in the table.
	 * @throws UninterpretableObjectException
	 *             If the object cannot be interpreted as a number.
	 * @throws NullPointerException
	 *             if object is null
	 */
	public static Float interpretObjectAsFloat(Object object)
			throws EmptyInterpretedObjectException,
			UninterpretableObjectException {
		Preconditions.checkNotNull(object, "The object must not be null.");

		if (object instanceof Number) {
			Number number = (Number) object;
			return new Float(number.floatValue());
		} else if (object instanceof short[]) {
			short[] objectAsShortArray = (short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsShortArray[0]);
		} else if (object instanceof Short[]) {
			Short[] objectAsShortArray = (Short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsShortArray[0].floatValue());
		} else if (object instanceof int[]) {
			int[] objectAsIntArray = (int[]) object;

			if (objectAsIntArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsIntArray[0]);
		} else if (object instanceof Integer[]) {
			Integer[] objectAsIntegerArray = (Integer[]) object;

			if (objectAsIntegerArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsIntegerArray[0].floatValue());
		} else if (object instanceof long[]) {
			long[] objectAsLongArray = (long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsLongArray[0]);
		} else if (object instanceof Long[]) {
			Long[] objectAsLongArray = (Long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsLongArray[0].floatValue());
		} else if (object instanceof float[]) {
			float[] objectAsFloatArray = (float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsFloatArray[0]);
		} else if (object instanceof Float[]) {
			Float[] objectAsFloatArray = (Float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsFloatArray[0].floatValue());
		} else if (object instanceof double[]) {
			double[] objectAsDoubleArray = (double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsDoubleArray[0]);
		} else if (object instanceof Double[]) {
			Double[] objectAsDoubleArray = (Double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsDoubleArray[0].floatValue());
		}

		String objectAsString = object.toString();
		try {
			return new Float(objectAsString);
		} catch (NumberFormatException e) {
			throw new UninterpretableObjectException(e);
		}
	}

	/**
	 * <p>
	 * This fixes a issue where if a column has an empty value, Prefuse will
	 * wrap that value in an appropriate array. If the value was empty, there is
	 * an empty array, otherwise the value is the first element in that array.
	 * </p>
	 * 
	 * @param object
	 *            The value of the column given by Prefuse. The object must not
	 *            be {@code null}. All non-boolean objects whose
	 *            {@link Object#toString()} method does not return
	 *            {@code "true"} will be evaluated to be {@code false}.
	 * @return The Boolean value that the object represents.
	 * @throws EmptyInterpretedObjectException
	 *             If the object represents an empty cell in the table.
	 * @throws NullPointerException
	 *             if object is null
	 */
	public static Boolean interpretObjectAsBoolean(Object object)
			throws EmptyInterpretedObjectException {
		Preconditions.checkNotNull(object, "The object must not be null.");

		if (object instanceof boolean[]) {
			boolean[] objectAsBooleanArray = (boolean[]) object;

			if (objectAsBooleanArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return objectAsBooleanArray[0];
		} else if (object instanceof Boolean[]) {
			Boolean[] objectAsBooleanArray = (Boolean[]) object;

			if (objectAsBooleanArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return objectAsBooleanArray[0];
		}

		String objectAsString = object.toString();
		return Boolean.valueOf(objectAsString);
	}

	/**
	 * <p>
	 * This fixes a issue where if a column has an empty value, Prefuse will
	 * wrap that value in an appropriate array. If the value was empty, there is
	 * an empty array, otherwise the value is the first element in that array.
	 * </p>
	 * 
	 * @param object
	 *            The value of the column given by Prefuse. The object must not
	 *            be {@code null}.
	 * @return The String value that the object represents.
	 * @throws EmptyInterpretedObjectException
	 *             If the object represents an empty cell in the table.
	 * @throws NullPointerException
	 *             if object is null
	 */
	public static String interpretObjectAsString(Object object)
			throws EmptyInterpretedObjectException {
		Preconditions.checkNotNull(object, "The object must not be null.");

		if (object instanceof String[]) {
			String[] objectAsBooleanArray = (String[]) object;

			if (objectAsBooleanArray.length == 0) {
				throw new EmptyInterpretedObjectException(EMPTY_CELL_MESSAGE);
			}
			return objectAsBooleanArray[0];
		}

		return object.toString();
	}

	/**
	 * This exception represents an empty cell for the Prefuse issue described
	 * by {@link NumberUtilities#interpretObjectAsDouble(Object)}
	 */
	public static class EmptyInterpretedObjectException extends
			PrefuseInterpretationException {
		private static final long serialVersionUID = 7761769662476407624L;

		/**
		 * @see Exception#Exception()
		 */
		public EmptyInterpretedObjectException() {
			super();
		}

		/**
		 * @see Exception#Exception(String)
		 */
		public EmptyInterpretedObjectException(String message) {
			super(message);
		}

		/**
		 * @see Exception#Exception(Throwable)
		 */
		public EmptyInterpretedObjectException(Throwable cause) {
			super(cause);
		}

		/**
		 * @see Exception#Exception(String, Throwable)
		 */
		public EmptyInterpretedObjectException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * This exception represents a failure to interpret the object.
	 */
	public static class UninterpretableObjectException extends
			PrefuseInterpretationException {
		private static final long serialVersionUID = 958527035061832770L;

		/**
		 * @see Exception#Exception()
		 */
		public UninterpretableObjectException() {
			super();
		}

		/**
		 * @see Exception#Exception(String)
		 */
		public UninterpretableObjectException(String message) {
			super(message);
		}

		/**
		 * @see Exception#Exception(Throwable)
		 */
		public UninterpretableObjectException(Throwable cause) {
			super(cause);
		}

		/**
		 * @see Exception#Exception(String, Throwable)
		 */
		public UninterpretableObjectException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Signals problem interpreting a value from Prefuse.
	 */
	public static class PrefuseInterpretationException extends Exception {
		private static final long serialVersionUID = 4798889267693852796L;

		/**
		 * @see Exception#Exception()
		 */
		public PrefuseInterpretationException() {
			super();
		}

		/**
		 * @see Exception#Exception(String)
		 */
		public PrefuseInterpretationException(String message) {
			super(message);
		}

		/**
		 * @see Exception#Exception(Throwable)
		 */
		public PrefuseInterpretationException(Throwable cause) {
			super(cause);
		}

		/**
		 * @see Exception#Exception(String, Throwable)
		 */
		public PrefuseInterpretationException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * There is an issue in Prefuse where a column that has missing data for one
	 * record will cause all other records in that column to be wrapped in an
	 * array and the missing data is the empty array.
	 * 
	 * <p>
	 * This method will unwrap those types if it finds they might have been
	 * caused by this issue. <code>null</code> will be returned if the empty
	 * object is found.
	 * </p>
	 * 
	 * @param object
	 *            The object to potentially unwrap. It should not be
	 *            <code>null</code> or a {@link NullPointerException} will be
	 *            thrown.
	 * @return The unwrapped <code>Object</code>.
	 */
	public static Object removePrefuseArrayWrapper(Object object) {
		Preconditions.checkNotNull(object);
		Object returnObject = object;

		if (object.getClass().isArray()) {
			// Only arrays of length 1 or 0 would be candidates for the
			// Prefuse bug.
			if (Array.getLength(object) == 0) {
				// The empty object was found.
				returnObject = null;
			} else if (Array.getLength(object) == 1) {
				// Array.get might return an array if the type was
				// primitive.
				Object fromArray = Array.get(object, 0);
				if (fromArray.getClass().isArray()) {
					// Further unwrapping is needed.
					returnObject = ((Object[]) fromArray)[0];
				} else {
					returnObject = fromArray;
				}
			}

		}
		return returnObject;
	}
	
	/**
	 * From Google Guava
	 * 
	 * Returns the {@code int} value that is equal to {@code value}, if
	 * possible.
	 * 
	 * @param value
	 *            any value in the range of the {@code int} type
	 * @return the {@code int} value that equals {@code value}
	 * @throws IllegalArgumentException
	 *             if {@code value} is greater than {@link Integer#MAX_VALUE} or
	 *             less than {@link Integer#MIN_VALUE}
	 */
	private static int checkedCast(long value) {
		int result = (int) value;
		if (!(result == value)) {
			throw new IllegalArgumentException("Out of range: " + value);
		}
		return result;
	}
}
