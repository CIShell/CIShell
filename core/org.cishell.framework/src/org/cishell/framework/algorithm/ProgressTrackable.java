/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 29, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.algorithm;

/**
 * An additional interface an {@link Algorithm} can implement that allows for
 * monitoring of progress, process cancellation, and current work description.
 * This was not included in the <code>Algorithm</code> interface because many 
 * of the algorithms will not be able to support these features (especially 
 * the algorithms that are wrapping executable programs). Even algorithms
 * that do implement this interface do not have to provide all of the features.
 * For instance, an algorithm may only support progress notification and not
 * cancellation.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface ProgressTrackable {
    
    /**
     * Sets the progress monitor this algorithm is to use. This method should
     * be called before an algorithm is executed. If this method is not set
     * prior to execution, the algorithm must run without it.
     * 
     * @param monitor The monitor the algorithm is to use
     */
    public void setProgressMonitor(ProgressMonitor monitor);
    
    /**
     * Returns the progress monitor currently in use, or <code>null</code> if
     * no monitor has been set
     * 
     * @return The current progress monitor, or <code>null</code> if there 
     * isn't one set
     */
    public ProgressMonitor getProgressMonitor();
}
