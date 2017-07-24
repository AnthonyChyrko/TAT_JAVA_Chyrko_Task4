package com.epam.library.controller.utils.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

import com.epam.library.controller.command.Command;

public class StAXParser extends AbstractParser {
	private final static Logger logger = Logger.getLogger(StAXParser.class);
	@Override
	public Map<String, Command> getCommands(String xmlPath) {
		Map<String, Command> mapComm = new HashMap<String, Command>();
		String commandKey = null;
		String commandPath = null;
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();		
		try {
			InputStream input = new FileInputStream(xmlPath);
			XMLStreamReader reader = inputFactory.createXMLStreamReader(input);
			
			while(reader.hasNext()){
				int type = reader.next();
				if(type == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("command")){					
					commandKey = reader.getAttributeValue(null, "name");
				}
				if(type == XMLStreamConstants.CHARACTERS ){
					commandPath = reader.getText().trim();					
					if (commandPath.isEmpty() || commandPath==null) {
						continue;
					}
//					System.out.println(commandKey +" - "+ commandPath);
					addCommand(mapComm, commandKey.toUpperCase(), commandPath);
				}			
			}
			
		} catch (FileNotFoundException | XMLStreamException e) {
			logger.error("Can't parse XML!",e);
		}		
		return mapComm;
	}

}
