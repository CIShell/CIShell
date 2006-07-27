/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 14, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API 
 * ***************************************************************************/
package org.cishell.service.conversion;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;

/**
 * A service for converting data to different formats. This service should 
 * utilize the pool of {@link AlgorithmFactory} services which have registered 
 * with the OSGi service registry and specified in its service dictionary that
 * they are a converter. A converter will specify what data format it takes in
 * ('in_data'), what it converts it to ('out_data'), and whether any information
 * will be lost in the conversion ('consersion'='lossless'|'lossy'). Using this
 * and other standard algorithm properties, a DataConversionService will try and
 * find the fastest, most efficient way to convert from a one format to another.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataConversionService {
	
	/**
	 * Finds a converter from one format to another if at all possible. The
	 * returned {@link Converter}s, which may be a composite of multiple
	 * algorithms, will take a {@link Data} of the specified 
	 * <code>inFormat</code> and convert it to a Data of type 
	 * <code>outFormat</code>. If there is no way to convert the data model,
	 * <code>null</code> will be returned.
	 * 
	 * @param inFormat  The type of data model to be converted. This String 
	 *                  should be formatted in the same way as an algorithm's
	 *                  {@link AlgorithmProperty#IN_DATA 'in_data'}.
	 * @param outFormat The type of data model that should be produced. This 
	 * 			        String should be formatted in the same way as an 
	 *                  algorithm's 
	 *                  {@link AlgorithmProperty#OUT_DATA 'out_data'}.
	 * @return An AlgorithmFactory that will convert a data model of the given
	 *         inFormat to the given outFormat, or <code>null</code> if there is 
	 *         no way to convert.
	 */
    public Converter[] findConverters(String inFormat, String outFormat);
    
    /**
     * Finds a converter from one format to another falling within the given
     * parameters. The max number of converters to use (maxHops) and the 
     * maximum allowed complexity for the converters to limit the impact a 
     * conversion will make. The returned {@link Converter}s, which may 
     * be a composite of multiple algorithms, will take a {@link Data} of 
     * the specified <code>inFormat</code> and convert it to a Data of type 
	 * <code>outFormat</code>. If there is no way to convert the data model 
	 * within the given parameters, <code>null</code> will be returned.
     * 
	 * @param inFormat  The type of data model to be converted. This String 
	 *                  should be formatted in the same way as an algorithm's
	 *                  {@link AlgorithmProperty#IN_DATA 'in_data'}.
	 * @param outFormat The type of data model that should be produced. This 
	 * 			        String should be formatted in the same way as an 
	 *                  algorithm's 
	 *                  {@link AlgorithmProperty#OUT_DATA 'out_data'}.
     * @param maxHops   The maximum number of converters to use for the 
     *                  conversion.
     * @param maxComplexity The maximum complexity the conversion algorithm can
     *                  have in order to be considered for use in the 
     *                  conversion. The format of the String is in big-O 
     *                  notation. Examples are 'O(n)', 'O(n^2)', 'O(log(n))'.
	 * @return An AlgorithmFactory that will convert a data model of the given
	 *         inFormat to the given outFormat, or <code>null</code> if there is 
	 *         no way to convert within the given parameters.
     */
    public Converter[] findConverters(String inFormat, String outFormat,
            int maxHops, String maxComplexity);
    
    /**
     * Tries to find all the converters that can be used to transform the given
     * Data to the specified output format
     * 
     * @param dm        The Data to convert
     * @param outFormat The output format to convert to
     * @return An array of converters (may be zero length) that can convert the
     *         given Data to the specified output format.
     */
    public Converter[] findConverters(Data dm, String outFormat);
    
    /**
     * Tries to convert a given Data to the specified output format. If 
     * the conversion fails or there is no way to convert it, this method will 
     * return a <code>null</code>
     * 
     * @param dm        The Data to convert
     * @param outFormat The format of the Data to be returned 
     * @return A Data with the specified output format, or 
     *         <code>null</code> if the conversion fails
     */
    public Data convert(Data dm, String outFormat);
        
    //  TODO: More methods of conversion here?
}
