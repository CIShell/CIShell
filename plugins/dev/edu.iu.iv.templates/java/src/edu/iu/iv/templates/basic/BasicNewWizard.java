/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 3, 2005 at Indiana University.
 */
package edu.iu.iv.templates.basic;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;


/**
 * This class is the Wizard class that plugs into the PDE new Plug-in Project
 * Wizard. It adds the Basic IVC Algorithm Wizard to the choice of available
 * Wizards.
 *
 * @author Team IVC (James Ellis)
 */
public class BasicNewWizard extends NewPluginTemplateWizard {

	/**
	 * Constructor for BasicNewWizard
	 */
	public BasicNewWizard() {
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
		setWindowTitle("IVC Basic Algorithm");
		setHelpAvailable(false);		
	}
	
	/**
	 * Creates the TemplateSections used by this Wizard and returns an array
	 * of them.  For this Basic wizard, only one TemplateSection is used,
	 * the BasicTemplate.
	 * 
	 * @return the array of ITemplateSections used, just one item, a BasicTemplate, in
	 * this Wizard
	 */
	public ITemplateSection[] createTemplateSections() {
	    BasicTemplate template = new BasicTemplate();
		return new ITemplateSection [] {template};
	}
}
