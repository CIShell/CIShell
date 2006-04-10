/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 12, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;

import org.eclipse.pde.ui.templates.AbstractTemplateSection;

import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.Validator;

public class GeneratedSourceCodePage extends ParameterMap implements GeneratedSourceCodePageValues {

    public GeneratedSourceCodePage() {
        Validator noNullValidator = new Validator() {
            public boolean isValid(Object value) {
                return value != null && value.toString().length() > 0;
            }};
        
        putTextOption(AbstractTemplateSection.KEY_PACKAGE_NAME, PACKAGE_LABEL,"","",null);
        putTextOption(KEY_PLUGIN_CLASS_NAME,PLUGIN_CLASS_LABEL,"",PLUGIN_CLASS_NAME, noNullValidator);
        putTextOption(KEY_ALGORITHM_CLASS_NAME,ALGORITHM_CLASS_LABEL,"",ALGORITHM_CLASS_NAME,noNullValidator);  
        putTextOption(KEY_ALGORITHM_NAME,ALGORITHM_NAME_LABEL, "", ALGORITHM_NAME, null);
        putSingleChoiceListOption(KEY_ALGORITHM_TYPE,ALGORITHM_TYPE_LABEL,"", ALGORITHM_TYPES,0,null);
    }

}
