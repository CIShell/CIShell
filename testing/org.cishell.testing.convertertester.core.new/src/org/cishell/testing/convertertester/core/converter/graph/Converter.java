package org.cishell.testing.convertertester.core.converter.graph;

import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Converter {
	
	private BundleContext bContext;
	
	private ServiceReference ref;
	
	public Converter(BundleContext bContext, ServiceReference ref) {
		this.bContext = bContext;
		this.ref = ref;
	}
	
	public ServiceReference getServiceReference() {
		return this.ref;
	}
	
	public ServiceReference getRef() {
		return this.ref;
	}
	
	public boolean isLossy() {
		String conversion = (String)
			ref.getProperty(AlgorithmProperty.CONVERSION);
		
		if (conversion == null) {
			return false; 
			//if lossiness is not defined, assume it is not lossy.
		}
		
		if (conversion.equals(AlgorithmProperty.LOSSY)) {
			return true;
		} else if (conversion.equals(AlgorithmProperty.LOSSLESS)) {
			return false;
		} else {
			//assuming lossy by default
			return true;
		}
	}
	
	public String getInData() {
		return (String) ref.getProperty(AlgorithmProperty.IN_DATA);
	}
	
	public String getOutData() {
		return (String) ref.getProperty(AlgorithmProperty.OUT_DATA);
	}
	
	public String getShortName() {
		return removePackagePrefix(getUniqueName());
	}
	
	public String getUniqueName() {
		return (String) this.ref.getProperty("service.pid");
	}
	
	public String toString() {
		return getUniqueName();
	}
	
	public Data[] execute(Data[] input, Hashtable parameters,
			CIShellContext cContext) {
		
		AlgorithmFactory convAlgFactory = 
			(AlgorithmFactory) this.bContext.getService(this.ref);
		Algorithm convAlg = convAlgFactory.createAlgorithm(input, parameters,
				cContext);
		
		Data[] output = convAlg.execute();
		
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
