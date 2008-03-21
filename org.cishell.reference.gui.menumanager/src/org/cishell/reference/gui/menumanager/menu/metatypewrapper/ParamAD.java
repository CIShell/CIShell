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
		
		this.defaultValueOverride = defaultValueOverride;
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

}
