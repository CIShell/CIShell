package org.cishell.utilities;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;


public class AlgorithmUtilities {
	// TODO: ISILoadAndCleanAlgorithmFactory should use this?
	// It's copied directly from it (and cleaned up a little bit)...
	public static AlgorithmFactory getAlgorithmFactoryByFilter(
			String filter, BundleContext bundleContext)
			throws AlgorithmNotFoundException {
		ServiceReference[] algorithmFactoryReferences;
		
		try {
			algorithmFactoryReferences = bundleContext.getServiceReferences(
				AlgorithmFactory.class.getName(), filter);
		} catch (InvalidSyntaxException invalidSyntaxException) {
			throw new AlgorithmNotFoundException(invalidSyntaxException);
		}
    	
    	if (algorithmFactoryReferences != null &&
    			algorithmFactoryReferences.length != 0) {
    		ServiceReference algorithmFactoryReference =
    			algorithmFactoryReferences[0];
    		
    		AlgorithmFactory algorithmFactory =
    			(AlgorithmFactory)bundleContext.getService(
    				algorithmFactoryReference);
    		
    		return algorithmFactory;
    	}
    	else {
    		throw new AlgorithmNotFoundException("Unable to find an " +
    			"algorithm that satisfied the following filter:\n" + filter);
    	}
	}
	
	public static AlgorithmFactory getAlgorithmFactoryByPID(
			String pid, BundleContext bundleContext)
			throws AlgorithmNotFoundException {
		String filter = "(service.pid=" + pid + ")";
		
		return getAlgorithmFactoryByFilter(filter, bundleContext);
	}
}