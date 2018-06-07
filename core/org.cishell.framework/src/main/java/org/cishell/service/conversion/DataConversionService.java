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
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;

/**
 * A service for converting data to different formats. This service should 
 * utilize the pool of {@link AlgorithmFactory} services which have registered 
 * with the OSGi service registry and specified in its service dictionary that
 * they are a converter. A converter will specify what data format it takes in
 * ('in_data'), what it converts it to ('out_data'), and whether any information
 * will be lost in the conversion ('conversion'='lossless'|'lossy'). Using this
 * and other standard algorithm properties, a DataConversionService will try and
 * find the fastest, most efficient way to convert from one format to another.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataConversionService {
	
	/**
	 * Finds converters from one format to another if at all possible. The
	 * returned {@link Converter}s, which may be a composite of multiple
	 * algorithms, will take a {@link Data} object of the specified 
	 * <code>inFormat</code> and convert it to a Data object of type 
	 * <code>outFormat</code>.
	 * 
	 * @param inFormat  The type of Data object to be converted. This String 
	 *                  should be formatted in the same way as an algorithm's
	 *                  {@link AlgorithmProperty#IN_DATA}.
	 * @param outFormat The type of Data object that should be produced. This 
	 * 			        String should be formatted in the same way as an 
	 *                  algorithm's {@link AlgorithmProperty#OUT_DATA}.
	 * @return An array of {@link Converter}s that can convert a Data object of
	 *         the given inFormat to the specified outFormat
	 */
    public Converter[] findConverters(String inFormat, String outFormat);
    
    /**
     * Tries to find all the converters that can be used to transform the given
     * Data object to the specified output format
     * 
     * @param data      The Data object to convert
     * @param outFormat The output format to convert to
     * @return An array of {@link Converter}s that can convert the
     *         given Data object to the specified output format
     */
    public Converter[] findConverters(Data data, String outFormat);
    
    /**
     * Tries to convert a given Data object to the specified output format
     * 
     * @param data      The Data to convert
     * @param outFormat The format of the Data object to be returned 
     * @return A Data object with the specified output format
     * @throws ConversionException If the data conversion fails while converting
     */
    public Data convert(Data data, String outFormat) throws ConversionException;
}
