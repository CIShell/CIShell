package org.cishell.templates.wizards.staticexecutable;

/*
 * For static executable algorithms, input data items are always files, and
 *  they are always referred to by an index (as opposed to an ID).
 * Thus, input data items only contain mime types and a position, and the
 *  position is determined by the delegate.
 */
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