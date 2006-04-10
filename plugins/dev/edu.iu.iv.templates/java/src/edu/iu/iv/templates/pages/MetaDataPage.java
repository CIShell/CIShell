/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 12, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;

import edu.iu.iv.common.parameter.ParameterMap;

/**
 * 
 * @author Bruce Herr
 */
public class MetaDataPage extends ParameterMap implements MetaDataPageValues {
    public MetaDataPage() {
        putTextOption(KEY_AUTHOR_NAME,AUTHOR_NAME_LABEL,"","",null);
        putTextOption(KEY_PLUGIN_DESCRIPTION,PLUGIN_DESCRIPTION_LABEL,"","",null);
        putTextOption(KEY_CITATION_STRING,CITATION_STRING_LABEL,"","",null);
        putTextOption(KEY_DOCUMENTATION_URL,DOCUMENATION_URL,"","",null);
    }
}
