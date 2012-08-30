package org.cishell.utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class JodaDateUtilities {
	public static String easyDateTimeFormat(String format) {
		DateTime currentDateAndTime = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat.forPattern(format);

		return formatter.print(currentDateAndTime);
	}

	public static String easyDateFormat() {
		return easyDateTimeFormat("MMM dd, yyyy");
	}

	public static String easyDateAndTimeFormat() {
		return easyDateTimeFormat("MMM dd, yyyy; hh:mmaa");
	}
}