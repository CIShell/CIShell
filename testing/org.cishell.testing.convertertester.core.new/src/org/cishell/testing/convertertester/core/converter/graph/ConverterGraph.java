package org.cishell.testing.convertertester.core.converter.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ConverterGraph {
	prefuse.data.Graph converterGraph;
	Map inDataToAlgorithm;
	Map fileExtensionTestConverters;
	Map fileExtensionCompareConverters;
	ServiceReference[] converters;
	BundleContext bContext;
	private static final String testOutData = "prefuse.data.Graph";
	
	public ConverterGraph(ServiceReference[] converters, BundleContext bContext){
		this.converters = converters;
		this.bContext = bContext;
		inDataToAlgorithm = new HashMap();//<String, ArrayList<ServiceReference>>();
		fileExtensionTestConverters = new ConcurrentHashMap();//<String, ArrayList<ConverterPath>>();
		fileExtensionCompareConverters = new ConcurrentHashMap();//<String, ConverterPath>();
		
		associateAlgorithms(this.converters, this.inDataToAlgorithm);
		createConverterPaths(this.inDataToAlgorithm, this.fileExtensionTestConverters, this.fileExtensionCompareConverters);
	
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
		String[] keySet = new String[algorithms.keySet().size()];
		keySet = (String[])algorithms.keySet().toArray(keySet);
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			if(s.startsWith("file-ext")){
				
				
				ConverterPath test = new ConverterPath(this.bContext);
			
				test.setInData(s);
			
				createPaths((ArrayList)algorithms.get(s), test, s);
				
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
			ConverterPath p = new ConverterPath(path, this.bContext);
			p.addAlgorithm((ServiceReference)refs.get(0));
			refs.remove(0);
			createPaths((ArrayList)this.inDataToAlgorithm.get(p.getOutData()), p, p.getOutData());
		
		}
		return null;		
	}
	
	private void addTestCycle(ConverterPath cp){
		String firstOutData, lastInData;
		firstOutData = ((ServiceReference)cp.getPath().get(0)).getProperty("out_data").toString();
		lastInData = ((ServiceReference)cp.getPath().get(cp.getPath().size()-1)).getProperty("in_data").toString();
		//System.out.println(firstOutData + " " + lastInData);
		if(firstOutData.equals(lastInData)){
			addTestPath(cp);
		}
	}
	
	private void addCompareCycle(ConverterPath cp){
		if(cp.getOutData() != null){
		if(cp.getOutData().equals(ConverterGraph.testOutData)){
			String key = cp.getInData() + " " + ((ServiceReference)cp.getPath().get(0)).getProperty("out_data").toString();
			//System.out.println(key);
		if(this.fileExtensionCompareConverters.get(key) == null){

		
			this.fileExtensionCompareConverters.put(key, new ConverterPath(cp, this.bContext));
		}
		else {
			ConverterPath tempPath = (ConverterPath)this.fileExtensionCompareConverters.get(key);
			int pathSize = tempPath.getPath().size();
			if(pathSize > cp.getPath().size()){
				
				this.fileExtensionCompareConverters.put(key, new ConverterPath(cp, this.bContext));
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
				
				forbidden.add(sr);
			}
		}
		srs.removeAll(forbidden);
		return srs;
	}
	
	private void addTestPath(ConverterPath p){
		String key = p.getInData();
		key += " ";
		key +=  ((ServiceReference)p.getPath().get(0)).getProperty("out_data").toString();
		if(this.fileExtensionTestConverters.get(key) == null){
		
			ArrayList paths = new ArrayList();
			paths.add(p);
			this.fileExtensionTestConverters.put(key, paths);
		
		}
		else{
			
			((ArrayList)this.fileExtensionTestConverters.get(key)).add(p);
			
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
		String[] keySet = new String[this.fileExtensionTestConverters.keySet().size()];
		keySet =	(String[])this.fileExtensionTestConverters.keySet().toArray(keySet);
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
		String[] keySet = new String[this.fileExtensionCompareConverters.keySet().size()];
			keySet = (String[])this.fileExtensionCompareConverters.keySet().toArray(keySet);
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			System.out.println(s);
			sb.append(printComparisonConverterPath(s));
		}
		sb.trimToSize();
		return sb.toString();
	}
	
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		String[] keySet = new String[this.inDataToAlgorithm.keySet().size()];
			keySet = (String[])this.inDataToAlgorithm.keySet().toArray(keySet);
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
	
	public ConverterPath[] getTestPath(String s){
		return (ConverterPath[])((ArrayList)this.fileExtensionTestConverters.get(s)).toArray(new ConverterPath[0]);
	}
	
	public ConverterPath[][] getTestPaths(){
		ConverterPath[][] paths = new ConverterPath[this.fileExtensionTestConverters.keySet().size()][];
		String[] fileExtensions = (String[])this.fileExtensionTestConverters.keySet().toArray(new String[0]);
		for(int i = 0; i < fileExtensions.length; i++){
			paths[i] = (getTestPath(fileExtensions[i]));
		}
		//this line may be busted
		return paths;
	}
	
	public ConverterPath getComparePath(String s){
		return (ConverterPath)this.fileExtensionCompareConverters.get(s);
	}
	
	
	public ConverterPath[] getComparePaths(){
		String[] fileExtensions = (String[])this.fileExtensionCompareConverters.keySet().toArray(new String[0]);
		ArrayList graphs = new ArrayList();
		for(int i = 0; i < fileExtensions.length; i++){
			graphs.add(getComparePath(fileExtensions[i]));
		}
		return (ConverterPath[])graphs.toArray(new ConverterPath[0]);
	}
	
	public Map getCompareMap(){
		return this.fileExtensionCompareConverters;
	}
	
	public Map getTestMap(){
		return this.fileExtensionTestConverters;
	}
	
	public File asNWB(){
		File f = getTempFile();
		Map nodes = assembleNodesSet();
		TreeSet output = assembleEdges(nodes);
		try{
		FileWriter out = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(out);
		
		writeNodes(bw,nodes);
		writeEdges(bw,output);
		}
		catch(IOException ex){
			System.out.println("Blurt!");
		}
		return f;
	}
	
	private void writeNodeHeader(BufferedWriter bw, int numNodes) throws IOException{
		bw.flush();
		bw.write("*Nodes " + numNodes + "\nid*int label*string\n");
	
	}
	
	private void writeNodes(BufferedWriter bw, Map nodes) throws IOException{
		System.out.println("*Nodes " + nodes.size() + "\n");
		writeNodeHeader(bw, nodes.size());
		String[] keySet = new String[nodes.keySet().size()];
		keySet = (String[])nodes.keySet().toArray(keySet);
		for(int i = 0; i < keySet.length; i++){
			bw.flush();
			bw.write(nodes.get(keySet[i]) + " \"" + keySet[i]+"\"\n");
		}
		
	}
	
	private void writeEdgeHeader(BufferedWriter bw, int numEdges) throws IOException{
		bw.flush();
		bw.write("*DirectedEdges " + numEdges + "\nsource*int target*int\n");
	}
	
		
	private void writeEdges(BufferedWriter bw, TreeSet edges) throws IOException{
		System.out.println("*DirectedEdges " + edges.size());
		writeEdgeHeader(bw,edges.size());
		
		String[] edgeArray = new String[edges.size()];
		edgeArray = (String[])edges.toArray(edgeArray);
		
		for(int i = 0; i < edgeArray.length; i++){
			System.out.println(edgeArray[i]);
			bw.flush();
			bw.write(edgeArray[i]+"\n");
		}
	}
	
	private Map assembleNodesSet(){
		
		Map nodesToInt = new ConcurrentHashMap();
		
		//create a set of all the in_data, out_data, and algorithm names
		String[] keySet = new String[this.inDataToAlgorithm.keySet().size()];
		keySet = (String[])this.inDataToAlgorithm.keySet().toArray(keySet);
		TreeSet nodeNames = new TreeSet();
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			nodeNames.add(s);
			ArrayList paths = (ArrayList)this.inDataToAlgorithm.get(s);
			ServiceReference[] references =  new ServiceReference[paths.size()];
			references = (ServiceReference[])paths.toArray(references);
			
			for(int j = 0; j < references.length; j++){
				ServiceReference r = references[j];
				nodeNames.add(r.getProperty("service.pid").toString());
			}
		}
		
		String[] names = new String[nodeNames.size()];
		names = (String[])nodeNames.toArray(names);
		
		for(int i = 0; i < names.length; i++){
			System.out.println(names[i] + " " + (i+1));
			nodesToInt.put(names[i], new Integer(i+1));
		}
		
		return nodesToInt;
	}
	
	private TreeSet assembleEdges(Map m){
		TreeSet edges = new TreeSet();
		String[] keySet = new String[m.size()];
		keySet = (String[])m.keySet().toArray(keySet);
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			System.out.println(keySet[i]);
			ArrayList paths = (ArrayList)this.inDataToAlgorithm.get(s);
			if(paths != null){
			ServiceReference[] references =  new ServiceReference[paths.size()];
			references = (ServiceReference[])paths.toArray(references);
			
			for(int j = 0; j < references.length; j++){
				String output1 = m.get(s).toString() + " ";
				String output2 = references[j].getProperty("service.pid").toString();
				output1 += m.get(output2).toString();
				output2 = m.get(output2).toString() + " " + m.get(references[j].getProperty("out_data")).toString(); 
				System.out.println(output1);
				System.out.println(output2);
				edges.add(output1);
				edges.add(output2);
			}
			}
			
		}
		return edges;
	}
	
	private File getTempFile(){
		File tempFile;

		String tempPath = System.getProperty("java.io.tmpdir");
		File tempDir = new File(tempPath+File.separator+"temp");
		if(!tempDir.exists())
			tempDir.mkdir();
		try{
			tempFile = File.createTempFile("NWB-Session-", ".nwb", tempDir);

		}catch (IOException e){
			
			tempFile = new File (tempPath+File.separator+"nwbTemp"+File.separator+"temp.nwb");

		}
		return tempFile;
	}
}