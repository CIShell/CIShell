package org.cishell.utilities;

import java.util.Calendar;
import java.util.GregorianCalendar;

// Patrick: I found this on Google.  Thanks to Paul Hill!
// Found at: http://www.xmission.com/~goodhill/dates/deltaDates.html

/**
 * Demonstration of delta day calculations.
 * @author Paul Hill
 * @copyright 2004 Paul Hill
 */
public class FAQCalendar extends GregorianCalendar {  
	private static final long serialVersionUID = 1L;

	/**
     * All minutes have this many milliseconds except the last minute of the day on a day defined with
     * a leap second.
     */
    public static final long MILLISECS_PER_MINUTE = 60*1000;
    
    /**
     * Number of milliseconds per hour, except when a leap second is inserted.
     */
    public static final long MILLISECS_PER_HOUR   = 60*MILLISECS_PER_MINUTE;
    
    /**
     * Number of leap seconds per day expect on 
     * <BR/>1. days when a leap second has been inserted, e.g. 1999 JAN  1.
     * <BR/>2. Daylight-savings "spring forward" or "fall back" days.
     */
    protected static final long MILLISECS_PER_DAY = 24*MILLISECS_PER_HOUR;

    /****
     * Value to add to the day number returned by this calendar to find the Julian Day number.
     * This is the Julian Day number for 1/1/1970.
     * Note: Since the unix Day number is the same from local midnight to local midnight adding
     * JULIAN_DAY_OFFSET to that value results in the chronologist, historians, or calenderists
     * Julian Day number.
     * @see http://www.hermetic.ch/cal_stud/jdn.htm
     */
    public static final long EPOCH_UNIX_ERA_DAY = 2440588L;
    
    /**
     * @see java.util.GregorianCalendar#GregorianCalendar()
     */
    public FAQCalendar() {
        super();
    }
    /**
     * @param millisecondTime - time as a binary Unix/Java time value.
     * @see java.util.GregorianCalendar
     */
    public FAQCalendar( long millisecondTime ) {
        super();
        this.setTimeInMillis( millisecondTime);
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(int, int, int)
     */
    public FAQCalendar( int y, int m, int d ) {
        super( y, m, d );
    }

    /**
     * @see java.util.GregorianCalendar#GregorianCalendar(int, int, int, int, int, int)
     */
    public FAQCalendar( int y, int m, int d, int h, int min, int s ) {
        super( y, m, d, h, min, s );
    }
    
    /**
     * @return Day number where day 0 is 1/1/1970, as per the Unix/Java date/time epoch.
     */
    public long getUnixDay() {
        long offset = get(Calendar.ZONE_OFFSET) + get(Calendar.DST_OFFSET);
        long day = (long)Math.floor( (double)(getTime().getTime() + offset ) / ((double)MILLISECS_PER_DAY) );
        return day;
    }

    /**
     * @return LOCAL Chronologists Julian day number each day starting from midnight LOCAL TIME.
     * @see http://tycho.usno.navy.mil/mjd.html for more information about local C-JDN
     */
    public long getJulianDay() {
        return getUnixDay() + EPOCH_UNIX_ERA_DAY;
    }
    /**
     * find the number of days from this date to the given end date.
     * later end dates result in positive values.
     * Note this is not the same as subtracting day numbers.  Just after midnight subtracted from just before
     * midnight is 0 days for this method while subtracting day numbers would yields 1 day.
     * @param end - any Calendar representing the moment of time at the end of the interval for calculation.
     */
    public long diffDayPeriods(Calendar end) {
        long endL   =  end.getTimeInMillis() +  end.getTimeZone().getOffset(  end.getTimeInMillis() );
        long startL = this.getTimeInMillis() + this.getTimeZone().getOffset( this.getTimeInMillis() );
        return (endL - startL) / MILLISECS_PER_DAY;
    }
}
