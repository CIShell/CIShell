package org.cishell.utilities;

import java.text.DecimalFormat;
import java.text.Format;

public class NumberUtilities {
	public static final String UNROUNDED_DECIMAL_PATTERN =
		"#.############################";
	public static final String NOT_A_NUMBER_PREFIX = "NOT A NUMBER";
	
	public static Double interpretObjectAsDouble(Object object)
			throws NumberFormatException {
		final String EMPTY_CELL_MESSAGE = "An empty number cell was found.";
		
		// TODO: These if's are a result of a "bug" in Prefuse's.
		// CSV Table Reader, which interprets a column as being an array type
		// if it has empty cells.
		if (object instanceof Number) {
			Number number = (Number)object;
			
			return new Double(number.doubleValue());
		} else if (object instanceof short[]) {
			short[] objectAsShortArray = (short[])object;
			
			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsShortArray[0]);
			}
		} else if (object instanceof Short[]) {
			Short[] objectAsShortArray = (Short[])object;
			
			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsShortArray[0].doubleValue());
			}
		} else if (object instanceof int[]) {
			int[] objectAsIntArray = (int[])object;
			
			if (objectAsIntArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsIntArray[0]);
			}
		} else if (object instanceof Integer[]) {
			Integer[] objectAsIntegerArray = (Integer[])object;
			
			if (objectAsIntegerArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsIntegerArray[0].doubleValue());
			}
		} else if (object instanceof long[]) {
			long[] objectAsLongArray = (long[])object;
			
			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsLongArray[0]);
			}
		} else if (object instanceof Long[]) {
			Long[] objectAsLongArray = (Long[])object;
			
			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsLongArray[0].doubleValue());
			}
		} else if (object instanceof float[]) {
			float[] objectAsFloatArray = (float[])object;
			
			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsFloatArray[0]);
			}
		} else if (object instanceof Float[]) {
			Float[] objectAsFloatArray = (Float[])object;
			
			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsFloatArray[0].doubleValue());
			}
		} else if (object instanceof double[]) {
			double[] objectAsDoubleArray = (double[])object;
			
			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return new Double(objectAsDoubleArray[0]);
			}
		} else if (object instanceof Double[]) {
			Double[] objectAsDoubleArray = (Double[])object;
			
			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			} else {
				return objectAsDoubleArray[0];
			}
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
	 * If numberAsString holds a number in scientific notation,
	 * convert it to decimal notation.
	 */
	public static String convertToDecimalNotation(String numberAsString) {
		// Check for a scientific notation delimiter.
		if (numberAsString.indexOf("E") != -1 || numberAsString.indexOf("e") != -1) {
			Format format =
				new DecimalFormat(UNROUNDED_DECIMAL_PATTERN);
			
			try {
				return format.format(new Double(numberAsString));
			} catch (NumberFormatException numberFormatException) {
				return NOT_A_NUMBER_PREFIX + " (" + numberAsString + ")";
			}
		} else {
			return numberAsString;
		}
	}

	public static double roundToNDecimalPlaces(double original, int decimalPlaceCount) {
		String formatString = "#." + StringUtilities.multiply("#", decimalPlaceCount);
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
}