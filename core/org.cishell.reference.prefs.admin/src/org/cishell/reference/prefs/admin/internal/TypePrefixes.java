package org.cishell.reference.prefs.admin.internal;

import org.osgi.service.metatype.AttributeDefinition;

public class TypePrefixes {

	public static final String FONT_PREFIX = "font:";
	public static final String DIRECTORY_PREFIX = "directory:";
	public static final String FILE_PREFIX = "file:";
	public static final String PATH_PREFIX = "path:";
	public static final String COLOR_PREFIX = "color:";
	
    
    public static boolean hasPrefix(AttributeDefinition prefAD, String prefix) {
    	return prefAD.getDefaultValue()[0].startsWith(prefix);
    }
}
