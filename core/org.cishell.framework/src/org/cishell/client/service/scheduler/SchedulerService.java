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
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.client.service.scheduler;

import java.util.Calendar;
import java.util.Map;

import org.cishell.framework.algorithm.Algorithm;

public interface SchedulerService {
    public void runNow(Algorithm algorithm);
    public void schedule(Algorithm algorithm);
    public void schedule(Algorithm algorithm, Calendar time);
    public boolean reschedule(Algorithm algorithm, Calendar newTime);
    public boolean unschedule(Algorithm algorithm);
    
    public void addSchedulerListener(SchedulerListener listener);
    public void removeSchedulerListener(SchedulerListener listener);
    
    public boolean isRunning();
    public void setRunning(boolean running);
    public Map getSchedule();
    public void clearSchedule();
    public boolean isEmpty();
}
