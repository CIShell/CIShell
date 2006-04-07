/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.io.File;

import javax.swing.JFileChooser;

import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.DIRECTORY
 * 
 * @author Bruce Herr
 */
public class DirectorySwingGUIComponent extends FileSwingGUIComponent {

    /**
     * @param parameter
     * @param builder
     */
    public DirectorySwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
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
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a Directory");
        chooser.setCurrentDirectory(new File(defaultPath));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = chooser.showOpenDialog(group);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().toString();
        }
        
        return defaultPath;
    }
      
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        //make sure file is a directory
        return value instanceof File && value != null && ((File) value).isDirectory();
    }
}
