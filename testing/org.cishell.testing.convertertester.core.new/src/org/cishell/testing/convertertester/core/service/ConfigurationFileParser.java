
package org.cishell.testing.convertertester.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.cishell.testing.convertertester.core.tester.ConfigurationFileConstants;


public class ConfigurationFileParser {
	//files
	private Queue comparisonFiles;
	//strings
	private Queue comparisonConverters;
	//strings
	private Queue testConverters;
	private boolean nodeIDChange = true;
	private String extension;
	private String configFile;
	

	private boolean processFileList  = false;
	private boolean processComparisonConvertersList = false;
	private boolean processTestConvertersList = false;


	public ConfigurationFileParser(){
		//strings
		comparisonConverters = new LinkedList();
		//strings
		testConverters = new LinkedList();
		//files
		comparisonFiles = new LinkedList();
		nodeIDChange = true;
	}

	public ConfigurationFileParser(File f) throws Exception{
		//strings
		comparisonConverters = new LinkedList();
		//strings
		testConverters = new LinkedList();
		//files
		comparisonFiles = new LinkedList();
		nodeIDChange = true;
		
		this.parseFile(f);
	}

	public void parseFile(File f){
		configFile = f.getName();
		int lineNum = 0;
		String line = null;
		BufferedReader reader;
		try{
			reader = new BufferedReader(new FileReader(f));

			//System.out.println("Beginning to parse.");

			while((line = reader.readLine()) != null){

				if(!line.trim().equals("")){

				//System.out.println("Parsing line: " + lineNum + " " + line);
				if(line.startsWith(ConfigurationFileConstants.TEST_GRAPHS)){
					line = line.replace(ConfigurationFileConstants.TEST_GRAPHS, "");
					//System.out.println(line);
					this.processFileList = true;
					this.processComparisonConvertersList = false;
					this.processTestConvertersList = false;	
				}
				if(line.startsWith(ConfigurationFileConstants.COMPARISON_CONVERTERS)){
					line = line.replace(ConfigurationFileConstants.COMPARISON_CONVERTERS, "");
					//System.out.println(line);
					this.processFileList = false;
					this.processComparisonConvertersList = true;
					this.processTestConvertersList = false;
				}
				if(line.startsWith(ConfigurationFileConstants.TEST_CONVERTERS)){
					line = line.replace(ConfigurationFileConstants.TEST_CONVERTERS, "");
					//System.out.println(line);
					this.processFileList = false;
					this.processComparisonConvertersList = false;
					this.processTestConvertersList = true;
				}
				if(line.startsWith(ConfigurationFileConstants.NODE_ID_CHANGE)){
					line = line.replace(ConfigurationFileConstants.NODE_ID_CHANGE, "");
					//System.out.println(line );
					this.nodeIDChange = new Boolean(line.toLowerCase()).booleanValue();
					this.processFileList = false;
					this.processComparisonConvertersList = false;
					this.processTestConvertersList = false;
				}
				if(line.startsWith(ConfigurationFileConstants.EXTENSION)){
					line = line.replace(ConfigurationFileConstants.EXTENSION, "");
					//System.out.println(line );
					this.extension = line;
					this.processFileList = false;
					this.processComparisonConvertersList = false;
					this.processTestConvertersList = false;
				}
				if(this.processFileList){
					this.processFiles(this.processLine(line));
				}
				if(this.processComparisonConvertersList){
					this.processComparisonConverters(this.processLine(line));
				}
				if(this.processTestConvertersList){
					this.processTestConverters(this.processLine(line));
				}
				}
				lineNum++;
				//System.out.println("Next line");
			}
		}
		catch(FileNotFoundException fnfe){
			System.out.println(fnfe);
		}
		catch(IOException iex){
			System.out.println(iex);
		}
		//System.out.println("Finished parsing");
	}

	private String[] processLine(String s){
		String[] line = s.split(",");
		for(int ii = 0; ii < line.length; ii++){
			String ss = (String) line[ii];
			line[ii] = ss.trim();
		}
		return line;
	}

	private void processFiles(String[] strings) throws FileNotFoundException{
		
		for(int ii = 0; ii < strings.length; ii++) {
			String s = strings[ii];
			if (! (s == null || s.length() == 0)) {

				if (s.charAt(0) != File.separatorChar) {
					
					/*
					 * relative paths are assumed to be relative to ther home
					 * directory
					 */
					s = System.getProperty("user.home") + File.separator + s; 
				}
				File f = new File(s);
					this.comparisonFiles.add(f);
			} 
		}
	}

	private void processTestConverters(String[] strings){
		for(int ii = 0; ii < strings.length; ii++) {
			String s = strings[ii];
			this.testConverters.add(s);
		}
	}

	private void processComparisonConverters(String[] strings){
		for(int ii = 0; ii < strings.length; ii++) {
			String s = strings[ii];
			this.comparisonConverters.add(s);
		}
	}

	public File[] getFiles(){
		File[] files = new File[this.comparisonFiles.size()];
		files = (File[]) this.comparisonFiles.toArray(files);
		return files;
	}

	public String[] getComparisonConverters(){
		return (String[]) this.comparisonConverters.toArray(new String[0]);
	}

	public String[] getTestConverters(){
		return (String []) this.testConverters.toArray(new String[0]);
	}

	public boolean getNodeIDChange(){
		return this.nodeIDChange;
	}

	public static String asString(File f, String s){
		String output = s;
		if(!f.isHidden()){
			if(f.isDirectory()){
				output += "Directory: ";
				output += f.getName()+ "\n";
				File[] files = f.listFiles();
				for (int ii = 0; ii < files.length; ii++){
					File ff = files[ii];
					output += asString(ff, s);
				}
			}
			else{
				output += "\t" + f.getName() + "\n";
			}
		}
		return output;
	}

	public String toString(){
		String output = "";
		output += "Files to test:\n";
		Iterator iter0 = this.comparisonFiles.iterator();
		while (iter0.hasNext()){
			File f = (File) iter0.next();
			output += asString(f,"");
		}

		output += "\nConverters to test:\n";
		Iterator iter1 = this.testConverters.iterator();
		while (iter1.hasNext()){
			String s = (String) iter1.next();
			output += s + "\n";
		}

		output += "\nConverters used to Compare files:\n";

		Iterator ii2 = this.comparisonConverters.iterator();
		while (ii2.hasNext()){
			String s = (String) ii2.next();
			output += s +"\n";
		}

		output += "\nNode IDs are expected to change: " + this.nodeIDChange + "\n";

		return output;
	}

	public String getExtension(){
		return this.extension;
	}
	
	public String getConfigFile(){
		return this.configFile;
	}
	
}


