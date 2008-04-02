package org.cishell.reference.prefs.admin;

import org.osgi.service.metatype.AttributeDefinition;

public interface PreferenceAD extends AttributeDefinition {
	
	public static final int TEXT = 19;
	public static final int DIRECTORY = 20;
	public static final int FILE = 21;
	public static final int FONT = 22;
	public static final int PATH = 23;
	public static final int CHOICE = 24;
	public static final int COLOR = 25;

	public abstract int getCardinality();

	public abstract String[] getDefaultValue();

	public abstract String getDescription();

	public abstract String getID();

	public abstract String getName();

	public abstract String[] getOptionLabels();

	public abstract String[] getOptionValues();

	public abstract int getType();

	public abstract int getPreferenceType();

	public abstract String validate(String value);

}