/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 20, 2005 at Indiana University.
 */
package edu.iu.iv.core.configuration;

import java.util.Iterator;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.IVC;
import edu.iu.iv.gui.builder.ChangeListener;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * An abstract implementation of ConfigurationPage that interfaces
 * with the underlying IVC backend. Please Extend this when creating
 * ConfigurationPages.
 * 
 * @author Bruce Herr
 */
public abstract class AbstractConfigurationPage extends PreferencePage 
		implements IWorkbenchPreferencePage, ConfigurationPage, ChangeListener {
    protected ParameterMap parameterMap;
    protected PropertyMap propertyMap;
    private SwtCompositeBuilder userArea;

    /**
     * 
     */
    public AbstractConfigurationPage() {
        parameterMap = new ParameterMap();
        propertyMap = new PropertyMap();
    }
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    public void performDefaults() {
        Iterator iterator = parameterMap.getAllParameters();
        
        while (iterator.hasNext()) {
            Parameter param = (Parameter) iterator.next();
            
            Object inputType = param.getPropertyValue(ParameterProperty.INPUT_TYPE);
            if (inputType == InputType.MULTI_CHOICE_LIST 
                    || inputType == InputType.SINGLE_CHOICE_LIST) {
                
                Object selection = param.getPropertyValue(ParameterProperty.DEFAULT_SELECTION);
                if (selection != null) {
                    param.setValue(selection);
                }
            } else {
                Object selection = param.getPropertyValue(ParameterProperty.DEFAULT_VALUE);
                if (selection != null) {
                    param.setValue(selection);
                }
            }
        }
        
        userArea.changeOccurred();
    }    
    
    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public abstract boolean save();
    
    public boolean performOk() {
        
        if (save()) {
            Iterator params = parameterMap.getAllParameters();
            String msg = "Configuration Parameters Saved: \n";
            
            while (params.hasNext()) {
                Parameter parameter = (Parameter) params.next();
                
                String name = (String) parameter.getPropertyValue(ParameterProperty.NAME);
                Object value = parameter.getValue();
                
                if (value != null && parameter.isEnabled()) {
                    msg += name + ": " + value + "\n";
                }
            }
            
            IVC.getInstance().getUserLogger().info(msg);
            
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * @see org.eclipse.jface.preference.IPreferencePage#isValid()
     */
    public boolean isValid() {
        if (userArea != null && !userArea.isValid()) {
            //return false;
        }
        
        return true;
    }
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        userArea = new SwtCompositeBuilder(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE, parameterMap);
        
        userArea.addChangeListener(this);
        
        return userArea.getUserArea();
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        //setPreferenceStore(IVCCorePlugin.getDefault().getPreferenceStore());
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#getErrorMessage()
     */
    public String getErrorMessage() {
        Object value = propertyMap.getPropertyValue(ConfigurationPageProperty.ERROR_MESSAGE);
        
        if (value == null) {
            return super.getErrorMessage();
        } else {
            return value.toString();
        }
    }
    
    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#getTitle()
     */
    public String getTitle() {
        Object value = propertyMap.getPropertyValue(ConfigurationPageProperty.TITLE);
        
        if (value == null) {
            return super.getTitle();
        } else {
            return value.toString();
        }
    }

    /**
     * @see edu.iu.iv.common.parameter.Parameterizable#getParameters()
     */
    public ParameterMap getParameters() {
        return parameterMap;
    }
    
    public void setParameters(ParameterMap parameterMap) {
        this.parameterMap = parameterMap;
    }

    /**
     * @see edu.iu.iv.common.property.PropertyAssignable#getProperties()
     */
    public PropertyMap getProperties() {
        return propertyMap;
    }
    
    public void changeOccured() {
        setValid(userArea.isValid());
        
        Object value = propertyMap.getPropertyValue(ConfigurationPageProperty.ERROR_MESSAGE);
        
        if (value == null) {
            //clear the error message
            setErrorMessage(null);
        } else {
            setErrorMessage(value.toString());
        }
    }
}
