/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 20, 2005 at Indiana University.
 */
package edu.iu.iv.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.iu.iv.IVCCorePlugin;
import edu.iu.iv.common.configuration.Configuration;

/**
 * 
 * @author Bruce Herr
 */
public class EclipseIVCConfiguration implements Configuration {
	private IPreferenceStore prefs;

    /**
     * 
     */
    public EclipseIVCConfiguration() {
        while (!Platform.isRunning());

		if(Platform.isRunning()){
		    prefs = IVCCorePlugin.getDefault().getPreferenceStore();
		}
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#getBoolean(java.lang.String)
     */
    public boolean getBoolean(String property) {
        return prefs.getBoolean(property);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#getDouble(java.lang.String)
     */
    public double getDouble(String property) {
        return prefs.getDouble(property);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#getFloat(java.lang.String)
     */
    public float getFloat(String property) {
        return prefs.getFloat(property);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#getInt(java.lang.String)
     */
    public int getInt(String property) {
        return prefs.getInt(property);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#getLong(java.lang.String)
     */
    public long getLong(String property) {
        return prefs.getLong(property);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#getString(java.lang.String)
     */
    public String getString(String property) {
        return prefs.getString(property);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, boolean)
     */
    public void setValue(String property, boolean value) {
        prefs.setValue(property, value);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, double)
     */
    public void setValue(String property, double value) {
        prefs.setValue(property, value);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, float)
     */
    public void setValue(String property, float value) {
        prefs.setValue(property, value);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, int)
     */
    public void setValue(String property, int value) {
        prefs.setValue(property, value);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, long)
     */
    public void setValue(String property, long value) {
        prefs.setValue(property, value);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, java.lang.String)
     */
    public void setValue(String property, String value) {
        prefs.setValue(property, value);
    }

    /**
     * @see edu.iu.iv.common.configuration.Configuration#contains(java.lang.String)
     */
    public boolean contains(String property) {
        return prefs.contains(property);
    }
}