package org.cishell.templates.staticexecutable.optiontypes;

import org.eclipse.pde.ui.templates.BaseOptionTemplateSection;

public class PlatformOption extends CustomStringOption {
	private String platformName;
	private String platformPath;
	
	public PlatformOption(BaseOptionTemplateSection section,
						  String name,
						  String label,
						  String platformName,
						  String platformPath) {
		super(section, name, label);
		
		this.platformName = platformName;
		this.platformPath = platformPath;
	}
	
	public String getPlatformName() {
		return this.platformName;
	}
	
	public String getPlatformPath() {
		return this.platformPath;
	}
}