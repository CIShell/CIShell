package org.cishell.templates.staticexecutable.providers;

import org.cishell.templates.wizards.staticexecutable.OutputDataItem;

public interface OutputDataProvider {
	public OutputDataItem[] getOutputDataItems();
	
	public String formServicePropertiesOutputDataString();
	public String formConfigPropertiesOutFilesString();
}