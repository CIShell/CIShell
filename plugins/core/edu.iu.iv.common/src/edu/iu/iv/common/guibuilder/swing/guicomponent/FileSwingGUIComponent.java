/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.FILE
 * 
 * @author Bruce Herr
 */
public class FileSwingGUIComponent extends AbstractSwingGUIComponent {
    protected JTextField text;

    /**
     * @param parameter
     * @param builder
     */
    public FileSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter, builder);
    }

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.FILE;
    }

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#addComponent(java.awt.Container, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Container group, String label, String description, Object defaultValue) {
        text = new JTextField();
        
        group.add(new JLabel(" "));
        group.add(text);
        
        if (defaultValue != null) {
            text.setText(defaultValue.toString());
            //text.setColumns(defaultValue.toString().length()+5);
        }
        
        
        text.setMaximumSize(new Dimension(text.getWidth()+2000,text.getHeight()+50));
        
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent arg0) {
                update();
            }
            
            public void keyReleased(KeyEvent e) {
                update();
            }
        });
        
        JButton browse = new JButton();
        group.add(browse);
        
        browse.setText("Browse");
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String fileName = getFile(text.getText());

                if (fileName != null) {
                    text.setText(fileName);
                }
            }});        
    }

    /**
     * Pop up a dialog and get the user's input on the File they want.
     * @param defaultPath the path to start at
     * @return the file they chose
     */
    protected String getFile(String defaultPath) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a File");
        chooser.setCurrentDirectory(new File(defaultPath));
        
        int returnVal = chooser.showOpenDialog(group);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().toString();
        }
        
        return defaultPath;
    }
    

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#getValue()
     */
    protected Object getValue() {
        return new File(text.getText());
    }

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        text.setText(value.toString());
    }
    
    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        //make sure its an actual file (and not a directory either)
        return value instanceof File && value != null && ((File) value).isFile();
    }
}
