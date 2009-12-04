/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.algorithm;

/**
 * A standard set of properties and values used for creating a service 
 * metadata Dictionary that is provided when registering an 
 * {@link AlgorithmFactory} with the OSGi service registry.
 * 
 * See the <a href="http://cishell.org/dev/docs/spec/cishell-spec-1.0.pdf">
 * CIShell Specification 1.0</a> for documentation on each property. 
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface AlgorithmProperty {
    public static final String IN_DATA = "in_data";
    public static final String OUT_DATA = "out_data";
    public static final String NULL_DATA = "null";
    
    public static final String PARAMETERS_PID = "parameters_pid";
    
    public static final String PARENTAGE = "parentage";
    public static final String DEFAULT_PARENTAGE="default";
        
    public static final String ALGORITHM_TYPE = "type";
    public static final String TYPE_CONVERTER = "converter";
    public static final String TYPE_VALIDATOR = "validator";
    public static final String TYPE_DATASET = "dataset";
    
    public static final String REMOTEABLE = "remoteable";
    public static final String REMOTE = "remote";

    public static final String LABEL = "label";
    public static final String DESCRIPTION = "description";
    
    public static final String MENU_PATH = "menu_path";
    public static final String ADDITIONS_GROUP = "additions";
    public static final String START_GROUP = "start";
    public static final String END_GROUP = "end";
    public static final String SHORTCUT = "shortcut";
    
    public static final String CONVERSION = "conversion";
    public static final String LOSSY = "lossy";
    public static final String LOSSLESS = "lossless";
    
    public static final String AUTHORS = "authors";
    public static final String IMPLEMENTERS = "implementers";
    public static final String INTEGRATORS = "integrators";
    
    public static final String DOCUMENTATION_URL = "documentation_url";
    public static final String REFERENCE = "reference";
    public static final String REFERENCE_URL = "reference_url";
    public static final String WRITTEN_IN = "written_in";
}
