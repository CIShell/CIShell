/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 7, 2005 at Indiana University.
 */
package edu.iu.iv.persistence.load;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.Logger;
import edu.iu.iv.core.UnsupportedModelException;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;
import edu.iu.iv.core.persistence.BasicFileResourceDescriptor;
import edu.iu.iv.core.persistence.FileResourceDescriptor;
import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.core.persistence.PersisterNotFoundException;
import edu.iu.iv.core.persistence.PersisterProperty;
import edu.iu.iv.core.plugin.AbstractPlugin;
import edu.iu.iv.datamodelview.DataModelTreeView;

/**
 *
 * @author Team IVC (James Ellis)
 */
public class LoadPlugin extends AbstractPlugin {
    
    private static final String DESCRIPTION = "Load file";
    private static final String UNSUPPORTED_REASON = "Loads a file so doesn't do anything with models.";
    private static File currentDir;

    private Logger logger = IVC.getInstance().getErrorLogger() ;
    
    public String getDescription() {
        return DESCRIPTION;
    }

    public void launch(DataModel model) {        
        FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
        
        if (currentDir == null) {
            currentDir = new File(System.getProperty("user.dir") + File.separator + "sampledata" + File.separator + "anything");
        }
        dialog.setFilterPath(currentDir.getPath());
        dialog.setText("Select a File");
        String fileName = dialog.open();

        //nothing was selected
        if (fileName == null) {
            return;
        }

        File file = new File(fileName);
        
        if (file.isDirectory()) {
            currentDir = file;
        } else {
            currentDir = new File(file.getPath());
        }
        
		try {

			// set the properties for the resource descriptor.
			// note that this relies on the fact that the compression is set
			// to nocompression by default.
			FileResourceDescriptor frd = new BasicFileResourceDescriptor(file);

			// get all the persisters that can load this type of file.
			List spl = IVC.getInstance().getPersistenceRegistry().getSupportingPersisters(frd);

			// no persisters found means
			// the file format is not supported
			if (spl.isEmpty()) {
				throw new PersisterNotFoundException("No supporting persisters found!");
			}

			//<filename>[.<data model type>][.<index>]
			// only one persister found, so load the model
			if (spl.size() == 1) {
			    Persister persister = (Persister)spl.iterator().next();
				Object data = persister.restore(frd);
				
                String modelLabel = frd.getFileName();
                if (frd.getFileExtension() != null)
                    modelLabel = modelLabel.substring(0, modelLabel.indexOf(frd
                            .getFileExtension()));
                String rmn = (String) persister.getProperties()
                        .getPropertyValue(
                                PersisterProperty.RESTORABLE_MODEL_NAME);
                if (rmn != null)
                    modelLabel += "." + rmn;
                DataModelType type = (DataModelType)persister.getProperties().getPropertyValue(PersisterProperty.RESTORABLE_MODEL_TYPE);
                
                DataModel dataModel = new BasicDataModel(data);
                dataModel.getProperties().setPropertyValue(DataModelProperty.LABEL, modelLabel);
                dataModel.getProperties().setPropertyValue(DataModelProperty.TYPE, type);
                
                IVC.getInstance().addModel(dataModel);
                //set the new model to be unmodified since it was loaded from a file
                dataModel.getProperties().setPropertyValue(DataModelProperty.MODIFIED, new Boolean(false));
				DataModelTreeView.getDefault().refresh();
                
                IVC.getInstance().getConsole().printAlgorithmInformation("Loaded: " + frd.getFilePath() + "\n");
                IVC.getInstance().getUserLogger().info("Data model label: " + modelLabel + "\n");				
				return;
			}

			// lots of persisters found, return the chooser
			new LoadPersisterChooser("Load", frd, window.getShell()).open();
		}
		 catch (PersisterNotFoundException e) {
		     IVC.showError("Error!", e.getMessage(), "");
	         IVC.getInstance().getErrorLogger().error(e.getMessage());
	         return;
		 }
		 catch (IOException e) {
		     String message = "There was and I/O error while loading this file. Please try again. If this message persists, the file may be corrupted.";
		     IVC.showError("Error!", message, "");
		     logger.error(e.getMessage());
		     return;
		}
		 catch (PersistenceException e) {
		     String message = "There was a Persistence Exception while loading this file. The file may be corrupt or invalid";
		     IVC.showError("Error!", message, "");
		     logger.error(e.getMessage());
		     return;
		}
		catch (UnsupportedModelException e) {
		    String message = "No plugins in the IVC seem to recognize this model, You might need to update the IVC.";
		    IVC.showError("Error!", message, "");
            logger.error("Unsupported model!") ;
		    return;
		}
		catch (Exception e) {
		    String message = "Unable to load file, may be corrupt or invalid";
		    IVC.showError("Error!", message, e.toString());
			logger.error(e.toString()) ;
			return;
		}
    }

    public boolean supports(DataModel model) {
        return true;
    }

    public String unsupportedReason(DataModel model) {
        return UNSUPPORTED_REASON;
    }

}
