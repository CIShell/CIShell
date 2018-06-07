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
package org.cishell.app.service.scheduler;

import java.util.Calendar;

import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;

/**
 * An abstract adapter class for notification of events happening in a 
 * {@link SchedulerService}. The methods in this class are empty. This class
 * exists as a convenience for creating listener objects.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public abstract class SchedulerAdapter implements SchedulerListener {

    public void algorithmError(Algorithm algorithm, Throwable error) { }

    public void algorithmFinished(Algorithm algorithm, Data[] createdData) { }

    public void algorithmRescheduled(Algorithm algorithm, Calendar time) { }
    
    public void algorithmUnscheduled(Algorithm algorithm) {}

    public void algorithmScheduled(Algorithm algorithm, Calendar time) { }

    public void algorithmStarted(Algorithm algorithm) { }

    public void schedulerCleared() { }

    public void schedulerRunStateChanged(boolean isRunning) { }
}
