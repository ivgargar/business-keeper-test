package org.businesskeeper.test.util;

import static org.junit.Assert.assertNotEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

import org.junit.Test;

public class DateUtilTest {
	@Test
	public void testGetLastFiveValidDaysFromFriday() {
		LocalDate now = LocalDate.parse("2018-02-16");
		
		for (int i = 0; i < 5; i++) {
			now = DateUtil.getPreviousValidDate(now);
			
			assertNotEquals(DayOfWeek.SATURDAY, DayOfWeek.of(now.get(ChronoField.DAY_OF_WEEK)));
			assertNotEquals(DayOfWeek.SUNDAY, DayOfWeek.of(now.get(ChronoField.DAY_OF_WEEK)));
		}
	}
	
	@Test
	public void testGetLastFiveValidDaysFromSaturday() {
		LocalDate now = LocalDate.parse("2018-02-17");
		
		for (int i = 0; i < 5; i++) {
			now = DateUtil.getPreviousValidDate(now);
			
			assertNotEquals(DayOfWeek.SATURDAY, DayOfWeek.of(now.get(ChronoField.DAY_OF_WEEK)));
			assertNotEquals(DayOfWeek.SUNDAY, DayOfWeek.of(now.get(ChronoField.DAY_OF_WEEK)));
		}
	}
	
	@Test
	public void testGetLastFiveValidDaysFromSunday() {
		LocalDate now = LocalDate.parse("2018-02-18");
		
		for (int i = 0; i < 5; i++) {
			now = DateUtil.getPreviousValidDate(now);
			
			assertNotEquals(DayOfWeek.SATURDAY, DayOfWeek.of(now.get(ChronoField.DAY_OF_WEEK)));
			assertNotEquals(DayOfWeek.SUNDAY, DayOfWeek.of(now.get(ChronoField.DAY_OF_WEEK)));
		}
	}
}
