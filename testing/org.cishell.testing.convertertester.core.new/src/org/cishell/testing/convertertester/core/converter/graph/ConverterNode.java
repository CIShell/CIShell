package org.cishell.testing.convertertester.core.converter.graph;

public class ConverterNode {
	String name;
	int id;
	
	public ConverterNode(String s, int i){
		name = s;
		id = i;
	}
	
	public String getName(){
		return name;
	}
	
	public int getID(){
		return id;
	}
	
	public void setName(String s){
		name = s;
	}
	
	public void setID(int i){
		id = i;
	}
	
	public String toString(){
		return name + " " + id;
	}

}
