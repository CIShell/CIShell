package org.cishell.testing.convertertester.core.converter.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
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
		String[] keySet = new String[algorithms.keySet().size()];
		keySet = (String[])algorithms.keySet().toArray(keySet);
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

		//	System.out.println("Adding a new Comparison Path:\n" + cp);
			this.fileExtensionCompareConverters.put(key, new ConverterPath(cp));
		}
		else {
			ConverterPath tempPath = (ConverterPath)this.fileExtensionCompareConverters.get(key);
			int pathSize = tempPath.getPath().size();
			if(pathSize > cp.getPath().size()){
				//ConverterPath oldPath = (ConverterPath)this.fileExtensionCompareConverters.get(key);
				//System.out.println("Replacing Comparision Path:\n" + oldPath + "with\n"
				//		+ cp);
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
				//System.out.println(sr.getProperty("service.pid") + " yes");
				forbidden.add(sr);
			}
		}
		srs.removeAll(forbidden);
		return srs;
	}
	
	private void addPath(ConverterPath p){
		if(this.fileExtensionTestConverters.get(p.getInData()) == null){
			//System.out.println("Adding a new path");
			ArrayList paths = new ArrayList();
			paths.add(p);
			this.fileExtensionTestConverters.put(p.getInData(), paths);
			//System.out.println("Successfully Added");
		}
		else{
			//System.out.println("Adding a path");
			((ArrayList)this.fileExtensionTestConverters.get(p.getInData())).add(p);
			//System.out.println("Successfully Added");
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
		String[] keySet = new String[this.fileExtensionTestConverters.keySet().size()];
			keySet = (String[])this.fileExtensionTestConverters.keySet().toArray(keySet);
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			sb.append(printTestConverterPath(s));
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
		return (ConverterPath[])((ArrayList)this.fileExtensionTestConverters.get(s)).toArray();
	}
	
	public ConverterPath[][] getTestPaths(){
		String[] fileExtensions = (String[])this.fileExtensionTestConverters.keySet().toArray();
		ArrayList graphs = new ArrayList();
		for(int i = 0; i < fileExtensions.length; i++){
			graphs.add(getTestPath(fileExtensions[i]));
		}
		return (ConverterPath[][])graphs.toArray();
	}
	
	public ConverterPath getComparePath(String s){
		return (ConverterPath)this.fileExtensionCompareConverters.get(s);
	}
	
	public ConverterPath[] getComparePaths(){
		String[] fileExtensions = (String[])this.fileExtensionCompareConverters.keySet().toArray();
		ArrayList graphs = new ArrayList();
		for(int i = 0; i < fileExtensions.length; i++){
			graphs.add(getComparePath(fileExtensions[i]));
		}
		return (ConverterPath[])graphs.toArray();
	}
	
	public File asNWB(){
		File f = getTempFile();
		Map nodes = assembleNodesSet();
		TreeSet output = assembleEdges(nodes);
		try{
		FileWriter out = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(out);
		
		writeNodes(bw,nodes);
		
		}
		catch(IOException ex){
			System.out.println("Blurt!");
		}
		return f;
	}
	
	private void writeNodeHeader(BufferedWriter bw, int numNodes) throws IOException{
		bw.flush();
		bw.write("*Nodes " + numNodes);
	
	}
	
	private void writeNodes(BufferedWriter bw, Map nodes) throws IOException{
		System.out.println("*Nodes " + nodes.size());
		writeNodeHeader(bw, nodes.size());
		String[] keySet = new String[nodes.keySet().size()];
		keySet = (String[])nodes.keySet().toArray(keySet);
		
		for(int i = 0; i < keySet.length; i++){
			bw.flush();
			bw.write(nodes.get(keySet[i]) + " " + keySet[i]);
		}
		
	}
	
	private void writeEdgeHeader(BufferedWriter bw, int numEdges) throws IOException{
		bw.flush();
		bw.write("*DirectedEdges " + numEdges);
	}
	
		
	private void writeEdges(BufferedWriter bw, TreeSet edges) throws IOException{
		System.out.println("*DirectedEdges " + edges.size());
		writeEdgeHeader(bw,edges.size());
		
		String[] edgeArray = new String[edges.size()];
		edgeArray = (String[])edges.toArray(edgeArray);
		
		for(int i = 0; i < edgeArray.length; i++){
			System.out.println(edgeArray[i]);
			bw.flush();
			bw.write(edgeArray[i]);
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
				nodeNames.add(s);
			}
		}
		
		String[] names = new String[nodeNames.size()];
		names = (String[])nodeNames.toArray(names);
		
		for(int i = 0; i < names.length; i++){
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
			
			ArrayList paths = (ArrayList)this.inDataToAlgorithm.get(s);
			ServiceReference[] references =  new ServiceReference[paths.size()];
			references = (ServiceReference[])paths.toArray(references);
			
			for(int j = 0; j < references.length; j++){
				String output = m.get(s).toString() + " ";
				output += m.get(references[j].getProperty("service.pid")).toString();
				edges.add(output);
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