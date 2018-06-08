package org.cishell.testing.convertertester.core.converter.graph;

import java.util.ArrayList;
import java.util.List;

import org.cishell.framework.algorithm.AlgorithmProperty;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConverterPath implements AlgorithmProperty {
	private LogService logger;
	private String inData = null;
	private String outData = null;
	private List<Converter> path;
	
	public ConverterPath(BundleContext bundleContext, LogService logger){
		this.logger = logger;
		this.path = new ArrayList<Converter>();
	}
	
	public ConverterPath(ConverterPath path, BundleContext bundleContext) {
		this.inData = path.getInData();
		this.outData = path.getOutData();
		this.path = new ArrayList<Converter>(path.getPath());
		
	}
	
	public void setInData(String inData) {
		this.inData = inData;
	}
	
	public void setOutData(String outData) {
		this.outData = outData;
	}
	
	public boolean add(Converter converter) {
		if (path.contains(converter)) {
			return false;
		} else {
			path.add(converter);
			this.setOutData(converter.getOutData());

			return true;
		}
	}
	
	public String getInData() {
		return this.inData;
	}
	
	public String getOutData() {
		return this.outData;
	}
	
	public String getAcceptedFileFormat() {
		if (size() > 0) {
			return (String) getServiceReference(0).getProperty(AlgorithmProperty.OUT_DATA);
		} else {
			this.logger.log(LogService.LOG_ERROR, "Converter Path cannot " +
					"determine accepted file format if there are no " + 
					"converters inside it. Returning null String.");

			return "";
		}
	}
	
	public List<Converter> getPath() {
		return this.path;
	}
	
	/// Inclusive.
	public List<Converter> getPathUpTo(Converter upToConverter) {
		int convIndex = -1;

		for (int ii = 0; ii < this.path.size(); ii++) {
			Converter aConvInPath = get(ii);
			
			if (aConvInPath.equals(upToConverter)) {
				convIndex = ii;
				break;
			}
		}
		
		if (convIndex != -1) {
			return this.path.subList(0, convIndex + 1);
		} else {
			throw new IllegalArgumentException("Attempted to get a " + 
					"subsection of a path up until a converter " + 
					"that does not exist in the original path");
		}
	}
	
	public Converter get(int index) {
		return (Converter) this.path.get(index);
	}
	
	public ServiceReference getServiceReference(int index) {
		Converter converter = this.path.get(index);
		ServiceReference serviceReference = converter.getServieReference();

		return serviceReference;
	}
	
	public Converter[] getPathAsArray() {
		return (Converter[]) this.path.toArray(new Converter[0]);
	}
	
	public boolean isLossy() {
		for (Converter converter : this.path) {
            if (converter.isLossy()) {
                return true;
            }
        } 
        
        return false;
	}
	
	public boolean preservesIDs() {
		// TODO: Determine this somehow.
		return false;
	}
	
	public int size() {
		return this.path.size();
	}
	
	public boolean containsConverterNamed(String converterName) {
		for (Converter converter : this.path) {
			if (converter.getShortName().equals(converterName) ||
					converter.getUniqueName().equals(converterName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getConverterName(int index) {
		return get(index).getUniqueName();
	}
}
