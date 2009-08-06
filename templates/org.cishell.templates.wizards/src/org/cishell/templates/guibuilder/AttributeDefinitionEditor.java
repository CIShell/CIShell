package org.cishell.templates.guibuilder;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AttributeDefinitionEditor extends Dialog {
    public static final String[] TYPE_LABELS = new String[]{
        "String","Integer","Long","Short","Double","Float","Boolean", "Char", 
        "Byte", "File", "Directory"
    };
    public static final int[] TYPE_VALUES = new int[] {
        1,3,2,4,7,8,11,5,6,1,1
    };
    
    private EditableAttributeDefinition attribute;
    private Text id;
    private Text name;
    private Text description;
    private Text defaultValue;
    private Combo type;
    
    public AttributeDefinitionEditor(Composite parent, EditableAttributeDefinition attr) {
        this(parent.getShell(), attr);
    }
    
    private AttributeDefinitionEditor(Shell parentShell, EditableAttributeDefinition attr) {
        super(parentShell);

        this.attribute = attr;
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        panel.setLayout(gridLayout);
        
        id = newTextInput(panel, "Unique ID");
        id.setText(attribute.getID());
        
        name = newTextInput(panel, "Name");
        name.setText(attribute.getName());
        
        description = newTextInput(panel, "Description");
        description.setText(attribute.getDescription());
        
        defaultValue = newTextInput(panel, "Default Value");
        defaultValue.setText(attribute.getDefaultValue()[0]);
        
        type = newListInput(panel, "Input Type");
        type.setItems(TYPE_LABELS);
        
        type.addSelectionListener(new SelectionListener(){
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                int i = type.getSelectionIndex();
                if (i == 9) { //file
                    defaultValue.setText("file:");
                }
                if (i == 10) { //directory
                    defaultValue.setText("directory:");
                }
                defaultValue.setEnabled(i != 9 && i != 10);
            }});
        
        for (int i=0; i < TYPE_VALUES.length; i++) {
            if (TYPE_VALUES[i] == attribute.getType()) {
                type.select(i);
                break;
            }
        }
        
        composite.getShell().setText("Parameter Editor");
        
        return composite;
    }
    
    private Text newTextInput(Composite panel, String text) {
        Label label = new Label(panel, SWT.NONE);
        label.setText(text);
        GridData data = new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
        label.setLayoutData(data);
        
        Text input = new Text(panel, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        input.setLayoutData(data);
        
        return input;
    }
    
    private Combo newListInput(Composite panel, String text) {
        Label label = new Label(panel, SWT.NONE);
        label.setText(text);
        GridData data = new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
        label.setLayoutData(data);
        
        Combo list = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        return list;
    }
    
    protected void okPressed() {
        attribute.setID(cleanText(id.getText()));
        attribute.setName(cleanText(name.getText()));
        String desc = cleanText(description.getText());
        if (desc.length() == 0) {
            desc = " ";
        }
        attribute.setDescription(desc);
        attribute.setDefaultValue(new String[]{cleanText(defaultValue.getText())});
        attribute.setType(TYPE_VALUES[type.getSelectionIndex()]);
        
        super.okPressed();
    }
    
    private String cleanText(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        
        return text;
    }
}
