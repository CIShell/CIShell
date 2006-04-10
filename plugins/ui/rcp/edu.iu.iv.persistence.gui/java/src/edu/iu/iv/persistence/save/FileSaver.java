/*
 * Created on Aug 19, 2004
 */
package edu.iu.iv.persistence.save;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.Logger;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.persistence.BasicFileResourceDescriptor;
import edu.iu.iv.core.persistence.FileResourceDescriptor;
import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.core.persistence.PersisterProperty;
import edu.iu.iv.datamodelview.DataModelTreeView;

/**
 * @author Team IVC
 */
public class FileSaver {
    private static File currentDir;

    private Shell parent;

    public FileSaver(Shell parent){
        this.parent = parent;
    }       

    private boolean confirmFileOverwrite(File file) {
        String message = "The file:\n" + file.getPath()
            + "\nalready exists. Are you sure you want to overwrite it?";
        return IVC.showConfirm("Confirm File Overwrite", message, "");        
    }

    private boolean isSaveFileValid(File file) {
        boolean valid = false;
        if (file.isDirectory()) {
            String message = "Destination cannot be a directory. Please choose a file";
            IVC.showError("Invalid Destination", message, "");
            valid = false;
        } else if (file.exists()) {
            valid = confirmFileOverwrite(file);
        }
        else
            valid = true ;
        return valid;
    }

    public boolean save(Persister persister, DataModel model) {
        Logger logger = IVC.getInstance().getErrorLogger() ;

        PropertyMap map = persister.getProperties();
        String ext = (String) map.getPropertyValue(PersisterProperty.SUPPORTED_FILE_EXTENSION);
        
        FileDialog dialog = new FileDialog(parent, SWT.SAVE);
        
        if (currentDir == null) {
            currentDir = new File(System.getProperty("user.home"));
        }
        dialog.setFilterPath(currentDir.getPath());
        
        dialog.setFilterExtensions(new String[]{"*" + ext});
        dialog.setText("Choose File");   
        dialog.setFileName((String)model.getProperties().getPropertyValue(DataModelProperty.LABEL));

        boolean done = false;
        
        while (!done) {        
            String fileName = dialog.open();
            if (fileName != null) {
                File selectedFile = new File(fileName);
                if (!isSaveFileValid(selectedFile))
                    continue;
                if (ext != null && ext.length() != 0)
                    if (!selectedFile.getPath().endsWith(ext))
                        selectedFile = new File(selectedFile.getPath() + ext) ;
                FileResourceDescriptor frd = new BasicFileResourceDescriptor(selectedFile, false);
                try {
                    persister.persist(model.getData(), frd);
                    
                    if (selectedFile.isDirectory()) {
                        currentDir = selectedFile;
                    } else {
                        currentDir = new File(selectedFile.getParent());
                    }
                    
                    done = true ;
                    IVC.getInstance().getConsole().printAlgorithmInformation("File saved: " + selectedFile.getPath() + "\n");
                    model.getProperties().setPropertyValue(DataModelProperty.MODIFIED, new Boolean(false));
                    DataModelTreeView.getDefault().refresh();                    
                } catch (IOException e) {
                    logger.warning("Could not save file!\n" + e.getMessage()) ;
                    String message = "There was an I/O error while saving the file. " +
                    	"The file was not saved correctly.";
                    IVC.showError("Error!", message, "");
                    done = true ;
                    return false;                    
                } catch (PersistenceException e) {
                    logger.warning("Could not save file!\n"+e.getMessage()) ;
                    String message = "There was an error saving the file. " +
                    "The file was not saved correctly.";
                    IVC.showError("Error!", message, "");
                    done = true ;
                    return false;
                }
            } else {
                IVC.getInstance().getConsole().printAlgorithmInformation("File save cancelled.\n");
                done = true;
                return false;
            }            
        }
        return true;
    }
}

