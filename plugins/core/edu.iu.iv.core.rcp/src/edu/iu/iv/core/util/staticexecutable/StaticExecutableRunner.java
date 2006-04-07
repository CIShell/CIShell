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

import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.UnsupportedModelException;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;
import edu.iu.iv.core.persistence.BasicFileResourceDescriptor;
import edu.iu.iv.core.persistence.FileResourceDescriptor;
import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.PersistenceRegistry;
import edu.iu.iv.core.persistence.Persister;

public class StaticExecutableRunner {
    String arguments;
    File[] extraFiles;
    String tempDirectory;
    Map    fileToInfoMap;

    public StaticExecutableRunner() {
        tempDirectory = makeTempDirectory();
        fileToInfoMap = new HashMap();
    }
        
    public void execute(String pluginID) {
        ResourceBundle properties = getResourceBundle(pluginID);
     
        if (properties == null) {
            throw new Error("No plugin.properties file exists for " + pluginID);
        }
        
        String pluginPath = IVC.getInstance().getPluginPath(pluginID);
        String subdirectory = properties.getString(pluginID + ".subdirectory");
        String basefiles = properties.getString(pluginID + ".basefiles");
        
        basefiles = pluginPath + subdirectory + File.separator + basefiles;
        
        String baseexecutable = properties.getString(pluginID + ".baseexecutable");
        
        String os = Platform.getOS();
        String arch = Platform.getOSArch();
        
        String platform = os + "." + arch;
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            platform = os;
            baseexecutable += ".exe";
        }
        
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
        
        File[] outputs = execute(tempDirectory, baseexecutable);
        
        if (outputs.length > 0) {
            //OutputtedFilesDialog.showGUI(outputs);
        	for (int i = 0; i < outputs.length; i++) {
        		Object        model = null;
        		String        label = null;
        		DataModelType type  = null; 
        		
        		if (fileToInfoMap.containsKey(outputs[i].getName())) {
        			FileInfo fileInfo = (FileInfo)fileToInfoMap.get(outputs[i].getName());
        			
        			fileInfo.setFileDesc(new BasicFileResourceDescriptor(outputs[i]));

        			Persister p = fileInfo.getPersister();
        		    try {
						model = p.restore(fileInfo.getFileDesc());
						label = fileInfo.getTitle();
						type  = fileInfo.getType();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (PersistenceException e) {
						e.printStackTrace();
					}	
        		} else {
        			PersistenceRegistry perReg = IVC.getInstance()
							.getPersistenceRegistry();
					try {
						model = perReg.load(new BasicFileResourceDescriptor(
								outputs[i]));
						//label = outputs[i].toString();
						label = outputs[i].getName();
						type  = DataModelType.OTHER;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (PersistenceException e) {
						e.printStackTrace();
					}
				}
        		
        		if (model != null && label != null && type != null) {
					DataModel dataModel = new BasicDataModel(model);
					PropertyMap propMap = dataModel.getProperties();
					propMap.put(DataModelProperty.LABEL, label);
					propMap.put(DataModelProperty.TYPE, type);
					try {
						IVC.getInstance().addModel(dataModel);
					} catch (UnsupportedModelException e) {
						e.printStackTrace();
					}
				}
        	}
        } else {
            IVC.getInstance().getConsole().print("No output detected.");
        }
        
        //remove(getTempDirectory());
    }
    
/*    
    private void remove(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            
            for (int i=0; i < files.length; i++) {
                remove(files[i]);
            }
        }
        
        file.delete();
    }
 */
    
    public File getTempDirectory() {
        return new File(tempDirectory);
    }
    
    public File[] execute(String basedirectory, String executable) {        
        String execFile = basedirectory + File.separator + executable;
        
        File dir = new File(basedirectory);
        String[] beforeFiles = dir.list();
        
        if (arguments == null) arguments = "";
        
        try {
            Runtime.getRuntime().exec(execFile + " " + arguments, null, new File(basedirectory)).waitFor();
            
            IVC.getInstance().getConsole().print("Execution of " + executable + " complete.\n");
        } catch (IOException e) {
            IVC.showError("Error Executing!",e.getMessage(),null);
        } catch (InterruptedException e) {
            IVC.showError("Error Executing!",e.getMessage(),null);;
        }
        
        
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
     * 
     * @param from
     * @param to
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
    
    protected void errorLog(String msg) {
        IVC.getInstance().getErrorLogger().error(msg);
    }
    
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
    
    public void setArguments(String args) {
        this.arguments = args;
    }
    
    public String getArguments() {
        return arguments;
    }
    
    public void setExtraFilesNeeded(File[] files) {
        extraFiles = files;
    }
    
    public File[] getExtraFilesNeeded() {
        return extraFiles;
    }
    
    public void registerFile(String filename, String title, 
    		                DataModelType type, Persister persister) {
    	
    	FileInfo fi = new FileInfo();
    	fi.setInfo(filename, title, type, persister);

    	fileToInfoMap.put(filename, fi);
    }
    
    /**
     * 
     * @author Team IVC
     * 
     */
    private class FileInfo {
    	private FileResourceDescriptor fileDesc;
    	private String                 filename;
    	private String                 title;
    	private DataModelType          type;
    	private Persister              persister;
    	
    	public void setInfo(String filename, String title,
    			            DataModelType type, Persister persister) {
    		this.filename  = filename;
    		this.title     = title;
    		this.type      = type;
    		this.persister = persister;
    		this.fileDesc  = null;
    	}
    	
    	public void setFileDesc(FileResourceDescriptor frd) {
    		this.fileDesc = frd;
    	}
    	
    	public String getFilename() {
    		return this.filename;
    	}
    	
    	public FileResourceDescriptor getFileDesc() {
    		return this.fileDesc;
    	}
    	
    	public String getTitle() {
    		return this.title;
    	}
    	
    	public DataModelType getType() {
    		return this.type;
    	}
    	
    	public Persister getPersister() {
    		return this.persister;
    	}
    }
}
