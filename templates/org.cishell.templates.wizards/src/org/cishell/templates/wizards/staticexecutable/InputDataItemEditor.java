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

/*
 * This editor provides the user with an interface to edit input data items
 *  (InputDataItem).
 */
public class InputDataItemEditor extends Dialog {
	private Text mimeTypeText;
	private InputDataItem inputDataItem;
    
    public InputDataItemEditor(Composite parent, InputDataItem inputDataItem) {
        this(parent.getShell(), inputDataItem);
    }
    
    private InputDataItemEditor(Shell parentShell,
    							InputDataItem inputDataItem) {
        super(parentShell);
        
        this.inputDataItem = inputDataItem;
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        panel.setLayout(gridLayout);
        
        this.mimeTypeText = createTextInput(panel, "Mime Type");
        this.mimeTypeText.setText(this.inputDataItem.getMimeType());
        
        composite.getShell().setText("Input Data Item Editor");
        
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
        this.inputDataItem.setMimeType(cleanText(this.mimeTypeText.getText()));
        
        super.okPressed();
    }
    
    private String cleanText(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        
        return text;
    }
}
