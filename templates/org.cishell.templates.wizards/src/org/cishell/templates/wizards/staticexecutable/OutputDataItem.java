package org.cishell.templates.wizards.staticexecutable;

/*
 * For static executable algorithms, output data items are always files.  It is
 *  up to the algorithm creators to make the names the output files their
 *  programs create and the names specified in the static executable algorithm
 *  match up, so CIShell can find the files.
 * Labels can be specified for the output files.
 * Output files for static executable algorithms also contain data types, which
 *   determine the icon CIShell uses for the associated data item in the Data
 *   Manager.
 *  Example data types are Network and Plot.
 * Output files must also specify appropriate mime types.
 */
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