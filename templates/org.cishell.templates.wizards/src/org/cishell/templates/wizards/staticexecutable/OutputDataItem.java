package org.cishell.templates.wizards.staticexecutable;

public class OutputDataItem {
	private String fileName = "";
	private String label = "";
	private String dataType = "";
	private String mimeType = "";
	
	public OutputDataItem() {
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public String getDataType() {
		return this.dataType;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}