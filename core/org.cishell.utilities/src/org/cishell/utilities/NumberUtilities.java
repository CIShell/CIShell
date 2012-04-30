package org.cishell.utilities;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

public final class NumberUtilities {
	public static final String UNROUNDED_DECIMAL_PATTERN = "#.############################";
	public static final String NOT_A_NUMBER_PREFIX = "NOT A NUMBER";

	public static final String EMPTY_CELL_MESSAGE = "An empty number cell was found.";
	
	private NumberUtilities() { 
		//Utility class don't instantiate
	}
	
	public static Number interpretObjectAsNumber(Object object)
			throws ParseException {
		if (object instanceof Number) {
			Number number = (Number) object;

			return number;
		} else if (object instanceof short[]) {
			short[] objectAsShortArray = (short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Short(objectAsShortArray[0]);
		} else if (object instanceof Short[]) {
			Short[] objectAsShortArray = (Short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return objectAsShortArray[0];
		} else if (object instanceof int[]) {
			int[] objectAsIntArray = (int[]) object;

			if (objectAsIntArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Integer(objectAsIntArray[0]);
		} else if (object instanceof Integer[]) {
			Integer[] objectAsIntegerArray = (Integer[]) object;

			if (objectAsIntegerArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return objectAsIntegerArray[0];
		} else if (object instanceof long[]) {
			long[] objectAsLongArray = (long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Long(objectAsLongArray[0]);
		} else if (object instanceof Long[]) {
			Long[] objectAsLongArray = (Long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return objectAsLongArray[0];
		} else if (object instanceof float[]) {
			float[] objectAsFloatArray = (float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Float(objectAsFloatArray[0]);
		} else if (object instanceof Float[]) {
			Float[] objectAsFloatArray = (Float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return objectAsFloatArray[0];
		} else if (object instanceof double[]) {
			double[] objectAsDoubleArray = (double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsDoubleArray[0]);
		} else if (object instanceof Double[]) {
			Double[] objectAsDoubleArray = (Double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return objectAsDoubleArray[0];
		}

		NumberFormat numberFormat = NumberFormat.getInstance();

		return numberFormat.parse(object.toString());
	}
	
	public static Double interpretObjectAsDouble(Object object) {
		// TODO: These if's are a result of a "bug" in Prefuse's.
		// CSV Table Reader, which interprets a column as being an array type
		// if it has empty cells.
		if (object instanceof Number) {
			Number number = (Number) object;

			return new Double(number.doubleValue());
		} else if (object instanceof short[]) {
			short[] objectAsShortArray = (short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsShortArray[0]);
		} else if (object instanceof Short[]) {
			Short[] objectAsShortArray = (Short[]) object;

			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsShortArray[0].doubleValue());
		} else if (object instanceof int[]) {
			int[] objectAsIntArray = (int[]) object;

			if (objectAsIntArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsIntArray[0]);
		} else if (object instanceof Integer[]) {
			Integer[] objectAsIntegerArray = (Integer[]) object;

			if (objectAsIntegerArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsIntegerArray[0].doubleValue());
		} else if (object instanceof long[]) {
			long[] objectAsLongArray = (long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsLongArray[0]);
		} else if (object instanceof Long[]) {
			Long[] objectAsLongArray = (Long[]) object;

			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsLongArray[0].doubleValue());
		} else if (object instanceof float[]) {
			float[] objectAsFloatArray = (float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsFloatArray[0]);
		} else if (object instanceof Float[]) {
			Float[] objectAsFloatArray = (Float[]) object;

			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsFloatArray[0].doubleValue());
		} else if (object instanceof double[]) {
			double[] objectAsDoubleArray = (double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return new Double(objectAsDoubleArray[0]);
		} else if (object instanceof Double[]) {
			Double[] objectAsDoubleArray = (Double[]) object;

			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			return objectAsDoubleArray[0];
		}

		String objectAsString = object.toString();

		return new Double(objectAsString);
	}

	// TODO: Make the plot/csv converter use these versions.
	public static String convertToDecimalNotation(double number) {
		String numberAsString = new Double(number).toString();

		return convertToDecimalNotation(numberAsString);
	}

	/*
	 * If numberAsString holds a number in scientific notation, convert it to
	 * decimal notation.
	 */
	public static String convertToDecimalNotation(String numberAsString) {
		// Check for a scientific notation delimiter.
		if (numberAsString.indexOf("E") != -1
				|| numberAsString.indexOf("e") != -1) {
			Format format = new DecimalFormat(UNROUNDED_DECIMAL_PATTERN);

			try {
				return format.format(new Double(numberAsString));
			} catch (NumberFormatException numberFormatException) {
				return NOT_A_NUMBER_PREFIX + " (" + numberAsString + ")";
			}
		}
		return numberAsString;
	}

	public static double roundToNDecimalPlaces(double original,
			int decimalPlaceCount) {
		String formatString = "#."
				+ StringUtilities.multiply("#", decimalPlaceCount);
		DecimalFormat format = new DecimalFormat(formatString);

		return Double.valueOf(format.format(original));
	}

	public static boolean isEven(long number) {
		return ((number % 2) == 0);
	}

	public static boolean isEven(float number) {
		return ((number % 2) == 0);
	}

	public static boolean isEven(double number) {
		return ((number % 2) == 0);
	}

	public static boolean isOdd(long target) {
		return !isEven(target);
	}

	public static boolean isOdd(float target) {
		return !isEven(target);
	}

	public static boolean isOdd(double target) {
		return !isEven(target);
	}

	/**
	 * The default return value to use for {@link #interpretObjectAsInteger}.
	 */
	public static final Integer INTERPRET_OBJECT_AS_INTEGER_DEFAULT = null;

	/**
	 * Try to interpret an {@link Object} as an {@link Integer}. <br />
	 * If you wish to use a custom default, see
	 * {@link #interpretObjectAsInteger(Object, Integer)}.
	 * 
	 * @return The {@link Integer} if the {@code target} can be parsed,
	 *         {@link NumberUtilities#INTERPRET_OBJECT_AS_INTEGER_DEFAULT}
	 *         otherwise.
	 */
	public static Integer interpretObjectAsInteger(Object object) {
		return interpretObjectAsInteger(object,
				INTERPRET_OBJECT_AS_INTEGER_DEFAULT);
	}

	/**
	 * Try to interpret an {@link Object} as an {@link Integer}.
	 * 
	 * @param defaultValue
	 *            The value to return if the {@code target} can not be parsed.
	 * 
	 * @return The {@link Integer} if the {@code target} can be parsed,
	 *         {@code defaultValue} otherwise.
	 */
	public static Integer interpretObjectAsInteger(Object object,
			Integer defaultValue) {
		if (object instanceof Integer) {
			return (Integer) object;
		} else if (object instanceof Number) {
			return ((Number) object).intValue();
		} else if (object instanceof String) {
			try {
				return Integer.valueOf((String) object);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		} else {
			try {
				return Integer.valueOf(String.valueOf(object));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}
}
