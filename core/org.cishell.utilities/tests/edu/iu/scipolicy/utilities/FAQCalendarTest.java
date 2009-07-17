package edu.iu.scipolicy.utilities;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.cishell.utilities.FAQCalendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FAQCalendarTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDiffDayPeriods() {
		FAQCalendar startCalendarDate = new FAQCalendar(1984, 0, 1);
		Calendar endCalendarDate = new GregorianCalendar(1984, 0, 25);
		
		if (startCalendarDate.diffDayPeriods(endCalendarDate) != 24)
			fail();
	}
}
