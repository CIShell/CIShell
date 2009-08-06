package org.cishell.templates.wizards.pages;

import org.cishell.templates.staticexecutable.providers.InputDataProvider;
import org.cishell.templates.wizards.pagepanels.AddInputDataPanel;
import org.cishell.templates.wizards.pagepanels.AddOutputDataPanel;
import org.cishell.templates.wizards.staticexecutable.InputDataItem;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class SpecifyInAndOutDataPage extends WizardPage
		implements InputDataProvider {
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
	
	private AddInputDataPanel createAndSetupInputDataPanel(Composite container,
											  final Composite parent) {
		AddInputDataPanel addInputDataPanel =
			new AddInputDataPanel(container, SWT.BORDER);
		addInputDataPanel.setLayoutData(createPanelLayoutData());
		
		return addInputDataPanel;
	}
	
	private AddOutputDataPanel createAndSetupOutputDataPanel(Composite container,
											   final Composite parent) {
		AddOutputDataPanel addOutputDataPanel =
			new AddOutputDataPanel(container, SWT.BORDER);
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
}