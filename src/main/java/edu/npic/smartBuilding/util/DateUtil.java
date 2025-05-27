package edu.npic.smartBuilding.util;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class DateUtil {
    public static List<String> calculateDuration(LocalDate dateFrom, LocalDate dateEnd) {
        if (dateFrom == null || dateEnd == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (dateEnd.isBefore(dateFrom)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        List<String> result = new ArrayList<>();
        Period period = Period.between(dateFrom, dateEnd);

        // If duration is within 7 days
        if (period.getDays() <= 7 && period.getMonths() == 0 && period.getYears() == 0) {
            LocalDate date = dateFrom;
            while (!date.isAfter(dateEnd)) {
                DayOfWeek day = date.getDayOfWeek();
                result.add(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                date = date.plusDays(1);
            }
        }
        // If duration is more than 7 days but within a month
        else if (period.getMonths() == 0 && period.getYears() == 0) {
            LocalDate date = dateFrom;
            while (!date.isAfter(dateEnd)) {
                String formattedDate = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        + "-" + date.getDayOfMonth();
                result.add(formattedDate);
                date = date.plusDays(1);
            }
        }
        // If duration spans multiple months but within a year
        else if (period.getYears() == 0) {
            LocalDate date = dateFrom;
            while (!date.isAfter(dateEnd)) {
                String monthName = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                if (!result.contains(monthName)) {
                    result.add(monthName);
                }
                date = date.plusMonths(1);
            }
        }
        // If duration spans multiple years
        else {
            LocalDate date = dateFrom;
            while (!date.isAfter(dateEnd)) {
                String year = String.valueOf(date.getYear());
                if (!result.contains(year)) {
                    result.add(year);
                }
                date = date.plusYears(1);
            }
        }

        return result;
    }



}
