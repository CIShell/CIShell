/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder;

import java.util.Iterator;

import edu.iu.iv.common.guibuilder.swing.SwingGUIBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.parameter.Parameterizable;
import edu.iu.iv.common.parameter.Validator;

/**
 * A GUI Building Class. GUI's are built in this class through the use of 
 * Parameters that describe what kind of input is needed. These are added to the
 * GUI and depending on the Builder in use by the System, will create an interface
 * through which the user can input data for Algorithms and such. 
 * 
 * @author Bruce Herr
 */
public abstract class GUIBuilder implements ParameterProperty {
    /** A Builder that doesn't do anything */
    public static GUIBuilder NULL_BUILDER = new GUIBuilder() {
        public void addSelectionListener(SelectionListener listener) {}

        protected GUIBuilder createGUIBuilder(String title, String message) {
            return NULL_BUILDER;
        }

        protected void addToGUI(Parameter paramater, InputType type) { }

        public void open() { System.out.println("NULL BUILDER Opened!"); }

        public void close() { }};
        
    /** The current builder in use */
    private static GUIBuilder builder;
    
    /**
     * Sets the Current GUIBuilder to be used by the IVC
     * @param builder the builder to use.
     */
    public static void setGUIBuilder(GUIBuilder builder) {
        if (builder != null) {
            GUIBuilder.builder = builder;
        }
    }

    /**
     * Creates a blank GUIBuilder given a title and message. 
     * @param title the title to use
     * @param message the message to appear at the top of the GUI
     * @return the created GUI Builder
     */
    public static GUIBuilder createGUI(String title, String message) {
        if (builder == null) {
            builder = SwingGUIBuilder.getGUIBuilder();
        }
        
        return builder.createGUIBuilder(title, message);
    }

    /**
     * Creates a filled GUIBuilder given a title, message, and ParameterMap.
     * This method will take the Parameters given in the ParameterMap and add
     * them to the GUI.
     *  
     * @param title the title to use
     * @param message the message to appear at the top of the GUI
     * @param params the ParamaterMap with Parameters to use for the GUI
     * @return the created GUI Builder
     */
    public static GUIBuilder createGUI(String title, String message, ParameterMap params) {
        GUIBuilder gui = GUIBuilder.createGUI(title,message);
        gui.addAllToGUI(params);
        
        return gui;
    }

    /**
     * Creates a filled GUIBuilder given a title, message, and ParameterMap.
     * This method will take the Parameters given in the Algorithm's ParameterMap 
     * and add them to the GUI.
     *  
     * @param title the title to use
     * @param message the message to appear at the top of the GUI
     * @param parameterizable the class with the ParamaterMap to use for the GUI
     * @return the created GUI Builder
     */
    public static GUIBuilder createGUI(String title, String message, Parameterizable parameterizable) {
        return createGUI(title, message, parameterizable.getParameters());
    }
   
    /**
     * Creates a Blank GUI Builder given a title and message. This is an abstract
     * method that each subclass of GUIBuilder must implement to create the GUI.
     * 
     * @param title the title
     * @param message the message
     * @return a blank GUI Builder
     */
    protected abstract GUIBuilder createGUIBuilder(String title, String message);

    /**
     * Adds the given SelectionListener to the GUIBuilder's GUI.  This SelectionListener
     * is what will give behavior to the "OK button" in the resulting GUI.  Instances should
     * provide a SelectionListener for the GUIBuilder so that the "OK button" will
     * have the desired behavior.
     *
     * @param listener the SelectionListener that this GUIBuilder should use to give
     *                 its "OK button" the desired behavior.
     */
    public abstract void addSelectionListener(SelectionListener listener);
        
    /**
     * Open the GUI for the user to provide input.
     */
    public abstract void open();
    
    /**
     * Close the GUI
     */
    public abstract void close();
    
    /**
     * Given a Parameter and its associated InputType, add it to the GUI.
     * 
     * @param paramater the parameter
     * @param type its InputType
     */
    protected abstract void addToGUI(Parameter paramater, InputType type);
    
    /**
     * Add all the Parameters in the given ParameterMap to the GUI
     * 
     * @param parameterMap the ParameterMap with Parameters to be added to the GUI
     */
    public void addAllToGUI(ParameterMap parameterMap) {
        Iterator params = parameterMap.getAllParameters();
        
        while (params.hasNext()) {
            addToGUI((Parameter) params.next());
        }
    }
    
    /**
     * Add a specific Parameter to the GUI.
     * 
     * @param param the parameter
     */
    public void addToGUI(Parameter param) {
        InputType type = (InputType) param.getPropertyValue(INPUT_TYPE);
        Validator validator = (Validator) param.getPropertyValue(VALIDATOR);
        
        //if there is no input type, then it is unsupported
        //so set that property and it will be displayed on the GUI
        if (type == null) { 
            type = InputType.UNSUPPORTED;
            param.setPropertyValue(INPUT_TYPE,type);
        }
        
        //if no validator provide a null one
        if (validator == null) { 
            validator = Validator.NULL_VALIDATOR;
            param.setPropertyValue(VALIDATOR,validator);
        }
        
        addToGUI(param, type);
    }
}
