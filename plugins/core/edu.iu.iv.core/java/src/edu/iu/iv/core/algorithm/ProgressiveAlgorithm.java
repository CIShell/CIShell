/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 28, 2005 at Indiana University.
 */
package edu.iu.iv.core.algorithm;

/**
 * A ProgressiveAlgorithm is one that reports its progress
 * during execution, and its getPercentageDone() method can be
 * queried to determine how much of the job has been completed.
 *
 * @author Team IVC
 */
public interface ProgressiveAlgorithm extends Algorithm {
    
    /**
     * Returns the value between 0 and 100, inclusive that represents
     * the percentage of computation that has been completed for this
     * Algorithm. A return value outside of this range will be interpreted
     * to mean that progress information is not available.
     * 
     * @return percentage or work that this Algorithm has completed
     */
    public int getPercentageDone();

}
