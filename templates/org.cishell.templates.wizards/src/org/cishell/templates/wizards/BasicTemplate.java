/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 8, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.wizards;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;

public abstract class BasicTemplate extends OptionTemplateSection {
    protected final String sectionID;
    protected Map valueMap;
    protected Map optionMap;

    protected BasicTemplate(String sectionID) {
        this.sectionID = sectionID;
        this.valueMap = new HashMap();
        this.optionMap = new HashMap();
    }

    /**
     * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getSectionId()
     */
    public String getSectionId() {
        return sectionID;
    }

    /**
     * @see org.eclipse.pde.ui.templates.BaseOptionTemplateSection#validateOptions(org.eclipse.pde.ui.templates.TemplateOption)
     */
    public abstract void validateOptions(TemplateOption changed);

    /**
     * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#updateModel(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected abstract void updateModel(IProgressMonitor monitor) throws CoreException;
        
    protected void registerOption(TemplateOption option, Object value, int pageIndex) {
        optionMap.put(option.getName(), option);
        
        super.registerOption(option, value, pageIndex);
    }

    protected TemplateOption getOption(String name) {
        return (TemplateOption) optionMap.get(name);
    }
    
    /**
     * Set a value for the key. this will be used in variable
     * substitution in generated files.
     * 
     * @param key
     * @param value
     */
    protected void setValue(String key, Object value) {
        valueMap.put(key, value);
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getReplacementString(java.lang.String, java.lang.String)
     */
    public String getReplacementString(String fileName, String key) {
        String replacement = null;
        
        if (getValue(key) != null) {
            replacement = getValue(key).toString();
        } else {
            replacement = super.getReplacementString(fileName, key);
        }
        
        return replacement;
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.IVariableProvider#getValue(java.lang.String)
     */
    public Object getValue(String key) {
        Object value = null;
        
        if (super.getValue(key) != null) {
            value = super.getValue(key);
        } else {
            value = valueMap.get(key);
        } 
        
        return value;
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#getPluginResourceBundle()
     */
    protected ResourceBundle getPluginResourceBundle() {
        return Activator.getDefault().getResourceBundle();
    }

    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getNewFiles()
     */
    public String[] getNewFiles() {
        return new String[] {"OSGI-INF/"};
    }

    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getUsedExtensionPoint()
     */
    public String getUsedExtensionPoint() {
        return null;
    }
    
    protected String getTemplateDirectory() {
        return "templates_3.0";
    }
    
    public IPluginReference[] getDependencies(String schemaVersion) {
        return new IPluginReference[]{};
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getInstallURL()
     */
    protected URL getInstallURL() {
        return Activator.getDefault().getBundle().getEntry("/");
    }
}
