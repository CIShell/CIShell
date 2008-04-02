package org.cishell.reference.prefs.admin.internal;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.cishell.reference.prefs.admin.PreferenceAD;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;

public class PreferenceADImpl implements AttributeDefinition, PreferenceAD {
		
	private static final String URI_FILE_PREFIX = "file:";
	
	private LogService log;
	
	private AttributeDefinition realAD;
	
	private int preferenceType;
	private String[] interpretedDefaultValue;
	
	private String platformIndepInstallDirPath;
	
	public PreferenceADImpl(LogService log, AttributeDefinition realAD) {
		this.log = log;
		
		this.realAD = realAD;
		
		this.preferenceType = inferPreferenceType(realAD);
		
		this.platformIndepInstallDirPath = generateIndepInstallDirPath();
		this.interpretedDefaultValue = interpretDefaultValue(this.realAD.getDefaultValue());
	}
	
	private int inferPreferenceType(AttributeDefinition realAD) {
		int preferenceType; 
		if (realAD.getType() == AttributeDefinition.STRING) {
			String defaultVal = realAD.getDefaultValue()[0];
			
			if (defaultVal.startsWith(TypePrefixes.DIRECTORY_PREFIX)) {
				preferenceType = DIRECTORY;
			} else if (defaultVal.startsWith(TypePrefixes.FILE_PREFIX)) {
				preferenceType = FILE;
			} else if (defaultVal.startsWith(TypePrefixes.FONT_PREFIX)) {
				preferenceType = FONT;
			} else if (defaultVal.startsWith(TypePrefixes.PATH_PREFIX)) {
				preferenceType = PATH;
			} else if (realAD.getOptionLabels() != null) {
				preferenceType = CHOICE;
			} else if (defaultVal.startsWith(TypePrefixes.COLOR_PREFIX)) {
				preferenceType = COLOR;
			} else {
				preferenceType = TEXT;
			}
		} else {
			preferenceType = realAD.getType();
		}
		
		return preferenceType;
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getCardinality()
	 */
	public int getCardinality() {
		return this.realAD.getCardinality();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getDefaultValue()
	 */
	public String[] getDefaultValue() {
		
		return interpretedDefaultValue;
	}
	
	private String generateIndepInstallDirPath() {
		String installDirPath = System.getProperty("osgi.install.area").replace(URI_FILE_PREFIX, "");
		File installDirFile = new File(installDirPath);
		URI platformIndependentFile = installDirFile.toURI();
		String platformIndepInstallDirPath = platformIndependentFile.toString();
		return platformIndepInstallDirPath;
	}
	
	private String[] interpretDefaultValue(String[] rawDefaultValues) {
		String[] interpretedDefaultValues = new String[rawDefaultValues.length];
		for (int i = 0; i < rawDefaultValues.length; i++) {
			String rawDefaultValue = rawDefaultValues[i];
			
			interpretedDefaultValues[i] = interpretDefaultValue(rawDefaultValue);
		}
		
		return interpretedDefaultValues;
	}
	private String interpretDefaultValue(String rawDefaultValue) {
		int preferenceType = getPreferenceType();
		
		if (preferenceType == DIRECTORY) {
			String uriFormattedDefaultValue = rawDefaultValue.replace(TypePrefixes.DIRECTORY_PREFIX, URI_FILE_PREFIX);
			return makePlatformSpecificPath(uriFormattedDefaultValue);
		} else if (preferenceType == FILE) {
			if (rawDefaultValue.equals("file:")) {
				//allows empty values
				return "";
			}
			String uriFormattedDefaultValue = rawDefaultValue; //already in URI form, semi-coincidentally 
			return makePlatformSpecificPath(uriFormattedDefaultValue);
		} else if (preferenceType == FONT) {
			return rawDefaultValue.replace(TypePrefixes.FONT_PREFIX, "");
		} else if (preferenceType == PATH) {
			return rawDefaultValue.replace(TypePrefixes.PATH_PREFIX, "");
		} else if (preferenceType == TEXT) {
			return rawDefaultValue;
		} else if (preferenceType == COLOR) {
			return rawDefaultValue.replace(TypePrefixes.COLOR_PREFIX, "");
		}else { 
			return rawDefaultValue;
		}	
	}
	
	private String makePlatformSpecificPath(String platformIndependentPath) {
		//if the original platformIndependentPath is relative, stick the home directory on to it.
		
		if (! platformIndependentPath.startsWith("file:/")) {
			//it's a relative path
			//make it absolute
			 platformIndependentPath = platformIndepInstallDirPath + platformIndependentPath.replace(URI_FILE_PREFIX, "");
		}
		//make the whole platformIndependentPath platform specific
		
		try {
			URI uriInterpretation = new URI(platformIndependentPath);
			File platformSpecificInterpretation = new File(uriInterpretation); 
			
			/*
			* may need to change to canonical path at some point, but that throws an IOException 
		    * (and may cause performance problems), so skipping for now
		     */
			String platformSpecificDirectory = platformSpecificInterpretation.getAbsolutePath(); 
			return platformSpecificDirectory;
			} catch (URISyntaxException e) {
				this.log.log(LogService.LOG_WARNING, "Invalid syntax  in preference AD " + realAD.getName());
				return System.getProperty("osgi.install.area").replace(URI_FILE_PREFIX, "");
			}	
			
		//return it
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getDescription()
	 */
	public String getDescription() {
		return this.realAD.getDescription();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getID()
	 */
	public String getID() {
		return this.realAD.getID();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getName()
	 */
	public String getName() {
		return this.realAD.getName();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getOptionLabels()
	 */
	public String[] getOptionLabels() {
		return this.realAD.getOptionLabels();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getOptionValues()
	 */
	public String[] getOptionValues() {
		return this.realAD.getOptionValues();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getType()
	 */
	public int getType() {
		return this.realAD.getType();
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#getPreferenceType()
	 */
	public int getPreferenceType() {
		return this.preferenceType;
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceAttributeDefinition#validate(java.lang.String)
	 */
	public String validate(String value) {
		return this.realAD.validate(value);
	}

}
