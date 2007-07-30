package org.cishell.testing.convertertester.core.converter.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceReference;

public class ConverterGraph {
	prefuse.data.Graph converterGraph;
	Map inDataToAlgorithm;
	Map fileExtensionTestConverters;
	Map fileExtensionCompareConverters;
	ServiceReference[] converters;
	private static final String testOutData = "prefuse.data.Graph";
	
	public ConverterGraph(ServiceReference[] converters){
		this.converters = converters;
		inDataToAlgorithm = new HashMap();//<String, ArrayList<ServiceReference>>();
		fileExtensionTestConverters = new ConcurrentHashMap();//<String, ArrayList<ConverterPath>>();
		fileExtensionCompareConverters = new ConcurrentHashMap();//<String, ConverterPath>();
		
		associateAlgorithms(this.converters, this.inDataToAlgorithm);
		createConverterPaths(this.inDataToAlgorithm, this.fileExtensionTestConverters, this.fileExtensionCompareConverters);
		//System.out.println("And here");
	}

	private void associateAlgorithms(ServiceReference[] sr, Map hm){
		for(int i = 0; i < sr.length; i++){
			ServiceReference srv = sr[i];
			String s = srv.getProperty("in_data").toString();
			if(hm.get(s) == null){
				ArrayList al = new ArrayList();
				al.add(srv);
				hm.put(s, al);
			}
			else{
				((ArrayList)hm.get(s)).add(srv);
			}
		}
	}
	
	private void createConverterPaths(Map algorithms, Map testPaths,
			Map comparePaths){
		String[] keySet = (String[])algorithms.keySet().toArray();
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			if(s.startsWith("file-ext")){
				
				
				ConverterPath test = new ConverterPath();
				//ConverterPath 
				test.setInData(s);
				//createPaths(algorithms, testPaths, comparePaths, test, s);
				createPaths((ArrayList)algorithms.get(s), test, s);
				//System.out.println("I've got here");
			}
		}
	}
	
	private ConverterPath createPaths(ArrayList algorithms, ConverterPath path, String dataType){
		ArrayList refs = removeReferences(algorithms, path);
		
			addCompareCycle(path);
		
		if(path.getInData().equals(path.getOutData())){
			addTestCycle(path);
			return path;
		}
		while(!refs.isEmpty()){
			ConverterPath p = new ConverterPath(path);
			p.addAlgoritm((ServiceReference)refs.get(0));
			refs.remove(0);
			createPaths((ArrayList)this.inDataToAlgorithm.get(p.getOutData()), p, p.getOutData());
		
		}
		return null;		
	}
	
	private void addTestCycle(ConverterPath cp){
		String firstOutData, lastInData;
		firstOutData = ((ServiceReference)cp.getPath().get(0)).getProperty("out_data").toString();
		lastInData = ((ServiceReference)cp.getPath().get(cp.getPath().size()-1)).getProperty("in_data").toString();
		if(firstOutData.equals(lastInData)){
			addPath(cp);
		}
	}
	
	private void addCompareCycle(ConverterPath cp){
		if(cp.getOutData() != null){
		if(cp.getOutData().equals(ConverterGraph.testOutData)){
			String key = cp.getInData() + " " + ((ServiceReference)cp.getPath().get(0)).getProperty("out_data").toString();
		if(this.fileExtensionCompareConverters.get(key) == null){

			System.out.println("Adding a new Comparison Path:\n" + cp);
			this.fileExtensionCompareConverters.put(key, new ConverterPath(cp));
		}
		else {
			ConverterPath tempPath = (ConverterPath)this.fileExtensionCompareConverters.get(key);
			int pathSize = tempPath.getPath().size();
			if(pathSize > cp.getPath().size()){
				ConverterPath oldPath = (ConverterPath)this.fileExtensionCompareConverters.get(key);
				System.out.println("Replacing Comparision Path:\n" + oldPath + "with\n"
						+ cp);
				this.fileExtensionCompareConverters.put(key, new ConverterPath(cp));
			}
		}
		}
		}
	}
	
	private static ArrayList removeReferences(ArrayList al, ConverterPath cp){
		ArrayList srs = new ArrayList(al);
		srs.removeAll(cp.getPath());
		ArrayList forbidden = new ArrayList();
		for(int i = 0; i < srs.size(); i++){
			ServiceReference sr = (ServiceReference)srs.get(i);
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
			ArrayList paths = new ArrayList();
			paths.add(p);
			this.fileExtensionTestConverters.put(p.getInData(), paths);
			System.out.println("Successfully Added");
		}
		else{
			System.out.println("Adding a path");
			((ArrayList)this.fileExtensionTestConverters.get(p.getInData())).add(p);
			System.out.println("Successfully Added");
		}
	}
		
	
	public String printTestConverterPath(String s){
		StringBuffer sb = new StringBuffer();
		ArrayList al = (ArrayList)this.fileExtensionTestConverters.get(s);
			for(int i = 0; i < al.size(); i++){
				ConverterPath cp = (ConverterPath)al.get(i);
				sb.append(cp.toString());
			}
			sb.trimToSize();
		return sb.toString();
	}
	
	public String printTestConverterPaths(){
		StringBuffer sb = new StringBuffer();
		String[] keySet = (String[])this.fileExtensionTestConverters.keySet().toArray();
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
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
		String[] keySet = (String[])this.fileExtensionTestConverters.keySet().toArray();
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			sb.append(printTestConverterPath(s));
		}
		sb.trimToSize();
		return sb.toString();
	}
	
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		String[] keySet = (String[])this.inDataToAlgorithm.keySet().toArray();
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			str.append(s + "\n");
			ArrayList al = (ArrayList)this.inDataToAlgorithm.get(s);
			for(int j = 0; j < al.size(); j++){
				ServiceReference sr = (ServiceReference)al.get(j);
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
	
	public ArrayList getTestGraph(String s){
		return (ArrayList)this.fileExtensionTestConverters.get(s);
	}
	
	public ArrayList getTestGraphs(){
		String[] fileExtensions = (String[])this.fileExtensionTestConverters.keySet().toArray();
		ArrayList graphs = new ArrayList();
		for(int i = 0; i < fileExtensions.length; i++){
			graphs.add(getTestGraph(fileExtensions[i]));
		}
		return graphs;
	}
	
	public ConverterPath getCompareGraph(String s){
		return (ConverterPath)this.fileExtensionCompareConverters.get(s);
	}
	
	public ArrayList getCompareGraphs(){
		String[] fileExtensions = (String[])this.fileExtensionCompareConverters.keySet().toArray();
		ArrayList graphs = new ArrayList();
		for(int i = 0; i < fileExtensions.length; i++){
			graphs.add(getCompareGraph(fileExtensions[i]));
		}
		return graphs;
	}
}