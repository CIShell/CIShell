/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.DIRECTORY
 * 
 * @author Bruce Herr
 */
public class DirectorySwtGUIComponent extends FileSwtGUIComponent {

    public DirectorySwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter, builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.DIRECTORY;
    }
    
    /**
     * @see edu.iu.iv.gui.builder.guicomponent.FileSwtGUIComponent#getFile(java.lang.String)
     */
    protected String getFile(String defaultPath) {
        DirectoryDialog dialog = new DirectoryDialog(builder.getShell(), SWT.OPEN);
        dialog.setText("Select a Directory");
        dialog.setFilterPath(defaultPath);

        return dialog.open();
    }
      
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        //make sure file is a directory
        return value instanceof File && value != null && ((File) value).isDirectory();
    }
}
