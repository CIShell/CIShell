/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 11, 2005 at Indiana University.
 */
package edu.iu.iv.common.parameter;

/**
 * Class used to validate if a particular value is valid or not.
 * 
 * @author Bruce Herr
 */
public interface Validator {
    /** a null validator. Always returns true. */
    public static final Validator NULL_VALIDATOR = new Validator() {
        public boolean isValid(Object value) { return true; }
    };
    
    /**
     * @param value the value
     * @return if the value is valid.
     */
    public boolean isValid(Object value);
}
