package org.cishell.testing.convertertester.core.converter.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceReference;

public class ConverterGraph {
	prefuse.data.Graph converterGraph;
	Map<String, ArrayList<ServiceReference>> inDataToAlgorithm;
	Map<String, ArrayList<ConverterPath>> fileExtensionTestConverters;
	Map<String, ConverterPath> fileExtensionCompareConverters;
	ServiceReference[] converters;
	private static final String testOutData = "prefuse.data.Graph";
	
	public ConverterGraph(ServiceReference[] converters){
		this.converters = converters;
		inDataToAlgorithm = new HashMap<String, ArrayList<ServiceReference>>();
		fileExtensionTestConverters = new ConcurrentHashMap<String, ArrayList<ConverterPath>>();
		fileExtensionCompareConverters = new ConcurrentHashMap<String, ConverterPath>();
		
		associateAlgorithms(this.converters, this.inDataToAlgorithm);
		createConverterPaths(this.inDataToAlgorithm, this.fileExtensionTestConverters, this.fileExtensionCompareConverters);
		//System.out.println("And here");
	}

	private void associateAlgorithms(ServiceReference[] sr, Map<String, ArrayList<ServiceReference>> hm){
		for(ServiceReference srv : sr){
			String s = srv.getProperty("in_data").toString();
			if(hm.get(s) == null){
				ArrayList<ServiceReference> al = new ArrayList<ServiceReference>();
				al.add(srv);
				hm.put(s, al);
			}
			else{
				hm.get(s).add(srv);
			}
		}
	}
	
	private void createConverterPaths(Map<String, ArrayList<ServiceReference>> algorithms, Map<String, ArrayList<ConverterPath>> testPaths,
			Map<String, ConverterPath> comparePaths){
		
		for(String s : algorithms.keySet()){
			if(s.startsWith("file-ext")){
				
				
				ConverterPath test = new ConverterPath();
				//ConverterPath 
				test.setInData(s);
				//createPaths(algorithms, testPaths, comparePaths, test, s);
				createPaths(algorithms.get(s), test, s);
				//System.out.println("I've got here");
			}
		}
	}
	
	private ConverterPath createPaths(ArrayList<ServiceReference> algorithms, ConverterPath path, String dataType){
		ArrayList<ServiceReference> refs = removeReferences(algorithms, path);
		
			addCompareCycle(path);
		
		if(path.getInData().equals(path.getOutData())){
			addTestCycle(path);
			return path;
		}
		while(!refs.isEmpty()){
			ConverterPath p = new ConverterPath(path);
			p.addAlgoritm(refs.get(0));
			refs.remove(0);
			createPaths(this.inDataToAlgorithm.get(p.getOutData()), p, p.getOutData());
		
		}
		return null;		
	}
	
	private void addTestCycle(ConverterPath cp){
		String firstOutData, lastInData;
		firstOutData = cp.getPath().get(0).getProperty("out_data").toString();
		lastInData = cp.getPath().get(cp.getPath().size()-1).getProperty("in_data").toString();
		if(firstOutData.equals(lastInData)){
			addPath(cp);
		}
	}
	
	private void addCompareCycle(ConverterPath cp){
		if(cp.getOutData() != null){
		if(cp.getOutData().equals(ConverterGraph.testOutData)){
			String key = cp.getInData() + " " + cp.getPath().get(0).getProperty("out_data").toString();
		if(this.fileExtensionCompareConverters.get(key) == null){

			System.out.println("Adding a new Comparison Path:\n" + cp);
			this.fileExtensionCompareConverters.put(key, new ConverterPath(cp));
		}
		else {
			int pathSize = this.fileExtensionCompareConverters.get(key).getPath().size();
			if(pathSize > cp.getPath().size()){
				ConverterPath oldPath = this.fileExtensionCompareConverters.get(key);
				System.out.println("Replacing Comparision Path:\n" + oldPath + "with\n"
						+ cp);
				this.fileExtensionCompareConverters.put(key, new ConverterPath(cp));
			}
		}
		}
		}
	}
	
	private static ArrayList<ServiceReference> removeReferences(ArrayList<ServiceReference> al, ConverterPath cp){
		ArrayList<ServiceReference> srs = new ArrayList<ServiceReference>(al);
		srs.removeAll(cp.getPath());
		ArrayList<ServiceReference> forbidden = new ArrayList<ServiceReference>();
		for(ServiceReference sr: srs){
			String ss = sr.getProperty("out_data").toString();
			if(ss.startsWith("file-ext") && (!ss.equals(cp.getInData()))){
				System.out.println(sr.getProperty("service.pid") + " yes");
				forbidden.add(sr);
			}
		}
		srs.removeAll(forbidden);
		return srs;
	}
	
	private void addPath(ConverterPath p){
		if(this.fileExtensionTestConverters.get(p.getInData()) == null){
			System.out.println("Adding a new path");
			ArrayList<ConverterPath> paths = new ArrayList<ConverterPath>();
			paths.add(p);
			this.fileExtensionTestConverters.put(p.getInData(), paths);
			System.out.println("Successfully Added");
		}
		else{
			System.out.println("Adding a path");
			this.fileExtensionTestConverters.get(p.getInData()).add(p);
			System.out.println("Successfully Added");
		}
	}
		
	
	public String printTestConverterPath(String s){
		StringBuffer sb = new StringBuffer();
			for(ConverterPath cp : this.fileExtensionTestConverters.get(s)){
				sb.append(cp.toString());
			}
			sb.trimToSize();
		return sb.toString();
	}
	
	public String printTestConverterPaths(){
		StringBuffer sb = new StringBuffer();
		for(String s : this.fileExtensionTestConverters.keySet()){
			sb.append(printTestConverterPath(s));
		}
		sb.trimToSize();
		return sb.toString();
	}
	
	
	public String printComparisonConverterPath(String s){
		return this.fileExtensionCompareConverters.get(s).toString();
	}
	
	public String printComparisonConverterPaths(){
		StringBuffer sb = new StringBuffer();
		for(String s: this.fileExtensionCompareConverters.keySet()){
			sb.append(printComparisonConverterPath(s));
		}
		sb.trimToSize();
		return sb.toString();
	}
	
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		for(String s : this.inDataToAlgorithm.keySet()){
			str.append(s + "\n");
			for(ServiceReference sr : this.inDataToAlgorithm.get(s)){
				str.append("\t" + sr.getProperty("service.pid") + "\n");
			}
		}
		str.append("Test Paths:\n");
		str.append(printTestConverterPaths());
		str.append("Comparison Paths:\n");
		str.append(printComparisonConverterPaths());
		str.trimToSize();
		return str.toString();
	}
}