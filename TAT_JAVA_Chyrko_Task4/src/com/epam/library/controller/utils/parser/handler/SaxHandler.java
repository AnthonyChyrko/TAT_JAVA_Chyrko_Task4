package com.epam.library.controller.utils.parser.handler;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler{
	private Map<String, String> commandMap = new HashMap<String, String>();
	private String commandKey;
	private String commandPath;
	
	private StringBuilder text;
	public Map<String, String> getCommandMap(){
		return commandMap;
	}
	
	public void startDocument() throws SAXException {
		System.out.println("Parsing started.");
	}
	public void endDocument() throws SAXException {
		System.out.println("Parsing ended.");
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		text = new StringBuilder();

		if (qName.equals("command")){
			commandKey = attributes.getValue("name");			
		}		
	}
	
	public void characters(char[] buffer, int start, int length) {
		text.append(buffer, start, length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equals("path")){
			commandPath = ""+text;
		}else{
			commandMap.put(commandKey, commandPath);
		}	
	}
	
	public void warning(SAXParseException exception) {
		System.err.println("WARNING: line " + exception.getLineNumber() + ": " + exception.getMessage());
	}
	
	public void error(SAXParseException exception) {
		System.err.println("ERROR: line " + exception.getLineNumber() + ": " + exception.getMessage());
	}
		
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("FATAL: line " + exception.getLineNumber() + ": " + exception.getMessage());
		throw (exception);
	}
	
	
}
