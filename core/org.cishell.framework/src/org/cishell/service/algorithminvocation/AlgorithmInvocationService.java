package org.cishell.service.algorithminvocation;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.osgi.framework.ServiceReference;

public interface AlgorithmInvocationService {
	public Data[] runAlgorithm(String pid, Data[] inputData) throws AlgorithmExecutionException;
	public Data[] wrapAlgorithm(
			String pid,
			CIShellContext callerCIShellContext,
			Data[] inputData,
			Dictionary<String, Object> parameters) throws AlgorithmExecutionException;

	public ServiceReference createUniqueServiceReference(ServiceReference actualServiceReference);
	public CIShellContext createUniqueCIShellContext(ServiceReference uniqueServiceReference);
}