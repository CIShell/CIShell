package org.cishell.reference.prefs.admin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public interface PreferenceOCD extends ObjectClassDefinition {

	//use in standard way
	public abstract AttributeDefinition[] getAttributeDefinitions(int filter);

	//use to get at the special preference attribute goodness.
	public abstract PreferenceAD[] getPreferenceAttributeDefinitions(
			int filter);

	public abstract String getDescription();

	public abstract String getID();

	public abstract InputStream getIcon(int size) throws IOException;

	public abstract String getName();

}