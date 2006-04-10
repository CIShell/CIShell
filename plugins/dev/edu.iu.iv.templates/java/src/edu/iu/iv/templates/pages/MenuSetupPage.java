/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 12, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;

import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.Validator;

public class MenuSetupPage extends ParameterMap implements MenuSetupPageValues {

    public MenuSetupPage() {
        Validator noNullValidator = new Validator() {
            public boolean isValid(Object value) {
                return value != null && value.toString().length() > 0;
            }};
        
        putTextOption(KEY_ACTION_LABEL,ACTION_LABEL_LABEL,"","",noNullValidator);
        putTextOption(KEY_MENUBAR_PATH, MENUBAR_PATH_LABEL, "", "", noNullValidator);
        putTextOption(KEY_TOOLTIP,TOOLTIP_LABEL,"","",noNullValidator);
    }
}
