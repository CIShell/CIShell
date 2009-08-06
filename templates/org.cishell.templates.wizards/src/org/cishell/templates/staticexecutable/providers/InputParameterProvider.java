package org.cishell.templates.staticexecutable.providers;

import org.osgi.service.metatype.AttributeDefinition;

public interface InputParameterProvider {
	public AttributeDefinition[] getInputParameters();
}