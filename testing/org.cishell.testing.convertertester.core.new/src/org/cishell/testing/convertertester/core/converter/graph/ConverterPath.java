package org.cishell.testing.convertertester.core.converter.graph;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;

public class ConverterPath {
	String in_data;
	String out_data = null;
	ArrayList<ServiceReference> path;
	
	public ConverterPath(){
		path = new ArrayList<ServiceReference>();
	}
	
	public ConverterPath(ConverterPath p){
		in_data = p.getInData();
		out_data = p.getOutData();
		
		this.path = new ArrayList<ServiceReference>(p.getPath());
		
	}
	
	public void setInData(String s){
		this.in_data = s;
	}
	
	public void setOutData(String s){
		this.out_data = s;
	}
	
	public boolean addAlgoritm(ServiceReference sr){
		boolean val = true;
		//System.out.println(this.in_data + " " + this.out_data);
		if(path.contains(sr)){
			System.out.println("Path already contains " + sr.getProperty("service.pid"));
			//this.setOutData(sr.getProperty("out_data").toString());
			return false;
		}
		//System.out.println("Adding: " + sr.getProperty("service.pid"));
		path.add(sr);
		this.setOutData(sr.getProperty("out_data").toString());
		return val;
	}
	
	public String getInData(){
		return this.in_data;
	}
	
	public String getOutData(){
		return this.out_data;
	}
	
	public List<ServiceReference> getPath(){
		return this.path;
	}
	
	public String toString(){
		String val = this.in_data +" -->\n";
		
		for(ServiceReference sr : this.path){
			val += "\t" + sr.getProperty("service.pid") + "\n";
		}
		val += "--> " + this.out_data + "\n";
		return val;
	}

}
