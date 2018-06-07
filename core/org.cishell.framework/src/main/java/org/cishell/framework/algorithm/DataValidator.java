/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 13, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.algorithm;

import org.cishell.framework.data.Data;


/**
 * An additional interface an {@link AlgorithmFactory} can implement that 
 * allows for further data validation beyond what is provided in the 
 * service dictionary's in_data/out_data specifications. This is useful in 
 * cases where an algorithm expects a certain type of data, but must 
 * make additional checks. For example, if an algorithm only worked on 
 * symmetric matrices, this interface would check the data ahead of time
 * to ensure that the given matrix was in fact a symmetric matrix.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataValidator {
    /**
     * Validates the given data that is proposed to be given to the
     * algorithm. It can return three different values:
     * 
     * <table>
     * <tr><td><code>null</code></td><td>No validation present</td></tr>
     * <tr><td><code>""</code></td><td>The data is valid</td></tr>
     * <tr><td><code>"..."</code></td>
     *     <td>A localized description of why its invalid</td></tr>
     * </table>
     * 
     * @param data The proposed data that may be given to create an Algorithm
     * @return <code>null</code>, "", or another string
     */
    public String validate(Data[] data);
}
