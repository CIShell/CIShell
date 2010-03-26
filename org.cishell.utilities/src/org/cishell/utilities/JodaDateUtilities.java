package org.cishell.utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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