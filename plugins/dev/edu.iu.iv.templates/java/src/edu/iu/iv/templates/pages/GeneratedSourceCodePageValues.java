/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 12, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;


/**
 * 
 * @author Bruce Herr
 */
public interface GeneratedSourceCodePageValues {    
    public static final String KEY_PLUGIN_CLASS_NAME = "pluginClassName"; 
    public static final String KEY_ALGORITHM_NAME = "algorithmName";
    public static final String KEY_ALGORITHM_CLASS_NAME = "algorithmClassName";
    public static final String KEY_ALGORITHM_TYPE = "algorithmType";
    
    public static final String PLUGIN_CLASS_LABEL = "Plugin Class Name:";
    public static final String ALGORITHM_NAME_LABEL = "Name of Algorithm:";
    public static final String ALGORITHM_CLASS_LABEL = "Algorithm Class Name:";
    public static final String PACKAGE_LABEL = "Package Name:";
    public static final String ALGORITHM_TYPE_LABEL = "Algorithm Type:";
    
    public static final String PLUGIN_CLASS_NAME = "SamplePlugin";     
    public static final String ALGORITHM_CLASS_NAME = "SampleAlgorithm";
    public static final String ALGORITHM_NAME = "Sample";
    
    public static final String ALG_TYPE_MODELING = "Modeling";
    public static final String ALG_TYPE_ANALYSIS = "Analysis";
    public static final String ALG_TYPE_VIS = "Visualization";
    public static final String ALG_TYPE_OTHER = "Other";
    
    public static String[] ALGORITHM_TYPES = new String[] {
        ALG_TYPE_MODELING, ALG_TYPE_ANALYSIS, ALG_TYPE_VIS, ALG_TYPE_OTHER
    };
    
    public static final String CODE_PAGE_DESCRIPTION = "Generated Source Code Settings"; 
}
