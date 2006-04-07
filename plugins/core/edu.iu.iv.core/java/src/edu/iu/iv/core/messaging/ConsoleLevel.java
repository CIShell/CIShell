/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;


/**
 * ConsoleLevel defines the different levels of messages that can be
 * output to a ConsoleHandler's console device.  These levels allow for
 * filtering of messages based on how high of a level of severity the user
 * wishes to view.
 *
 * @author Team IVC
 */
public class ConsoleLevel implements Comparable {
    public static final ConsoleLevel USER_ACTIVITY = new ConsoleLevel(0);
    public static final ConsoleLevel SYSTEM_INFORMATION = new ConsoleLevel(1);
    public static final ConsoleLevel ALGORITHM_INFORMATION = new ConsoleLevel(2);
    public static final ConsoleLevel SYSTEM_WARNING = new ConsoleLevel(3);
    public static final ConsoleLevel SYSTEM_ERROR = new ConsoleLevel(4);
    private static boolean showOnlyCriticalErrors;
    private int priority;

    //private constructor to disallow outside creation
    private ConsoleLevel(int priority) {
        this.priority = priority;

        Configuration cfg = IVC.getInstance().getConfiguration();
        boolean showAll = cfg.getBoolean(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE);
        boolean showCritical = cfg.getBoolean(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE);
        
        showOnlyCriticalErrors = !showAll && showCritical;
    }

    /**
     * Sets the flag to determine whether or not warnings should be
     * shown as well as errors if the ConsoleLevel is SYSTEM_ERROR.
     *
     * @param showOnlyCritical true if warnings should not be displayed, but
     * critical errors should, false if both warnings and errors should be
     * diplayed when the ConsoleLevel is at SYSTEM_ERROR
     */
    public static void setShowOnlyCriticalErrors(boolean showOnlyCritical) {
        showOnlyCriticalErrors = showOnlyCritical;
    }

    /**
     * Compares the given ConsoleLevel to this one, based on priority.
     *
     * @param object the ConsoleLevel to compare to this console level.
     *
     * @return a negative value if this ConsoleLevel is lower priority than
     *  the given one, zero if they are equal, or a positive value if this
     *  ConsoleLevel is higher priority than the given one.
     */
    public int compareTo(Object object) {
        if (object instanceof ConsoleLevel) {
            ConsoleLevel level = (ConsoleLevel) object;

            //if its warning, but we are showing only critical, dont show
            if ((this == SYSTEM_WARNING) && showOnlyCriticalErrors) {
                return 1;
            }

            return this.priority - level.priority;
        } else {
            throw new IllegalArgumentException(
                "Only ConsoleLevels can be compared.");
        }
    }
}
