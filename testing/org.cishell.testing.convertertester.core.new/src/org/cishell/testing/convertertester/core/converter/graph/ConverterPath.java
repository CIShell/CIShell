package org.cishell.testing.convertertester.core.converter.graph;

import java.util.ArrayList;
import java.util.List;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConverterPath implements AlgorithmProperty {
	
	private BundleContext bContext;
	private LogService log;
	private String in_data = null;
	private String out_data = null;
	
	private ArrayList path;
	
	private boolean algPathCached = false;
	private ArrayList algPath;
	
	
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
	
	public boolean addAlgorithm(ServiceReference sr){

		boolean val = true;
		
		if(path.contains(sr)){
			System.out.println("Path already contains " + sr.getProperty("service.pid"));
			
			return false;
		}
		

		path.add(sr);
		invalidateAlgPath();
		this.setOutData(sr.getProperty("out_data").toString());
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
	
	public ServiceReference getRef(int index) {

		return (ServiceReference) this.path.get(index);
	}
	
	public AlgorithmFactory getAlg(int index) {

		if (! algPathCached) cacheAlgPath();
		return (AlgorithmFactory) this.algPath.get(index);
	}
	
	public ServiceReference[] getPathAsArray(){

		return (ServiceReference[])this.path.toArray(new ServiceReference[0]);
	}
	
	public AlgorithmFactory[] getPathAsAlgorithms(){

		if (! algPathCached) cacheAlgPath();
		
		return (AlgorithmFactory[]) this.algPath.toArray(new AlgorithmFactory[0]);
	}
	
	public String toString(){

		String val = this.in_data +" -->\n";
		
		for(int i = 0; i < this.path.size(); i++){
			ServiceReference sr = (ServiceReference)this.path.get(i);
			val += "\t" + sr.getProperty("service.pid") + "\n";
		}
		val += "--> " + this.out_data + "\n";
		return val;
	}
	
	public boolean isLossy() {

		String lossiness = LOSSLESS;
        for (int i = 0; i < this.path.size(); i++) {
        	ServiceReference sr = (ServiceReference) this.path.get(i);
        	
            if (LOSSY.equals(sr.getProperty(CONVERSION))) {
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
	
	
	public ConverterPath appendNonMutating(ConverterPath otherPath) {
		
		List thisConvPath = this.path;
		List otherConvPath = otherPath.getPath();
		
		List thisConvPathCopy = new ArrayList(thisConvPath);
		thisConvPathCopy.addAll(otherConvPath);
		ConverterPath combinedPath = new ConverterPath(this.bContext, this.log);
		
		List combinedConvPath = thisConvPathCopy;
		for (int ii = 0; ii < combinedConvPath.size(); ii++) {
			ServiceReference sr = (ServiceReference) combinedConvPath.get(ii);
			
			combinedPath.addAlgorithm(sr);
		}
		
		return combinedPath;
	}
	
	public int size() {

		return this.path.size();
	}
	
	public String getConverterName(int index) {
		return (String) getRef(index).getProperty("service.pid");
	}

	
	private void cacheAlgPath() {
		this.algPath = new ArrayList();
		
		for (int i = 0; i < this.path.size(); i++){
			ServiceReference sr  = (ServiceReference)this.path.get(i);
			
			AlgorithmFactory alg = (AlgorithmFactory) this.bContext.getService(sr);
			this.algPath.add(alg);
		}
		
		this.algPathCached = true;
	}
	
	private void invalidateAlgPath() {
		
		this.algPathCached = false;
	}
}
