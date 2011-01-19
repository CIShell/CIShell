package org.cishell.reference.service.metatype;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class BasicObjectClassDefinition implements ObjectClassDefinition {
	
	List<AttributeDefinition> optionalAttributeDefinitions = new ArrayList<AttributeDefinition>();
	List<AttributeDefinition> requiredAttributeDefinitions = new ArrayList<AttributeDefinition>();
	List<AttributeDefinition> allAttributeDefinitions = new ArrayList<AttributeDefinition>();
	private String ID;
	private String name;
	private String description;
	private InputStream icon;
	
	public BasicObjectClassDefinition(String ID, String name, String description, InputStream icon) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.icon = icon;
	}
	
	public void addAttributeDefinition(int flag, AttributeDefinition definition) {
		if (flag == REQUIRED) {
			this.requiredAttributeDefinitions.add(definition);
		} else if (flag == OPTIONAL) {
			this.optionalAttributeDefinitions.add(definition);
		}

		this.allAttributeDefinitions.add(definition);
	}

	public AttributeDefinition[] getAttributeDefinitions(int flag) {
		List<AttributeDefinition> results = new ArrayList<AttributeDefinition>();
		
		if (flag == REQUIRED) {
			results.addAll(this.requiredAttributeDefinitions);
		} else if (flag == OPTIONAL) {
			results.addAll(this.optionalAttributeDefinitions);
		} else {
			results.addAll(this.allAttributeDefinitions);
		}
		
		return makeArray(results);
	}
	
	private AttributeDefinition[] makeArray(List<AttributeDefinition> definitions) {
		return definitions.toArray(new AttributeDefinition[0]);
	}

	public String getDescription() {
		return description;
	}

	public String getID() {
		return ID;
	}

	public InputStream getIcon(int arg0) throws IOException {
		return icon;
	}

	public String getName() {
		return name;
	}

}
