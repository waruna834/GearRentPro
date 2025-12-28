package com.gearrentpro.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {
    
    /**
     * Check if a date is weekend (Saturday or Sunday)
     */
    public static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || 
               date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
    
    /**
     * Calculate number of days between two dates (inclusive)
     */
    public static int getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    /**
     * Get all weekend days in date range
     */
    public static int countWeekendDays(LocalDate startDate, LocalDate endDate) {
        int weekendDays = 0;
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            if (isWeekend(current)) {
                weekendDays++;
            }
            current = current.plusDays(1);
        }
        
        return weekendDays;
    }
    
    /**
     * Get all weekday count in date range
     */
    public static int countWeekdayDays(LocalDate startDate, LocalDate endDate) {
        int totalDays = getDaysBetween(startDate, endDate);
        int weekendDays = countWeekendDays(startDate, endDate);
        return totalDays - weekendDays;
    }
    
    /**
     * Check if date ranges overlap
     */
    public static boolean doRangesOverlap(LocalDate start1, LocalDate end1, 
                                         LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !end2.isBefore(start1);
    }
}