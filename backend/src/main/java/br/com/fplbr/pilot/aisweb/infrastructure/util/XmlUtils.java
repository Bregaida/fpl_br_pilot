package br.com.fplbr.pilot.aisweb.infrastructure.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * Utility class for XML parsing operations.
 */
public class XmlUtils {
    
    /**
     * Parses an XML string into a Document object.
     *
     * @param xmlString the XML string to parse
     * @return the parsed Document object
     * @throws Exception if there's an error parsing the XML
     */
    public static Document parseXml(String xmlString) throws Exception {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new IllegalArgumentException("XML string cannot be null or empty");
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlString)));
    }
    
    /**
     * Gets the text content of a child element with the specified tag name.
     *
     * @param parent the parent element
     * @param tagName the tag name of the child element
     * @return the text content of the first matching child element, or null if not found
     */
    public static String getChildElementText(Element parent, String tagName) {
        if (parent == null || tagName == null) {
            return null;
        }
        
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0) {
            return null;
        }
        
        Node node = nodeList.item(0);
        return node.getTextContent().trim();
    }
    
    /**
     * Gets an integer value from a child element with the specified tag name.
     *
     * @param parent the parent element
     * @param tagName the tag name of the child element
     * @param defaultValue the default value to return if the element is not found or not a valid integer
     * @return the integer value, or the default value if not found or invalid
     */
    public static int getChildElementInt(Element parent, String tagName, int defaultValue) {
        String text = getChildElementText(parent, tagName);
        if (text == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets a double value from a child element with the specified tag name.
     *
     * @param parent the parent element
     * @param tagName the tag name of the child element
     * @param defaultValue the default value to return if the element is not found or not a valid double
     * @return the double value, or the default value if not found or invalid
     */
    public static double getChildElementDouble(Element parent, String tagName, double defaultValue) {
        String text = getChildElementText(parent, tagName);
        if (text == null) {
            return defaultValue;
        }
        
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets a boolean value from a child element with the specified tag name.
     *
     * @param parent the parent element
     * @param tagName the tag name of the child element
     * @param defaultValue the default value to return if the element is not found
     * @return the boolean value, or the default value if not found
     */
    public static boolean getChildElementBoolean(Element parent, String tagName, boolean defaultValue) {
        String text = getChildElementText(parent, tagName);
        if (text == null) {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(text);
    }
    
    /**
     * Creates a new Document from an XML string.
     *
     * @param xmlString the XML string to parse
     * @return the parsed Document
     * @throws Exception if there's an error parsing the XML
     */
    public static Document createDocument(String xmlString) throws Exception {
        return parseXml(xmlString);
    }
    
    /**
     * Gets a NodeList of elements with the given tag name from a parent element.
     *
     * @param parent the parent element
     * @param tagName the tag name to search for
     * @return a NodeList of matching elements
     */
    public static NodeList nl(String tagName, Element parent) {
        if (parent == null || tagName == null) {
            return null;
        }
        return parent.getElementsByTagName(tagName);
    }
    
    /**
     * Gets the trimmed text content of an element.
     *
     * @param element the element to get text from
     * @return the trimmed text content, or null if the element is null
     */
    public static String textTrim(Element element) {
        return element != null && element.getTextContent() != null ? 
               element.getTextContent().trim() : null;
    }
    
    /**
     * Gets the value of an attribute from an element.
     *
     * @param element the element to get the attribute from
     * @param attrName the name of the attribute
     * @return the attribute value, or null if the attribute doesn't exist or element is null
     */
    public static String attr(Element element, String attrName) {
        if (element == null || attrName == null || !element.hasAttribute(attrName)) {
            return null;
        }
        return element.getAttribute(attrName);
    }
}
