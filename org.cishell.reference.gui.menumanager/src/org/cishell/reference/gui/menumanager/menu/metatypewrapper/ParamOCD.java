package org.cishell.reference.gui.menumanager.menu.metatypewrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class ParamOCD implements ObjectClassDefinition {
	
	private ObjectClassDefinition realOCD;
	private ParamAD[] wrappedADs;
	
	private LogService log;
	
	private Dictionary defaultOverrider;
	
	public ParamOCD(ObjectClassDefinition realOCD, Dictionary defaultOverrider) {
		this.realOCD = realOCD;
		this.defaultOverrider = defaultOverrider;
		
		//TODO: don't always return all attributeDefinitions, regardless of filter
		this.wrappedADs = wrapAttributeDefinitions(realOCD.getAttributeDefinitions(ObjectClassDefinition.ALL));
	}
	
	private ParamAD[] wrapAttributeDefinitions(AttributeDefinition[] realAttributeDefinitions) {
		ParamAD[] wrappedADs = new ParamAD[realAttributeDefinitions.length];
		
		for (int i = 0; i < realAttributeDefinitions.length; i++) {
			AttributeDefinition realAD = realAttributeDefinitions[i];
			
			String[] defaultOverrideValue = getDefaultOverrideValue(realAD.getID(), defaultOverrider);
			
			ParamAD wrappedAD = new ParamAD(this.log, realAD, defaultOverrideValue);
			
			wrappedADs[i] = wrappedAD;
		}
		
		return wrappedADs;
	}
	
	private String[] getDefaultOverrideValue(String overrideKey, Dictionary defaultOverrider) {
		if (defaultOverrider != null) {
			String defaultOverrideValue = (String) defaultOverrider.get(overrideKey);
			if (defaultOverrideValue != null) {
				return new String[]{defaultOverrideValue};
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public AttributeDefinition[] getAttributeDefinitions(int filter) {
		return wrappedADs;
	}
	
	public String getDescription() {
		return this.realOCD.getDescription();
	}
	
	public String getID() {
		return this.realOCD.getID();
	}

	public InputStream getIcon(int size) throws IOException {
		return this.realOCD.getIcon(size);
	}

	public String getName() {
		return this.realOCD.getName();
	}

}
