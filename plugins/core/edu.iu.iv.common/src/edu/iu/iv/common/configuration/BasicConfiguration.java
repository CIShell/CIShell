/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 19, 2005 at Indiana University.
 */
package edu.iu.iv.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

/**
 * A configuration class with a backing configuration file.
 * 
 * @author Bruce Herr
 */
public class BasicConfiguration implements Configuration {
	private Preferences prefs;
	private File configFile;
	
	/**
	 * create a new config file object with the configFile being read in and parsed
	 * for any data it may have. If the file is not there, then a new config file
	 * with that name will be created with no data currently in it when it is written out.
	 * 
	 * @param configFile the config file to use. it does not have to be currently created.
	 * @param root the root node of the preferences to be used.
	 * @throws IOException if there is any IO problems while reading the file an exception will be thrown.
	 */
	public BasicConfiguration(File configFile, String root) throws IOException {
		this.configFile = configFile;
		prefs = Preferences.userRoot().node(root);
		
		try {
		    prefs.clear();
		    
			if (configFile != null && configFile.exists()) {
				Preferences.importPreferences(new FileInputStream(configFile));
			} else if (configFile != null) {
				writeConfigFile();
			}
		} catch (FileNotFoundException e) {
			throw new IOException(e.getMessage());
		} catch (InvalidPreferencesFormatException e) {
			throw new IOException(e.getMessage());
		} catch (BackingStoreException e) {
		    throw new IOException(e.getMessage());
        }	
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String property) {
		return prefs.getBoolean(property.toLowerCase(), true);
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#getDouble(java.lang.String)
	 */
	public double getDouble(String property) {
		return prefs.getDouble(property.toLowerCase(), 0);
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#getFloat(java.lang.String)
	 */
	public float getFloat(String property) {
		return prefs.getFloat(property.toLowerCase(), 0);
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#getInt(java.lang.String)
	 */
	public int getInt(String property) {
		return prefs.getInt(property.toLowerCase(), 0);
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#getLong(java.lang.String)
	 */
	public long getLong(String property) {
		return prefs.getLong(property.toLowerCase(), 0);
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#getString(java.lang.String)
	 */
	public String getString(String property) {
		return prefs.get(property.toLowerCase(), null);
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, boolean)
	 */
	public void setValue(String property, boolean value) {
		prefs.putBoolean(property.toLowerCase(), value);
        writeCfg();
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, double)
	 */
	public void setValue(String property, double value) {
		prefs.putDouble(property.toLowerCase(), value);
        writeCfg();
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, float)
	 */
	public void setValue(String property, float value) {
		prefs.putFloat(property.toLowerCase(), value);
        writeCfg();
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, int)
	 */
	public void setValue(String property, int value) {
		prefs.putInt(property.toLowerCase(), value);
        writeCfg();
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, long)
	 */
	public void setValue(String property, long value) {
		prefs.putLong(property.toLowerCase(), value);
        writeCfg();
	}
	
	/**
	 * @see edu.iu.iv.common.configuration.Configuration#setValue(java.lang.String, java.lang.String)
	 */
	public void setValue(String property, String value) {
		prefs.put(property.toLowerCase(), value);
        writeCfg();
	}
	
	//-- Non-interface methods
	
	/**
	 * Gets the backing java.util.prefs.Preferences node
	 * @return the backing java preferences node.
	 */
	public Preferences getPreferences() {
		return prefs;
	}
	
	private void writeCfg() {
		try {
            writeConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Writes out the config file
	 * @throws IOException if a file error occurs
	 */
	public void writeConfigFile() throws IOException {
		if (configFile == null) {
			return;
		}
		
		try {
			prefs.exportNode(new FileOutputStream(configFile));
			prefs.flush();
		} catch (FileNotFoundException e) {
			throw new IOException(e.getMessage());
		} catch (BackingStoreException e) {
			throw new IOException(e.getMessage());
		}
	}

    /**
     * @see edu.iu.iv.common.configuration.Configuration#contains(java.lang.String)
     */
    public boolean contains(String property) {
        return prefs.get(property, null) == null;
    }
    
    public void finalize() {
        try {
            prefs.flush();
            writeConfigFile();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
