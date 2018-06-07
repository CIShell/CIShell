package org.cishell.testing.convertertester.core.tester2.util;

public class FormatUtil {
	
	public static final int NUM_DECIMALS = 1;
	
	/**
	 * 
	 * @param decimal 0.0f to 1.0f
	 * @return 
	 */
	public static float formatToPercent(float decimal) {
		float temp = decimal;
		temp *= 100f;
		temp *= Math.pow(10.0, NUM_DECIMALS);
		temp = Math.round(temp);
		temp = temp / (float) (Math.pow(10.0, NUM_DECIMALS));
		float percent = temp;
		
		return percent;
	}
}
