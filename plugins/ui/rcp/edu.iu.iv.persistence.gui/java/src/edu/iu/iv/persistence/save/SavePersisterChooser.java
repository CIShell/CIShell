/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Jan 28, 2005 at Indiana University.
 */
package edu.iu.iv.persistence.save;

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
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.core.persistence.PersisterProperty;
import edu.iu.iv.gui.IVCDialog;


/**
 * SavePersisterChooser is a simple user interface to allow for selection
 * among several Persisters that support the selected model, in the event
 * that more than one is found.
 *
 * @author Team IVC
 */
public class SavePersisterChooser  extends IVCDialog {
    protected DataModel model;
    protected Persister[] persisterArray;
    private List persisterList;
    private StyledText detailPane;

    /**
     * Creates a new SavePersisterChooser object.
     *
     * @param title the desired Title of the SavePersisterChooser window
     * @param model the model that this SavePersisterChooser is attempting to save
     * @param window the IWorkbenchWindow that this SavePersisterChooser belongs to
     * @param persisters the Persisters that can be used to save the given model
     */
    public SavePersisterChooser(String title, DataModel model, Shell parent, Persister[] persisters) {
        super(parent, title, IVCDialog.QUESTION);               
        
        persisterArray = persisters;
        
        this.model = model;
        
        setDescription("The model you have selected can be saved" +
                " using the following formats.\n" + "Please select one of them.");
        setDetails("This dialog allows the user to choose among all available " +
        		"formats for saving the selected data model.  Choose any of the formats " +
        		"to continue saving the data model.");
    }   

    /*
     * Initialize the GUI for this SavePersisterChooser
     */
    private Composite initGUI(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        content.setLayout(layout);                

        Group persisterGroup = new Group(content, SWT.NONE);
        persisterGroup.setText("Persisters");
        persisterGroup.setLayout(new FillLayout());
        GridData persisterData = new GridData(GridData.FILL_BOTH);
        persisterData.widthHint = 200;
        persisterGroup.setLayoutData(persisterData);

        persisterList = new List(persisterGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
        initPersisterList();
        persisterList.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    List list = (List) e.getSource();
                    int selection = list.getSelectionIndex();

                    if (selection != -1) {
                        updateDetailPane(persisterArray[selection]);
                    }
                }
            });

        Group detailsGroup = new Group(content, SWT.NONE);
        detailsGroup.setText("Details");
        detailsGroup.setLayout(new FillLayout());        
        GridData detailsData = new GridData(GridData.FILL_BOTH);
        detailsData.widthHint = 200;
        detailsGroup.setLayoutData(detailsData);
        
        detailPane = initDetailPane(detailsGroup);

        //select the first item by default
        persisterList.setSelection(0);
        updateDetailPane(persisterArray[0]);
        
        return content;
    }

    /*
     * Initialize the Listbox of Persisters using the stored Persister array
     */
    private void initPersisterList() {
        for (int i = 0; i < persisterArray.length; ++i) {
            PropertyMap map = persisterArray[i].getProperties();

            // get the name of the persister from the property map
            String name = (String) map.getPropertyValue(PersisterProperty.PERSISTER_NAME);

            // if someone was sloppy enough to not provide a name, then use the
            // name of the class instead.
            if ((name == null) || (name.length() == 0)) {
                name = persisterArray[i].getClass().getName();
            }

            persisterList.add(name);
        }
    }

    /*
     * Sets up the DetailPane where the details from the Persister PropertyMaps are displayed. 
     */
    private StyledText initDetailPane(Group detailsGroup) {
        StyledText detailPane = new StyledText(detailsGroup, SWT.H_SCROLL | SWT.V_SCROLL);
        detailPane.setEditable(false);
        detailPane.getCaret().setVisible(false);

        return detailPane;
    }

    /*
     * Changes the information displayed in the DetailsPane whenever a new Persister
     * is selected.
     */
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

    /*
     * When a Persister is chosen to Persist this model, this method handles the job
     * of opening the FileSaver and saving the model.
     */
    protected void selectionMade(int selectedIndex) {
        getShell().setVisible(false);
        final Persister persister = persisterArray[selectedIndex];
        final FileSaver saver = new FileSaver(getShell());
        close(saver.save(persister, model));
    }

    public void createDialogButtons(Composite parent) {
        Button select = new Button(parent, SWT.PUSH);
        select.setText("Select");
        select.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    int index = persisterList.getSelectionIndex();

                    if (index != -1) {
                        selectionMade(index);
                    }
                }
            });

        Button cancel = new Button(parent, SWT.NONE);
        cancel.setText("Cancel");
        cancel.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    close(false);
                }
            });
    }

    public Composite createContent(Composite parent) {
        return initGUI(parent);
    }
}
