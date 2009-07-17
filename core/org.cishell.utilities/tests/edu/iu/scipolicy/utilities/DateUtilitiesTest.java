package edu.iu.scipolicy.utilities;

import static org.junit.Assert.fail;

import java.util.Date;

import org.cishell.utilities.DateUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateUtilitiesTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateDaysBetweenSameDates() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = startDate;
		
		Date[] datesBetween =
			DateUtilities.generateDaysBetweenDates(startDate, endDate);
		
		if ((datesBetween.length != 1) || (!datesBetween[0].equals(startDate)))
			fail();
	}
	
	@Test
	public void testGenerateDaysBetweenReversedDates() {
		Date startDate = new Date(1984, 0, 2);
		Date endDate = new Date(1984, 0, 1);
		
		Date[] datesBetween =
			DateUtilities.generateDaysBetweenDates(startDate, endDate);
		
		if ((datesBetween == null) || (datesBetween.length != 0))
			fail();
	}
	
	@Test
	public void testGenerateDaysBetweenDates() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date(1984, 0, 25);
		
		Date[] datesBetween =
			DateUtilities.generateDaysBetweenDates(startDate, endDate);
		
		if ((datesBetween == null) || (datesBetween.length == 0))
			fail();
		
		for (int ii = 0; ii < datesBetween.length; ii++) {
			int expectedDay = (ii + 1);
			
			if ((datesBetween[ii].getYear() != startDate.getYear()) ||
				(datesBetween[ii].getMonth() != startDate.getMonth()) ||
				(datesBetween[ii].getDate() != expectedDay))
			{
				fail();
			}
		}
	}

	@Test
	public void testCalculateDaysBetweenDateArray() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date(1984, 0, 2);
		Date[] dateArray = new Date[] { startDate, endDate };
		
		if (DateUtilities.calculateDaysBetween(dateArray) != 2)
			fail();
	}

	@Test
	public void testCalculateDaysBetweenDateDate() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date(1984, 0, 25);
		
		if (DateUtilities.calculateDaysBetween(startDate, endDate) != 24)
			fail();
	}

	@Test
	public void testCalculateMonthsBetween() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date(1984, 1, 1);
		
		if (DateUtilities.calculateMonthsBetween(startDate, endDate) != 1)
			fail();
	}

	@Test
	public void testGetNewYearsDatesFromDateSet() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date (2000, 2, 15);
		
		Date[] datesBetween =
			DateUtilities.generateDaysBetweenDates(startDate, endDate);
		
		Date[] newYearsDates =
			DateUtilities.getNewYearsDatesFromDateSet(datesBetween);
		
		for (int ii = 0; ii < newYearsDates.length; ii++) {
			if ((newYearsDates[ii].getYear() != (1984 + ii)) ||
				(newYearsDates[ii].getMonth() != 0) ||
				(newYearsDates[ii].getDate() != 1))
			{
				fail();
			}
		}
	}

	@Test
	public void testGenerateNewYearsDatesBetweenDates() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date(2000, 2, 15);
		
		Date[] newYearsDates =
			DateUtilities.generateNewYearsDatesBetweenDates(startDate, endDate);
		
		for (int ii = 0; ii < newYearsDates.length; ii++) {
			if ((newYearsDates[ii].getYear() != (1984 + ii)) ||
				(newYearsDates[ii].getMonth() != 0) ||
				(newYearsDates[ii].getDate() != 1))
			{
				fail();
			}
		}
	}

	@Test
	public void testGenerateFirstOfTheMonthDatesBetweenDatesDateDate() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date(1984, 11, 31);
		
		Date[] datesBetween =
			DateUtilities.generateDaysBetweenDates(startDate, endDate);
		
		Date[] firstOfTheMonthDates =
			DateUtilities.generateFirstOfTheMonthDatesBetweenDates
				(startDate, endDate);
		
		for (int ii = 0; ii < firstOfTheMonthDates.length; ii++) {
			if ((firstOfTheMonthDates[ii].getYear() != startDate.getYear()) ||
				(firstOfTheMonthDates[ii].getMonth() != ii) ||
				(firstOfTheMonthDates[ii].getDate() != 1))
			{
				fail();
			}
		}
	}

	@Test
	public void testGenerateFirstOfTheMonthDatesBetweenDatesDateArray() {
		Date startDate = new Date(1984, 0, 1);
		Date endDate = new Date(1984, 11, 31);
		
		Date[] datesBetween =
			DateUtilities.generateDaysBetweenDates(startDate, endDate);
		
		Date[] firstOfTheMonthDates =
			DateUtilities.generateFirstOfTheMonthDatesBetweenDates
				(datesBetween);
		
		for (int ii = 0; ii < firstOfTheMonthDates.length; ii++) {
			if ((firstOfTheMonthDates[ii].getYear() != startDate.getYear()) ||
				(firstOfTheMonthDates[ii].getMonth() != ii) ||
				(firstOfTheMonthDates[ii].getDate() != 1))
			{
				fail();
			}
		}
	}
}
