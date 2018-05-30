package org.cishell.templates.wizards.pages;

import org.cishell.templates.staticexecutable.providers.InputDataProvider;
import org.cishell.templates.staticexecutable.providers.OutputDataProvider;
import org.cishell.templates.wizards.pagepanels.AddInputDataPanel;
import org.cishell.templates.wizards.pagepanels.AddOutputDataPanel;
import org.cishell.templates.wizards.staticexecutable.InputDataItem;
import org.cishell.templates.wizards.staticexecutable.OutputDataItem;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/*
 * This page allows users the specify input and output data.
 * Input data is handled in
 *  org.cishell.templates.wizards.pagepanels.AddInputDataPanel, and output data
 *  is handled in
 *  org.cishell.templates.wizards.pagepanels.AddOutputDataPanel.
 */
public class SpecifyInAndOutDataPage extends WizardPage
		implements InputDataProvider, OutputDataProvider {
	private AddInputDataPanel addInputDataPanel;
	private AddOutputDataPanel addOutputDataPanel;
	
	public SpecifyInAndOutDataPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(createContainerLayout());
		this.addInputDataPanel =
			createAndSetupInputDataPanel(container, parent);
		this.addOutputDataPanel =
			createAndSetupOutputDataPanel(container, parent);
		
		setControl(container);
	}
	
	private Layout createContainerLayout() {
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		
		return layout;
	}
	
	private AddInputDataPanel createAndSetupInputDataPanel(
			Composite container, final Composite parent) {
		AddInputDataPanel addInputDataPanel =
			new AddInputDataPanel(container, SWT.NONE);
		addInputDataPanel.setLayoutData(createPanelLayoutData());
		
		return addInputDataPanel;
	}
	
	private AddOutputDataPanel createAndSetupOutputDataPanel(
			Composite container, final Composite parent) {
		AddOutputDataPanel addOutputDataPanel =
			new AddOutputDataPanel(container, SWT.NONE);
		addOutputDataPanel.setLayoutData(createPanelLayoutData());
		
		return addOutputDataPanel;
	}
	
	private Object createPanelLayoutData() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		
		return data;
	}
	
	public InputDataItem[] getInputDataItems() {
		return this.addInputDataPanel.getInputDataItems();
	}
	
	public String formServicePropertiesInputDataString() {
		InputDataItem[] inputDataItems = getInputDataItems();
		
		StringBuffer inputDataStringInProgress = new StringBuffer();
		
		if (inputDataItems.length != 0) {
			inputDataStringInProgress.append(
				"file:" + inputDataItems[0].getMimeType());
			
			for (int ii = 1; ii < inputDataItems.length; ii++) {
				inputDataStringInProgress.append(
					",file:" + inputDataItems[0].getMimeType());
			}
		}
		
		String inputDataString = inputDataStringInProgress.toString();
		
		return inputDataString;
	}
	
	public OutputDataItem[] getOutputDataItems() {
		return this.addOutputDataPanel.getOutputDataItems();
	}
	
	public String formServicePropertiesOutputDataString() {
		OutputDataItem[] outputDataItems = getOutputDataItems();
		
		StringBuffer outputDataStringInProgress = new StringBuffer();
		
		if (outputDataItems.length != 0) {
			outputDataStringInProgress.append(
				"file:" + outputDataItems[0].getMimeType());
			
			for (int ii = 1; ii < outputDataItems.length; ii++) {
				outputDataStringInProgress.append(
					",file:" + outputDataItems[0].getMimeType());
			}
		}
		
		String outputDataString = outputDataStringInProgress.toString();
		
		return outputDataString;
	}
	
	public String formConfigPropertiesOutFilesString() {
		OutputDataItem[] outputDataItems = getOutputDataItems();
		
		StringBuffer outputFilesStringInProgress = new StringBuffer();
		
		for (int ii = 0; ii < outputDataItems.length; ii++) {
			String outFileBase = "outFile[" + ii + "]";
			outputFilesStringInProgress.append(
				outFileBase + "=" + outputDataItems[ii].getFileName() + "\n");
			outputFilesStringInProgress.append(
				outFileBase + ".label=" +
				outputDataItems[ii].getLabel() +
				"\n");
			outputFilesStringInProgress.append(
				outFileBase + ".type=" +
				outputDataItems[ii].getDataType() +
				"\n");
		}
		
		String outputDataString = outputFilesStringInProgress.toString();
		
		return outputDataString;
	}
}