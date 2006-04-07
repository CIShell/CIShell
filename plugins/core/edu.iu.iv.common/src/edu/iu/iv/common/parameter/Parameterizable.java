/*
 * Created on May 18, 2005
 *
 */
package edu.iu.iv.common.parameter;

/**
 * A class that provides a ParameterMap that can be displayed
 * to the user using the GUI Builder and get input for this class.
 * 
 * @author sprao Project: tv
 */
public interface Parameterizable {
    /**
     * @return a parameterMap that encodes what input is needed for this class.
     */
	public ParameterMap getParameters() ;

}
