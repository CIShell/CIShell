/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 26, 2005 at Indiana University.
 */
package edu.iu.iv.core.util.staticexecutable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.UnsupportedModelException;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;
import edu.iu.iv.core.persistence.BasicFileResourceDescriptor;
import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.PersistenceRegistry;
import edu.iu.iv.core.persistence.Persister;

/**
 * Drops to the command line to execute a native program
 * @author Team NWB
 */
public class StaticExecutableRunner {
    private String    arguments;
    private File[]    extraFiles;
    private String    tempDirectory;
    private Map       fileToInfoMap;
    
    //To set the hierarchy in the data model viewer
    private DataModel parentDataModel;
    private String    parentFilename;

    /**
     * Initialize class level variables
     *
     */
    public StaticExecutableRunner() {
        tempDirectory   = makeTempDirectory();
        fileToInfoMap   = new HashMap();
        parentDataModel = null;
        parentFilename  = null;
    }


    /**
     * Execute the plugin by copying the application into a temporary directory
     * and then executing it
     * @param pluginID ID of the plugin to execute
     */
    public void execute(String pluginID) {
    	//Get the plugin
        ResourceBundle properties = getResourceBundle(pluginID);
     
        if (properties == null) {
            throw new Error("No plugin.properties file exists for " + pluginID);
        }
        
        String pluginPath = IVC.getInstance().getPluginPath(pluginID);
        String subdirectory = properties.getString(pluginID + ".subdirectory");
        String basefiles = properties.getString(pluginID + ".basefiles");
        
        basefiles = pluginPath + subdirectory + File.separator + basefiles;
        
        String baseexecutable = properties.getString(pluginID + ".baseexecutable");
        
        //Get the system's environment
        String os = Platform.getOS();
        String arch = Platform.getOSArch();
        
        String platform = os + "." + arch;
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            platform = os;
            baseexecutable += ".exe";
        }
        
        //Create the directory to execute
        String executableDir = properties.getString(pluginID + "." + platform);
        executableDir = pluginPath + subdirectory + File.separator + executableDir;
        
        File tempDir = new File(tempDirectory);
        copy(new File(basefiles), tempDir);
        copy(new File(executableDir), tempDir);
        
        if (extraFiles != null) {
            for (int i=0; i < extraFiles.length; i++) {
                copy(extraFiles[i], tempDir);
            }
        }
        
        //FIXME: Surely java has a way to do this!!!!
        if (new File("/bin/chmod").exists()) {
            try {
                Runtime.getRuntime().exec("/bin/chmod +x " + tempDirectory + File.separator + baseexecutable).waitFor();
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        //execute the program getting all of the new files
        File[] outputs = execute(tempDirectory, baseexecutable);
        
        if (outputs.length > 0) {
        	//New files were created, get persisters for them
        	ArrayList arrayList = new ArrayList();
            //OutputtedFilesDialog.showGUI(outputs);
        	
            String pluginName = properties.getString(pluginID + ".name");
            
        	for (int i = 0; i < outputs.length; i++) {
            	DataModel dataModel = null;
        		if (fileToInfoMap.containsKey(outputs[i].getName())) {
        			//there was a specialized persister registered for this file
        			FileInfo fileInfo = (FileInfo)fileToInfoMap.get(outputs[i].getName());
        			
        			Persister p = fileInfo.getPersister();
        		    try {
						dataModel = new BasicDataModel(p.restore(new BasicFileResourceDescriptor(outputs[i])));
						dataModel.getProperties().setPropertyValue(DataModelProperty.LABEL, 
								                                   fileInfo.getTitle() + 
								                                   " (" +
								                                   pluginName +
								                                   ")");
						dataModel.getProperties().setPropertyValue(DataModelProperty.TYPE,
								                                   fileInfo.getType());
						
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (PersistenceException e) {
						e.printStackTrace();
					}	
        		} else {
        			//Use the general file persister for the file
        			PersistenceRegistry perReg = IVC.getInstance()
							.getPersistenceRegistry();
					try {
						dataModel = 
							new BasicDataModel(perReg.load(new BasicFileResourceDescriptor(outputs[i])));
						dataModel.getProperties().setPropertyValue(DataModelProperty.LABEL,
								outputs[i].getName() + " (" + pluginName + ")");
						dataModel.getProperties().setPropertyValue(DataModelProperty.TYPE, DataModelType.OTHER);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (PersistenceException e) {
						e.printStackTrace();
					}
				}
        		
        		if (outputs[i].getName().equals(this.parentFilename) && dataModel != null) {
        			//There exists a parent data model for all output of this plugin
        			this.parentDataModel = dataModel;
        			try {
						IVC.getInstance().addModel(dataModel);
					} catch (UnsupportedModelException e) {
						e.printStackTrace();
					}
        		}
        		else {
        			//else just add the model to the list
        			arrayList.add(dataModel);
        		}
        		
        	} //end for

        	//Add the datamodels to IVC
        	for (int i = 0; i < arrayList.size(); ++i) {
        		DataModel dm = (DataModel)arrayList.get(i);
        		if (this.parentDataModel != null) {
        			dm.getProperties().setPropertyValue(DataModelProperty.PARENT,
        					                            this.parentDataModel);
        		}
        		try {
					IVC.getInstance().addModel(dm);
				} catch (UnsupportedModelException e) {
					e.printStackTrace();
				}
        	}
        	
        } else {
            IVC.getInstance().getConsole().print("No output detected.");
        }
    }
    
    /**
     * Get the temporary directory this class is running in
     * @return The temporary file
     */
    public File getTempDirectory() {
        return new File(tempDirectory);
    }
    
    /**
     * Drops to the command line and executes the program
     * @param basedirectory The directory where the necessary files to execute reside
     * @param executable The name of the program to execute
     * @return List of generated files
     */
    private File[] execute(String basedirectory, String executable) {        
        String execFile = basedirectory + File.separator + executable;
        
        File dir = new File(basedirectory);
        String[] beforeFiles = dir.list();
        
        if (arguments == null) arguments = "";
        
        try {
        	//Drops to console to execute
            Runtime.getRuntime().exec(execFile + " " + arguments, null, new File(basedirectory)).waitFor();
            
            IVC.getInstance().getConsole().print("Execution of " + executable + " complete.\n");
        } catch (IOException e) {
            IVC.showError("Error Executing!",e.getMessage(),null);
        } catch (InterruptedException e) {
            IVC.showError("Error Executing!",e.getMessage(),null);;
        }
        
        //get the outputted files
        String[] afterFiles = dir.list();
        
        Arrays.sort(beforeFiles);
        Arrays.sort(afterFiles);
        
        List outputs = new ArrayList();
        String tempDir = tempDirectory + File.separator;
        
        int beforeIndex = 0;
        int afterIndex = 0;
        
        while (beforeIndex < beforeFiles.length && afterIndex < afterFiles.length) {
            if (beforeFiles[beforeIndex].equals(afterFiles[afterIndex])) {
                beforeIndex++;
                afterIndex++;
            } else {
                outputs.add(new File(tempDir + afterFiles[afterIndex]));
                afterIndex++;
            }
        }  
        
        //get any remaining new files
        while (afterIndex < afterFiles.length) {
            outputs.add(new File(tempDir + afterFiles[afterIndex]));
            afterIndex++;
        }
        
        return (File[]) outputs.toArray(new File[]{});
    }
    
    /**
     * Copies files whether they are directories or regular files to the destination
     * directory
     * @param from File to copy from
     * @param to File to copy to
     */
    private void copy(File from, File to) {
        if (from.isDirectory()) {            
            File[] files = from.listFiles();
            
            for (int i=0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    File newTo = new File(to.getPath() + File.separator + files[i].getName());
                    newTo.mkdirs();
                    
                    copy(files[i], newTo);
                } else {
                    copy(files[i],to);
                }
            }
        } else {
            try {
                to = new File(to.getPath() + File.separator + from.getName());
                to.createNewFile();
                
                FileChannel src = new FileInputStream(from).getChannel();
                FileChannel dest = new FileOutputStream(to).getChannel();
                
                dest.transferFrom(src,0,src.size());
                
                dest.close();
                src.close();
            } catch (FileNotFoundException e) {
                errorLog(e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                errorLog(e.toString());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Create a temporary directory for the executable runner
     * @return The name of the temporary directory
     */
    private String makeTempDirectory() {
        String temp = IVC.getInstance().getTemporaryFilesFolder();
        File dir = null;
        try {
            dir = File.createTempFile("StaticExecutableRunner", ".tmp",new File(temp));
        } catch (IOException e) {
            throw new Error(e);
        }

        dir.delete();
        dir.mkdir();
        
        return dir.toString();
    }
    
    /**
     * Prints an error message to IVC
     * @param msg The error message
     */
    protected void errorLog(String msg) {
        IVC.getInstance().getErrorLogger().error(msg);
    }
    
    /**
     * Retrieve the properties of the plugin (file location, architecture, etc)
     * @param pluginID The name of the plugin
     * @return Properties of the plugin
     */
    private ResourceBundle getResourceBundle(String pluginID) {
        String pluginFSPath = IVC.getInstance().getPluginPath(pluginID);
        
        try {
            FileInputStream in = new FileInputStream(pluginFSPath + "plugin.properties");
            return new PropertyResourceBundle(in);
        } catch (FileNotFoundException e1) {
            errorLog(e1.toString());
        } catch (IOException e) {
            errorLog(e.toString());
        }
        
        return null;
    }
    
    /**
     * Sets any arguments to pass through the command line
     * @param args The argument to pass
     */
    public void setArguments(String args) {
        this.arguments = args;
    }
    
    /**
     * Get the current arguments
     * @return The arguments
     */
    public String getArguments() {
        return arguments;
    }
    
    /**
     * Set the extra files needed
     * @param files An array of files
     */
    public void setExtraFilesNeeded(File[] files) {
        extraFiles = files;
    }
    
    /**
     * Retrieve the extra files needed by the plugin
     * @return Array of files
     */
    public File[] getExtraFilesNeeded() {
        return extraFiles;
    }
    
    /**
     * Set the datamodel's filename that will be the parent of all the
     * output produced.  The filename will be a product of the algorithm
     * executed.
     * @param filename Filename of parent
     */
    public void setParentFilename(String filename) {
    	this.parentFilename = filename;
    }
    
    /**
     * Parent datamodel for all of the output produced.
     * @param dm Parent datamodel
     */
    public void setParentDataModel(DataModel dm) {
    	this.parentDataModel = dm;
    }
    
    /**
     * Register a file with a persister.
     * 
     * @param filename Filename of the persister
     * @param title Title of datamodel
     * @param type Type of data (network, text file, etc)
     * @param persister The persister of the file
     */
    public void registerFile(String filename, String title, 
    		                DataModelType type, Persister persister) {
    	
    	FileInfo fi = new FileInfo();
    	fi.setInfo(filename, title, type, persister);

    	fileToInfoMap.put(filename, fi);
    }
    
    /**
     * Holds the fileinfo for any registered files
     * 
     * @author Team IVC
     */
    private class FileInfo {
    	private String                 filename;
    	private String                 title;
    	private DataModelType          type;
    	private Persister              persister;
    	
    	/**
    	 * Populate the file data
    	 * @param filename Name of the file
    	 * @param title Title of the datamodel
    	 * @param type Type of datamodel contained in the file
    	 * @param persister Persister handling the file
    	 */
    	public void setInfo(String filename, String title,
    			            DataModelType type, Persister persister) {
    		this.filename  = filename;
    		this.title     = title;
    		this.type      = type;
    		this.persister = persister;
    	}
    	
    	/**
    	 * The filename
    	 * @return filename
    	 */
    	public String getFilename() {
    		return this.filename;
    	}
    	    	
    	/**
    	 * Title of the datamodel produced
    	 * @return Title
    	 */
    	public String getTitle() {
    		return this.title;
    	}
    	
    	/**
    	 * Type of datamodel produced
    	 * @return Datamodel type
    	 */
    	public DataModelType getType() {
    		return this.type;
    	}
    	
    	/**
    	 * The persister of the file
    	 * @return Persister
    	 */
    	public Persister getPersister() {
    		return this.persister;
    	}
    }
}
