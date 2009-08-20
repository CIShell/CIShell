package org.cishell.templates.wizards.staticexecutable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class OutputDataItemEditor extends Dialog {
	private Text fileNameText;
	private Text labelText;
	private Text dataTypeText;
	private Text mimeTypeText;
	private OutputDataItem outputDataItem;
    
    public OutputDataItemEditor(Composite parent,
    							OutputDataItem outputDataItem) {
        this(parent.getShell(), outputDataItem);
    }
    
    private OutputDataItemEditor(Shell parentShell, OutputDataItem outputDataItem) {
        super(parentShell);
        
        this.outputDataItem = outputDataItem;
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        panel.setLayout(gridLayout);
        
        this.fileNameText = createTextInput(panel, "File Name");
        this.fileNameText.setText(this.outputDataItem.getFileName());
        
        this.labelText = createTextInput(panel, "Label");
        this.labelText.setText(this.outputDataItem.getLabel());
        
        this.dataTypeText = createTextInput(panel, "Data Type");
        this.dataTypeText.setText(this.outputDataItem.getDataType());
        
        this.mimeTypeText = createTextInput(panel, "Mime Type");
        this.mimeTypeText.setText(this.outputDataItem.getMimeType());
        
        composite.getShell().setText("Output Data Item Editor");
        
        return composite;
    }
    
    private Text createTextInput(Composite panel, String text) {
        Label label = new Label(panel, SWT.NONE);
        label.setText(text);
        GridData data = new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
        label.setLayoutData(data);
        
        Text input = new Text(panel, SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        input.setLayoutData(data);
        
        return input;
    }
    
    protected void okPressed() {
    	this.outputDataItem.setFileName(
    		cleanText(this.fileNameText.getText()));
    	this.outputDataItem.setLabel(cleanText(this.labelText.getText()));
    	this.outputDataItem.setDataType(
    		cleanText(this.dataTypeText.getText()));
        this.outputDataItem.setMimeType(
        	cleanText(this.mimeTypeText.getText()));
        
        super.okPressed();
    }
    
    private String cleanText(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        
        return text;
    }
}
