package org.cishell.testing.convertertester.algorithm;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConverterTesterAlgorithmUtil implements AlgorithmProperty {
	public static ServiceReference[] getConverterReferences(
			BundleContext bContext) {
		  String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+"))";

		  try {
		  ServiceReference[] refs = bContext.getServiceReferences(
				  AlgorithmFactory.class.getName(), filter);
		  
		  return refs;
		  } catch (InvalidSyntaxException e) {
			  System.err.println("Invalid syntax '" + filter +
					  "' for filtering service references. Attempted to " +
					  "obtain all converter references.");
			  e.printStackTrace();
			  return null;
		  }
	}
}
