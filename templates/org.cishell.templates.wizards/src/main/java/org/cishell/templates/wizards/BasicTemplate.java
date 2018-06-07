/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 8, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.wizards.templates.ControlStack;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;

// TODO Could we safely reduce some of the method visibilities here?
public abstract class BasicTemplate extends OptionTemplateSection {
	/*
	 * TODO: This is a hack to fix a bug where File seems to exclude empty
	 *  directories from its list of sub files on certain platforms.
	 */
	public static final String HACK_PLACEHOLDER_FILE_NAME = "!PLACEHOLDER!";
	
    protected final String sectionID;
    protected Map valueMap;
    protected Map optionMap;

    protected BasicTemplate(String sectionID) {
        this.sectionID = sectionID;
        this.valueMap = new HashMap();
        this.optionMap = new HashMap();
    }

    /**
     * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getSectionId()
     */
    public String getSectionId() {
        return sectionID;
    }

    /**
     * @see org.eclipse.pde.ui.templates.BaseOptionTemplateSection#validateOptions(org.eclipse.pde.ui.templates.TemplateOption)
     */
    public abstract void validateOptions(TemplateOption changed);

    /**
     * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#updateModel(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected abstract void updateModel(IProgressMonitor monitor) throws CoreException;
        
    protected void registerOption(TemplateOption option, Object value, int pageIndex) {
        optionMap.put(option.getName(), option);
        
        super.registerOption(option, value, pageIndex);
    }

    protected TemplateOption getOption(String name) {
        return (TemplateOption) optionMap.get(name);
    }
    
    /**
     * Set a value for the key. this will be used in variable
     * substitution in generated files.
     * 
     * @param key
     * @param value
     */
    protected void setValue(String key, Object value) {
        valueMap.put(key, value);
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getReplacementString(java.lang.String, java.lang.String)
     */
    public String getReplacementString(String fileName, String key) {
        String replacement = null;
        
        if (getValue(key) != null) {
            replacement = getValue(key).toString();
        } else {
            replacement = super.getReplacementString(fileName, key);
        }
        
        return replacement;
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.IVariableProvider#getValue(java.lang.String)
     */
    public Object getValue(String key) {
        Object value = null;
        
        if (super.getValue(key) != null) {
            value = super.getValue(key);
        } else {
            value = valueMap.get(key);
        } 
        
        return value;
    }
    
    public boolean shouldProcessFile(File file) {
    	return true;
    }
    
    public void execute(IProject project,
    					IPluginModelBase model,
    					IProgressMonitor monitor) throws CoreException {
		this.project = project;
		this.model = model;
		generateFiles(monitor);
		updateModel(monitor);
	}
    
    protected void generateFiles(IProgressMonitor progressMonitor)
    		throws CoreException {
		generateFiles(progressMonitor, getTemplateLocation());
	}
    
    protected void generateFiles(IProgressMonitor progressMonitor,
    						     URL locationURL) throws CoreException {
		progressMonitor.setTaskName(
			PDEUIMessages.AbstractTemplateSection_generating);

		if (locationURL == null) {
			return;
		}
		
		URL resolvedLocationURL;
		
		try {
			resolvedLocationURL = FileLocator.resolve(locationURL);
			resolvedLocationURL = FileLocator.toFileURL(locationURL);
		} catch (IOException e) {
			return;
		}
		
		String resolvedLocationURLProtocol = resolvedLocationURL.getProtocol();
		String resolvedLocationURLFileName = resolvedLocationURL.getFile();
		
		if ("file".equals(resolvedLocationURLProtocol)) {
			File templateDirectory = new File(resolvedLocationURLFileName);
			
			if (!templateDirectory.exists()) {
				return;
			}
			
			generateFilesFromDirectory(templateDirectory,
									   project,
									   true,
									   false,
									   true,
									   progressMonitor);
		} else if ("jar".equals(resolvedLocationURLProtocol)) {
			int exclamationIndex = resolvedLocationURLFileName.indexOf('!');
			
			if (exclamationIndex < 0) {
				return;
			}
			
			URL fileURL = null;
			
			try {
				String fileNameUpToExclamationMark =
					resolvedLocationURLFileName.substring(0, exclamationIndex);
				fileURL = new URL(fileNameUpToExclamationMark);
			} catch (MalformedURLException malformedURLException) {
				return;
			}
			
			File pluginJarFile = new File(fileURL.getFile());
			
			if (!pluginJarFile.exists()) {
				return;
			}
			
			// "/some/path/"
			String templateDirectoryName =
				resolvedLocationURLFileName.substring(exclamationIndex + 1);
			IPath templateDirectoryPath = new Path(templateDirectoryName);
			ZipFile zipFile = null;
			
			try {
				zipFile = new ZipFile(pluginJarFile);
				generateFilesFromZipFile(zipFile,
										 templateDirectoryPath,
										 project,
										 true,
										 false,
										 progressMonitor);
			} catch (ZipException zipException) {
			} catch (IOException ioException1) {
			} finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException ioException2) {
					}
				}
			}

		}
		
		progressMonitor.subTask("");
		progressMonitor.worked(1);
	}
    
    protected void generateFilesFromDirectory(
    		File sourceFile,
    		IContainer destinationContainer,
    		boolean isFirstLevel,
    		boolean isBinaryFile,
    		boolean shouldProcessAsTemplate,
    		IProgressMonitor progressMonitor) throws CoreException {
		File[] sourceSubFiles = sourceFile.listFiles();

		for (int ii = 0; ii < sourceSubFiles.length; ii++) {
			File sourceSubFile = sourceSubFiles[ii];
			
			if (sourceSubFiles[ii].getName().equals(
					HACK_PLACEHOLDER_FILE_NAME)) {
				continue;
			}
			
			boolean shouldProcessSubFileAsTemplate =
				shouldProcessAsTemplate && shouldProcessFile(sourceSubFile);
			
			if (sourceSubFile.isDirectory()) {
				IContainer subDestinationContainer = null;

				if (isFirstLevel) {
					isBinaryFile = false;
					
					if (!isOkToCreateFolder(sourceSubFile)) {
						continue;
					}

					if (sourceSubFile.getName().equals("java")) {
						IFolder sourceFolder =
							getSourceFolder(progressMonitor);
						subDestinationContainer = generateJavaSourceFolder(
							sourceFolder, progressMonitor);
					} else if (sourceSubFile.getName().equals("bin")) {
						isBinaryFile = true;
						subDestinationContainer = destinationContainer;
					}
				}
				
				if (subDestinationContainer == null) {
					if (!isOkToCreateFolder(sourceSubFile)) {
						continue;
					}
					
					String folderName;
					
					if (shouldProcessSubFileAsTemplate) {
						folderName = getProcessedString(
							sourceSubFile.getName(), sourceSubFile.getName());
					} else {
						folderName = sourceSubFile.getName();
					}
					
					subDestinationContainer =
						destinationContainer.getFolder(new Path(folderName));
				}
				
				if (subDestinationContainer instanceof IFolder &&
						!subDestinationContainer.exists()) {
					((IFolder)subDestinationContainer).create(
						true, true, progressMonitor);
				}
				
				generateFilesFromDirectory(sourceSubFile,
										   subDestinationContainer,
										   false,
										   isBinaryFile,
										   shouldProcessSubFileAsTemplate,
										   progressMonitor);
			} else {
				if (isOkToCreateFile(sourceSubFile)) {
					if (isFirstLevel) {
						isBinaryFile = false;
					}
					
					InputStream inputStream = null;
					
					try {
						inputStream = new FileInputStream(sourceSubFile);
						copyFile(sourceSubFile.getName(),
								 inputStream,
								 destinationContainer,
								 isBinaryFile,
								 shouldProcessSubFileAsTemplate,
								 progressMonitor);
					} catch (IOException ioException1) {
					} finally {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException ioException2) {
							}
						}
					}
				}
			}
		}
	}
    
    protected void generateFilesFromZipFile(
    		ZipFile zipFile,
    		IPath filePath,
    		IContainer destinationContainer,
    		boolean isFirstLevel,
    		boolean isBinary,
    		IProgressMonitor progressMonitor) throws CoreException {
		int pathLength = filePath.segmentCount();
		// Immediate children.
		// "dir/" or "dir/file.java"
		Map childZipEntries = new HashMap();

		for (Enumeration zipEntries = zipFile.entries();
				zipEntries.hasMoreElements();) {
			ZipEntry zipEntry = (ZipEntry)zipEntries.nextElement();
			IPath entryPath = new Path(zipEntry.getName());
			if (entryPath.segmentCount() <= pathLength) {
				// ancestor or current directory
				continue;
			}
			
			if (!filePath.isPrefixOf(entryPath)) {
				// Not a descendant.
				continue;
			}
			
			if (entryPath.segmentCount() == pathLength + 1) {
				childZipEntries.put(zipEntry.getName(), zipEntry);
			} else {
				String name = entryPath.uptoSegment(pathLength + 1).
					addTrailingSeparator().toString();
				
				if (!childZipEntries.containsKey(name)) {
					ZipEntry dirEntry = new ZipEntry(name);
					childZipEntries.put(name, dirEntry);
				}
			}
		}

		for (Iterator childZipEntryIterator =
					childZipEntries.values().iterator();
				childZipEntryIterator.hasNext();) {
			ZipEntry zipEnry = (ZipEntry)childZipEntryIterator.next();
			String name = new Path(zipEnry.getName()).lastSegment().toString();
			if (zipEnry.isDirectory()) {
				IContainer subDestinationContainer = null;

				if (isFirstLevel) {
					isBinary = false;
					
					if (name.equals("java")) {
						IFolder sourceFolder =
							getSourceFolder(progressMonitor);
						subDestinationContainer = generateJavaSourceFolder(
							sourceFolder, progressMonitor);
					} else if (name.equals("bin")) {
						isBinary = true;
						subDestinationContainer = destinationContainer;
					}
				}
				
				if (subDestinationContainer == null) {
					File newFolder = new File(filePath.toFile(), name);
					
					if (!isOkToCreateFolder(newFolder)) {
						continue;
					}
					
					String folderName = getProcessedString(name, name);
					subDestinationContainer =
						destinationContainer.getFolder(new Path(folderName));
				}
				
				if (subDestinationContainer instanceof IFolder &&
						!subDestinationContainer.exists()) {
					((IFolder)subDestinationContainer).create(
						true, true, progressMonitor);
				}
				
				generateFilesFromZipFile(zipFile,
							  filePath.append(name),
							  subDestinationContainer,
							  false,
							  isBinary,
							  progressMonitor);
			} else {
				if (isOkToCreateFile(new File(filePath.toFile(), name))) {
					if (isFirstLevel) {
						isBinary = false;
					}
					
					InputStream inputStream = null;
					
					try {
						inputStream = zipFile.getInputStream(zipEnry);
						copyFile(name,
								 inputStream,
								 destinationContainer,
								 isBinary,
								 true,
								 progressMonitor);
					} catch (IOException ioException1) {
					} finally {
						if (inputStream != null)
							try {
								inputStream.close();
							} catch (IOException ioException2) {
							}
					}
				}
			}
		}
	}
    
    public IFolder generateJavaSourceFolder(
    		IFolder sourceFolder,
    		IProgressMonitor monitor) throws CoreException {
		Object packageValue = getValue(KEY_PACKAGE_NAME);
		String packageName;
		
		if (packageValue != null) {
			packageName = packageValue.toString();
		} else {
			packageName = null;
		}
		
		if (packageName == null) {
			packageName = model.getPluginBase().getId();
		}
		
		IPath path = new Path(packageName.replace('.', File.separatorChar));
		
		if (sourceFolder != null) {
			path = sourceFolder.getProjectRelativePath().append(path);
		}

		for (int ii = 1; ii <= path.segmentCount(); ii++) {
			IPath subpath = path.uptoSegment(ii);
			IFolder subfolder = project.getFolder(subpath);
			
			if (subfolder.exists() == false) {
				subfolder.create(true, true, monitor);
			}
		}
		
		return project.getFolder(path);
	}
    
    private String getProcessedString(String fileName, String source) {
		if (source.indexOf('$') == -1) {
			return source;
		}
		
		int locationIndex = -1;
		StringBuffer buffer = new StringBuffer();
		boolean shouldReplace = false;
		
		for (int ii = 0; ii < source.length(); ii++) {
			char currentCharacter = source.charAt(ii);
			
			if (currentCharacter == '$') {
				if (shouldReplace) {
					String key = source.substring(locationIndex, ii);
					String value;
					
					if (key.length() == 0) {
						value = "$";
					} else {
						value = getReplacementString(fileName, key);
					}
					
					buffer.append(value);
					shouldReplace = false;
				} else {
					shouldReplace = true;
					locationIndex = ii + 1;
					
					continue;
				}
			} else if (!shouldReplace) {
				buffer.append(currentCharacter);
			}
		}
		return buffer.toString();
	}
    
    private void copyFile(
    		String fileName,
    		InputStream inputStream,
    		IContainer destinationContainer,
    		boolean isBinary,
    		boolean shouldProcessSubFileAsTemplate,
    		IProgressMonitor progressMonitor) throws CoreException {
		String targetFileName;
		
		if (shouldProcessSubFileAsTemplate) {
			targetFileName = getProcessedString(fileName, fileName);
		} else {
			targetFileName = fileName;
		}

		progressMonitor.subTask(targetFileName);
		IFile destinationFile =
			destinationContainer.getFile(new Path(targetFileName));

		try {
			InputStream processedInputStream;
			
			if (shouldProcessSubFileAsTemplate) {
				processedInputStream =
					getProcessedStream(fileName, inputStream, isBinary);
			} else {
				processedInputStream = inputStream;
			}
			
			if (destinationFile.exists()) {
				destinationFile.setContents(
					processedInputStream, true, true, progressMonitor);
			} else {
				destinationFile.create(
					processedInputStream, true, progressMonitor);
			}
			
			processedInputStream.close();

		} catch (IOException ioException) {
		}
	}
    
    private InputStream getProcessedStream(
    		String fileName,
    		InputStream inputStream,
    		boolean isBinary) throws IOException, CoreException {
		if (isBinary) {
			return inputStream;
		}

		InputStreamReader inputStreamReader =
			new InputStreamReader(inputStream);
		int bufferSize = 1024;
		char[] characterBuffer = new char[bufferSize];
		int readCharacterCount = 0;
		StringBuffer keyStringBuffer = new StringBuffer();
		StringBuffer outStringBuffer = new StringBuffer();
		StringBuffer preStringBuffer = new StringBuffer();
		boolean isOnNewLine = true;
		ControlStack preControlStack = new ControlStack();
		preControlStack.setValueProvider(this);

		boolean shouldReplace = false;
		boolean shouldPreProcess = false;
		boolean foundEscapeCharacter = false;
		while (readCharacterCount != -1) {
			readCharacterCount = inputStreamReader.read(characterBuffer);
			
			for (int ii = 0; ii < readCharacterCount; ii++) {
				char currentCharacter = characterBuffer[ii];

				if (foundEscapeCharacter) {
					StringBuffer stringBuffer;
					
					if (shouldPreProcess) {
						stringBuffer = preStringBuffer;
					} else {
						stringBuffer = outStringBuffer;
					}
					
					stringBuffer.append(currentCharacter);
					foundEscapeCharacter = false;
					
					continue;
				}

				if (isOnNewLine && currentCharacter == '%') {
					// PreProcessor line.
					shouldPreProcess = true;
					preStringBuffer.delete(0, preStringBuffer.length());
					
					continue;
				}
				
				if (shouldPreProcess) {
					if (currentCharacter == '\\') {
						foundEscapeCharacter = true;
						
						continue;
					}
					
					if (currentCharacter == '\n') {
						// Handle line.
						shouldPreProcess = false;
						isOnNewLine = true;
						String line = preStringBuffer.toString().trim();
						preControlStack.processLine(line);
						
						continue;
					}
					
					preStringBuffer.append(currentCharacter);

					continue;
				}

				if (preControlStack.getCurrentState() == false) {
					continue;
				}

				if (currentCharacter == '$') {
					if (shouldReplace) {
						shouldReplace = false;
						String key = keyStringBuffer.toString();
						String value;
						
						if (key.length() == 0) {
							value = "$";
						} else {
							value = getReplacementString(fileName, key);
						}
						
						outStringBuffer.append(value);
						keyStringBuffer.delete(0, keyStringBuffer.length());
					} else {
						shouldReplace = true;
					}
				} else {
					if (shouldReplace) {
						keyStringBuffer.append(currentCharacter);
					}
					else {
						outStringBuffer.append(currentCharacter);
						
						if (currentCharacter == '\n') {
							isOnNewLine = true;
						} else
							isOnNewLine = false;
					}
				}
			}
		}
		
		return new ByteArrayInputStream(
			outStringBuffer.toString().getBytes(project.getDefaultCharset()));
	}
    
    /**
     * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#getPluginResourceBundle()
     */
    protected ResourceBundle getPluginResourceBundle() {
        return Activator.getDefault().getResourceBundle();
    }

    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getNewFiles()
     */
    public String[] getNewFiles() {
        return new String[] {"OSGI-INF/"};
    }

    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getUsedExtensionPoint()
     */
    public String getUsedExtensionPoint() {
        return null;
    }
    
    protected String getTemplateDirectory() {
        return "templates_3.0";
    }
    
    public IPluginReference[] getDependencies(String schemaVersion) {
        return new IPluginReference[]{};
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getInstallURL()
     */
    protected URL getInstallURL() {
        return Activator.getDefault().getBundle().getEntry("/");
    }
}
