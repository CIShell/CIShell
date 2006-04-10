/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 28, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.parameter.Validator;
import edu.iu.iv.gui.builder.guicomponent.BooleanSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.ColorSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.DirectorySwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.DoubleSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.FileSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.FloatSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.IntegerSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.MultiChoiceListSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.PasswordSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.SingleChoiceListSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.TextSwtGUIComponent;
import edu.iu.iv.gui.builder.guicomponent.UnsupportedSwtGUIComponent;

/**
 * A Class that Builds SWT Composites given correctly filled Parameters. This can
 * be used for Preference pages, Full GUIs, and anywhere you would use a composite
 * SWT Component.
 * 
 * @author Bruce Herr
 */
public class SwtCompositeBuilder implements ParameterProperty {
    private Composite parent;
    private Composite userArea;
    private ScrolledComposite userScroll;
//    private Composite userScroll;
    private Set componentSet;
    private Set listeners;

    /**
     * Create a composite with initially zero Parameters and using a default
     * style (SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE).
     * 
     * @param parent the parent of the composite.
     */
    public SwtCompositeBuilder(Composite parent) {
        this(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
    }
    

    /**
     * Create a composite with initially zero Parameters.
     * 
     * @param parent the parent of the composite.
     * @param style the swt style to use on the composite
     */
    public SwtCompositeBuilder(Composite parent, int style) {
        this.parent = parent;
        this.componentSet = new HashSet();
        this.listeners = new HashSet();
        
        userScroll = new ScrolledComposite(parent, style);
        userScroll.setLayout(new GridLayout(1, true));
        userScroll.setExpandHorizontal(true);
        userScroll.setExpandVertical(true);
        userScroll.setAlwaysShowScrollBars(false);
        
        userArea = new Composite(userScroll, SWT.NONE);
        
//        userArea = new Composite(parent, SWT.NONE);
        
        userArea.setLayout(new GridLayout(4,false));
        
        GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
        userArea.setLayoutData(gd);
        
//        userScroll = userArea;
        
        GridData userData = new GridData();
        userData.grabExcessVerticalSpace = true;
        userData.grabExcessHorizontalSpace = true;
        userData.verticalAlignment = SWT.FILL;
        userData.horizontalAlignment = SWT.FILL;
        
        userScroll.setLayoutData(userData);
        userScroll.setContent(userArea);        
    }
    
    /**
     * Create a composite gui with initial Parameters from the ParameterMap and
     * using a default swt style.
     * 
     * @param parent the parent of the composite
     * @param parameters the parameters to use
     */
    public SwtCompositeBuilder(Composite parent, ParameterMap parameters) {
        this(parent);
        
        addAllToGUI(parameters);        
    }

    /**
     * Create a composite gui with initial Parameters from the ParameterMap
     * 
     * @param parent the parent of the composite
     * @param style the swt style to use on the composite
     * @param parameters the parameters to use
     */
    public SwtCompositeBuilder(Composite parent, int style, ParameterMap parameters) {
        this(parent, style);
        
        addAllToGUI(parameters);
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
        //String name = (String) param.getPropertyValue(NAME);
        //String desc = (String) param.getPropertyValue(DESCRIPTION);
        InputType type = (InputType) param.getPropertyValue(INPUT_TYPE);
        //Object value = param.getPropertyValue(DEFAULT_VALUE);
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
            componentSet.add(new TextSwtGUIComponent(parameter, this));
        } else if (type == InputType.BOOLEAN) {
            componentSet.add(new BooleanSwtGUIComponent(parameter, this));
        } else if (type == InputType.INTEGER) {
            componentSet.add(new IntegerSwtGUIComponent(parameter, this));
        } else if (type == InputType.DOUBLE) {
            componentSet.add(new DoubleSwtGUIComponent(parameter, this));
        } else if (type == InputType.FLOAT) {
            componentSet.add(new FloatSwtGUIComponent(parameter, this));
        } else if (type == InputType.FILE) {
            componentSet.add(new FileSwtGUIComponent(parameter, this));
        } else if (type == InputType.DIRECTORY) {
            componentSet.add(new DirectorySwtGUIComponent(parameter, this));
        } else if (type == InputType.SINGLE_CHOICE_LIST) {
            componentSet.add(new SingleChoiceListSwtGUIComponent(parameter, this));
        } else if (type == InputType.MULTI_CHOICE_LIST) {
            componentSet.add(new MultiChoiceListSwtGUIComponent(parameter, this));
        } else if (type == InputType.COLOR) {
            componentSet.add(new ColorSwtGUIComponent(parameter, this));
        } else if (type == InputType.PASSWORD) {
            componentSet.add(new PasswordSwtGUIComponent(parameter, this));
        } else {
            componentSet.add(new UnsupportedSwtGUIComponent(parameter, this));
        }
        
        //update the scrolling size
        userScroll.setMinSize(userArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        userScroll.setSize(userArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
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
    public boolean isValid() {
        Iterator components = componentSet.iterator();
        
        boolean isValid = true;
        while (components.hasNext()) {
            SwtGUIComponent component = (SwtGUIComponent) components.next();
            if (!component.isValid()) {
                isValid = false;
            }
        }

        return isValid;
    }
    
    /**
     * Get this GUI's associated shell.
     * @return the shell
     */
    public Shell getShell() {
        return parent.getShell();
    }

    /**
     * Return the created composite GUI.
     * @return the composite
     */
    public Composite getUserArea() {        
        return userArea;
    }
    
    public Composite getComposite() {
        return userScroll;
//        return userArea;
    }
}
