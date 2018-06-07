package org.cishell.reference.service.metatype;

import org.osgi.service.metatype.AttributeDefinition;

public class BasicAttributeDefinition implements AttributeDefinition {

	private String id;
	private String name;
	private String description;
	private int type;
	private int cardinality;
	private String[] defaultValue;
	private AttributeValueValidator validator;
	private String[] optionLabels;
	private String[] optionValues;

	public BasicAttributeDefinition(String id, String name, String description, int type, int cardinality, String[] defaultValue, AttributeValueValidator validator, String[] optionLabels, String[] optionValues) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.cardinality = cardinality;
		this.defaultValue = defaultValue;
		
		if(validator == null) {
			this.validator = new AbstractAttributeValueValidator(){};
		} else {
			this.validator = validator;
		}
		
		this.optionLabels = optionLabels;
		this.optionValues = optionValues;
	}

	public BasicAttributeDefinition(String id, String name, String description, int type) {
		this(id, name, description, type, 0, null, null, null, null);
	}

	public BasicAttributeDefinition(String id, String name, String description, int type, int cardinality, String[] defaultValue) {
		this(id, name, description, type, cardinality, defaultValue, null, null, null);
	}
	
	public BasicAttributeDefinition(String id, String name, String description, int type, String defaultValue) {
		this(id, name, description, type, 0, new String[]{defaultValue});
	}
	
	public BasicAttributeDefinition(String id, String name, String description, int type, int cardinality) {
		this(id, name, description, type, cardinality, null, null, null, null);
	}

	public BasicAttributeDefinition(String id, String name, String description, int type, String[] optionLabels, String[] optionValues) {
		this(id, name, description, type, 0, null, null, optionLabels, optionValues);
	}

	public int getCardinality() {
		return cardinality;
	}

	public String[] getDefaultValue() {
		return defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String[] getOptionLabels() {
		return optionLabels;
	}

	public String[] getOptionValues() {
		return optionValues;
	}

	public int getType() {
		return type;
	}

	public String validate(String value) {
		return validator.validate(value);
	}

}
