/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu)
 */
package $packageName$;

import edu.iu.iv.core.algorithm.AbstractAlgorithm;
import edu.iu.iv.core.algorithm.ProgressiveAlgorithm;
import edu.iu.iv.core.algorithm.AlgorithmProperty;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.common.parameter.ParameterMap;

/**
 * Class to implement the Algorithm for this IVC Plug-in.
 *
 * @author
 */
public class $algorithmClassName$ extends AbstractAlgorithm 
        implements ProgressiveAlgorithm {    
    
    private static final String ALGORITHM_NAME = "$algorithmName$";
    private int progress;
    
    /**
     * Creates a new $algorithmClassName$.
     */
	public $algorithmClassName$() {
	    progress = 0;
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
	
    /**
     * Returns the amount of processing that has been completed for this
     * Progressive Algorithm. This must be a value between zero and 100, and
     * should be updated accordingly during Algorithm execution.
     * @return the amount of processing that has been completed for this
     * Progressive Algorithm
     */
    public int getPercentageDone() {
        return progress;
    }
}