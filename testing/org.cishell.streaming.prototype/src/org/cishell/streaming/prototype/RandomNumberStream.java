package org.cishell.streaming.prototype;

import org.cishell.streaming.prototype.streamlib.StreamImpl;

public class RandomNumberStream extends StreamImpl<String> {

	public static final int MAX_EMISSIONS = 20;
	
	private int numEmissions = 0;
	
	public String yield() {
		numEmissions++;
		return "Generated " + Math.random() + " | Time step ID: " + numEmissions;
	}
	
	public boolean isFinished() {
		return numEmissions == MAX_EMISSIONS;
	}
}
