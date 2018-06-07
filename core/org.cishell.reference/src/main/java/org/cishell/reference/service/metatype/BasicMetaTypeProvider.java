package org.cishell.reference.service.metatype;

import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

public class BasicMetaTypeProvider implements MetaTypeProvider {
	
	private ObjectClassDefinition definition;

	public BasicMetaTypeProvider(ObjectClassDefinition definition) {
		this.definition = definition;
	}
	
	public String[] getLocales() {
		// We support no locale specific localizations, which is indicated by returning null
		return null;
	}

	public ObjectClassDefinition getObjectClassDefinition(String arg0,
			String arg1) {
		return definition;
	}

}
