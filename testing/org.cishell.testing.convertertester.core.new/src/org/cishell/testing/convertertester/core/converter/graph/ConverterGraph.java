package org.cishell.testing.convertertester.core.converter.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConverterGraph {
	prefuse.data.Graph converterGraph;
	Map inDataToConverters;
	Map fileExtensionTestConverters;
	Map fileExtensionCompareConverters;
	Converter[] converters;
	BundleContext bContext;
	private LogService log;
	private static final String testOutData = "prefuse.data.Graph";
	
	public ConverterGraph(ServiceReference[] converterRefs,
			BundleContext bContext, LogService log) {
		this.bContext = bContext;
		this.log = log;
		
		this.converters = createConverters(converterRefs);
		
		inDataToConverters = 
			new HashMap();//<String, List<Convertere>>();
		fileExtensionTestConverters = 
			new ConcurrentHashMap();//<String, List<ConverterPath>>();
		fileExtensionCompareConverters = 
			new ConcurrentHashMap();//<String, ConverterPath>();
		
		associateConverters(this.converters, this.inDataToConverters);
		createConverterPaths(this.inDataToConverters, this.fileExtensionTestConverters, this.fileExtensionCompareConverters);
	
	}
	
	private Converter[] createConverters(ServiceReference[] convRefs) {
		List converters = new ArrayList();
		
		for (int ii = 0; ii < convRefs.length; ii++) {
			converters.add(new Converter(this.bContext, convRefs[ii]));
		}
		
		return (Converter[]) converters.toArray(new Converter[0]);
	}

	private void associateConverters(Converter[] cs, Map hm){
		for (int i = 0; i < cs.length; i++){
			Converter c = cs[i];
			
			String s = c.getInData();
			
			if(hm.get(s) == null){
				List l = new ArrayList();
				l.add(c);
				hm.put(s, l);
			}
			else{
				((List)hm.get(s)).add(c);
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
				
				
				ConverterPath test = new ConverterPath(this.bContext, this.log);
			
				test.setInData(s);
			
				createPaths((List)algorithms.get(s), test, s);
				
			}
		}
	
	}
	
	private ConverterPath createPaths(List algorithms, ConverterPath path, String dataType){
		List cs = removeReferences(algorithms, path);
		
			addCompareCycle(path);
		
		if(path.getInData().equals(path.getOutData())){
			addTestCycle(path);
			return path;
		}
		while(!cs.isEmpty()){
			ConverterPath p = new ConverterPath(path, this.bContext);
			p.add((Converter) cs.get(0));
			cs.remove(0);
			createPaths((List)this.inDataToConverters.get(p.getOutData()), p, p.getOutData());
		
		}
		return null;		
	}
	
	private void addTestCycle(ConverterPath cp){
		String firstOutData, lastInData;
		firstOutData = ((Converter) cp.getPath().get(0)).getOutData();
		lastInData = ((Converter)cp.getPath().get(cp.getPath().size()-1)).getInData();
		if(firstOutData.equals(lastInData)){
			addTestPath(cp);
		}
	}
	
	private void addCompareCycle(ConverterPath cp){
		if(cp.getOutData() != null){
		if(cp.getOutData().equals(ConverterGraph.testOutData)){
			String key = cp.getInData() + " " + ((Converter) cp.getPath().get(0)).getOutData();
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
	
	private static List removeReferences(List al, ConverterPath cp){
		List cs = new ArrayList(al);
		cs.removeAll(cp.getPath());
		List forbidden = new ArrayList();
		for(int i = 0; i < cs.size(); i++){
			Converter c = (Converter) cs.get(i);
			String outData = c.getOutData();
			if(outData.startsWith("file-ext") && (!outData.equals(cp.getInData()))){
				
				forbidden.add(c);
			}
		}
		cs.removeAll(forbidden);
		return cs;
	}
	
	private void addTestPath(ConverterPath p){
		String key = p.getInData();
		key += " ";
		key +=  ((Converter)p.getPath().get(0)).getOutData();
		if(this.fileExtensionTestConverters.get(key) == null){
		
			List paths = new ArrayList();
			paths.add(p);
			this.fileExtensionTestConverters.put(key, paths);
		
		}
		else{
			
			((List)this.fileExtensionTestConverters.get(key)).add(p);
			
		}
	}
		
	
	public String printTestConverterPath(String s){
		StringBuffer sb = new StringBuffer();
		List al = (List)this.fileExtensionTestConverters.get(s);
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
			//System.out.println(s);
			sb.append(printComparisonConverterPath(s));
		}
		sb.trimToSize();
		return sb.toString();
	}
	
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		String[] keySet = new String[this.inDataToConverters.keySet().size()];
			keySet = (String[])this.inDataToConverters.keySet().toArray(keySet);
		for(int i = 0; i < keySet.length; i++){
			String s = keySet[i];
			str.append(s + "\r\n");
			List al = (List)this.inDataToConverters.get(s);
			for(int j = 0; j < al.size(); j++){
				Converter c = (Converter)al.get(j);
				str.append("\t" + c.getUniqueName() + "\r\n");
			}
		}
		str.append("Test Paths:\r\n");
		str.append(printTestConverterPaths());
		str.append("Comparison Paths:\r\n");
		str.append(printComparisonConverterPaths());
		str.trimToSize();
		return str.toString();
	}
	
	public ConverterPath[] getTestPath(String s){
		return (ConverterPath[])((List)this.fileExtensionTestConverters.get(s)).toArray(new ConverterPath[0]);
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
		List graphs = new ArrayList();
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
	
	public Converter[] getAllConverters() {
		return this.converters;
	}
	
	public File asNWB() {
		
		Map nodes = assembleNodesSet();
		TreeSet edges = assembleEdges(nodes);
		
		File f = getTempFile();
		try {
			BufferedWriter writer = 
				new BufferedWriter(new FileWriter(f));
		
			writeNodes(writer,nodes);
			writeEdges(writer,edges);
			
		} catch(IOException e) {
			System.out.println("Blurt!");
			this.log.log(LogService.LOG_ERROR,
					"IOException while creating converter graph file",
					e);
		}
		
		return f;
	}
	
	private void writeNodeHeader(BufferedWriter bw, int numNodes) throws IOException{
		bw.flush();
		bw.write("*Nodes " + numNodes + "\r\nid*int label*string\r\n");
	
	}
	
	private void writeNodes(BufferedWriter bw, Map nodes) throws IOException{
		writeNodeHeader(bw, nodes.size());
		
		String[] keySet = new String[nodes.keySet().size()];
		
		keySet = (String[])nodes.keySet().toArray(keySet);
		for(int i = 0; i < keySet.length; i++){
			bw.write(nodes.get(keySet[i]) + " \"" + keySet[i]+"\"\r\n");
		}
		
		bw.flush();
		
	}
	
	private void writeEdgeHeader(BufferedWriter bw, int numEdges) throws IOException{
		bw.flush();
		bw.write("*DirectedEdges " + numEdges + "\r\nsource*int target*int\r\n");
	}
	
		
	private void writeEdges(BufferedWriter bw, TreeSet edges) throws IOException{
		writeEdgeHeader(bw,edges.size());
		
		String[] edgeArray = new String[edges.size()];
		edgeArray = (String[])edges.toArray(edgeArray);
		
		for(int i = 0; i < edgeArray.length; i++){
			bw.write(edgeArray[i]+"\r\n");
		}
		
		bw.flush();
	}
	
	private Map assembleNodesSet(){
		
		Map nodesToInt = new ConcurrentHashMap();
		
		//create a set of all the in_data, out_data, and algorithm names
		
		String[] inDatas = new String[this.inDataToConverters.keySet().size()];
		inDatas = (String[])this.inDataToConverters.keySet().toArray(inDatas);
		
		TreeSet nodeNameList = new TreeSet();
		
		//for each unique in_data...
		for(int i = 0; i < inDatas.length; i++){
			String inData = inDatas[i];
			
			//add the in_data string to our list of node names
			nodeNameList.add(inData);
			
			List convsList = (List)this.inDataToConverters.get(inData);
			Converter[] convs =  new Converter[convsList.size()];
			convs = (Converter[])convsList.toArray(convs);
			
			//for each converter associated with each in_data...
			for(int j = 0; j < convs.length; j++){
				Converter c = convs[j];
				//add the name of the converter to our list of node names
				nodeNameList.add(c.getShortName());
			}
		}
		
		String[] names = new String[nodeNameList.size()];
		names = (String[])nodeNameList.toArray(names);
		
		//for each node name in our list of node names ...
		for(int i = 0; i < names.length; i++){
			//associate that name with a unique integer in our map
			nodesToInt.put(names[i], new Integer(i+1));
		}
		
		//return our map of nodes to unique integers
		return nodesToInt;
	}
	
	private TreeSet assembleEdges(Map nodeNameToInt){
		TreeSet edges = new TreeSet();
		
		
		String[] nodeNames = new String[nodeNameToInt.size()];
		nodeNames = (String[])nodeNameToInt.keySet().toArray(nodeNames);
		//for each node name in our list of node names...
		for(int i = 0; i < nodeNames.length; i++){
			String nodeName = nodeNames[i];
			
			/*
			 * check to see if that node name is associated with a list of 
			 * converters.
			 * 
			 * (Node names are either the names of in_data formats or the
			 * names of converters)
			 */
			List converterList = (List)this.inDataToConverters.get(nodeName);
			
			//if our node name is associated with a list of converters...
			if(converterList != null) {
				//(then our node name must be the name of an in_data format)
				Converter[] convs =  new Converter[converterList.size()];
				convs = (Converter[])converterList.toArray(convs);
				
				//for each converter...
				for(int j = 0; j < convs.length; j++){
					String convName = convs[j].getShortName();
					
					String nodeNumber = nodeNameToInt.get(nodeName).toString();
					String convNumber = nodeNameToInt.get(convName).toString();
					
					//add an edge from our original node to this converter
					String edge1 = nodeNumber + " " + convNumber;
					edges.add(edge1);
					//and add an edge from this converter to our original node
					String edge2 = convNumber + " " + nodeNumber;
					edges.add(edge2);
	
				}
			}
			
		}
		
		//return our set of edges
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