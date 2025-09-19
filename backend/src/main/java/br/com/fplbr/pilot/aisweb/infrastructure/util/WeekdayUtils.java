package br.com.fplbr.pilot.aisweb.infrastructure.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Utility class for working with weekdays and date calculations.
 */
public class WeekdayUtils {
    
    // Locale for day names (using Brazilian Portuguese as default)
    private static final Locale LOCALE = new Locale("pt", "BR");
    
    /**
     * Gets the day of week name in the specified style.
     *
     * @param dayOfWeek the day of week (1-7, where 1 is Monday and 7 is Sunday)
     * @param style the text style (e.g., TextStyle.FULL, TextStyle.SHORT)
     * @return the formatted day name
     */
    public static String getDayName(int dayOfWeek, TextStyle style) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 (Monday) and 7 (Sunday)");
        }
        
        // Convert to DayOfWeek enum (1=Monday, 7=Sunday)
        DayOfWeek day = DayOfWeek.of(dayOfWeek);
        return day.getDisplayName(style, LOCALE);
    }
    
    /**
     * Gets the day of week number (1-7) from a day name or abbreviation.
     *
     * @param dayName the day name or abbreviation (case insensitive)
     * @return the day of week number (1=Monday, 7=Sunday), or -1 if not found
     */
    public static int getDayNumber(String dayName) {
        if (dayName == null || dayName.trim().isEmpty()) {
            return -1;
        }
        
        String normalizedDay = dayName.trim().toLowerCase(LOCALE);
        
        // Check full day names
        for (DayOfWeek day : DayOfWeek.values()) {
            String fullName = day.getDisplayName(TextStyle.FULL, LOCALE).toLowerCase(LOCALE);
            String shortName = day.getDisplayName(TextStyle.SHORT, LOCALE).toLowerCase(LOCALE);
            
            if (fullName.equals(normalizedDay) || shortName.equals(normalizedDay)) {
                return day.getValue();
            }
        }
        
        // Check numeric values (1-7)
        try {
            int dayNum = Integer.parseInt(normalizedDay);
            if (dayNum >= 1 && dayNum <= 7) {
                return dayNum;
            }
        } catch (NumberFormatException e) {
            // Not a number, continue checking
        }
        
        return -1; // Not found
    }
    
    /**
     * Checks if a given date falls on a weekend (Saturday or Sunday).
     *
     * @param date the date to check
     * @return true if the date is a weekend day, false otherwise
     */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    /**
     * Checks if a given date falls on a weekday (Monday to Friday).
     *
     * @param date the date to check
     * @return true if the date is a weekday, false otherwise
     */
    public static boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }
    
    /**
     * Gets the next occurrence of a specific day of the week from the given date.
     *
     * @param date the reference date
     * @param targetDay the target day of week (1=Monday, 7=Sunday)
     * @return the date of the next occurrence of the target day
     */
    public static LocalDate getNextDayOfWeek(LocalDate date, int targetDay) {
        if (targetDay < 1 || targetDay > 7) {
            throw new IllegalArgumentException("Target day must be between 1 (Monday) and 7 (Sunday)");
        }
        
        int currentDay = date.getDayOfWeek().getValue();
        int daysToAdd = (targetDay - currentDay + 7) % 7;
        return date.plusDays(daysToAdd == 0 ? 7 : daysToAdd);
    }
    
    /**
     * Parses a string representing days of the week (e.g., "1,3,5" or "seg,qua,sex") 
     * into a set of day numbers (1-7).
     *
     * @param daysString the string containing day representations
     * @return a set of day numbers (1-7)
     */
    public static Set<Integer> parseDaysOfWeek(String daysString) {
        Set<Integer> days = new HashSet<>();
        
        if (daysString == null || daysString.trim().isEmpty()) {
            return days;
        }
        
        // Split by common separators (comma, space, semicolon, etc.)
        String[] parts = daysString.split("[,;\s]+");
        
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                int dayNum = getDayNumber(part);
                if (dayNum > 0) {
                    days.add(dayNum);
                }
            }
        }
        
        return days;
    }
    
    /**
     * Formats a set of day numbers into a human-readable string.
     *
     * @param days the set of day numbers (1-7)
     * @param style the text style for day names
     * @return a formatted string of day names
     */
    public static String formatDaysOfWeek(Set<Integer> days, TextStyle style) {
        if (days == null || days.isEmpty()) {
            return "";
        }
        
        // Convert to array and sort
        Integer[] dayArray = days.toArray(new Integer[0]);
        Arrays.sort(dayArray);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dayArray.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getDayName(dayArray[i], style));
        }
        
        return sb.toString();
    }
}
