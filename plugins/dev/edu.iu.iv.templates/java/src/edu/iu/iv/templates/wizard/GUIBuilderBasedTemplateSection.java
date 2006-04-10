/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 27, 2005 at Indiana University.
 */
package edu.iu.iv.templates.wizard;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.AbstractNewPluginTemplateWizard;
import org.eclipse.pde.ui.templates.AbstractTemplateSection;

import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.Parameterizable;
import edu.iu.iv.templates.TemplatesPlugin;

/**
 * 
 * @author Bruce Herr
 */
public abstract class GUIBuilderBasedTemplateSection extends AbstractTemplateSection {
    public static final String KEY_EXTRA_IMPORTS = "extraImports";
    
    private List pages;
    private Map valueMap;
    private String usedExtensionPoint;
    private String[] newFiles;
    private String sectionID;

    /**
     * 
     */
    public GUIBuilderBasedTemplateSection() {
        pages = new ArrayList();
        valueMap = new HashMap();
        newFiles = new String[] {};
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#updateModel(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected abstract void updateModel(IProgressMonitor monitor) throws CoreException;

    /**
     * Set the section id of this template.
     * 
     * @param sectionID
     */
    protected void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }
    
    /**
     * The section id of this template. This id helps 
     * identify where files are to be copied, descriptions
     * to be used, etc...
     * 
     * @return the section id of this template
     */
    protected String getSectionID() {
        return sectionID;
    }
    
    /**
     * Set the extension point used
     * @param usedExtensionPoint
     */
    protected void setUsedExtensionPoint(String usedExtensionPoint) {
        this.usedExtensionPoint = usedExtensionPoint;
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getUsedExtensionPoint()
     */
    public String getUsedExtensionPoint() {
        return this.usedExtensionPoint;
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
        } else if (valueMap.get(key) != null) {
            value = valueMap.get(key);
        } else {
            value = getParameterValue(key);
        }
        
        return value;
    }
    
    public String getStringValue(String key) {
        Object v = getValue(key);
        
        return v == null ? null : v.toString();
    }
        
    /**
     * Get a value from the parameter maps of all the 
     * pages present. 
     * 
     * @param key the key
     * @return the value of that key, null if no value is present.
     */
    public Object getParameterValue(String key) {
        Iterator iter = pages.iterator();
        
        while (iter.hasNext()) {
            Object o = iter.next();
            
            if (o instanceof Parameterizable) {
                Parameterizable p = (Parameterizable) o;
                
                Parameter val = p.getParameters().get(key);
                
                if (val != null) return val.getValue();
            }
        }
        
        return null;
    }
    
    
    protected void setParameterValue(String key, Object value) {
        Iterator iter = pages.iterator();
        
        while (iter.hasNext()) {
            Object o = iter.next();
            
            if (o instanceof Parameterizable) {
                Parameterizable p = (Parameterizable) o;
                
                Parameter val = p.getParameters().get(key);
                
                if (val != null) {
                    val.setValue(value);
                    return;
                }
            }
        }
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getNumberOfWorkUnits()
     */
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + getPageCount();
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getPageCount()
     */
    public int getPageCount() {
        return pages.size();
    }
    
    public WizardPage addPage(ParameterMap parameters) {
        GUIBuilderBasedWizardPage page = 
            new GUIBuilderBasedWizardPage("" + getPageCount()+1);
        page.setParameters(parameters);
        
        if (getPageCount() > 0) {
            WizardPage prevPage = getPage(getPageCount()-1);
            page.setPreviousPage(prevPage);
        }
       
        pages.add(page);
        return page;
    }
    
    public WizardPage addPage(WizardPage wizardPage, String title, String description) {
        wizardPage.setTitle(title);
        wizardPage.setDescription(description);
        
        if (getPageCount() > 0) {
            WizardPage prevPage = getPage(getPageCount()-1);
            wizardPage.setPreviousPage(prevPage);
        }
        
        pages.add(wizardPage);
        
        return wizardPage;
    }
    
    
    public WizardPage addPage(ParameterMap parameters, String title, String description) {
        WizardPage page = addPage(parameters);
        page.setTitle(title);
        page.setDescription(description);
        
        return page;
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#addPages(org.eclipse.jface.wizard.Wizard)
     */
    public void addPages(Wizard wizard) {
        for (int i=0; i < getPageCount(); i++) {
            wizard.addPage(getPage(i));
        }
        
        IFieldData data = ((AbstractNewPluginTemplateWizard) wizard).getData();
        initializeFields(data);
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getPage(int)
     */
    public WizardPage getPage(int pageIndex) {
        return (WizardPage) pages.get(pageIndex);
    }
    
    protected void setNewFiles(String[] newFiles) {
        this.newFiles = newFiles;
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getNewFiles()
     */
    public String[] getNewFiles() {
        return newFiles;
    }
            
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getLabel()
     */
    public String getLabel() {
        String key = "template." + getSectionID() + ".name";
        return getPluginResourceString(key);
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.ITemplateSection#getDescription()
     */
    public String getDescription() {
        String key = "template." + getSectionID() + ".desc";
        return getPluginResourceString(key);
    }
    
    /**
     * @return the template subdirectory where the templates are located
     */
    protected String getTemplateDirectory() {
        return "templates";
    }
    
    /**
     * Returns the install URL of the plug-in that contributes this template.
     * 
     * @return the install URL of the contributing plug-in
     */
    protected URL getInstallURL() {
        return TemplatesPlugin.getDefault().getBundle().getEntry("/");
    }

    /**
     * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#getPluginResourceBundle()
     */
    protected ResourceBundle getPluginResourceBundle() {
        return TemplatesPlugin.getDefault().getResourceBundle();
    }
    
    /**
     * Implements the abstract method by looking for templates using the
     * following path:
     * <p>
     * [install location]/[templateDirectory]/[sectionId]
     * 
     * @return the URL of the location where files to be emitted by this
     *         template are located.
     */
    public URL getTemplateLocation() {
        URL url = getInstallURL();
        try {
            String location = getTemplateDirectory() + "/"
                    + getSectionID() + "/";
            return new URL(url, location);
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    protected void initializeFields(IFieldData data) { }
    public void initializeFields(IPluginModelBase model) { }
    
    /**
     * Modifies the superclass implementation by adding the initialization step
     * before commencing execution. This is important because some options may
     * not be initialized and users may choose to press 'Finish' before the
     * wizard page where the options are were shown for the first time.
     */
    public void execute(IProject project, IPluginModelBase model,
            IProgressMonitor monitor) throws CoreException {
        initializeFields(model);
        super.execute(project, model, monitor);
    }
}
