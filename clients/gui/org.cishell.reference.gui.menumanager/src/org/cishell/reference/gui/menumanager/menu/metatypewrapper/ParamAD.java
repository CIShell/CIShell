package org.cishell.reference.gui.menumanager.menu.metatypewrapper;

import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;

public class ParamAD implements AttributeDefinition {
		
	private LogService log;
	
	private AttributeDefinition realAD;
	
	private String[] defaultValueOverride;
	
	public ParamAD(LogService log, AttributeDefinition realAD, String[] defaultValueOverride) {
		this.log = log;
		
		this.realAD = realAD;
		this.defaultValueOverride = replaceSpecialValues(defaultValueOverride);
	}

	public int getCardinality() {
		return this.realAD.getCardinality();
	}

	public String[] getDefaultValue() {
		if (defaultValueOverride != null) {
			return defaultValueOverride;
		} else {
			return realAD.getDefaultValue();
		}
	}

	public String getDescription() {
		return this.realAD.getDescription();
	}
	
	public String getID() {
		return this.realAD.getID();
	}
	
	public String getName() {
		return this.realAD.getName();
	}

	public String[] getOptionLabels() {
		return this.realAD.getOptionLabels();
	}

	public String[] getOptionValues() {
		return this.realAD.getOptionValues();
	}

	public int getType() {
		return this.realAD.getType();
	}

	public String validate(String value) {
		return this.realAD.validate(value);
	}
	
	private String[] replaceSpecialValues(String[] overrideValues) {
		try {
		String[] defaultValues = realAD.getDefaultValue();
		if (defaultValues == null) return new String[0];
		String[] replacedValues = new String[defaultValues.length];
		for (int i = 0; i < defaultValues.length; i++) {
			
			if (defaultValues[i] != null && defaultValues[i].contains(":") && overrideValues[i] != null && overrideValues[i].equals("")) {
				replacedValues[i] = defaultValues[i].substring(0, defaultValues[i].indexOf(":") + 1);
			} else {
				replacedValues[i] = overrideValues[i];
			}
			if (defaultValues[i].contains(":") && overrideValues[i] != null && ! overrideValues[i].equals("") && ! overrideValues[i].contains(":")) {
				String prefix = defaultValues[i].substring(0, defaultValues[i].indexOf(":") + 1);
				replacedValues[i] = prefix + overrideValues[i];
			}
		}
		return replacedValues;
		} catch (Exception e) {
			e.printStackTrace();
			return overrideValues;
		}
	}

}
