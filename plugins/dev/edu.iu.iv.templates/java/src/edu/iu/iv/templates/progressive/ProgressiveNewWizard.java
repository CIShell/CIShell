/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 3, 2005 at Indiana University.
 */
package edu.iu.iv.templates.progressive;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;


/**
 * This class is the Wizard class that plugs into the PDE new Plug-in Project
 * Wizard. It adds the Progressive IVC Algorithm Wizard to the choice of available
 * Wizards.
 *
 * @author Team IVC (James Ellis)
 */
public class ProgressiveNewWizard extends NewPluginTemplateWizard {

	/**
	 * Constructor for ProgressiveNewWizard
	 */
	public ProgressiveNewWizard() {
		super();
	}

	/**
	 * Initializes this Wizard
	 * 
	 * @param data The IFieldData object representing the data captured
	 * by the new Plug-in Project wizard prior to the usage of this Wizard
	 */
	public void init(IFieldData data) {
		super.init(data);
		setWindowTitle("IVC Progressive Algorithm");
		setHelpAvailable(false);		
	}
	
	/**
	 * Creates the TemplateSections used by this Wizard and returns an array
	 * of them.  For this Basic wizard, only one TemplateSection is used,
	 * the ProgressiveTemplate.
	 * 
	 * @return the array of ITemplateSections used, just one item, a ProgressiveTemplate, in
	 * this Wizard
	 */
	public ITemplateSection[] createTemplateSections() {
	    ProgressiveTemplate template = new ProgressiveTemplate();
		return new ITemplateSection [] {template};
	}
}
