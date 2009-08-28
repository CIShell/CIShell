package org.cishell.streaming.prototype;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.streaming.prototype.streamcore.DefaultStream;
import org.cishell.streaming.prototype.streamcore.Stream;
import org.cishell.streaming.prototype.streamcore.StreamAlgorithm;

public class IncreasingNumberStreamAlgorithm extends StreamAlgorithm<Integer> {
	@SuppressWarnings("unchecked") // TODO
	public IncreasingNumberStreamAlgorithm(Data[] data, Dictionary parameters,
			CIShellContext context) {
		super(data, parameters, context);
	}

	@Override
	protected Stream<Integer> createStream() {
		return new IncreasingNumberStream();
	}

	private class IncreasingNumberStream extends DefaultStream<Integer> {
		public static final int MAX_EMISSIONS = 20;

		private int numEmissions = 0;

		public Integer next() {				
			// TODO Debug only
			System.out.println("About to produce " + (numEmissions));
			return numEmissions++;
		}

		public boolean isFinished() {
			return numEmissions == MAX_EMISSIONS;
		}
	}
	
	public static class Factory implements AlgorithmFactory {
		@SuppressWarnings("unchecked") // TODO
		public Algorithm createAlgorithm(Data[] data, Dictionary parameters,
				CIShellContext context) {
			return new IncreasingNumberStreamAlgorithm(
					data, parameters, context);
		}
	}
}
