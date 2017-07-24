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

import com.epam.library.controller.command.Command;

public class StAXParser extends AbstractParser {

	@Override
	public Map<String, Command> getCommands(String xmlPath) {
		Map<String, Command> mapComm = new HashMap<String, Command>();
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();		
		try {
			InputStream input = new FileInputStream(xmlPath);
			XMLStreamReader reader = inputFactory.createXMLStreamReader(input);
			
			while(reader.hasNext()){
				int type = reader.next();
				if(type == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("command")){
					System.out.println("What is here? - "+type + " - "+reader.getAttributeValue(null, "name"));
				}
				if(type == XMLStreamConstants.CHARACTERS ){
					String commandPath = reader.getText().trim();					
					if (commandPath.isEmpty()) {
						continue;
					}
					System.out.println(commandPath);
										
				}
			}
			
			
			
			
		} catch (FileNotFoundException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

}
