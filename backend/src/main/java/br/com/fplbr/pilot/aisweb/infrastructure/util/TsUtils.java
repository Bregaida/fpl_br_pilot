package br.com.fplbr.pilot.aisweb.infrastructure.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Utility class for timestamp operations.
 */
public class TsUtils {
    
    private static final DateTimeFormatter TS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    /**
     * Formats a LocalDateTime to ISO 8601 UTC string.
     *
     * @param dateTime the LocalDateTime to format
     * @return the formatted string in ISO 8601 UTC format
     */
    public static String formatToIsoUtc(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(TS_FORMATTER);
    }
    
    /**
     * Parses an ISO 8601 UTC string to LocalDateTime.
     *
     * @param isoString the ISO 8601 UTC string to parse
     * @return the parsed LocalDateTime, or null if the input is null or empty
     */
    public static LocalDateTime parseFromIsoUtc(String isoString) {
        if (isoString == null || isoString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(isoString, TS_FORMATTER);
    }
    
    /**
     * Gets the current timestamp in ISO 8601 UTC format.
     *
     * @return the current timestamp as an ISO 8601 UTC string
     */
    public static String nowAsIsoUtc() {
        return formatToIsoUtc(LocalDateTime.now());
    }
    
    /**
     * Parses a timestamp string in the format "{ts 'yyyy-MM-dd HH:mm:ss'}" to LocalDateTime.
     *
     * @param tsString the timestamp string to parse
     * @return the parsed LocalDateTime, or null if the input is null or empty
     */
    public static LocalDateTime parseTs(String tsString) {
        if (tsString == null || tsString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Remove the {ts '...'} wrapper if present
            String cleanTs = tsString.trim();
            if (cleanTs.startsWith("{ts '") && cleanTs.endsWith("'}")) {
                cleanTs = cleanTs.substring(5, cleanTs.length() - 2).trim();
            }
            
            // Handle different timestamp formats
            if (cleanTs.contains(" ") && cleanTs.contains(":")) {
                // Format: yyyy-MM-dd HH:mm:ss or similar
                if (cleanTs.contains(".")) {
                    // With milliseconds
                    return LocalDateTime.parse(cleanTs, 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                } else {
                    // Without milliseconds
                    return LocalDateTime.parse(cleanTs, 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
            } else if (cleanTs.contains("T")) {
                // ISO-8601 format
                return LocalDateTime.parse(cleanTs, DateTimeFormatter.ISO_DATE_TIME);
            } else {
                // Date only
                return LocalDate.parse(cleanTs, DateTimeFormatter.ISO_DATE).atStartOfDay();
            }
        } catch (Exception e) {
            // If parsing fails, try to extract a date from the string
            try {
                // Try to find a date pattern in the string
                java.util.regex.Matcher matcher = 
                    Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(tsString);
                if (matcher.find()) {
                    String dateStr = matcher.group();
                    return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE).atStartOfDay();
                }
                
                // Try another common format (dd/MM/yyyy)
                matcher = Pattern.compile("\\d{2}/\\d{2}/\\d{4}").matcher(tsString);
                if (matcher.find()) {
                    String dateStr = matcher.group();
                    return LocalDate.parse(dateStr, 
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
                }
                
                return null;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
