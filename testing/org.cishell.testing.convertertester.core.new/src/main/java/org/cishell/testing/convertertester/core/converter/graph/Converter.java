package org.cishell.testing.convertertester.core.converter.graph;

import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Converter {
	private BundleContext bundleContext;
	private ServiceReference serviceReference;
	
	public Converter(BundleContext bundleContext, ServiceReference serviceReference) {
		this.bundleContext = bundleContext;
		this.serviceReference = serviceReference;
	}
	
	public ServiceReference getServiceReference() {
		return this.serviceReference;
	}
	
	public ServiceReference getServieReference() {
		return this.serviceReference;
	}
	
	public boolean isLossy() {
		String conversion =
			(String) this.serviceReference.getProperty(AlgorithmProperty.CONVERSION);
		
		if (conversion == null) {
			return false; 
			// If lossiness is not defined, assume it is not lossy.
		}
		
		if (conversion.equals(AlgorithmProperty.LOSSY)) {
			return true;
		} else if (conversion.equals(AlgorithmProperty.LOSSLESS)) {
			return false;
		} else {
			// Assuming lossy by default.
			return true;
		}
	}
	
	public String getInData() {
		return (String) this.serviceReference.getProperty(AlgorithmProperty.IN_DATA);
	}
	
	public String getOutData() {
		return (String) this.serviceReference.getProperty(AlgorithmProperty.OUT_DATA);
	}
	
	public String getShortName() {
		return removePackagePrefix(getUniqueName());
	}
	
	public String getUniqueName() {
		return (String) this.serviceReference.getProperty("service.pid");
	}
	
	public String toString() {
		return getUniqueName();
	}
	
	public Data[] execute(
			Data[] input, Hashtable<String, Object> parameters, CIShellContext ciShellContext)
			throws AlgorithmExecutionException {

		AlgorithmFactory converterFactory = 
			(AlgorithmFactory) this.bundleContext.getService(this.serviceReference);
		Algorithm converter = converterFactory.createAlgorithm(input, parameters, ciShellContext);
		Data[] output = converter.execute();
		
		return output;
	}
	

	/*
	 * Returns everything after the last period in the OSGi service pid.
	 */
	private String removePackagePrefix(String pid) {
		int startIndex = pid.lastIndexOf(".") + 1;
		return pid.substring(startIndex);
	}
}
