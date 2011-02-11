package org.cishell.reference.app.service.algorithminvocation;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.service.algorithminvocation.AlgorithmInvocationService;
import org.osgi.framework.ServiceReference;

public class AlgorithmInvocationServiceImpl implements AlgorithmInvocationService {
	public Data[] runAlgorithm(String pid, Data[] inputData) throws AlgorithmExecutionException {
		// TODO: AlgorithmWrapper, etc.
		return null;
	}

	public Data[] wrapAlgorithm(
			String pid,
			CIShellContext callerCIShellContext,
			Data[] inputData,
			Dictionary<String, Object> parameters) throws AlgorithmExecutionException {
		// TODO: Get the algorithm, call it, etc.
		return null;
	}

	public ServiceReference createUniqueServiceReference(ServiceReference actualServiceReference) {
		// TODO
		return null;
	}

	public CIShellContext createUniqueCIShellContext(ServiceReference uniqueServiceReference) {
		// TODO:
		return null;
	}
}