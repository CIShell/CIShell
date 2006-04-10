/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 3, 2005 at Indiana University.
 */
package edu.iu.iv.templates.basic;


import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;

import edu.iu.iv.templates.TemplatesPlugin;


/**
 * This class creates the TemplateSection for the Basic IVC Algorithm Wizard.
 * It is responsible for actually initializing the plugin.xml file based on
 * the information input, as well as copying over any template Java files, such
 * as the AbstractPlugin implementor and the Algorithm implementor in the case
 * of the Basic Algorithm template.
 *
 * @author Team IVC (James Ellis)
 */
public class BasicTemplate extends OptionTemplateSection {
   
    //main page title
	private static final String PAGE_TITLE = "IVC Basic Algorithm Plug-in";
    
	//keys for option dictionary
	private static final String KEY_PLUGIN_CLASS_NAME = "pluginClassName"; 
	private static final String KEY_ALGORITHM_NAME = "algorithmName";
	private static final String KEY_ALGORITHM_CLASS_NAME = "algorithmClassName"; 
	private static final String KEY_MENU_ID = "menuId";
	private static final String KEY_MENU_LABEL = "menuLabel";
	private static final String KEY_GROUP_NAME = "groupName";
	private static final String KEY_ACTION_LABEL = "actionLabel";
	private static final String KEY_MENUBAR_PATH = "menubarPath";
	private static final String KEY_TOOLTIP = "tooltip";
		
	//wizard option labels
	private static final String PLUGIN_CLASS_LABEL = "Plugin Class Name:";
	private static final String ALGORITHM_NAME_LABEL = "Name of Algorithm:";
	private static final String ALGORITHM_CLASS_LABEL = "Algorithm Class Name:";
	private static final String PACKAGE_LABEL = "Package Name:";
	private static final String MENU_ID_LABEL = "New Menu ID (optional):";
	private static final String MENU_LABEL_LABEL = "New Menu Label (optional):";
	private static final String GROUP_NAME_LABEL = "New Group Name (optional):";
	private static final String ACTION_LABEL_LABEL = "Plug-in Menu Item Label:";
	private static final String MENUBAR_PATH_LABEL = "Menubar Path:\n(i.e. visualization/additions)";
	private static final String TOOLTIP_LABEL = "Menu Item Tooltip:";	
	
	//default values for wizard options
	private static final String PLUGIN_CLASS_NAME = "SamplePlugin";		
	private static final String ALGORITHM_CLASS_NAME = "SampleAlgorithm";
	private static final String ALGORITHM_NAME = "Sample";
	
	//page descriptions
	private static final String PAGE_ZERO_DESCRIPTION = "Generated Source Code Settings";
	private static final String PAGE_ONE_DESCRIPTION = "Optionally specify to add a new Menu, " +
				"and optionally a new Group to that Menu, to the IVC Menubar." +
				"This is unnecesary if you are adding to an existing menu.";
	private static final String PAGE_TWO_DESCRIPTION = "New Plug-in's Menu Item Settings.  The " +
				"Menubar Path should be seperated by '/' characters, " +
				"specifying the path to the new Menu Item in the IVC Menubar.  " +
				"This is typically the menu name, then any group name " +
				"that is being added to.";
	
	private TemplateOption menubarOption;
	private TemplateOption menuIdOption;
	private TemplateOption menuLabelOption;

	/**
	 * Creates a new BasicTemplate.
	 */
	public BasicTemplate() {
	    //3 pages
		setPageCount(3);
		//create the options on those pages
		createOptions();		
	}
	
	/**
	 * Returns the unique name of this section. This name will be used to
	 * construct the template file location in the contributing plug-in.
	 * 
	 * @return the unique name of this section, "basic", which is where the
	 * template files are located inside of the template directory.
	 */
	public String getSectionId() {
		return "basic";
	}
	/**
	 * Returns the directory where all the templates are located in the
	 * contributing plug-in.
	 * 
	 * @return "templates" for the IVC plug-in development templates
	 */
	protected String getTemplateDirectory() {
		return "templates";
	}
	
	/**
	 * Returns the number of work units that this template will consume during
	 * the execution. This number is used to calculate the total number of work
	 * units when initializing the progress indicator.
	 * 
	 * @return the number of work units that this template will consume during
	 * the execution
	 */
	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}
	
	/*
	 * Creates all of the options for the wizard pages. There are 3 pages:
	 *  1. Basic class information
	 * 		- package name
	 *      - plugin class name
	 *      - algorithm class name
	 *  2. New Menu information (optional)
	 *      - menu id
	 *      - menu label
	 *      - group name
	 *  3. New Menu Item information
	 *      - menu item label
	 *      - menubar path
	 *      - tooltip
	 */
	private void createOptions() {
	    //basic class information
		addOption(KEY_PACKAGE_NAME, PACKAGE_LABEL, null, 0);
		addOption(KEY_PLUGIN_CLASS_NAME, PLUGIN_CLASS_LABEL, PLUGIN_CLASS_NAME, 0);
		addOption(KEY_ALGORITHM_CLASS_NAME, ALGORITHM_CLASS_LABEL, ALGORITHM_CLASS_NAME, 0);
		addOption(KEY_ALGORITHM_NAME, ALGORITHM_NAME_LABEL, ALGORITHM_NAME, 0);
				
		//new menu information
		addOption(KEY_MENU_ID, MENU_ID_LABEL, "", 1);
		addOption(KEY_MENU_LABEL, MENU_LABEL_LABEL, "", 1);
		addOption(KEY_GROUP_NAME, GROUP_NAME_LABEL, "", 1);
		
		//new menu item information
		addOption(KEY_ACTION_LABEL, ACTION_LABEL_LABEL, null, 2);
		addOption(KEY_MENUBAR_PATH, MENUBAR_PATH_LABEL, null, 2);
		addOption(KEY_TOOLTIP, TOOLTIP_LABEL, null, 2);
		
		//menu stuff isnt required (page 1), save references for dynamic requirement
		TemplateOption[] unrequiredOptions = getOptions(1);
		for(int i = 0; i < unrequiredOptions.length; i++){
		    unrequiredOptions[i].setRequired(false);
		    if(unrequiredOptions[i].getName().equals(KEY_MENU_ID))
		        menuIdOption = unrequiredOptions[i];
		    if(unrequiredOptions[i].getName().equals(KEY_MENU_LABEL))
		        menuLabelOption = unrequiredOptions[i];
		}
		
		//save menubar option reference
		TemplateOption[] options = getOptions(2);
		for(int i = 0; i < options.length; i++){
		    if(options[i].getName().equals(KEY_MENUBAR_PATH)){
		        menubarOption = options[i];
		        break;
		    }		    
		}		    
		
	}

	/**
	 * Adds the pages of this Template to the Wizard.  This Template has three pages:
	 * 	1. Basic class information
	 *  2. New Menu information (optional)
	 *  3. New Menu Item information
	 */
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0);
		page.setTitle(PAGE_TITLE);
		page.setDescription(PAGE_ZERO_DESCRIPTION);
		wizard.addPage(page);
		
		page = createPage(1);
		page.setTitle(PAGE_TITLE);
		page.setDescription(PAGE_ONE_DESCRIPTION);
		wizard.addPage(page);
		
		page = createPage(2);
		page.setTitle(PAGE_TITLE);
		page.setDescription(PAGE_TWO_DESCRIPTION);
		wizard.addPage(page);
		
		markPagesAdded();
	}

	/**
	 * Determines if the given TemplateOption is valid.  This checks that if it
	 * is a required option, it is non-empty.  It is triggered after any user
	 * edits of an options value.
	 * 
	 * @param source the TemplateOption to check for validity
	 */
	public void validateOptions(TemplateOption source) {
	    //dynamically control requirement of menu id/label if one is filled out
	    if(source == menuIdOption){ 
	        if(!source.isEmpty())
	            menuLabelOption.setRequired(true);
	        else
	            menuLabelOption.setRequired(false);
	    }
	    
	    if(source == menuLabelOption){ 
	        if(!source.isEmpty())
	            menuIdOption.setRequired(true);
	        else
	            menuIdOption.setRequired(false);
	    }	        	    
	    
		if (source.isRequired() && source.isEmpty()) {
			flagMissingRequiredOption(source);
		} else {		    
			validateContainerPage(getPage(source));
		}
	}
	
	/*
	 * Returns the page number that the given TemplateOption is on
	 */
	private int getPage(TemplateOption source){
	    TemplateOption[] options = getOptions(0);
	    for (int i = 0; i < options.length; i++) {
	        if(options[i] == source)
	            return 0;
	    }
	    options = getOptions(1);
	    for (int i = 0; i < options.length; i++) {
	        if(options[i] == source)
	            return 1;
	    }
	    options = getOptions(2);
	    for (int i = 0; i < options.length; i++) {
	        if(options[i] == source)
	            return 2;
	    }
	    return -1;
	    
	}

	/*
	 * validates the given page by checking all of its contained options
	 */
	private void validateContainerPage(int page) {	    	    
		TemplateOption[] allPageOptions = getOptions(page);
		for (int i = 0; i < allPageOptions.length; i++) {
			TemplateOption nextOption = allPageOptions[i];
			if (nextOption.isRequired() && nextOption.isEmpty()) {
				flagMissingRequiredOption(nextOption);
				return;
			}
		}
		resetPageState();
	}

	/**
	 * Returns true if this template depends on values set in the parent wizard.
	 * Values in the parent wizard include plug-in id, plug-in name, plug-in
	 * class name, plug-in provider etc. If the template does depend on these
	 * values, initializeFields will be called when the page is
	 * made visible in the forward direction (going from the first page to the
	 * pages owned by this template).
	 */ 
	public boolean isDependentOnParentWizard() {
		return true;
	}

	/*
	 * Initializes field data based on previous input in wizard pages
	 */
	protected void initializeFields(IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		String id = data.getId();	
		initializeOption(KEY_PACKAGE_NAME, id);
		initializeOption(KEY_ACTION_LABEL, data.getName());
		initializeOption(KEY_TOOLTIP, "Select to launch " + data.getName());
		
		String menuLabel = getStringOption(KEY_MENU_LABEL);
		String menuId = getStringOption(KEY_MENU_ID);
		String group = getStringOption(KEY_GROUP_NAME);
				
		if(!menuLabel.equals("") && !menuId.equals("")){
		    if(!group.equals(""))
		        menubarOption.setValue(menuId + "/" + group);
		    else
		        menubarOption.setValue(menuId + "/additions");
		    resetPageState();
		}
		else{
		    menubarOption.setValue(null);
		    validateContainerPage(2);
		}			    			
	}
	
	/*
	 * Initializes field data based on previous input in wizard pages
	 */
	public void initializeFields(IPluginModelBase model) {
		// In the new extension wizard, the model exists so 
		// we can initialize directly from it
		String pluginId = model.getPluginBase().getId();
		initializeOption(KEY_PACKAGE_NAME, pluginId);
		initializeOption(KEY_ACTION_LABEL, model.getPluginBase().getName());
		initializeOption(KEY_TOOLTIP, "Select to launch " + model.getPluginBase().getName());
		
		String menuLabel = getStringOption(KEY_MENU_LABEL);
		String menuId = getStringOption(KEY_MENU_ID);
		String group = getStringOption(KEY_GROUP_NAME);
		
		if(!menuLabel.equals("") && !menuId.equals("")){
		    if(!group.equals(""))
		        menubarOption.setValue(menuId + "/" + group);
		    else
		        menubarOption.setValue(menuId + "/additions");
		    resetPageState();
		}		
	}

	/**
	 * Returns identifier of the extension point used in this section.
	 * 
	 * @return extension point id if this section contributes into an extension
	 *         point or null if not applicable.
	 */
	public String getUsedExtensionPoint() {
		return "org.eclipse.ui.actionSets";
	}

	/*
	 * initializes the plugin model based on the information input into
	 * the wizard. This does the job of creating the extension points and
	 * dependencies and initializing their settings
	 */
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginModelFactory factory = model.getPluginFactory();
		
		//add dependency of edu.iu.iv.core
		IPluginImport iimport = factory.createImport();
		iimport.setId("edu.iu.iv.core");
		if(!iimport.isInTheModel())	
		    plugin.add(iimport);
		
		//add action set extension for menubar item
		IPluginExtension extension = createExtension("org.eclipse.ui.actionSets", true);
		IPluginElement setElement = factory.createElement(extension);
		setElement.setName("actionSet");
		setElement.setAttribute("id", plugin.getId() + ".actionSet");
		setElement.setAttribute("label", plugin.getId() + ".actionSet");
		setElement.setAttribute("visible", "true"); 

		//optionally add a menu to the action set
		String menuId = getStringOption(KEY_MENU_ID);
		String menuLabel = getStringOption(KEY_MENU_LABEL);
		String groupName = getStringOption(KEY_GROUP_NAME);		
		if(!menuId.equals("") && !menuLabel.equals("")){
			IPluginElement menuElement = factory.createElement(setElement);
			menuElement.setName("menu");
			menuElement.setAttribute("label", menuLabel);
			menuElement.setAttribute("id", menuId);
			//optionally add a group to that menu
			if(!groupName.equals("")){
				IPluginElement groupElement = factory.createElement(menuElement);
				groupElement.setName("separator");
				groupElement.setAttribute("name", groupName);
				menuElement.add(groupElement);
				setElement.add(menuElement);
			}
		}

		//create the menu item (action in the actionset) for the plugin
		String fullClassName = getFullPluginClassName(); 
		IPluginElement actionElement = factory.createElement(setElement);
		actionElement.setName("action");
		actionElement.setAttribute("id", fullClassName); 
		actionElement.setAttribute("label", getStringOption(KEY_ACTION_LABEL));
		String path = getStringOption(KEY_MENUBAR_PATH);	
		if(path != null)
		    actionElement.setAttribute("menubarPath", path);
		actionElement.setAttribute("tooltip", getStringOption(KEY_TOOLTIP));
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
	
	/*
	 * returns the fully qualified class name of the plugin class created to
	 * implement AbstractPlugin (implementing IWorkbenchWindowActionDelegate
	 * for the action in the actionset)
	 */
	private String getFullPluginClassName(){
	    return getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_PLUGIN_CLASS_NAME);
	}
	
	
	/**
	 * Returns an array of tokens representing new files and folders created by
	 * this template section. The information is collected for the benefit of
	 * <code>build.properties</code> file so that the generated files and
	 * folders are included in the binary build. The tokens will be added as-is
	 * to the variable <code>bin.includes</code>. 
	 * 
	 * @return an array of strings that fully describe the files and folders
	 *         created by this template section as required by <code>
	 *         bin.includes</code> variable in <code>build.properties</code>
	 *         file.
	 */
	public String[] getNewFiles() {
		return new String[] {};
	}
	
	/**
	 * Returns the resource bundle that corresponds to
	 * the best match of plugin.properties file for the current
	 * locale.
	 * 
	 * @return resource bundle for plug-in properties file or null
	 *         if not found.
	 */
	protected ResourceBundle getPluginResourceBundle() {
		return TemplatesPlugin.getDefault().getResourceBundle();
	}

	/**
	 * Returns the install URL of the plug-in that contributes this template.
	 * 
	 * @return the install URL of the contributing plug-in
	 */
	protected URL getInstallURL() {
		return TemplatesPlugin.getDefault().getBundle().getEntry("/");
	}
	
}
