package org.cishell.reference.service.metatype;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class BasicObjectClassDefinition implements ObjectClassDefinition {
	
	List attributeDefinitionsOptional = new ArrayList();
	List attributeDefinitionsRequired = new ArrayList();
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
		if(flag == REQUIRED) {
			this.attributeDefinitionsRequired.add(definition);
		} else if(flag == OPTIONAL) {
			this.attributeDefinitionsOptional.add(definition);
		}
	}
	
	
	public AttributeDefinition[] getAttributeDefinitions(int flag) {
		
		List results = new ArrayList();
		
		if(flag == REQUIRED || flag == ALL) {
			results.addAll(this.attributeDefinitionsRequired);
		}
		
		if(flag == OPTIONAL || flag == ALL) {
			results.addAll(this.attributeDefinitionsOptional);
			
		}
		
		return makeArray(results);
	}
	
	private AttributeDefinition[] makeArray(List definitions) {
		AttributeDefinition[] result = new AttributeDefinition[definitions.size()];
		
		for(int ii = 0; ii < definitions.size(); ii++) {
			result[ii] = (AttributeDefinition) definitions.get(ii);
		}
		
		return result;
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
