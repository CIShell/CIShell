/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 3, 2005 at Indiana University.
 */
package edu.iu.iv.templates.persister;


import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPlugin;
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
 * This class creates the TemplateSection for the IVC Persister Wizard.
 * It is responsible for actually initializing the plugin.xml file based on
 * the information input, as well as copying over any template Java files, such
 * as the PersisterPlugin.java file that is responsible for causing all
 * of the Persisters contained in the new Persister project to get
 * registered with IVC upon launching the application.
 *
 * @author Team IVC (James Ellis)
 */
public class PersisterTemplate extends OptionTemplateSection {
   
    //main page title
	private static final String PAGE_TITLE = "IVC Persister Plug-in";
    
	//keys for option dictionary
	private static final String KEY_PLUGIN_CLASS_NAME = "pluginClassName"; 
	
	//wizard option labels
	private static final String PLUGIN_CLASS_LABEL = "Plugin Class Name:";
	private static final String PACKAGE_LABEL = "Package Name:";

	//default values for wizard options
	private static final String PLUGIN_CLASS_NAME = "PersisterProviderPlugin";		

	//page descriptions
	private static final String PAGE_ZERO_DESCRIPTION = "Generated Source Code Settings";
	
	/**
	 * Creates a new BasicTemplate.
	 */
	public PersisterTemplate() {
	    //1 pages
		setPageCount(1);	
		createOptions();
	}
	
	private void createOptions(){
	    //basic class information
		addOption(KEY_PACKAGE_NAME, PACKAGE_LABEL, null, 0);
		addOption(KEY_PLUGIN_CLASS_NAME, PLUGIN_CLASS_LABEL, PLUGIN_CLASS_NAME, 0);
	}
	
	/**
	 * Adds the pages of this Template to the Wizard.  This Template has one page:
	 * 	1. Basic class information
	 */
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0);
		page.setTitle(PAGE_TITLE);
		page.setDescription(PAGE_ZERO_DESCRIPTION);
		wizard.addPage(page);
	}
	
	
	
	/**
	 * Returns the unique name of this section. This name will be used to
	 * construct the template file location in the contributing plug-in.
	 * 
	 * @return the unique name of this section, "persister", which is where the
	 * template files are located inside of the template directory.
	 */
	public String getSectionId() {
		return "persister";
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
	


	/**
	 * Determines if the given TemplateOption is valid.  This checks that if it
	 * is a required option, it is non-empty.  It is triggered after any user
	 * edits of an options value.
	 * 
	 * @param source the TemplateOption to check for validity
	 */
	public void validateOptions(TemplateOption source) {
	    validateContainer();
	}
	
	/*
	 * validates the given page by checking all of its contained options
	 */
	private void validateContainer() {	    	    
		TemplateOption[] allPageOptions = getOptions(0);
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
	}
	
	/*
	 * Initializes field data based on previous input in wizard pages
	 */
	public void initializeFields(IPluginModelBase model) {
		// In the new extension wizard, the model exists so 
		// we can initialize directly from it
		String pluginId = model.getPluginBase().getId();
		initializeOption(KEY_PACKAGE_NAME, pluginId);
	}
	

	/**
	 * Returns identifier of the extension point used in this section.
	 * 
	 * @return extension point id if this section contributes into an extension
	 *         point or null if not applicable.
	 */
	public String getUsedExtensionPoint() {
		return "org.eclipse.ui.startup";
	}

	/*
	 * initializes the plugin model based on the information input into
	 * the wizard. This does the job of creating the extension points and
	 * dependencies and initializing their settings
	 */
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		//set the class for the plugin
		((IPlugin)plugin).setClassName(getFullPluginClassName());
		IPluginModelFactory factory = model.getPluginFactory();						
		
		//add dependency of edu.iu.iv.core
		IPluginImport core = factory.createImport();
		core.setId("edu.iu.iv.core");
		if(!core.isInTheModel())	
		    plugin.add(core);			
		
		//add startup extension
		IPluginExtension startupExtension = createExtension("org.eclipse.ui.startup", true);		
		IPluginElement startupElement = factory.createElement(startupExtension);
		startupElement.setName("startup");
		startupElement.setAttribute("class", getFullPluginClassName());
		startupExtension.add(startupElement);				
		if (!startupExtension.isInTheModel())
			plugin.add(startupExtension);	
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
		
	
	/*
	 * returns the fully qualified class name of the plugin class created to
	 * implement AbstractPlugin (implementing IWorkbenchWindowActionDelegate
	 * for the action in the actionset)
	 */
	private String getFullPluginClassName(){
	    return getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_PLUGIN_CLASS_NAME);
	}
	
}
