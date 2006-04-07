/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 13, 2004 at Indiana University.
 */
package edu.iu.iv.core.algorithm;

import edu.iu.iv.common.parameter.Parameterizable;
import edu.iu.iv.common.property.PropertyAssignable;

/**
 * An Algorithm has a ParameterMap to publish what Inputs are needed, a PropertyMap to give meta
 * information about the Algorithm, and an execute method that takes the Values set for the Parameters in 
 * the ParameterMap and does some sort of processing. There is also a subinterface called ProgressiveAlgorithm
 * that adds another method getPercentageDone() which can be polled while the ProgressiveAlgorithm's execute 
 * method is running to see how far along the algorithm is in its processing. Some algorithms include: LSA, 
 * Topics, and Betweenness Centrality algorithm.
 * 
 * @author Team IVC
 */
//Created by: Josh Bonner
public interface Algorithm extends Parameterizable, PropertyAssignable {
	
	/**
	 * Runs the algorithm.
	 * 
	 * @return true if the algorithm ran successfully, false otherwise.
	 */
	public boolean execute();
}
