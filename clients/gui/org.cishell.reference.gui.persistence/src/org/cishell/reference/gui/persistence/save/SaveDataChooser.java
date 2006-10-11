/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Jan 28, 2005 at Indiana University.
 */
package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;
import java.util.Enumeration;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import org.cishell.reference.gui.common.AbstractDialog;

/**
 * SaveDataChooser is a simple user interface to allow for selection
 * among several Persisters that support the selected model, in the event
 * that more than one is found.
 *
 * @author Team IVC
 */
public class SaveDataChooser extends AbstractDialog {
    protected Data data;
    protected Converter[] converterArray;
    private List converterList;
    private StyledText detailPane;
    //private Shell parent;
    CIShellContext context;
    public static final Image QUESTION = Display.getCurrent().getSystemImage(SWT.ICON_QUESTION);

    /**
     * Creates a new SaveChooser object.
     *
     * @param data The data object to save
     * @param parent The parent shell
     * @param converterArray The array of converters to persist the data
     * @param title Title of the Window
     * @param brandPluginID The plugin that supplies the branding
     * @param context The CIShellContext to retrieve available services
     */
    public SaveDataChooser(Data data, Shell parent, Converter[] converterArray,
    						String title, CIShellContext context) {
    	super(parent, title, QUESTION);
        this.data = data;        
        this.converterArray = converterArray;
        this.context = context;
    }   

    /**
     * Initialize the GUI for the chooser
     * @param parent The parent window
     * @return The new window containing the chooser
     */
    private Composite initGUI(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        content.setLayout(layout);
        //parent.setLayout(layout);                

        Group converterGroup = new Group(content, SWT.NONE);
        converterGroup.setText("Pick the Output Data Type");
        converterGroup.setLayout(new FillLayout());
        GridData persisterData = new GridData(GridData.FILL_BOTH);
        persisterData.widthHint = 200;
        converterGroup.setLayoutData(persisterData);

        converterList = new List(converterGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
        initConverterList();
        converterList.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    List list = (List) e.getSource();
                    int selection = list.getSelectionIndex();

                    if (selection != -1) {
                        updateDetailPane(converterArray[selection]);
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
        converterList.setSelection(0);
        updateDetailPane(converterArray[0]);
        
        return content;
    }

    /**
     * Initialize the Listbox of Persisters using the stored Persister array
     */
    private void initConverterList() {
        for (int i = 0; i < converterArray.length; ++i) {
            Dictionary dict = converterArray[i].getProperties();

            // get the name of the persister from the property map
            String outData = (String) dict.get(AlgorithmProperty.LABEL);

            // if someone was sloppy enough to not provide a name, then use the
            // name of the class instead.
            if ((outData == null) || (outData.length() == 0)) {
                outData = converterArray[i].getClass().getName();
            }

            converterList.add(outData);
        }
    }

    /**
     * Sets up the DetailPane where the details from the Persister PropertyMaps are displayed.
     * @param detailsGroup The detail pane to init
     * @return A style of the text 
     */
    private StyledText initDetailPane(Group detailsGroup) {
        StyledText detailPane = new StyledText(detailsGroup, SWT.H_SCROLL | SWT.V_SCROLL);
        detailPane.setEditable(false);
        detailPane.getCaret().setVisible(false);

        return detailPane;
    }

    /**
     * Changes the information displayed in the DetailsPane whenever a new Persister
     * is selected.
     * @param converter A converter that contains the properties for the detail pane
     */
    private void updateDetailPane(Converter converter) {
        Dictionary dict = converter.getProperties();
        Enumeration keysEnum = dict.keys();

        detailPane.setText("");

        while (keysEnum.hasMoreElements()) {
            Object key = keysEnum.nextElement();
            Object val = dict.get(key);
	
            StyleRange styleRange = new StyleRange();
            styleRange.start = detailPane.getText().length();
            detailPane.append(key + ":\n");
            styleRange.length = key.toString().length() + 1;
            styleRange.fontStyle = SWT.BOLD;
            detailPane.setStyleRange(styleRange);
	
            detailPane.append(val + "\n");
        }
    }

    /**
     * When a Persister is chosen to Persist this model, this method handles the job
     * of opening the FileSaver and saving the model.
     * @param selectedIndex The chosen converter
     */
    protected void selectionMade(int selectedIndex) {
        getShell().setVisible(false);
        final Converter converter = converterArray[selectedIndex];
        final FileSaver saver = new FileSaver(getShell(), context);
        close(saver.save(converter, data));
    }

    /**
     * Create the buttons for either cancelling or continuing with
     * the save
     * 
     * @param parent The GUI to place the buttons
     */
    public void createDialogButtons(Composite parent) {
        Button select = new Button(parent, SWT.PUSH);
        select.setText("Select");
        select.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    int index = converterList.getSelectionIndex();

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

    /**
     * Checks for the number of file savers.  If there is one
     * converter then it will save directly, otherwise intialize the chooser.
     * 
     * @param parent The parent GUI for new dialog windows.
     */
    public Composite createContent(Composite parent) {
    	if (converterArray.length == 1) {
            final FileSaver saver = new FileSaver((Shell)parent, context);
            close(saver.save(converterArray[0], data));
            return parent;
    	}
    	else {
    		return initGUI(parent);
    	}
    }
}
