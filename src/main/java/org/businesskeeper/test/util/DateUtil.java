package org.businesskeeper.test.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class DateUtil {
	public static LocalDate getPreviousValidDate(LocalDate date) {
		LocalDate auxLocalDate = date.minusDays(1);
		while (DayOfWeek.SATURDAY.equals(DayOfWeek.of(auxLocalDate.get(ChronoField.DAY_OF_WEEK))) || DayOfWeek.SUNDAY.equals(DayOfWeek.of(auxLocalDate.get(ChronoField.DAY_OF_WEEK)))) {
			auxLocalDate = auxLocalDate.minusDays(1);
		}
		return auxLocalDate;
	}
}
