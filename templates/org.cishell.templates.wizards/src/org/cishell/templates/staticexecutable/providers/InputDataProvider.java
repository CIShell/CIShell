package org.cishell.templates.staticexecutable.providers;

import org.cishell.templates.wizards.staticexecutable.InputDataItem;

public interface InputDataProvider {
	public InputDataItem[] getInputDataItems();
	
	public String formServicePropertiesInputDataString();
}