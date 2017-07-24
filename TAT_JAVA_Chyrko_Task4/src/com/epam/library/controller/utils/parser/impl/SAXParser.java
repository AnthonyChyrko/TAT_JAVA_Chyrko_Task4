package com.epam.library.controller.utils.parser.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.epam.library.controller.command.Command;
import com.epam.library.controller.utils.parser.handler.SaxHandler;

public class SAXParser extends AbstractParser{
	private final static Logger logger = Logger.getLogger(SAXParser.class);
	SaxHandler handler;
	XMLReader reader;
	@Override
	public Map<String, Command> getCommands(String xmlPath) {
		Map<String, Command> mapComm = new HashMap<String, Command>();
		handler = new SaxHandler();
		try {
			reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(xmlPath));
			// включение проверки действительности
			reader.setFeature("http://xml.org/sax/features/validation", true);
			// включение обработки пространств имен
			reader.setFeature("http://xml.org/sax/features/namespaces", true);
			// включение канонизации строк
			reader.setFeature("http://xml.org/sax/features/string-interning", true);
			// отключение обработки схем
			reader.setFeature("http://apache.org/xml/features/validation/schema", false);			

			for (Map.Entry entry : handler.getCommandMap().entrySet()) {
			    addCommand(mapComm, entry.getKey().toString().toUpperCase(), ""+entry.getValue());
			}
			
		} catch (SAXException e) {
			logger.error("SAXException!",e);
		} catch (IOException e) {
			logger.error("IOException!",e);
		}
		return mapComm;
	}

}
