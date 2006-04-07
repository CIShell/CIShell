/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 28, 2005 at Indiana University.
 */
package edu.iu.iv.core.algorithm;

import java.util.Iterator;

import edu.iu.iv.common.guibuilder.GUIBuilder;
import edu.iu.iv.common.guibuilder.SelectionListener;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.IVC;

/**
 * An abstract class to facilitate the creation of algorithms.
 * This class creates the parameter and property maps and also
 * provides several convenience methods to use.
 * 
 * @author Bruce Herr
 */
public abstract class AbstractAlgorithm implements Algorithm {
    protected PropertyMap propertyMap;
    protected ParameterMap parameterMap;
    
    public AbstractAlgorithm() {
        propertyMap = new PropertyMap();
        parameterMap = new ParameterMap();
    }

    /**
     * @see edu.iu.iv.core.algorithm.Algorithm#execute()
     */
    public abstract boolean execute();
    
    /**
     * @see edu.iu.iv.core.algorithm.Algorithm#getProperties()
     */
    public PropertyMap getProperties() {
        return propertyMap;
    }
    
    /**
     * @see edu.iu.iv.core.algorithm.Algorithm#getParameters()
     */
    public ParameterMap getParameters() {
        return parameterMap;
    }
    
    /**
     * Method that will log the parameters set in the algorithm
     * to the user log. ALL algorithms should log their parameters
     * regardless of whether they use the gui builder or not.
     *
     */
    protected void logParameters() {
        Iterator params = parameterMap.getAllParameters();
        String msg = "Input Parameters Used: \n";
        
        while (params.hasNext()) {
            Parameter parameter = (Parameter) params.next();
            
            String name = (String) parameter.getPropertyValue(ParameterProperty.NAME);
            Object value = parameter.getValue();
            
            if (value != null && parameter.isEnabled()) {
                msg += name + ": " + value + "\n";
            }
        }
        
        print(msg);
    }
    
    /**
     * log a message to the user log.
     * 
     * @param msg
     */
    protected void log(String msg) {
        IVC.getInstance().getUserLogger().info(msg);
    }
    
    /**
     * print a message to the console.
     * 
     * @param msg
     */
    protected void print(String msg) {
        IVC.getInstance().getConsole().printAlgorithmInformation(msg);
    }
    
    /**
     * creates a GUI, gets input from the user,
     * then schedules the algorithm to be run by the scheduler.
     * 
     * @param title the title of the GUI to be created
     * @param message a message to be displayed before the Inputs
     * @return the GUI created and opened (if there are any parameters in the parameterMap)
     */
    public GUIBuilder createGUIandRun(String title, String message) {        
        final GUIBuilder gui = GUIBuilder.createGUI(title,message, this);
        
        gui.addSelectionListener(new SelectionListener() {
            public void widgetSelected() {
                IVC.getInstance().getScheduler().schedule(AbstractAlgorithm.this);
                
                gui.close();
            }});
        
        if (parameterMap.size() > 0) {
            gui.open();
        } else {
            IVC.getInstance().getScheduler().schedule(AbstractAlgorithm.this);
        }
        
        return gui;
    }
}
