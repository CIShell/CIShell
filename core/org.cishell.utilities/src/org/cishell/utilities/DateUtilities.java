package org.cishell.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// TODO: Fix this class.
public class DateUtilities {
	public final static double AVERAGE_MILLIS_PER_MONTH =
		(365.24 * 24 * 60 * 60 * 1000 / 12);
	
	// TODO: Is this actually necessary?
	public static Date[] generateDaysBetweenDates(Date startDate, Date endDate) {
		GregorianCalendar startDateCalendar =
			new GregorianCalendar(startDate.getYear() + 1900,
								  startDate.getMonth(),
								  startDate.getDate());
		
		GregorianCalendar endDateCalendar =
			new GregorianCalendar(endDate.getYear() + 1900,
								  endDate.getMonth(),
								  endDate.getDate());
		
		// Return an empty set of days (Dates) if the start date is actually AFTER
		// the end date.
		if (startDateCalendar.getTimeInMillis() > endDateCalendar.getTimeInMillis())
			return new Date [0];
		
		// There is at least one day between the provided start and end dates (dates
		// themselves included).
		
		ArrayList workingDaysBetweenDates = new ArrayList();
		GregorianCalendar currentCalendarForDateThatWeAreCalculating =
			(GregorianCalendar)startDateCalendar.clone();
		final Date actualEndDateAccordingToCalendar = endDateCalendar.getTime();
		boolean shouldKeepGeneratingDaysBetweenDates = true;
		
		// This is the meat of the Date generation.
		while (shouldKeepGeneratingDaysBetweenDates) {
			// Get the current calculated date.
			Date currentCalculatedDate =
				currentCalendarForDateThatWeAreCalculating.getTime();
			
			// Add the current date that we are calculating.
			workingDaysBetweenDates.add(currentCalculatedDate);
			
			// Move the current calendar for the date that we are calculating
			// forward in time a day.
			currentCalendarForDateThatWeAreCalculating.add(Calendar.DATE, 1);
			
			// Should we stop now?
			if ((currentCalculatedDate.getYear() ==
					actualEndDateAccordingToCalendar.getYear()) &&
				(currentCalculatedDate.getMonth() ==
					actualEndDateAccordingToCalendar.getMonth()) &&
				(currentCalculatedDate.getDate() ==
					actualEndDateAccordingToCalendar.getDate()))
			{
				shouldKeepGeneratingDaysBetweenDates = false;
			}
		}
		
		Date[] finalDaysBetweenDates = new Date [workingDaysBetweenDates.size()];
		
		return (Date[])workingDaysBetweenDates.toArray(finalDaysBetweenDates);
	}
	
	public static int calculateDaysBetween(Date[] dateSet) {
		return dateSet.length;
	}
	
	public static int calculateDaysBetween(Date startDate, Date endDate) {
		FAQCalendar startDateCalendar = new FAQCalendar(startDate.getYear(),
														startDate.getMonth(),
														startDate.getDate());
		
		FAQCalendar endDateCalendar = new FAQCalendar(endDate.getYear(),
													  endDate.getMonth(),
													  endDate.getDate());
		
		return (int)startDateCalendar.diffDayPeriods(endDateCalendar);
	}
	
	public static int calculateMonthsBetween(Date startDate, Date endDate) {
		int roundedMonthsBetween = (int)Math.round
			((endDate.getTime() - startDate.getTime()) / AVERAGE_MILLIS_PER_MONTH);
		
		if (roundedMonthsBetween > 0) {
			return roundedMonthsBetween;
		}
		else {
			// HACK(?): There must be at least one month between
			// (even if they're both the same month).
			return 1;
		}
	}
	
	// Assumes dateSet is sorted from earliest to latest.
	public static Date[] getNewYearsDatesFromDateSet(Date[] dateSet) {
		ArrayList workingNewYearsDates = new ArrayList();
		
		// Return an empty set if there are no dates.
		if (dateSet.length == 0)
			return new Date [0];
		
		// If the first date is not a new year's date, add a new year's date for
		// that date's year.
		if ((dateSet[0].getMonth() != 0) || (dateSet[0].getDate() != 1))
			workingNewYearsDates.add(new Date(dateSet[0].getYear(), 0, 1));
		
		// Find each date that has the month and day of 1-1 (well, 0-1 because Date
		// is stupid).
		for (int ii = 0; ii < dateSet.length; ii++) {
			if ((dateSet[ii].getMonth() == 0) && (dateSet[ii].getDate() == 1))
				workingNewYearsDates.add(dateSet[ii]);
		}
		
		Date[] finalNewYearsDates = new Date [workingNewYearsDates.size()];
		
		return (Date[])workingNewYearsDates.toArray(finalNewYearsDates);
	}
	
	public static Date[] generateNewYearsDatesBetweenDates(Date startDate,
														   Date endDate)
	{
		final int startDateYear = startDate.getYear();
		final int endDateYear = endDate.getYear();
		// The number of years between the two years (inclusive).
		final int numYearsBetween = ((endDateYear - startDateYear) + 1);
		
		// Return an empty array if the start date is after the end date.
		if (numYearsBetween == 0)
			return new Date[] { };
		
		Date[] newYearsDatesBetween = new Date [numYearsBetween];
		
		for (int ii = 0; ii < numYearsBetween; ii++)
			newYearsDatesBetween[ii] = new Date((startDateYear + ii), 0, 1);
		
		return newYearsDatesBetween;
	}
	
	// TODO: This could also REALLY be improved.
	public static Date[] generateFirstOfTheMonthDatesBetweenDates(Date[] dateSet) {
		ArrayList workingFirstOfTheMonthDates = new ArrayList();
		
		// Find each date that has the day of 1.
		for (int ii = 0; ii < dateSet.length; ii++) {
			if (dateSet[ii].getDate() == 1)
				workingFirstOfTheMonthDates.add(dateSet[ii]);
		}
		
		Date[] finalFirstOfTheMonthDates =
			new Date [workingFirstOfTheMonthDates.size()];
		
		return (Date[])workingFirstOfTheMonthDates.toArray
			(finalFirstOfTheMonthDates);
	}
	
	public static Date[] generateFirstOfTheMonthDatesBetweenDates(Date startDate,
																  Date endDate)
	{
		Date[] allDaysBetweenDates = generateDaysBetweenDates(startDate, endDate);
		
		return generateFirstOfTheMonthDatesBetweenDates(allDaysBetweenDates);
	}
	
	//TODO: These should be sorted so the first format checked is the most likely format, etc...
	private static final DateFormat[] ACCEPTED_DATE_FORMATS = { 
		DateFormat.getDateInstance(DateFormat.FULL),
		new SimpleDateFormat("d-MM-yy"),
		new SimpleDateFormat("d-MM-yyyy"),
		new SimpleDateFormat("dd-MM-yy"),
		new SimpleDateFormat("dd-MM-yyyy"),
		new SimpleDateFormat("d/MM/yy"),
		new SimpleDateFormat("dd/MM/yy"),
		new SimpleDateFormat("d/MM/yyyy"),
		new SimpleDateFormat("dd/MMM/yyyy"),
		new SimpleDateFormat("d-MMM-yy"),
		new SimpleDateFormat("d-MMM-yyyy"),
		new SimpleDateFormat("dd-MMM-yy"),
		new SimpleDateFormat("dd-MMM-yyyy"),
		new SimpleDateFormat("d/MMM/yy"),
		new SimpleDateFormat("dd/MMM/yy"),
		new SimpleDateFormat("d/MMM/yyyy"),
		new SimpleDateFormat("dd/MMM/yyyy"),
		new SimpleDateFormat("yyyy"),
		DateFormat.getDateInstance(DateFormat.SHORT),
		DateFormat.getDateInstance(DateFormat.MEDIUM),
		DateFormat.getDateInstance(DateFormat.LONG),
	};
	
	public static Date parseDate(String dateString)
			throws ParseException {
		for (DateFormat format : ACCEPTED_DATE_FORMATS) {
			try {
				format.setLenient(false);
				Date date = format.parse(dateString);
				
				if (date.getYear() < 1900) {
					date.setYear(date.getYear() + 1900);
				}
				
				return date;
			}
			catch (ParseException dateParseException) {
				continue;
			}
		}
		
		String exceptionMessage = "Could not parse the field " +
								  "'" + dateString + "'" +
								  " as a date.";
		
		throw new ParseException(exceptionMessage, 0);
	}
	
	public static Date interpretObjectAsDate(Object object)
			throws ParseException{
		final String EMPTY_DATE_MESSAGE = "An empty date was found.";
		
		String objectAsString = object.toString();
		
		// TODO: These if's are a result of a "bug" in Prefuse's.
		// CSV Table Reader, which interprets a column as being an array type
		// if it has empty cells.
		if (object instanceof Date) {
			return (Date)object;
		}
		else if (object instanceof short[]) {
			short[] year = (short[])object;
			
			if (year.length == 0) {
				throw new ParseException(EMPTY_DATE_MESSAGE, 0);
			}
			else {
				objectAsString = "" + year[0];
			}
		}
		else if (object instanceof Short[]) {
			Short[] year = (Short[])object;
			
			if (year.length == 0) {
				throw new ParseException(EMPTY_DATE_MESSAGE, 0);
			}
			else {
				objectAsString = "" + year[0];
			}
		}
		else if (object instanceof int[]) {
			int[] year = (int[])object;
			
			if (year.length == 0) {
				throw new ParseException(EMPTY_DATE_MESSAGE, 0);
			}
			else {
				objectAsString = "" + year[0];
			}
		}
		else if (object instanceof Integer[]) {
			Integer[] year = (Integer[])object;
			
			if (year.length == 0) {
				throw new ParseException(EMPTY_DATE_MESSAGE, 0);
			}
			else {
				objectAsString = year.toString();
			}
		}
		else if (object instanceof long[]) {
			long[] year = (long[])object;
			
			if (year.length == 0) {
				throw new ParseException(EMPTY_DATE_MESSAGE, 0);
			}
			else {
				objectAsString = "" + year[0];
			}
		}
		else if (object instanceof Long[]) {
			Long[] year = (Long[])object;
			
			if (year.length == 0) {
				throw new ParseException(EMPTY_DATE_MESSAGE, 0);
			}
			else {
				objectAsString = "" + year[0];
			}
		}
		else if (object instanceof String[]) {
			String[] year = (String[])object;
			
			if (year.length == 0) {
				throw new ParseException(EMPTY_DATE_MESSAGE, 0);
			}
			else {
				objectAsString = year[0];
			}
		}
		
		return parseDate(objectAsString);
	}
	
//	private java.util.Date parseDate(String dateString) 
//		throws AlgorithmExecutionException {
//		for (DateFormat format : ACCEPTED_DATE_FORMATS) {
//			try {
//				format.setLenient(false);
//				java.util.Date date = format.parse(dateString);
//				//WE PARSED THE DATE SUCCESSFULLY (if we get to this point)!
//				//Finish up our processing and return the date.
//				
//				//TODO: Methinks this is a hack we should eliminate
//				if (date.getYear() < 1900)
//					date.setYear(date.getYear() + 1900);
//				java.sql.Date dateForSQL = new java.sql.Date(date.getTime());
//				return dateForSQL;
//			} catch (ParseException e) {
//				continue;
//			}
//		}
//		
//		//we could not parse the date with any of the accepted formats.
//		
//		String exceptionMessage = 
//			"Could not parse the field " + 
//			"'" + dateString + "'" +
//			" as a date. Aborting the algorithm.";
//		throw new AlgorithmExecutionException(exceptionMessage);
//	}
}
