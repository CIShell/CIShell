/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu)
 */
package $packageName$;

import edu.iu.iv.core.algorithm.AbstractAlgorithm;
import edu.iu.iv.core.algorithm.AlgorithmProperty;

/**
 * Class to implement the Algorithm for this IVC Plug-in.
 *
 * @author
 */
public class $algorithmClassName$ extends AbstractAlgorithm {    
    private static final String ALGORITHM_NAME = "$algorithmName$";
    
    /**
     * Creates a new $algorithmClassName$.
     */
	public $algorithmClassName$() {
	    propertyMap.put(AlgorithmProperty.LABEL, ALGORITHM_NAME);
	}
	
	/**
	 * Executes this $algorithmClassName$.
	 * 
	 * @return true if the Algorithm was successful, false if not
	 */
	public boolean execute(){
	    return true;
	}
}