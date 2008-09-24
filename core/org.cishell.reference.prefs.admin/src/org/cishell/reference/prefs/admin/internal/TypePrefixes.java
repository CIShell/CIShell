package org.cishell.reference.prefs.admin.internal;

import java.util.HashMap;
import java.util.Map;

import org.cishell.reference.prefs.admin.PreferenceAD;
import org.osgi.service.metatype.AttributeDefinition;

public class TypePrefixes {

	public static final String FONT_PREFIX = "font:";
	public static final String DIRECTORY_PREFIX = "directory:";
	public static final String FILE_PREFIX = "file:";
	public static final String PATH_PREFIX = "path:";
	public static final String COLOR_PREFIX = "color:";
	
	private static Map prefixToTypeID = new HashMap();
	static {
		prefixToTypeID.put(FONT_PREFIX, new Integer(PreferenceAD.FONT));
		prefixToTypeID.put(DIRECTORY_PREFIX, new Integer(PreferenceAD.DIRECTORY));
		prefixToTypeID.put(FILE_PREFIX, new Integer(PreferenceAD.FILE));
		prefixToTypeID.put(PATH_PREFIX, new Integer(PreferenceAD.PATH));
		prefixToTypeID.put(COLOR_PREFIX, new Integer(PreferenceAD.COLOR));
	}
	
	private static Map typeIDToPrefix = new HashMap();
	static {
		typeIDToPrefix.put(new Integer(PreferenceAD.FONT), FONT_PREFIX);
		typeIDToPrefix.put(new Integer(PreferenceAD.DIRECTORY), DIRECTORY_PREFIX);
		typeIDToPrefix.put(new Integer(PreferenceAD.FILE), FILE_PREFIX);
		typeIDToPrefix.put(new Integer(PreferenceAD.PATH), PATH_PREFIX);
		typeIDToPrefix.put(new Integer(PreferenceAD.COLOR), COLOR_PREFIX);
		typeIDToPrefix.put(new Integer(PreferenceAD.CHOICE), "");
		typeIDToPrefix.put(new Integer(PreferenceAD.PATH), "");
		typeIDToPrefix.put(new Integer(PreferenceAD.TEXT), "");
	}
    
    public static boolean hasPrefix(AttributeDefinition prefAD, String prefix) {
    	return prefAD.getDefaultValue()[0].startsWith(prefix);
    }
    
    //Will return "" for unrecognized prefixes
    public static String getPrefPrefixFromPrefTypeID(Integer typeID) {
    	String prefix = (String) typeIDToPrefix.get(typeID);
    	if (prefix != null) {
    		return prefix;
    	} else {
    		return "";
    	}
    }
    
    //Will return -1 for unrecognized type IDs.
    public static Integer getPrefTypeIDFromPrefix(String prefix) {
    	Integer typeID = (Integer) prefixToTypeID.get(prefix);
    	if (typeID != null) {
    		return typeID;
    	} else {
    		System.err.println("Warning: unable to return valid type ID for prefix '" + prefix + "' in TypePrefix.prefPrefixToPrefTypeID");
    		return new Integer(-1);
    	}
    }
}
