package com.epam.library.controller.utils.parser.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.epam.library.controller.command.Command;

import com.epam.library.controller.utils.parser.Parser;

public abstract class AbstractParser implements Parser{
	private final static Logger logger = Logger.getLogger(AbstractParser.class);
	
	Command command;
	Class commandClass = null;
	
	void addCommand(Map<String, Command> commandMap, String commandKey, String commandPath){
		try {
			commandClass = Class.forName(commandPath);
		} catch (ClassNotFoundException e) {
			logger.error("Can't find a command path!",e);
		}
		
		try {
			command = (Command) commandClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Can't create command!",e);
		}
		commandMap.put(commandKey, command);
	}
}
