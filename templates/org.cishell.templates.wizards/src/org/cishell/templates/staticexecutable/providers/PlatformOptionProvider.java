package org.cishell.templates.staticexecutable.providers;

public interface PlatformOptionProvider {
	public PlatformOption getExecutableFileOption(String platformName);
	public PlatformOption[] getExecutableFileOptions();
	public void addExecutableFileOption(PlatformOption executableFileOption);
	public PlatformOption createExecutableFileOption(
		String platformName, String platformPath);
	public String formExecutableFileOptionName(String platformName);
	
	public PlatformOption[] getRelatedFileOptions(String platformName);
	public void addRelatedFileOption(PlatformOption relatedFileOption);
	public void removeRelatedFileOption(PlatformOption relatedFileOption);
	public PlatformOption createRelatedFileOption(
		String platformName, String platformPath);
	public String formRelatedFileOptionName(String platformName);
}