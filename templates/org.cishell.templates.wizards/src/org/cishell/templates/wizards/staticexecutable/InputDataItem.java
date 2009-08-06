package org.cishell.templates.wizards.staticexecutable;

public class InputDataItem {
	String mimeType;
	
	public InputDataItem(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}