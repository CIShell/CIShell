/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.TEXT
 * 
 * @author Bruce Herr
 */
public class TextSwingGUIComponent extends AbstractSwingGUIComponent {
    protected JTextField text;
    
    public TextSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter,builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.TEXT;
    }
    
    /**
     * Given the text in the textbox, return the parsed Object value depending
     * on the subclass of this class (or just the text for this class in 
     * particular)
     * 
     * @param text the text of the textbox
     * @return the parsed value
     */
    protected Object getValue(String text) {
        return text;
    }
    

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        text.setText(value.toString());
        text.setColumns(value.toString().length()+5);
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#addComponent(java.awt.Container, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Container group, String label, String description, Object defaultValue) {
        text = createTextField();
        
        group.add(new JLabel(" "));
        group.add(text);
        
        if(defaultValue != null) {
            text.setText(defaultValue.toString());
        } else {
            text.setText("");
        }
        text.setColumns(text.getColumns()+5);
        
        text.setMaximumSize(new Dimension(text.getWidth()+2000,text.getHeight()+50));
        
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent arg0) {
                update();
            }
            
            public void keyReleased(KeyEvent e) {
                update();
            }
        });
    }
    
    /**
     * @return a newly created JTextField to be used.
     */
    protected JTextField createTextField() {
        return new JTextField();
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#getValue()
     */
    protected Object getValue() {
        return getValue(text.getText());
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof String;
    }   
}
