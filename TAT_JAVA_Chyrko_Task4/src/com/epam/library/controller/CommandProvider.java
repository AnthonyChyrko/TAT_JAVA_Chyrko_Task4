package com.epam.library.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.epam.library.controller.command.Command;
import com.epam.library.controller.utils.parser.Parser;
import com.epam.library.controller.utils.parser.factory.ParserFactory;

public class CommandProvider {
	private final static Logger logger = Logger.getLogger(CommandProvider.class);
	
	private Map<String, Command> repository = new HashMap<>();
	private static final String PATH_TO_XML = ".\\src\\commandList.xml";

	CommandProvider() {
		ParserFactory factory = ParserFactory.getInstance();
		//TODO SWITCH PARSERS YOU CAN HERE!
//		Parser parser = factory.getDomParser();
//		Parser parser = factory.getSaxParser();
		Parser parser = factory.getStaxParser();
		repository = parser.getCommands(PATH_TO_XML);
	}
	
	Command getCommand(String name){
		Command command = repository.get(name);
		if(command == null){
			command = repository.get("Wrong_Request");
		}		
		return command;		
	}
}
