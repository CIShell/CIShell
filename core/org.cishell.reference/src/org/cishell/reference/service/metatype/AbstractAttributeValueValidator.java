package org.cishell.reference.service.metatype;

public abstract class AbstractAttributeValueValidator implements
		AttributeValueValidator {

	public String validate(String value) {
		// indicates no validation needed/done
		return null;
	}

}
