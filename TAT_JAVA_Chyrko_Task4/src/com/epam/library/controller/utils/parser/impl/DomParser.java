package com.epam.library.controller.utils.parser.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.epam.library.controller.command.Command;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomParser extends AbstractParser {

	@Override
	public Map<String, Command> getCommands(String xmlPath) {
		Map<String, Command> mapComm = new HashMap<String, Command>();
		
		DOMParser parser = new DOMParser();
		try {
			parser.parse(xmlPath);
		} catch (SAXException | IOException e) {
			System.out.println("Не можем распарсить?");
			e.printStackTrace();
		}
		Document document = parser.getDocument();
		Element root = document.getDocumentElement();
		NodeList commandNodeList = root.getElementsByTagName("command");
		for (int i = 0; i < commandNodeList.getLength(); i++) {
			Node node = commandNodeList.item(i);
			Element el = (Element) node;
			String key = el.getAttribute("name");
			String path = el.getTextContent().trim();
			addCommand(mapComm, key.toUpperCase(), path);
		}
		
		return mapComm;
	}

}
