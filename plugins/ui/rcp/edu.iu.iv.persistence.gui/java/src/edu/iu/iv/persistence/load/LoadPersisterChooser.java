/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Jan 24, 2005 at Indiana University.
 */
package edu.iu.iv.persistence.load;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.common.property.Property;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.Logger;
import edu.iu.iv.core.UnsupportedModelException;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;
import edu.iu.iv.core.persistence.FileResourceDescriptor;
import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.core.persistence.PersisterProperty;
import edu.iu.iv.datamodelview.DataModelTreeView;
import edu.iu.iv.gui.IVCDialog;

/**
 *
 * @author Team IVC (James Ellis)
 */
public class LoadPersisterChooser extends IVCDialog {
    private FileResourceDescriptor descriptor;
    private Logger logger;
    private Persister[] persisterArray;
    private List persisterList;
    private StyledText detailPane;
    
    public LoadPersisterChooser(String title, FileResourceDescriptor descriptor, Shell parent){
        super(parent, title, IVCDialog.QUESTION);

        this.descriptor = descriptor;
        this.logger = IVC.getInstance().getErrorLogger();
        
        setDescription("The file you have selected can be loaded"
                + " using the following models.\n"
                + "Please select one of them.");
        setDetails("This dialog allows the user to choose among all available " +
        		"formats for loading the selected data model.  Choose any of the formats " +
        		"to continue loading the data model.");
    }    
    
    private Composite initGUI(Composite parent) {        
        Composite content = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;        
        content.setLayout(layout);        
               
        Group persisterGroup = new Group(content, SWT.NONE);
        persisterGroup.setText("Persisters");
        persisterGroup.setLayout(new FillLayout());        
        GridData persisterListGridData = new GridData(GridData.FILL_BOTH);
        persisterListGridData.widthHint = 200;
        persisterGroup.setLayoutData(persisterListGridData);
        
        persisterList = new List(persisterGroup, SWT.H_SCROLL |SWT.V_SCROLL | SWT.SINGLE);
        initPersisterArray();
        initPersisterList();
        persisterList.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                List list = (List)e.getSource();
                int selection = list.getSelectionIndex();
                if(selection != -1){                    
                    updateDetailPane(persisterArray[selection]);
                }                
            }                      
        });
        
        Group detailsGroup = new Group(content, SWT.NONE);
        detailsGroup.setText("Details");       
        detailsGroup.setLayout(new FillLayout());        
        GridData detailsGridData = new GridData(GridData.FILL_BOTH);
        detailsGridData.widthHint = 200;
        detailsGroup.setLayoutData(detailsGridData);
        
        detailPane = initDetailPane(detailsGroup);

        persisterList.setSelection(0);
        updateDetailPane(persisterArray[0]);
        
        return content;
    }
    
    private void initPersisterList(){        
        for (int i = 0; i < persisterArray.length; ++i) {
            PropertyMap map = persisterArray[i].getProperties();
            // get the name of the persister from the property map
            String name = (String) map
                    .getPropertyValue(PersisterProperty.PERSISTER_NAME);
            // if someone was sloppy enough to not provide a name, then use the
            // name of the class instead.
            if (name == null || name.length() == 0)
                name = persisterArray[i].getClass().getName();
            persisterList.add(name);
        }
    }
    
    private void initPersisterArray() {
        // get supporting persisters
        java.util.List persisterList = IVC.getInstance().getPersistenceRegistry().getSupportingPersisters(descriptor);
        
        int size = persisterList.size();
        persisterArray = new Persister[size];
        for (int i = 0; i < size; ++i)
            persisterArray[i] = (Persister) persisterList.get(i);
    }
        
    private StyledText initDetailPane(Group detailsGroup) {
        StyledText detailPane = new StyledText(detailsGroup, SWT.H_SCROLL | SWT.V_SCROLL);
        detailPane.setEditable(false);
        detailPane.getCaret().setVisible(false);
        return detailPane;
    }
    
    private void updateDetailPane(Persister persister) {
        PropertyMap map = persister.getProperties();
        Iterator iterator = map.getAllPropertiesSet().iterator();
    
        detailPane.setText("");
    
        while (iterator.hasNext()) {
            Property property = (Property) iterator.next();
            if(property.getAcceptableClass().equals(String.class)){
	            String val = (String) map.getPropertyValue(property);
	            
	            StyleRange styleRange = new StyleRange();
	        	styleRange.start = detailPane.getText().length();
	            detailPane.append(property.getName() + ":\n");                            	
	        	styleRange.length = property.getName().length() + 1;
	        	styleRange.fontStyle = SWT.BOLD;
	        	detailPane.setStyleRange(styleRange);
	        	
	            detailPane.append(val + "\n");               
            }
        }
    }    
    
    private void selectionMade(int selectedIndex) {        
        Persister persister = persisterArray[selectedIndex];
        try {
            Object model = persister.restore(descriptor);
            String modelLabel = descriptor.getFileName() ;
            if (descriptor.getFileExtension() != null)
                modelLabel = modelLabel.substring(0, modelLabel.indexOf(descriptor.getFileExtension())) ;
            String rmn = (String) persister.getProperties().getPropertyValue(PersisterProperty.RESTORABLE_MODEL_NAME) ;
            if (rmn != null)
                modelLabel += "." + rmn ;
            
            /**
             * FIXME: This piece of code doesn't have enough checks to ensure that
             * the data it gets back from the persister is correct. There should, at
             * the very least be null pointer checks.
             * Another issue is that for all of these properties that are set using
             * property maps, etc, we cannot make any subset of properties mandatory
             * at compile time. To deal with this we need:
             * - Default behavior in case the correct number and type of properties
             * aren't obtained from property maps.
             * - More meaningful error messages as well logger entries that make it
             * adequately clear that these properties are required.
             * - Documentation should specify this clearly.
             * - Also check if its possible to check for whether or not a plugin
             * has provided the required properties in property maps while installing
             * so that it can basically refuse to install a plugin if the properties
             * aren't defined correctly. Maybe these are things that can be moved
             * into the jarmanifest/plugin.xml and set by templates automatically.
             */
            DataModelType type = (DataModelType)persister.getProperties().getPropertyValue(PersisterProperty.RESTORABLE_MODEL_TYPE);
            
            DataModel dataModel = new BasicDataModel(model);
            dataModel.getProperties().setPropertyValue(DataModelProperty.LABEL, modelLabel);
            dataModel.getProperties().setPropertyValue(DataModelProperty.TYPE, type);
            
            IVC.getInstance().addModel(dataModel);
            //set the new model to be unmodified since it was loaded from a file
            dataModel.getProperties().setPropertyValue(DataModelProperty.MODIFIED, new Boolean(false));
            DataModelTreeView.getDefault().refresh();
            
            IVC.getInstance().getConsole().printAlgorithmInformation("File Loaded: " + descriptor.getFilePath() + "\n");
            IVC.getInstance().getUserLogger().info("Data model label: " + modelLabel + "\n");
            
        } catch (OutOfMemoryError e) {
            String message = "There is not enough memory to load this file. Please try unloading some models or start the JVM with more memory.";
            IVC.showError("Error!", message, "");
            logger.warning("IVC out of memory.") ;
        } catch (IOException e) {
            String message = "There was and I/O error while loading this file. Please try again. If this message persists, the file may be corrupted.";
            IVC.showError("Error!", message, "");            
            logger.warning("I/O error during file load.") ;
        } catch (PersistenceException e) {
            IVC.showError("Error!", e.getMessage(), "");
            logger.warning(e.getMessage());
        }
        catch (UnsupportedModelException ume) {
            String message = "No plugins in the IVC seem to recognize this model, You might need to update the IVC.";
            IVC.showError("Error!", message, "");
            logger.error("Unsupported model!") ;
        }
        finally {
            close(false); 
        }        
        close(true); 
    }

    public void createDialogButtons(Composite parent) {
        Button select = new Button(parent, SWT.PUSH);
        select.setText("Select");
        select.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                int index = persisterList.getSelectionIndex();
                if(index != -1){
                    selectionMade(index);
                }
            }
        });
        
        Button cancel = new Button(parent, SWT.NONE);
        cancel.setText("Cancel");
        cancel.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                close(false);
            }
        });
    }

    public Composite createContent(Composite parent) {
        return initGUI(parent);
    }
}
