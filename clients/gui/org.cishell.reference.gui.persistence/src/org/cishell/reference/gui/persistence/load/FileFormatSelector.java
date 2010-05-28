/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Jan 24, 2005 at Indiana University.
 */
package org.cishell.reference.gui.persistence.load;

import org.cishell.framework.algorithm.AlgorithmFactory;
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

public class FileFormatSelector extends AbstractDialog {
	private BundleContext bundleContext;
	private AlgorithmFactory validator;
	private ServiceReference[] validatorReferences;
	private List validatorList;

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
			Shell parent,
			BundleContext bundleContext,
			ServiceReference[] validatorReferences) {
		super(parent, title, AbstractDialog.QUESTION);
		this.bundleContext = bundleContext;
		this.validatorReferences = validatorReferences;

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

	public AlgorithmFactory getValidator() {
		return this.validator;
	}

	private Composite initializeGUI(Composite parent) {        
		Composite content = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;        
		content.setLayout(layout);        

		Group validatorGroup = new Group(content, SWT.NONE);
		// Shall this label be moved out of the code?
		validatorGroup.setText("Load as...");
		validatorGroup.setLayout(new FillLayout());        
		GridData validatorListGridData = new GridData(GridData.FILL_BOTH);
		validatorListGridData.widthHint = 200;
		validatorGroup.setLayoutData(validatorListGridData);

		this.validatorList = new List(validatorGroup, SWT.H_SCROLL |SWT.V_SCROLL | SWT.SINGLE);
		// initPersisterArray();
		initializePersisterList();
		this.validatorList.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent mouseEvent) {
				List list = (List)mouseEvent.getSource();
				int selection = list.getSelectionIndex();

				if (selection != -1) {
					selectionMade(selection);
				}
			}
		});

		this.validatorList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				List list = (List)selectionEvent.getSource();
				int selection = list.getSelectionIndex();

				if (selection != -1) {                    
					// updateDetailPane(validatorReferences[selection]);
				}                
			}                      
		});
		
		validatorList.setSelection(0);

		return content;
	}

	private void initializePersisterList() {        
		for (int ii = 0; ii < this.validatorReferences.length; ++ii) {
			String name = (String)this.validatorReferences[ii].getProperty("label");

			/*
			 * If someone was sloppy enough to not provide a name, then use the name of the
			 *  class instead.
			 */
			if (name == null || name.length() == 0) {
				name = this.validatorReferences[ii].getClass().getName();
			}

			this.validatorList.add(name);
		}
	}

	private void selectionMade(int selectedIndex) {
		this.validator =
			(AlgorithmFactory)this.bundleContext.getService(this.validatorReferences[selectedIndex]);
		close(true);
//		AlgorithmFactory validator =
//			(AlgorithmFactory)this.bundleContext.getService(this.persisterArray[selectedIndex]);
//		Data[] data = null;
//		boolean loadSuccess = false;
//
//		try {
//			data =
//				new Data[] { new BasicData(this.selectedFile.getPath(), String.class.getName()) };
//			data = validator.createAlgorithm(data, null, this.ciShellContext).execute();
//			loadSuccess = true;
//		} catch (Throwable exception) {
//			this.logger.log(
//				LogService.LOG_ERROR, "Error occurred while executing selection", exception);
//			exception.printStackTrace();
//			loadSuccess = false;
//		}
//
//		if ((data != null) && loadSuccess) {
//			this.logger.log(LogService.LOG_INFO, "Loaded: " + this.selectedFile.getPath());
//
//			for (int ii = 0; ii < data.length; ii++) {
//				this.returnList.add(data[ii]);
//			}
//
//			close(true); 
//		} else {
//			this.logger.log(LogService.LOG_ERROR, "Unable to load with selected loader");
//		}
	}

	public void createDialogButtons(Composite parent) {
		Button select = new Button(parent, SWT.PUSH);
		select.setText("Select");
		select.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent selectionEvent) {
				int index = FileFormatSelector.this.validatorList.getSelectionIndex();

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
