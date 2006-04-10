/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 27, 2005 at Indiana University.
 */
package edu.iu.iv.templates.executable;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;

import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.gui.builder.builder.ParameterMapConverter;
import edu.iu.iv.templates.pages.ExecutableInformationPage;
import edu.iu.iv.templates.pages.ExecutableInformationPageValues;
import edu.iu.iv.templates.pages.GeneratedSourceCodePage;
import edu.iu.iv.templates.pages.GeneratedSourceCodePageValues;
import edu.iu.iv.templates.pages.MenuSetupPage;
import edu.iu.iv.templates.pages.MenuSetupPageValues;
import edu.iu.iv.templates.pages.MetaDataPage;
import edu.iu.iv.templates.pages.MetaDataPageValues;
import edu.iu.iv.templates.pages.ParameterMapPage;
import edu.iu.iv.templates.pages.ParameterMapPageValues;
import edu.iu.iv.templates.wizard.GUIBuilderBasedTemplateSection;

/**
 * 
 * @author Bruce Herr
 */
public class ExecutableTemplate extends GUIBuilderBasedTemplateSection 
        implements GeneratedSourceCodePageValues, MenuSetupPageValues, MetaDataPageValues, ExecutableInformationPageValues, ParameterMapPageValues {
    public static final String SECTION_ID = "executable";
    public static final String USED_EXTENSION_POINT = "org.eclipse.ui.actionSets";
    private static final String PAGE_TITLE = "IVC Executable-Based Algorithm Plug-in";
    private ParameterMapPage parameterMapPage;
    
    public ExecutableTemplate() {
        this(SECTION_ID);
    }
    
    public ExecutableTemplate(String sectionID) {
        setSectionID(sectionID);
        setUsedExtensionPoint(USED_EXTENSION_POINT);
        
        createPages();
    }
    
    protected void createPages() {
        parameterMapPage = new ParameterMapPage(PAGE_TITLE);
        
        addPage(new GeneratedSourceCodePage(), PAGE_TITLE, CODE_PAGE_DESCRIPTION);
        addPage(new MenuSetupPage(), PAGE_TITLE, MENU_PAGE_DESCRIPTION);
        addPage(new MetaDataPage(), PAGE_TITLE, METADATA_PAGE_DESCRIPTION);
        addPage(parameterMapPage, PAGE_TITLE, PARAMETER_MAP_PAGE_DESCRIPTION);
        addPage(new ExecutableInformationPage(), PAGE_TITLE, EXECUTABLE_INFORMATION_PAGE_DESCRIPTION);
    }
        
    /*
     * Initializes field data based on previous input in wizard pages
     */
    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        String id = data.getId();   
        setParameterValue(KEY_PACKAGE_NAME, id);
        setParameterValue(KEY_ACTION_LABEL, data.getName());
        setParameterValue(KEY_TOOLTIP, "Select to launch " + data.getName());                           
    }
    
    /*
     * Initializes field data based on previous input in wizard pages
     */
    public void initializeFields(IPluginModelBase model) {
        // In the new extension wizard, the model exists so 
        // we can initialize directly from it
        String pluginId = model.getPluginBase().getId();
        
        setParameterValue(KEY_PACKAGE_NAME, pluginId);
        setParameterValue(KEY_ACTION_LABEL, model.getPluginBase().getName());
        setParameterValue(KEY_TOOLTIP, "Select to launch " + model.getPluginBase().getName());
    }
    
    /**
     * @see edu.iu.iv.templates.wizard.GUIBuilderBasedTemplateSection#updateModel(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        IPluginModelFactory factory = model.getPluginFactory();

        addDependency("edu.iu.iv.core");
        
        //add action set extension for menubar item
        IPluginExtension extension = createExtension("org.eclipse.ui.actionSets", true);
        IPluginElement setElement = factory.createElement(extension);
        setElement.setName("actionSet");
        setElement.setAttribute("id", plugin.getId() + ".actionSet");
        setElement.setAttribute("label", plugin.getId() + ".actionSet");
        setElement.setAttribute("visible", "true"); 

        //create the menu item (action in the actionset) for the plugin
        String fullClassName = getFullPluginClassName(); 
        IPluginElement actionElement = factory.createElement(setElement);
        actionElement.setName("action");
        actionElement.setAttribute("id", fullClassName); 
        actionElement.setAttribute("label", getStringValue(KEY_ACTION_LABEL));
        String path = getStringValue(KEY_MENUBAR_PATH);    
        if(path != null)
            actionElement.setAttribute("menubarPath", path);
        actionElement.setAttribute("tooltip", getStringValue(KEY_TOOLTIP));
        actionElement.setAttribute("class", fullClassName);
        setElement.add(actionElement);
        extension.add(setElement);
        if (!extension.isInTheModel())
            plugin.add(extension);
        
        //add startup extension
        IPluginExtension startupExtension = createExtension("org.eclipse.ui.startup", true);        
        IPluginElement startupElement = factory.createElement(startupExtension);
        startupElement.setName("startup");
        startupElement.setAttribute("class", getFullPluginClassName());
        startupExtension.add(startupElement);               
        if (!startupExtension.isInTheModel())
            plugin.add(startupExtension);
    }
    

    
    public IPluginReference[] getDependencies(String schemaVersion) {
        return new IPluginReference[]{};
    }
    
    protected void addDependency(String dependency) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        IPluginModelFactory factory = model.getPluginFactory();
        
        //add dependency of edu.iu.iv.core
        IPluginImport iimport = factory.createImport();
        iimport.setId(dependency);
        if(!iimport.isInTheModel()) 
            plugin.add(iimport);
    }
    
    public String[] getNewFiles() {
        return new String[] {"plugin.properties", getStringValue(KEY_SUBDIRECTORY) + "/"};
    }
    
    protected void generateFiles(IProgressMonitor monitor) throws CoreException {
        int[] selections = (int[]) getValue(KEY_SUPPORTED_PLATFORMS);
        String platformDirectories = "";
        String id = model.getPluginBase().getId();
        
        String projectFolder = project.getLocation().toOSString();
        String basefolder = getStringValue(KEY_SUBDIRECTORY);
        String dest = projectFolder + File.separator + basefolder;
        String defaultsFolder = dest + File.separator + getStringValue(KEY_BASE_FILES);
        
        new File(dest).mkdirs();
        new File(defaultsFolder).mkdirs();
        
        for (int i=0; i < selections.length; i++) {
            String platform = SUPPORTED_PLATFORMS[selections[i]];
            
            String platDest = dest + File.separator + platform;
            new File(platDest).mkdirs();
            
            setValue(platform,platform);
            
            platformDirectories += id + "." + platform + " = "    
                                + getStringValue(platform) + "\n";
        }
        
        setValue(KEY_PLATFORM_SPECIFIC_DIRECTORIES,platformDirectories);
        generateParameterMapKeys(parameterMapPage.getParameterMap());
        
        super.generateFiles(monitor);
        
        project.refreshLocal(IResource.DEPTH_INFINITE,monitor);
    }
    
    private void generateParameterMapKeys(ParameterMap pmap) {
        ParameterMapConverter converter = ParameterMapConverter.getInstance();
        
        String guiCode = "";
        
        if (pmap.size() > 0) {
            guiCode = "//Auto-generated gui code. May require customization.\n\t\t";
            guiCode +=  converter.convertToJavaCode(pmap,"\t\t","parameterMap");
        }
        
        String imports = converter.getImportsNeeded(pmap,"");
        
        Object otherImports = getValue(KEY_EXTRA_IMPORTS);
        if (otherImports != null) {
            imports += otherImports;
        }
        
        setValue(KEY_GUI_CODE, guiCode);
        setValue(KEY_EXTRA_IMPORTS, imports);
    }
    
    /*
     * returns the fully qualified class name of the plugin class created to
     * implement AbstractPlugin (implementing IWorkbenchWindowActionDelegate
     * for the action in the actionset)
     */
    private String getFullPluginClassName(){
        return getStringValue(KEY_PACKAGE_NAME) + "." + getStringValue(KEY_PLUGIN_CLASS_NAME);
    }
}
