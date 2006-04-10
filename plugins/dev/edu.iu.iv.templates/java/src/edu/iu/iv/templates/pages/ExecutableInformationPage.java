/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 12, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;

import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.Validator;

/**
 * 
 * @author Bruce Herr
 */
public class ExecutableInformationPage extends ParameterMap implements ExecutableInformationPageValues {
    public ExecutableInformationPage() {
        Validator noNullValidator = new Validator() {
            public boolean isValid(Object value) {
                return value != null && value.toString().length() > 0;
            }};
        
        putTextOption(KEY_SUBDIRECTORY,SUBDIRECTORY_LABEL,"","algorithm",noNullValidator);
        putTextOption(KEY_BASE_EXECUTABLE,BASE_EXECUTABLE_LABEL,"","MyProgram",noNullValidator);
        putTextOption(KEY_BASE_FILES,BASE_FILES_LABEL,"","default",null);
        putMultiChoiceListOption(KEY_SUPPORTED_PLATFORMS,PLATFORM_DIRECTORIES_LABEL,"",
                SUPPORTED_PLATFORM_LABELS,new int[]{},null);
    }
}
