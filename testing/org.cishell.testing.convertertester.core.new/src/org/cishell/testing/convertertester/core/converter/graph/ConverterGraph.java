package org.cishell.testing.convertertester.core.converter.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		fileExtensionTestConverters = new HashMap<String, ArrayList<ConverterPath>>();
		fileExtensionCompareConverters = new HashMap<String, ConverterPath>();
		
		associateAlgorithms(this.converters, this.inDataToAlgorithm);
		createConverterPaths(this.inDataToAlgorithm, this.fileExtensionTestConverters, this.fileExtensionCompareConverters);
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
				createPaths(algorithms, testPaths, comparePaths, test, s);
			}
		}
	}
	
	private void createPaths(Map<String,ArrayList<ServiceReference>> algorithms, Map<String, ArrayList<ConverterPath>> testPaths,
			Map<String, ConverterPath> comparePaths, ConverterPath path, String dataType){
		/*
		if(path.getInData().equals(path.getOutData())){
			if(testPaths.get(path.getInData()) == null){
				ArrayList<ConverterPath> paths = new ArrayList<ConverterPath>();
				paths.add(new ConverterPath(path));
				testPaths.put(path.getInData(), paths);
				System.out.println(path);
			}
			else{
				testPaths.get(path.getInData()).add(new ConverterPath(path));
				System.out.println(path);
			}
		}*/
		try{
		ArrayList<ServiceReference> algs = new ArrayList<ServiceReference>(algorithms.get(dataType));
		
		algs.removeAll(path.getPath());
		for(ServiceReference sr : algs){
			//for(ServiceReference sr: algs){
				System.out.println(sr.getProperty("service.pid"));
			//}
			ConverterPath p = new ConverterPath(path);
			System.out.println();
			if(p.addAlgoritm(sr)){
				algs.remove(sr);
				createPaths(algorithms, testPaths,comparePaths,p,p.getOutData());
			}
			
			else{
				if(testPaths.get(path.getInData()) == null){
					ArrayList<ConverterPath> paths = new ArrayList<ConverterPath>();
					paths.add(p);
					testPaths.put(path.getInData(), paths);
					System.out.println(p);
				}
				else{
					testPaths.get(path.getInData()).add(p);
					System.out.println(p);
				}
				algs.remove(sr);
			}
		}
		}catch(NullPointerException npe){
			npe.printStackTrace();
		}
		}
		
	
	
	public String toString(){
		String str = "";
		for(String s : this.inDataToAlgorithm.keySet()){
			str += s + "\n";
			for(ServiceReference sr : this.inDataToAlgorithm.get(s)){
				str += "\t" + sr.getProperty("service.pid") + "\n";
			}
		}
		
		for(String s : this.fileExtensionTestConverters.keySet()){
			for(ConverterPath cp : this.fileExtensionTestConverters.get(s)){
				str += cp.toString();
			}
		}
		
		return str;
	}
}