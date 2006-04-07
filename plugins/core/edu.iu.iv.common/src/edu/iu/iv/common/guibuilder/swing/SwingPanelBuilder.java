/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 23, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing;

import java.awt.Container;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.iu.iv.common.guibuilder.swing.guicomponent.BooleanSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.ColorSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.DirectorySwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.DoubleSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.FileSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.FloatSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.IntegerSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.MultiChoiceListSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.PasswordSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.SingleChoiceListSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.TextSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.guicomponent.UnsupportedSwingGUIComponent;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.parameter.Validator;

/**
 * A Class that Builds Swing Panels given correctly filled Parameters. This can
 * be used for Preference pages, Full GUIs, and anywhere you would use a JPanel
 * 
 * @author Bruce Herr
 */
public class SwingPanelBuilder extends JScrollPane implements ParameterProperty {
    private static final long serialVersionUID = 3305446092114943111L;
    protected JPanel userArea;
    protected Set componentSet;
    protected Set listeners;
    
    /**
     * Create a composite with initially zero Parameters.
     */
    public SwingPanelBuilder() {
        super(VERTICAL_SCROLLBAR_AS_NEEDED,HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        componentSet = new HashSet();
        listeners = new HashSet();
        
        userArea = new JPanel();
        userArea.setLayout(new BoxLayout(userArea, BoxLayout.Y_AXIS));
        
        setViewportView(userArea);
    }
    
    /**
     * Return the created Container.
     * @return the composite
     */
    public Container getUserArea() {
        return userArea;
    }
    
    /**
     * Add all the Parameters given in the given ParameterMap the the GUI
     * 
     * @param parameters the parameters to use.
     */
    public void addAllToGUI(ParameterMap parameters) {
        Iterator params = parameters.getAllParameters();
        
        while (params.hasNext()) {
            addToGUI((Parameter) params.next());
        }
    }
    
    /**
     * Add a single Parameter to the GUI
     * 
     * @param param the parameter
     */
    public void addToGUI(Parameter param) {
        InputType type = (InputType) param.getPropertyValue(INPUT_TYPE);
        Validator validator = (Validator) param.getPropertyValue(VALIDATOR);
        
        //ensure there is an InputType
        if (type == null) { 
            type = InputType.UNSUPPORTED;
            param.setPropertyValue(INPUT_TYPE,type);
        }
        //and a validator
        if (validator == null) { 
            validator = Validator.NULL_VALIDATOR;
            param.setPropertyValue(VALIDATOR,validator);
        }
        
        addToGUI(param, type);
    }
    
    /**
     * Add the given Parameter with associated input type to the GUI
     * 
     * @param parameter the parameter
     * @param type its InputType
     */
    protected void addToGUI(Parameter parameter, InputType type) {
        if (parameter == null) {
            return;
        }
        
        //add a new corresponding component depending on the type
        if (type == InputType.TEXT) {
            componentSet.add(new TextSwingGUIComponent(parameter, this));
        } else if (type == InputType.PASSWORD) {
            componentSet.add(new PasswordSwingGUIComponent(parameter, this));
        } else if (type == InputType.BOOLEAN) {
            componentSet.add(new BooleanSwingGUIComponent(parameter, this));
        } else if (type == InputType.INTEGER) {
            componentSet.add(new IntegerSwingGUIComponent(parameter, this));
        } else if (type == InputType.DOUBLE) {
            componentSet.add(new DoubleSwingGUIComponent(parameter, this));
        } else if (type == InputType.FLOAT) {
            componentSet.add(new FloatSwingGUIComponent(parameter, this));
        } else if (type == InputType.FILE) {
            componentSet.add(new FileSwingGUIComponent(parameter, this));
        } else if (type == InputType.DIRECTORY) {
            componentSet.add(new DirectorySwingGUIComponent(parameter, this));
        } else if (type == InputType.SINGLE_CHOICE_LIST) {
            componentSet.add(new SingleChoiceListSwingGUIComponent(parameter, this));
        } else if (type == InputType.MULTI_CHOICE_LIST) {
            componentSet.add(new MultiChoiceListSwingGUIComponent(parameter, this));
        } else if (type == InputType.COLOR) {
            componentSet.add(new ColorSwingGUIComponent(parameter, this));
        } else {
            componentSet.add(new UnsupportedSwingGUIComponent(parameter, this));
        }
        
        //update the scrolling size
    }
    
    /**
     * when this method is called, it notifies its listeners that a change has
     * occured within the GUI.
     */
    public void changeOccurred() {
        Iterator iterator = listeners.iterator();
        
        while (iterator.hasNext()) {
            ((ChangeListener) iterator.next()).changeOccured();
        }
    }
    
    /**
     * Add a change listener to the GUI to be notified when changes occur to the
     * GUI.
     * 
     * @param listener the listener
     */
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * @return if all the Component's values in the GUI are valid
     */
    public boolean getValidity() {
        Iterator components = componentSet.iterator();
        
        boolean isValid = true;
        while (components.hasNext()) {
            SwingGUIComponent component = (SwingGUIComponent) components.next();
            if (!component.isValid()) {
                isValid = false;
            }
        }

        return isValid;
    }
}
