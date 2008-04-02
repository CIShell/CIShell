package org.cishell.testalgorithm4;

import java.util.Dictionary;
import java.util.Enumeration;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;

public class TestAlgorithm4 implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    
    private Dictionary preferences;
    
    public TestAlgorithm4(Data[] data, Dictionary parameters, CIShellContext context, Dictionary preferences) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
        this.preferences = preferences;
    }

    public Data[] execute() {
    	printPreferences(this.preferences);
        return null;
    }
    
    private void printPreferences(Dictionary preferences) {
		System.out.println("  Preferences are as follows:");
		if (preferences == null) {
			System.out.println("    Dictionary is null!");
		} else {
			Enumeration propertiesKeys = preferences.keys();
			
			while (propertiesKeys.hasMoreElements()) {
				String propertiesKey = (String) propertiesKeys.nextElement();
				
				Object propertiesValue = preferences.get(propertiesKey);
				System.out.println("    " + propertiesKey + ":" + propertiesValue);
			}
		}
}
}