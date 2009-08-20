/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 10, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.wizards.staticexecutable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cishell.templates.staticexecutable.optiontypes.CustomStringOption;
import org.cishell.templates.staticexecutable.optiontypes.PlatformOption;
import org.cishell.templates.staticexecutable.providers.PlatformOptionProvider;
import org.cishell.templates.wizards.BasicTemplate;
import org.cishell.templates.wizards.pages.ChooseExecutableFilesPage;
import org.cishell.templates.wizards.pages.ChooseSourceCodeFilesPage;
import org.cishell.templates.wizards.pages.ParameterListBuilderPage;
import org.cishell.templates.wizards.pages.SpecifyInAndOutDataPage;
import org.cishell.templates.wizards.pages.SpecifyTemplateStringPage;
import org.cishell.templates.wizards.utilities.MultiHashMapWithCounts;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;


public class NewStaticExecutableAlgorithmTemplate extends BasicTemplate
		implements PlatformOptionProvider {
	public static final int CREATE_PROJECT_PAGE_NUMBER = 0;
	public static final int PROJECT_BUNDLE_PROPERTIES_PAGE_NUMBER = 1;
	public static final int CHOOSE_EXECUTABLE_FILES_PAGE_NUMBER = 2;
	public static final int PROJECT_PROPERTIES_PAGE_NUMBER = 3;
	public static final int PROJECT_PARAMETERS_PAGE_NUMBER = 4;
	public static final int SPECIFY_INPUT_AND_OUTPUT_DATA_PAGE_NUMBER = 5;
	public static final int SPECIFY_TEMPLATE_STRING_PAGE_NUMBER = 6;
	public static final int SOURCE_CODE_FILES_PAGE_NUMBER = 7;
	
	public static final String CREATE_PROJECT_PAGE_ID = "createProjectPage";
	public static final String CHOOSE_EXECUTABLE_FILES_PAGE_ID =
		"chooseExecutableFilesPage";
	public static final String SETUP_PARAMETERS_PAGE_ID =
		"setupParametersPage";
	public static final String SPECIFY_IN_AND_OUT_DATA_PAGE_ID =
		"specifyInAndOutDataPage";
	public static final String SPECIFY_TEMPLATE_STRING_PAGE_ID =
		"specifyTemplateStringPage";
	public static final String CHOOSE_SOURCE_CODE_FILES_PAGE_ID =
		"chooseSoureCodeFilesPage";
	
	public static final String BUNDLE_NAME_ID = "bundleName";
	public static final String BUNDLE_NAME_LABEL = "Bundle Name";
	public static final String DEFALT_BUNDLE_NAME =
		"My Static Executable Algorithm Bundle";
	
	public static final String BUNDLE_SYMBOLIC_NAME_ID = "bundleSymbolicName";
	public static final String BUNDLE_SYMBOLIC_NAME_NAME =
		"Bundle Symbolic Name";
	public static final String DEFAULT_BUNDLE_SYMBOLIC_NAME =
		"org.my.algorithm";
	
	public static final String BUNDLE_VERSION_ID = "bundleVersion";
	public static final String BUNDLE_VERSION_LABEL = "Bundle Version";
	public static final String DEFAULT_BUNDLE_VERSION = "0.0.1";
	
	public static final String EXECUTABLE_NAME_ID = "executableName";
	public static final String EXECUTABLE_NAME_LABEL = "Executable Name";
	public static final String DEFAULT_EXECUTABLE_NAME = "executable_name";
	
	public static final String ON_MENU_ID = "onMenu";
	public static final String ON_MENU_LABEL = "On the Menu";
	public static final boolean DEFAULT_ON_MENU_STATUS = false; 
	public static final String IS_ON_MENU_ID = "isOnMenu";
	
	public static final String MENU_PATH_ID = "menuPath";
	public static final String MENU_PATH_LABEL = "Menu Path (Optional)";
	public static final String DEFAULT_MENU_PATH = "menu_path";
	public static final String FULL_MENU_PATH = "fullMenuPath";
	
	public static final String MENU_GROUP_ID = "menuGroup";
	public static final String MENU_GROUP_LABEL = "Menu Item Placement";
	
	public static final String LABEL_ID = "label";
	public static final String LABEL_LABEL = "Label (Optional)";
	public static final String DEFAULT_LABEL = "label";
	public static final String HAS_LABEL_ID = "hasLabel";
	
	public static final String DESCRIPTION_ID = "description";
	public static final String DESCRIPTION_LABEL = "Description (Optional)";
	public static final String DEFAULT_DESCRIPTION = "description";
	public static final String HAS_DESCRIPTION_ID = "hasDescription";
	
	public static final String IMPLEMENTERS_ID = "implementers";
	public static final String IMPLEMENTERS_LABEL = "Implementers (Optional)";
	public static final String DEFAULT_IMPLEMENTERS =
		"implementer1, implementer2, ...";
	public static final String HAS_IMPLEMENTERS_ID = "hasImplementers";
	
	public static final String INTEGRATORS_ID = "integrators";
	public static final String INTEGRATORS_LABEL = "Integrators (Optional)";
	public static final String DEFAULT_INTEGRATORS =
		"integrator1, integrator2, ...";
	public static final String HAS_INTEGRATORS_ID = "hasIntegrators";
	
	public static final String REFERENCE_ID = "reference";
	public static final String REFERENCE_LABEL = "Reference (Optional)";
	public static final String DEFAULT_REFERENCE = "reference";
	public static final String HAS_REFERENCE_ID = "hasReference";
	
	public static final String REFERENCE_URL_ID = "referenceURL";
	public static final String REFERENCE_URL_LABEL =
		"Reference URL (Optional)";
	public static final String DEFAULT_REFERENCE_URL = "reference_url";
	public static final String HAS_REFERENCE_URL_ID = "hasReferenceURL";
	
	public static final String DOCUMENTATION_URL_ID = "documentationURL";
	public static final String DOCUMENTATION_URL_LABEL =
		"Documentation URL (Optional)";
	public static final String DEFAULT_DOCUMENTATION_URL = "documentation_url";
	public static final String HAS_DOCUMENTATION_URL_ID =
		"hasDocumentationURL";
	
	public static final String WRITTEN_IN_ID = "writtenIn";
	public static final String WRITTEN_IN_LABEL =
		"Language Written In (Optional)";
	public static final String DEFAULT_WRITTEN_IN = "C/C++/Fortran/etc.";
	public static final String HAS_WRITTEN_IN_ID = "hasWrittenIn";
	
	public static final String REMOTABLE_ID = "remotable";
	public static final String REMOTABLE_LABEL = "Remotable?";
	public static final boolean DEFAULT_REMOTABLE_VALUE = true;
	
	public static final String IN_DATA_ID = "inData";
	public static final String HAS_IN_DATA_ID = "hasInData";
	
	public static final String ATTRIBUTE_DEFINITIONS_ID =
		"attributeDefinitions";
	
	public static final String OUT_DATA_ID = "outData";
	public static final String HAS_OUT_DATA_ID = "hasOutData";
	public static final String OUT_FILES_ID = "outFiles";
	public static final String OUT_FILE_LABELS_ID = "outFileLabels";
	public static final String OUT_FILE_TYPES_ID = "outFileTypes";
	
	public static final String BASE_EXECUTABLE_FILE_OPTION_NAME =
		"executableFileOption";
	public static final String BASE_RELATED_FILE_OPTION_NAME =
		"relatedFileOption";
	
	public static final String MENU_START_LABEL = "start";
	public static final String MENU_START_DESCRIPTION =
		"Beginning of the menu";
	public static final String MENU_ADDITIONS_LABEL = "additions";
	public static final String MENU_ADDITIONS_DESCRIPTION =
		"Anywhere on the menu";
	public static final String MENU_END_LABEL = "end";
	public static final String MENU_END_DESCRIPTION =
		"End of the menu";
	
	public static final String TEMPLATE_STRING_ID = "templateString";
	public static final String TEMPLATE_STRING_LABEL = "";
	public static final String DEFAULT_TEMPLATE_STRING = "";
	
	public static final String CHOOSE_SOURCE_CODE_FILES_ID = "sourceCodeFiles";
	public static final String CHOOSE_SOURCE_CODE_FILES_LABEL =
		"Choose an archive file that contains your source code files";
	
	public static final String[][] GROUP_CHOICES = new String[][] {
		{ MENU_START_LABEL, MENU_START_DESCRIPTION },
		{ MENU_ADDITIONS_LABEL, MENU_ADDITIONS_DESCRIPTION },
		{ MENU_END_LABEL, MENU_END_DESCRIPTION }
	};
	
	public static final String[] IGNORED_REPLACEMENT_STRING_FILES =
			new String[] {
		"ant.sh",
		"build.xml"
	};
	
	public static final String[] IGNORED_REPLACEMENT_STRING_DIRECTORIES =
			new String[] {
		"l10n",
		"lib"
	};
	
    private WizardNewProjectCreationPage createProjectPage;
    private WizardPage bundlePropertiesPage;
    private ChooseExecutableFilesPage chooseExecutableFilesPage;
    private WizardPage projectPropertiesPage;
    private ParameterListBuilderPage projectParametersPage;
    private SpecifyInAndOutDataPage inputAndOutputDataPage;
    private SpecifyTemplateStringPage specifyTemplateStringPage;
    private ChooseSourceCodeFilesPage sourceCodeFilesPage;
    
    private TemplateOption executableNameOption;
    private Map platformExecutableOptions = new HashMap();
    private MultiHashMapWithCounts relatedFileOptions =
    	new MultiHashMapWithCounts();
    private CustomStringOption templateStringOption;
    private CustomStringOption sourceCodeFilesOption;

    public NewStaticExecutableAlgorithmTemplate() {
        super("static_executable");
        
        setPageCount(8);
        
        setupCreateProjectPage();
        setupBundlePropertiesPage();
        setupChooseExecutableFilesPage();
        setupProjectPropertiesPage();
        setupProjectParametersPage();
        setupInputAndOutputDataPage();
        setupTemplateStringPage();
        setupSourceCodeFilesPage();
    }
    
    public TemplateOption getSourceCodeFilesTemplateOption() {
    	return this.sourceCodeFilesOption;
    }
    
    public void addPages(Wizard wizard) {
        this.createProjectPage = createCreateProjectPage();
        wizard.addPage(this.createProjectPage);
        
        this.bundlePropertiesPage = createBundlePropertiesPage();
        wizard.addPage(this.bundlePropertiesPage);
        
        this.chooseExecutableFilesPage = createChooseExecutableFilesPage();
        wizard.addPage(this.chooseExecutableFilesPage);
        
        this.projectPropertiesPage = createProjectPropertiesPage();
        wizard.addPage(this.projectPropertiesPage);
        
        this.projectParametersPage = createProjectParametersPage();
        wizard.addPage(this.projectParametersPage);
        
        this.inputAndOutputDataPage = createInputAndOutputDataPage();
        wizard.addPage(this.inputAndOutputDataPage);
        
        this.specifyTemplateStringPage = createTemplateStringPage();
        wizard.addPage(this.specifyTemplateStringPage);
        
        this.sourceCodeFilesPage = createSourceCodeFilesPage();
        wizard.addPage(this.sourceCodeFilesPage);
        
        markPagesAdded();
    }
    
    /*
     * execute is basically called when the user clicks the Finish button in
     *  the wizard.  This is where all of the template options should be
     *  processed and/or created.
     * Template options are basically key/value string pairs.  If a template
     *  option's key is found in the project template files (in the
     *  templates_3.0 directory), it is replaced with the template option's
     *  value.
     */
    public void execute(IProject project,
    					IPluginModelBase model,
    					IProgressMonitor monitor) throws CoreException {
    	// Choose Executable Files Page

    	setValue(EXECUTABLE_NAME_ID, executableNameOption.getValue());
    	
    	// Project Properties Page
    	
    	// See comments on handleEmptyOption.
    	handleEmptyOption(ON_MENU_ID, IS_ON_MENU_ID, Boolean.TRUE);
    	handleEmptyOption(LABEL_ID, HAS_LABEL_ID, "");
    	handleEmptyOption(DESCRIPTION_ID, HAS_DESCRIPTION_ID, "");
    	handleEmptyOption(IMPLEMENTERS_ID, HAS_IMPLEMENTERS_ID, "");
    	handleEmptyOption(INTEGRATORS_ID, HAS_INTEGRATORS_ID, "");
    	handleEmptyOption(REFERENCE_ID, HAS_REFERENCE_ID, "");
    	handleEmptyOption(REFERENCE_URL_ID, HAS_REFERENCE_URL_ID, "");
    	handleEmptyOption(DOCUMENTATION_URL_ID, HAS_DOCUMENTATION_URL_ID, "");
    	handleEmptyOption(WRITTEN_IN_ID, HAS_WRITTEN_IN_ID, "");
    	
    	String menuPath = (String)getValue(MENU_PATH_ID);
        if (!menuPath.endsWith("/")) {
            menuPath += "/";
        }
        
        String choice = (String)getOption(MENU_GROUP_ID).getValue();

        for (int ii = 0; ii < GROUP_CHOICES.length; ii++) {
            if (GROUP_CHOICES[ii][1].equals(choice) ||
            		GROUP_CHOICES[ii][0].equals(choice)) {
                menuPath += GROUP_CHOICES[ii][0];
                
                break;
            }
            
        }
        
        setValue(FULL_MENU_PATH, menuPath);
    	
    	// Project Parameters Page
    	
    	setValue(ATTRIBUTE_DEFINITIONS_ID,
    			 this.projectParametersPage.toOutputString());
    	
    	// In and Out Data Page
    	
    	/*
    	 * These options are not tied directly to input fields on any of the
    	 *  pages.  Their values are derviced from the input fields that ARE on
    	 *  the pages.
    	 */
    	addOption(IN_DATA_ID,
    			  "",
    			  this.inputAndOutputDataPage.
    			  	formServicePropertiesInputDataString(),
    			  SPECIFY_INPUT_AND_OUTPUT_DATA_PAGE_NUMBER);
    	handleEmptyOption(IN_DATA_ID, HAS_IN_DATA_ID, "");

    	addOption(OUT_DATA_ID,
    			  "",
    			  this.inputAndOutputDataPage.
    			  	formServicePropertiesOutputDataString(),
    			  SPECIFY_INPUT_AND_OUTPUT_DATA_PAGE_NUMBER);
    	handleEmptyOption(OUT_DATA_ID, HAS_OUT_DATA_ID, "");
    	
    	addOption(OUT_FILES_ID,
    			  "",
    			  this.inputAndOutputDataPage.
    			  	formConfigPropertiesOutFilesString(),
    			  SPECIFY_INPUT_AND_OUTPUT_DATA_PAGE_NUMBER);

    	super.execute(project, model, monitor);
    }
    
    public boolean shouldProcessFile(File file) {
    	String fileName = file.getName();
    	String filePath = file.getParent();
    	
    	for (int ii = 0; ii < IGNORED_REPLACEMENT_STRING_FILES.length; ii++) {
    		if (IGNORED_REPLACEMENT_STRING_FILES[ii].equals(fileName)) {
    			return false;
    		}
    	}
    	
    	/* TODO What if the path happens to include an ignored string,
    	 * like "lib", but without the meaning intended here?
    	 * Like ".../workspace/reginald_libby/...".
    	 */
    	for (int ii = 0;
    			ii < IGNORED_REPLACEMENT_STRING_DIRECTORIES.length;
    			ii++) {
    		if (filePath.contains(
    				IGNORED_REPLACEMENT_STRING_DIRECTORIES[ii])) {
    			return false;
    		}
    	}
    	
    	return true;
    }

    protected void updateModel(IProgressMonitor monitor) throws CoreException {
    }

    public void validateOptions(TemplateOption changedTemplateOption) {
        int pageIndex = getPageIndex(changedTemplateOption);
        
        switch (pageIndex) {
        case PROJECT_PROPERTIES_PAGE_NUMBER:
            if (changedTemplateOption.getName().equals(ON_MENU_ID)) {
                if (Boolean.TRUE == changedTemplateOption.getValue()) {
                    getOption(MENU_PATH_ID).setEnabled(true);
                    getOption(MENU_GROUP_ID).setEnabled(true);
                } else {
                    getOption(MENU_PATH_ID).setEnabled(false);
                    getOption(MENU_GROUP_ID).setEnabled(false);
                }
            }
            
            break;
        default:
            break;
        }
    }

    public IProject getProjectHandle() {
        return createProjectPage.getProjectHandle();
    }
    
    private WizardNewProjectCreationPage createCreateProjectPage() {
    	WizardNewProjectCreationPage createProjectPage =
    		new WizardNewProjectCreationPage(CREATE_PROJECT_PAGE_ID);
        createProjectPage.setTitle(
        	"Create a New Static Executable Project");
        createProjectPage.setDescription("Enter the project name");
        
        return createProjectPage;
    }
    
    private WizardPage createBundlePropertiesPage() {
    	WizardPage bundlePropertiesPage =
    		createPage(PROJECT_BUNDLE_PROPERTIES_PAGE_NUMBER);
        bundlePropertiesPage.setTitle("Bundle Properties");
        bundlePropertiesPage.setDescription(
        	"Enter the Bundle Name, Bundle Symbolic Name, and Bundle Version");
        
        return bundlePropertiesPage;
    }
    
    private ChooseExecutableFilesPage createChooseExecutableFilesPage() {
    	ChooseExecutableFilesPage chooseExecutableFilesPage =
    		new ChooseExecutableFilesPage(CHOOSE_EXECUTABLE_FILES_PAGE_ID,
    									  this.executableNameOption,
    									  this);
        chooseExecutableFilesPage.setTitle("Choose Executable Files");
        chooseExecutableFilesPage.setDescription(
        	"Choose the executable files and any files they depend on");
        
        return chooseExecutableFilesPage;
    }
    
    private WizardPage createProjectPropertiesPage() {
    	WizardPage projectPropertiesPage =
    		createPage(PROJECT_PROPERTIES_PAGE_NUMBER);
        projectPropertiesPage.setTitle("Project Properties");
        projectPropertiesPage.setDescription("Enter Project Properties");
        
        return projectPropertiesPage;
    }
    
    private ParameterListBuilderPage createProjectParametersPage() {
    	ParameterListBuilderPage projectParametersPage =
    		new ParameterListBuilderPage(SETUP_PARAMETERS_PAGE_ID);
        projectParametersPage.setTitle("Algorithm Parameters");
        projectParametersPage.setDescription("Enter Project Parameters");
        
        return projectParametersPage;
    }
    
    private SpecifyInAndOutDataPage createInputAndOutputDataPage() {
    	SpecifyInAndOutDataPage inputAndOutputDataPage =
    		new SpecifyInAndOutDataPage(SPECIFY_IN_AND_OUT_DATA_PAGE_ID);
        inputAndOutputDataPage.setTitle("Input and Output Data");
        inputAndOutputDataPage.setDescription(
        	"Enter the Input and Output Data");

        return inputAndOutputDataPage;
    }
    
    private SpecifyTemplateStringPage createTemplateStringPage() {
    	SpecifyTemplateStringPage specifyTemplateStringPage =
    		new SpecifyTemplateStringPage(
    			SPECIFY_TEMPLATE_STRING_PAGE_ID,
    			projectParametersPage,
    			inputAndOutputDataPage,
    			this.templateStringOption);
        specifyTemplateStringPage.setTitle("Template String");
        specifyTemplateStringPage.setDescription(
        	"Enter the Template String Used to Execute Your Program");

        return specifyTemplateStringPage;
    }
    
    private ChooseSourceCodeFilesPage createSourceCodeFilesPage() {
    	ChooseSourceCodeFilesPage sourceCodeFilesPage =
    		new ChooseSourceCodeFilesPage(
    			CHOOSE_SOURCE_CODE_FILES_PAGE_ID,
    			this.sourceCodeFilesOption);
        sourceCodeFilesPage.setTitle("Source Code Files (Optional)");
        sourceCodeFilesPage.setDescription(
        	"Enter the Source Code Files for Your Program");

        return sourceCodeFilesPage;
    }
    
    private void setupCreateProjectPage() { }
    
    private void setupBundlePropertiesPage() {
    	addOption(BUNDLE_NAME_ID,
    			  BUNDLE_NAME_LABEL,
    			  DEFALT_BUNDLE_NAME,
    			  PROJECT_BUNDLE_PROPERTIES_PAGE_NUMBER).setRequired(true);
    	
    	addOption(BUNDLE_SYMBOLIC_NAME_ID,
    			  BUNDLE_SYMBOLIC_NAME_NAME,
    			  DEFAULT_BUNDLE_SYMBOLIC_NAME,
    			  PROJECT_BUNDLE_PROPERTIES_PAGE_NUMBER).setRequired(true);
    	
    	addOption(BUNDLE_VERSION_ID,
    			  BUNDLE_VERSION_LABEL,
    			  DEFAULT_BUNDLE_VERSION,
    			  PROJECT_BUNDLE_PROPERTIES_PAGE_NUMBER).setRequired(true);
    }
    
    private void setupChooseExecutableFilesPage() {
    	this.executableNameOption = addOption(
    		EXECUTABLE_NAME_ID,
    		EXECUTABLE_NAME_LABEL,
    		DEFAULT_EXECUTABLE_NAME,
    		CHOOSE_EXECUTABLE_FILES_PAGE_NUMBER);
    	this.executableNameOption.setRequired(true);
    }
    
    private void setupProjectPropertiesPage() {
    	addOption(ON_MENU_ID,
    			  ON_MENU_LABEL,
    			  DEFAULT_ON_MENU_STATUS,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(true);
    	
    	addOption(MENU_PATH_ID,
    			  MENU_PATH_LABEL,
    			  DEFAULT_MENU_PATH,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setEnabled(false);
    	
    	addOption(MENU_GROUP_ID,
    			  MENU_GROUP_LABEL,
    			  GROUP_CHOICES,
    			  MENU_ADDITIONS_DESCRIPTION,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setEnabled(false);
    	
    	addOption(LABEL_ID,
    			  LABEL_LABEL,
    			  DEFAULT_LABEL,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(REFERENCE_ID,
    			  REFERENCE_LABEL,
    			  DEFAULT_REFERENCE,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(REFERENCE_URL_ID,
    			  REFERENCE_URL_LABEL,
    			  DEFAULT_REFERENCE_URL,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(DESCRIPTION_ID,
    			  DESCRIPTION_LABEL,
    			  DEFAULT_DESCRIPTION,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(IMPLEMENTERS_ID,
    			  IMPLEMENTERS_LABEL,
    			  DEFAULT_IMPLEMENTERS,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(INTEGRATORS_ID,
    			  INTEGRATORS_LABEL,
    			  DEFAULT_INTEGRATORS,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(DOCUMENTATION_URL_ID,
    			  DOCUMENTATION_URL_LABEL,
    			  DEFAULT_DOCUMENTATION_URL,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(WRITTEN_IN_ID,
    			  WRITTEN_IN_LABEL,
    			  DEFAULT_WRITTEN_IN,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(false);
    	
    	addOption(REMOTABLE_ID,
    			  REMOTABLE_LABEL,
    			  DEFAULT_REMOTABLE_VALUE,
    			  PROJECT_PROPERTIES_PAGE_NUMBER).setRequired(true);
    }
    
    private void setupProjectParametersPage() { }
    
    private void setupInputAndOutputDataPage() { }
    
    private void setupTemplateStringPage() {
    	this.templateStringOption = new CustomStringOption(
    		this,
    		TEMPLATE_STRING_ID,
    		TEMPLATE_STRING_LABEL);
    	registerOption(
    		this.templateStringOption,
    		DEFAULT_TEMPLATE_STRING,
    		SPECIFY_TEMPLATE_STRING_PAGE_NUMBER);
    	this.templateStringOption.setRequired(true);
    }
    
    private void setupSourceCodeFilesPage() {
    	this.sourceCodeFilesOption = new CustomStringOption(
    		this, CHOOSE_SOURCE_CODE_FILES_ID, CHOOSE_SOURCE_CODE_FILES_LABEL);
    	registerOption(
    		this.sourceCodeFilesOption, "", SOURCE_CODE_FILES_PAGE_NUMBER);
    	this.sourceCodeFilesOption.setRequired(false);
    }
    
    /*
     * As far as I know, there are no conditionals in the templating langauge
     *  used when processing the new project templates (in the templates_3.0
     *  directory).
     * To avoid having empty values in the properties files, the templates must
     *  be setup to comment out key/value lines in case the values are empty.
     * What handleEmptyOption does is check if the value of the option optionID
     *  is NOT equal to compareTo, and if so, it sets the "comment-out"
     *  template option isEmptyOptionID to have the value "#", which will
     *  comment out the line if it's placed accordingly in the template.
     * For example, if there were the line
     *   $isOptionEmpty$key=$value$
     *  the template option isOptionEmpty would be set to "#" if the template
     *  option value is empty.
     */
    private void handleEmptyOption(
    		String optionID, String isEmptyOptionID, String compareTo) {
    	if (getOption(optionID).getValue() == null ||
    			getOption(optionID).getValue().toString().equals(compareTo)) {
    		setValue(isEmptyOptionID, "#");
    	} else {
    		setValue(isEmptyOptionID, "");
    	}
    }
    
    private void handleEmptyOption(
    		String optionID, String isEmptyOptionID, Boolean compareTo) {
    	if (getOption(optionID).getValue() != compareTo) {
    		setValue(isEmptyOptionID, "#");
    	} else {
    		setValue(isEmptyOptionID, "");
    	}
    }
    
    public PlatformOption getExecutableFileOption(String platformName) {
    	if (!this.platformExecutableOptions.containsKey(platformName)) {
    		return (PlatformOption)
    			this.platformExecutableOptions.get(DEFAULT_LABEL);
    	} else {
    		return (PlatformOption)
    			this.platformExecutableOptions.get(platformName);
    	}
    }
    
    public PlatformOption[] getExecutableFileOptions() {
    	Object[] keys = this.platformExecutableOptions.keySet().toArray();
    	PlatformOption[] executableFileOptions =
    		new PlatformOption[keys.length];
    	
    	for (int ii = 0; ii < keys.length; ii++) {
    		executableFileOptions[ii] = (PlatformOption)
    			this.platformExecutableOptions.get(keys[ii]);
    	}
    	
    	return executableFileOptions;
    }
    
    public void addExecutableFileOption(PlatformOption executableFileOption) {
    	this.platformExecutableOptions.put(
    		executableFileOption.getPlatformName(),
    		executableFileOption);
    }
    
    public PlatformOption createExecutableFileOption(String platformName,
    												 String platformPath) {
    	String optionName = formExecutableFileOptionName(platformName);
    	PlatformOption executableFileOption = new PlatformOption(
    		this, optionName, "", platformName, platformPath);
    	registerOption(
    		executableFileOption, "", CHOOSE_EXECUTABLE_FILES_PAGE_NUMBER);
    	addExecutableFileOption(executableFileOption);
    	
    	return executableFileOption;
    }
    
    public String formExecutableFileOptionName(String platformName) {
    	String optionName = BASE_EXECUTABLE_FILE_OPTION_NAME + platformName;
    	
    	return optionName;
    }
    
    public PlatformOption[] getRelatedFileOptions(String platformName) {
    	if (this.relatedFileOptions.containsKey(platformName)) {
    		Set relatedFileOptionSet =
    			(Set)this.relatedFileOptions.get(platformName);
    		
    		return (PlatformOption[])
    			relatedFileOptionSet.toArray(new PlatformOption[0]);
    	} else {
    		return new PlatformOption[] {};
    	}
    }
    
    public void addRelatedFileOption(PlatformOption relatedFileOption) {
    	this.relatedFileOptions.put(
    		relatedFileOption.getPlatformName(), relatedFileOption);
    }
    
    public void removeRelatedFileOption(TemplateOption relatedFileOption) {
    	if (relatedFileOption instanceof PlatformOption) {
    		PlatformOption relatedFilePlatformOption =
    			(PlatformOption)relatedFileOption;
    		
    		this.relatedFileOptions.removeValue(
    			relatedFilePlatformOption.getPlatformName(),
    			relatedFilePlatformOption);
    	}
    }
    
    public PlatformOption createRelatedFileOption(String platformName,
    											  String platformPath) {
    	String optionName = formRelatedFileOptionName(platformName);
    	PlatformOption relatedFileOption = new PlatformOption(
    		this, optionName, "", platformName, platformPath);
    	registerOption(
    		relatedFileOption, "", CHOOSE_EXECUTABLE_FILES_PAGE_NUMBER);
    	addRelatedFileOption(relatedFileOption);
    	
    	return relatedFileOption;
    }
    
    public String formRelatedFileOptionName(String platformName) {
    	String optionName = BASE_RELATED_FILE_OPTION_NAME +
    						platformName +
    						relatedFileOptions.getCount(platformName);
    	
    	return optionName;
    }
}