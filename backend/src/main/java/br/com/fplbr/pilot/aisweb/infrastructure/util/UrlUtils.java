package br.com.fplbr.pilot.aisweb.infrastructure.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for URL encoding and decoding operations.
 */
public class UrlUtils {
    
    /**
     * Decodes a URL-encoded string using UTF-8 encoding.
     * 
     * @param url The URL-encoded string to decode
     * @return The decoded string, or the original string if an error occurs
     */
    public static String decode(String url) {
        if (url == null) {
            return null;
        }
        
        try {
            return URLDecoder.decode(url, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Should not happen with UTF-8
            return url;
        }
    }
    
    /**
     * URL-encodes a string using UTF-8 encoding.
     * 
     * @param value The string to encode
     * @return The URL-encoded string, or the original string if an error occurs
     */
    public static String encode(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                    .replace("+", "%20"); // Replace + with %20 for better URL compatibility
        } catch (UnsupportedEncodingException e) {
            // Should not happen with UTF-8
            return value;
        }
    }
    
    /**
     * Extracts a query parameter value from a URL.
     * 
     * @param url The URL to extract the parameter from
     * @param paramName The name of the parameter to extract
     * @return The parameter value, or null if not found
     */
    public static String getQueryParam(String url, String paramName) {
        if (url == null || paramName == null) {
            return null;
        }
        
        String[] pairs = url.split("[?&]");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && paramName.equals(pair.substring(0, idx))) {
                return decode(pair.substring(idx + 1));
            }
        }
        return null;
    }
    
    /**
     * Extracts the file name from a URL.
     * 
     * @param url The URL to extract the file name from
     * @return The file name, or an empty string if not found
     */
    public static String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        
        // Remove query parameters if present
        String path = url.split("\\?")[0];
        
        // Get the last segment of the path
        String[] segments = path.split("/");
        return segments.length > 0 ? segments[segments.length - 1] : "";
    }
    
    /**
     * Extracts the file extension from a URL or file name.
     * 
     * @param urlOrFileName The URL or file name to extract the extension from
     * @return The file extension (without the dot), or an empty string if not found
     */
    public static String getFileExtension(String urlOrFileName) {
        if (urlOrFileName == null || urlOrFileName.isEmpty()) {
            return "";
        }
        
        // Remove query parameters if present
        String fileName = urlOrFileName.split("\\?")[0];
        
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }
    
    /**
     * Decodes HTML entities and CDATA sections from a string.
     * 
     * @param input the input string to decode
     * @return the decoded string
     */
    public static String decodeCdataAndCut(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Remove CDATA markers if present
        String result = input.replace("<![CDATA[", "").replace("]]>", "");
        
        // Decode HTML entities
        result = result.replace("&amp;", "&")
                      .replace("&lt;", "<")
                      .replace("&gt;", ">")
                      .replace("&quot;", "\"")
                      .replace("&apos;", "'");
        
        return result.trim();
    }
    
    /**
     * Extracts the public ID from a URL.
     * 
     * @param url the URL to extract from
     * @return the public ID, or null if not found
     */
    public static String extractPublicId(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        // Try to extract from common URL patterns
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("public_id=([^&]+)");
        java.util.regex.Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return decode(matcher.group(1));
        }
        
        // Try to extract from the last part of the path
        String[] parts = url.split("/");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            // Remove query parameters and fragments
            lastPart = lastPart.split("[?#]")[0];
            // Remove file extension if present
            int dotIndex = lastPart.lastIndexOf('.');
            return dotIndex > 0 ? lastPart.substring(0, dotIndex) : lastPart;
        }
        
        return null;
    }
    
    /**
     * Extracts the 'p' query parameter from a URL.
     * 
     * @param url the URL to extract from
     * @return the value of the 'p' parameter, or null if not found
     */
    public static String extractQueryP(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        // Try to extract the 'p' parameter
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[?&]p=([^&]+)");
        java.util.regex.Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return decode(matcher.group(1));
        }
        
        return null;
    }
    
    /**
     * Extracts the file extension from a URL.
     * 
     * @param url the URL to extract the extension from
     * @return the file extension (without the dot), or null if not found
     */
    public static String extractExtension(String url) {
        return getFileExtension(url);
    }
}
