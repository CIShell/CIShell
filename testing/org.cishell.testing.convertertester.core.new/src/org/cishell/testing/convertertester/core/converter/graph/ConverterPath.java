package org.cishell.testing.convertertester.core.converter.graph;

import java.util.ArrayList;
import java.util.List;

import org.cishell.framework.algorithm.AlgorithmProperty;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConverterPath implements AlgorithmProperty {
	
	private BundleContext bContext;
	private LogService log;
	private String in_data = null;
	private String out_data = null;
	
	private List path;
	
	public ConverterPath(BundleContext bContext, LogService log){
		this.bContext = bContext;
		this.log = log;
		
		path = new ArrayList();
	}
	
	public ConverterPath(ConverterPath p, BundleContext bContext) {
		this.bContext = bContext;

		in_data = p.getInData();
		out_data = p.getOutData();
		
		this.path = new ArrayList(p.getPath());
		
	}
	
	public void setInData(String s){

		this.in_data = s;
	}
	
	public void setOutData(String s){

		this.out_data = s;
	}
	
	public boolean add(Converter c){

		boolean val = true;
		
		if(path.contains(c)){	
			return false;
		}
		
		path.add(c);
		this.setOutData(c.getOutData());
		return val;
	}
	
	public String getInData(){

		return this.in_data;
	}
	
	public String getOutData(){

		return this.out_data;
	}
	
	public String getAcceptedFileFormat() {
		if (size() > 0) {
			return (String) getRef(0).getProperty(AlgorithmProperty.OUT_DATA);
		} else {
			this.log.log(LogService.LOG_ERROR, "Converter Path cannot " +
					"determine accepted file format if there are no " + 
					"converters inside it. Returning null String.");
			return "";
		}
	}
	
	public List getPath(){

		return this.path;
	}
	
	//inclusive
	public List getPathUpTo(Converter upToConv) {
		int convIndex = -1;
		for (int ii = 0; ii < this.path.size(); ii++) {
			Converter aConvInPath = get(ii);
			
			if (aConvInPath.equals(upToConv)) {
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
	
	public ServiceReference getRef(int index) {

		Converter c = (Converter) this.path.get(index);
		ServiceReference ref = c.getRef();
		return ref;
	}
	
	public Converter[] getPathAsArray(){

		return (Converter[]) this.path.toArray(new Converter[0]);
	}
	
	public boolean isLossy() {

		String lossiness = LOSSLESS;
        for (int i = 0; i < this.path.size(); i++) {
        	Converter c = (Converter) this.path.get(i);
        	
            if (c.isLossy()) {
            	System.out.println("FOUND A LOSSY CONVERTER!");
                lossiness = LOSSY;
            }
        } 
        
        boolean result = lossiness.equals(LOSSY);
        return result;
	}
	
	public boolean preservesIDs() {

		//TODO: Determine this somehow.
		return false;
	}
	
	public int size() {

		return this.path.size();
	}
	
	public boolean containsConverterNamed(String convName) {
		for (int ii = 0; ii < this.path.size(); ii++) {
			Converter conv = (Converter) this.path.get(ii);
			
			if (conv.getShortName().equals(convName) ||
					conv.getUniqueName().equals(convName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getConverterName(int index) {
		return (String) get(index).getUniqueName();
	}
}
