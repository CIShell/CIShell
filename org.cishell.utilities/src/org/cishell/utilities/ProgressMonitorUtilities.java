package org.cishell.utilities;

import org.cishell.framework.algorithm.AlgorithmCanceledException;
import org.cishell.framework.algorithm.ProgressMonitor;

public class ProgressMonitorUtilities {
	public static void handleCanceledAlgorithm(ProgressMonitor progressMonitor)
			throws AlgorithmCanceledException {
		if (progressMonitor.isCanceled()) {
			throw new AlgorithmCanceledException();
		}
	}

	public static void handlePausedAlgorithm(ProgressMonitor progressMonitor) {
		while (progressMonitor.isPaused()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
	}

	public static void handleCanceledOrPausedAlgorithm(ProgressMonitor progressMonitor)
			throws AlgorithmCanceledException {
		handleCanceledAlgorithm(progressMonitor);
		handlePausedAlgorithm(progressMonitor);
	}
}