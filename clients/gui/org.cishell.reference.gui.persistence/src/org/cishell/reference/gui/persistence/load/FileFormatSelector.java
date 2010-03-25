/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Jan 24, 2005 at Indiana University.
 */
package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.ArrayList;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.common.AbstractDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class FileFormatSelector extends AbstractDialog {
	private File selectedFile;
	private LogService logger;
	private ServiceReference[] persisterArray;
	private List persisterList;
//	private StyledText detailPane;
	private CIShellContext ciShellContext;
	private BundleContext bundleContext;
	private ArrayList returnList;

//	private static final String[] DETAILS_ITEM_KEY = 
//	{"format_name", "supported_file_extension", "format_description" };

	/*
	 * Other possible keys could be restorable_model_name, restorable_model_description
	 * */
	
//	private static final String[] DETAILS_ITEM_KEY_DISPLAY_VALUE = 
//	{"Format name", "Supported file extension", "Format description"};
	
	/*
	 * Other possible keys display values could be "Restorable model name",
	 *  "Restorable model description"
	 */

	public FileFormatSelector(
			String title,
			File selectedFile, 
			Shell parent,
			CIShellContext ciShellContext,
			BundleContext bundleContext, 
			ServiceReference[] persisterArray,
			ArrayList returnList) {
		super(parent, title, AbstractDialog.QUESTION);
		this.ciShellContext = ciShellContext;
		this.bundleContext = bundleContext;
		this.persisterArray = persisterArray;
		this.returnList = returnList;
		this.selectedFile = selectedFile;
		this.logger = (LogService)ciShellContext.getService(LogService.class.getName());

		// Shall this part be moved out of the code?
		setDescription(
			"The file you have selected can be loaded" +
			" using one or more of the following formats.\n" +
			"Please select the format you would like to try.");
		setDetails(
			"This dialog allows the user to choose among all available " +
			"formats for loading the selected data model.  Choose any of the formats " +
			"to continue loading the dataset.");
	}    

	private Composite initializeGUI(Composite parent) {        
		Composite content = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;        
		content.setLayout(layout);        

		Group persisterGroup = new Group(content, SWT.NONE);
		// Shall this label be moved out of the code?
		persisterGroup.setText("Load as...");
		persisterGroup.setLayout(new FillLayout());        
		GridData persisterListGridData = new GridData(GridData.FILL_BOTH);
		persisterListGridData.widthHint = 200;
		persisterGroup.setLayoutData(persisterListGridData);

		this.persisterList = new List(persisterGroup, SWT.H_SCROLL |SWT.V_SCROLL | SWT.SINGLE);
		// initPersisterArray();
		initializePersisterList();
		this.persisterList.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent mouseEvent) {
				List list = (List)mouseEvent.getSource();
				int selection = list.getSelectionIndex();

				if (selection != -1) {
					selectionMade(selection);
				}
			}
		});

		this.persisterList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				List list = (List)selectionEvent.getSource();
				int selection = list.getSelectionIndex();

				if (selection != -1) {                    
					// updateDetailPane(persisterArray[selection]);
				}                
			}                      
		});
		
		persisterList.setSelection(0);

		/* Group detailsGroup = new Group(content, SWT.NONE);
		// Shall this label be moved out of the code?    
		detailsGroup.setText("Details");       
		detailsGroup.setLayout(new FillLayout());        
		GridData detailsGridData = new GridData(GridData.FILL_BOTH);
		detailsGridData.widthHint = 200;
		detailsGroup.setLayoutData(detailsGridData);

		detailPane = initDetailPane(detailsGroup);

		persisterList.setSelection(0);
		updateDetailPane(persisterArray[0]); */

		return content;
	}

	private void initializePersisterList() {        
		for (int ii = 0; ii < this.persisterArray.length; ++ii) {
			String name = (String)this.persisterArray[ii].getProperty("label");

			/*
			 * If someone was sloppy enough to not provide a name, then use the name of the
			 *  class instead.
			 */
			if (name == null || name.length() == 0) {
				name = this.persisterArray[ii].getClass().getName();
			}

			this.persisterList.add(name);
		}
	}


	/* private StyledText initDetailPane(Group detailsGroup) {
		StyledText detailPane = new StyledText(detailsGroup, SWT.H_SCROLL | SWT.V_SCROLL);
		detailPane.setEditable(false);
		detailPane.getCaret().setVisible(false);

		return detailPane;
	}*/

	/* private void updateDetailPane(ServiceReference persister) {

		detailPane.setText("");

		for (int ii = 0; ii < DETAILS_ITEM_KEY.length; ii++){
			String val = (String)persister.getProperty(DETAILS_ITEM_KEY[ii]);

			StyleRange styleRange = new StyleRange();
			styleRange.start = detailPane.getText().length();
			detailPane.append(DETAILS_ITEM_KEY_DISPLAY_VALUE[ii] + ":\n");                            	
			styleRange.length = DETAILS_ITEM_KEY[ii].length() + 1;
			styleRange.fontStyle = SWT.BOLD;
			detailPane.setStyleRange(styleRange);

			detailPane.append(val + "\n");
		}
	} */

	private void selectionMade(int selectedIndex) {
		AlgorithmFactory persister =
			(AlgorithmFactory)this.bundleContext.getService(this.persisterArray[selectedIndex]);
		Data[] data = null;
		boolean loadSuccess = false;

		try {
			data =
				new Data[] { new BasicData(this.selectedFile.getPath(), String.class.getName()) };
			data = persister.createAlgorithm(data, null, this.ciShellContext).execute();
			loadSuccess = true;
		} catch (Throwable exception) {
			this.logger.log(
				LogService.LOG_ERROR, "Error occurred while executing selection", exception);
			exception.printStackTrace();
			loadSuccess = false;
		}

		if ((data != null) && loadSuccess) {
			this.logger.log(LogService.LOG_INFO, "Loaded: " + this.selectedFile.getPath());

			for (int ii = 0; ii < data.length; ii++) {
				Data dataItem = data[ii];
				FileLoad.relabelWithFilename(dataItem, selectedFile);
				this.returnList.add(dataItem);
			}

			close(true); 
		} else {
			this.logger.log(LogService.LOG_ERROR, "Unable to load with selected loader");
		}
	}

	public void createDialogButtons(Composite parent) {
		Button select = new Button(parent, SWT.PUSH);
		select.setText("Select");
		select.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent selectionEvent) {
				int index = FileFormatSelector.this.persisterList.getSelectionIndex();

				if (index != -1) {
					selectionMade(index);
				}
			}
		});
		select.setFocus();

		Button cancel = new Button(parent, SWT.NONE);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent selectionEvent) {
				close(false);
			}
		});
	}

	public Composite createContent(Composite parent) {
		return initializeGUI(parent);
	}
}
