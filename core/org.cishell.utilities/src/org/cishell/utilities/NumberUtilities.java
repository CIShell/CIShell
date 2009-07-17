package org.cishell.utilities;

public class NumberUtilities {
	public static Double interpretObjectAsDouble(Object object)
			throws NumberFormatException {
		final String EMPTY_CELL_MESSAGE = "An empty number cell was found.";
		
		// TODO: These if's are a result of a "bug" in Prefuse's.
		// CSV Table Reader, which interprets a column as being an array type
		// if it has empty cells.
		if (object instanceof Number) {
			Number number = (Number)object;
			
			return new Double(number.doubleValue());
		}
		else if (object instanceof short[]) {
			short[] objectAsShortArray = (short[])object;
			
			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return new Double((double)objectAsShortArray[0]);
			}
		}
		else if (object instanceof Short[]) {
			Short[] objectAsShortArray = (Short[])object;
			
			if (objectAsShortArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return objectAsShortArray[0].doubleValue();
			}
		}
		else if (object instanceof int[]) {
			int[] objectAsIntArray = (int[])object;
			
			if (objectAsIntArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return new Double((double)objectAsIntArray[0]);
			}
		}
		else if (object instanceof Integer[]) {
			Integer[] objectAsIntegerArray = (Integer[])object;
			
			if (objectAsIntegerArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return objectAsIntegerArray[0].doubleValue();
			}
		}
		else if (object instanceof long[]) {
			long[] objectAsLongArray = (long[])object;
			
			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return new Double((double)objectAsLongArray[0]);
			}
		}
		else if (object instanceof Long[]) {
			Long[] objectAsLongArray = (Long[])object;
			
			if (objectAsLongArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return objectAsLongArray[0].doubleValue();
			}
		}
		else if (object instanceof float[]) {
			float[] objectAsFloatArray = (float[])object;
			
			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return new Double((double)objectAsFloatArray[0]);
			}
		}
		else if (object instanceof Float[]) {
			Float[] objectAsFloatArray = (Float[])object;
			
			if (objectAsFloatArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return objectAsFloatArray[0].doubleValue();
			}
		}
		else if (object instanceof double[]) {
			double[] objectAsDoubleArray = (double[])object;
			
			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return new Double(objectAsDoubleArray[0]);
			}
		}
		else if (object instanceof Double[]) {
			Double[] objectAsDoubleArray = (Double[])object;
			
			if (objectAsDoubleArray.length == 0) {
				throw new NumberFormatException(EMPTY_CELL_MESSAGE);
			}
			else {
				return objectAsDoubleArray[0];
			}
		}
		
		String objectAsString = object.toString();
		
		return new Double(objectAsString);
	}
}