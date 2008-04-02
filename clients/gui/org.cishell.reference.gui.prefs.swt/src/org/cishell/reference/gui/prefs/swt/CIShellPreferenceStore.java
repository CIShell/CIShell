package org.cishell.reference.gui.prefs.swt;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.cishell.reference.prefs.admin.PreferenceAD;
import org.cishell.reference.prefs.admin.PreferenceOCD;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.osgi.service.cm.Configuration;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.ObjectClassDefinition;

public class CIShellPreferenceStore implements IPersistentPreferenceStore {

	private LogService log;
	
	private PreferenceOCD prefOCD; //could be useful if we implement some new features
	private Configuration prefConf;
	private Dictionary prefDict;
	private Map prefDefaults;
	
	private boolean needsSaving = false;
	
	public CIShellPreferenceStore(LogService log, PreferenceOCD prefOCD, Configuration prefConf) {
		this.log = log;
		this.prefOCD = prefOCD;
		this.prefConf = prefConf;
		this.prefDict = prefConf.getProperties();
		
		if (this.prefDict == null) {
			this.log.log(LogService.LOG_WARNING, "The configuration dictionary for the configuration object " + 
					prefConf.getPid() + "is null. \r\n" + "This may be due to an error in a bundles metadata, or may" +
							" be an internal error. This will likely cause errors related to preferences.");
		}
		
		generatePrefDefaults(prefOCD);
	}
	
	public boolean contains(String name) {
		return prefDict.get(name) != null;
	}

	public boolean getBoolean(String name) {
		return Boolean.parseBoolean((String) this.prefDict.get(name));
	}

	public boolean getDefaultBoolean(String name) {
		return Boolean.valueOf(((String) this.prefDefaults.get(name))).booleanValue();
	}

	public double getDefaultDouble(String name) {
		return Double.valueOf(((String) this.prefDefaults.get(name))).doubleValue();
	}

	public float getDefaultFloat(String name) {
		return Float.valueOf(((String) this.prefDefaults.get(name))).floatValue();
	}

	public int getDefaultInt(String name) {
		return Integer.valueOf(((String) this.prefDefaults.get(name))).intValue();
	}

	public long getDefaultLong(String name) {
		return Long.valueOf(((String) this.prefDefaults.get(name))).longValue();
	}

	public String getDefaultString(String name) {
		return ((String) this.prefDefaults.get(name));
	}

	public double getDouble(String name) {
		return Double.parseDouble(((String) this.prefDict.get(name)));
	}

	public float getFloat(String name) {
		return Float.parseFloat(((String) this.prefDict.get(name)));
	}

	public int getInt(String name) {
		return Integer.parseInt(((String) this.prefDict.get(name)));
	}

	public long getLong(String name) {
		return Long.parseLong(((String) this.prefDict.get(name)));
	}

	public String getString(String name) {	
		String result = (String) this.prefDict.get(name);
		return result;
	}

	public boolean isDefault(String name) {
		return prefDefaults.get(name).equals(prefDict.get(name));
	}

	public boolean needsSaving() {
		return this.needsSaving;
	}

	public void putValue(String name, String value) {
		this.prefDict.put(name, value);
	}


	public void setToDefault(String name) {
		this.needsSaving = true;	
		String defaultVal = (String) this.prefDefaults.get(name);
		this.prefDict.put(name, defaultVal);
	}
	
	public void setValue(String name, double value) {
		this.needsSaving = true;		
		this.prefDict.put(name, String.valueOf(value));
	}

	public void setValue(String name, float value) {
		this.needsSaving = true;
		this.prefDict.put(name, String.valueOf(value));
	}

	public void setValue(String name, int value) {
		this.needsSaving = true;
		this.prefDict.put(name, String.valueOf(value));
	}

	public void setValue(String name, long value) {
		this.needsSaving = true;
		this.prefDict.put(name,String.valueOf(value));
	}

	public void setValue(String name, String value) {
		this.needsSaving = true;
		this.prefDict.put(name, value);
	}

	public void setValue(String name, boolean value) {
		this.needsSaving = true;

		this.prefDict.put(name, String.valueOf(value));
	}

	public void save() throws IOException {
		this.needsSaving = false;
		this.prefConf.update(this.prefDict);
	}
	
	private void generatePrefDefaults(PreferenceOCD prefOCD) {
		PreferenceAD[] prefADs = prefOCD.getPreferenceAttributeDefinitions(ObjectClassDefinition.ALL);
		Map prefDefaults = new HashMap(prefADs.length);
		for (int ii = 0; ii < prefADs.length; ii++) {
			PreferenceAD prefAD = prefADs[ii];
			prefDefaults.put(prefAD.getID(), prefAD.getDefaultValue()[0]);
		}
		this.prefDefaults = prefDefaults;
	}
	
	//We don't set defaults like this (they are defined by the default field in MetaType Attribute Definitions)

	public void setDefault(String name, double value) {
	}

	public void setDefault(String name, float value) {
	}

	public void setDefault(String name, int value) {
	}

	public void setDefault(String name, long value) {
	}

	public void setDefault(String name, String defaultObject) {
	}

	public void setDefault(String name, boolean value) {
	}
	
	//more unsupported methods

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
	}
	
	public void firePropertyChangeEvent(String name, Object oldValue,
			Object newValue) {
	}
	
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
	}

	

}
