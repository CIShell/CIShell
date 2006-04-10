/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.AbstractSwtGUIComponent;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.FILE
 * 
 * @author Bruce Herr
 */
public class FileSwtGUIComponent extends AbstractSwtGUIComponent {
    protected Text text;
    protected Button browse;
    
    public FileSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter,builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.FILE;
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#addComponent(org.eclipse.swt.widgets.Composite, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Composite group, String label, String description, Object defaultValue) {
        text = new Text(group, SWT.BORDER);
        
        GridData gd = new GridData(SWT.FILL,SWT.CENTER,true,false);
        text.setLayoutData(gd);
        
        if (defaultValue != null) {
            text.setText(defaultValue.toString());
        }
        
        text.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    update();
                }
            });
        
        browse = new Button(group, SWT.PUSH);
        browse.setText("Browse");
        browse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String fileName = getFile(text.getText());

                if (fileName != null) {
                    text.setText(fileName);
                    update();
                }
            }});        
    }

    /**
     * Pop up a dialog and get the user's input on the File they want.
     * @param defaultPath the path to start at
     * @return the file they chose
     */
    protected String getFile(String defaultPath) {
        FileDialog dialog = new FileDialog(builder.getShell(), SWT.OPEN);
        dialog.setText("Select a File");
        dialog.setFilterPath(defaultPath);

        return dialog.open();
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#getValue()
     */
    protected Object getValue() {
        return new File(text.getText());
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        text.setText(value.toString());
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        //make sure its an actual file (and not a directory either)
        return value instanceof File && value != null && ((File) value).isFile() && !((File) value).isDirectory();
    }
    
    protected void setEnabled(boolean isEnabled) {
        text.setEnabled(isEnabled);
        browse.setEnabled(isEnabled);
    }
}
