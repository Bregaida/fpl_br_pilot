package br.com.fplbr.pilot.aisweb.infrastructure.util;

/**
 * Utility class for geographical calculations and validations.
 */
public class GeoUtils {
    
    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Validates if a latitude value is within the valid range (-90 to 90 degrees).
     *
     * @param latitude the latitude to validate
     * @return true if the latitude is valid, false otherwise
     */
    public static boolean isValidLatitude(double latitude) {
        return latitude >= -90.0 && latitude <= 90.0;
    }
    
    /**
     * Validates if a longitude value is within the valid range (-180 to 180 degrees).
     *
     * @param longitude the longitude to validate
     * @return true if the longitude is valid, false otherwise
     */
    public static boolean isValidLongitude(double longitude) {
        return longitude >= -180.0 && longitude <= 180.0;
    }
    
    /**
     * Converts degrees to radians.
     *
     * @param degrees the angle in degrees
     * @return the angle in radians
     */
    public static double toRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }
    
    /**
     * Calculates the distance between two points on Earth using the Haversine formula.
     *
     * @param lat1 latitude of the first point in degrees
     * @param lon1 longitude of the first point in degrees
     * @param lat2 latitude of the second point in degrees
     * @param lon2 longitude of the second point in degrees
     * @return the distance between the two points in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = toRadians(lat1);
        double lon1Rad = toRadians(lon1);
        double lat2Rad = toRadians(lat2);
        double lon2Rad = toRadians(lon2);
        
        // Calculate the differences
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Calculate the distance in kilometers
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Parses a coordinate string in the format "DDMMSS[NSEW]" to decimal degrees.
     *
     * @param coordString the coordinate string to parse
     * @return the coordinate in decimal degrees
     * @throws IllegalArgumentException if the coordinate string is invalid
     */
    public static double parseCoordinate(String coordString) {
        if (coordString == null || coordString.trim().isEmpty()) {
            throw new IllegalArgumentException("Coordinate string cannot be null or empty");
        }
        
        coordString = coordString.trim().toUpperCase();
        
        // Extract direction (last character)
        char direction = coordString.charAt(coordString.length() - 1);
        if (direction != 'N' && direction != 'S' && direction != 'E' && direction != 'W') {
            throw new IllegalArgumentException("Invalid direction in coordinate string: " + coordString);
        }
        
        // Extract the numeric part
        String numericPart = coordString.substring(0, coordString.length() - 1);
        
        try {
            double degrees, minutes, seconds = 0;
            
            // Parse based on the length of the string
            if (numericPart.length() >= 4) {
                // Format: DDMM or DDMMSS
                degrees = Double.parseDouble(numericPart.substring(0, 2));
                minutes = Double.parseDouble(numericPart.substring(2, Math.min(4, numericPart.length())));
                
                if (numericPart.length() >= 6) {
                    // Format: DDMMSS
                    seconds = Double.parseDouble(numericPart.substring(4, 6));
                    
                    // Handle fractional seconds if present
                    if (numericPart.length() > 6) {
                        seconds += Double.parseDouble("0." + numericPart.substring(6));
                    }
                }
            } else if (numericPart.length() == 2) {
                // Format: DD
                degrees = Double.parseDouble(numericPart);
                minutes = 0;
            } else {
                throw new IllegalArgumentException("Invalid coordinate format: " + coordString);
            }
            
            // Calculate decimal degrees
            double decimalDegrees = degrees + (minutes / 60.0) + (seconds / 3600.0);
            
            // Apply direction (negative for S and W)
            if (direction == 'S' || direction == 'W') {
                decimalDegrees = -decimalDegrees;
            }
            
            return decimalDegrees;
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric format in coordinate: " + coordString, e);
        }
    }
    
    /**
     * Splits a coordinate string containing latitude and longitude into separate values.
     * 
     * @param geoString the coordinate string to split (format: "lat,lng" or "lat lng")
     * @return array with [latitude, longitude] in decimal degrees
     */
    public static double[] splitLatLng(String geoString) {
        if (geoString == null || geoString.trim().isEmpty()) {
            return new double[]{0.0, 0.0};
        }
        
        // Try comma separator first
        String[] parts = geoString.split(",");
        if (parts.length == 2) {
            try {
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                return new double[]{lat, lng};
            } catch (NumberFormatException e) {
                // Fall through to space separator
            }
        }
        
        // Try space separator
        parts = geoString.split("\\s+");
        if (parts.length == 2) {
            try {
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                return new double[]{lat, lng};
            } catch (NumberFormatException e) {
                // Return default values
            }
        }
        
        return new double[]{0.0, 0.0};
    }
}
