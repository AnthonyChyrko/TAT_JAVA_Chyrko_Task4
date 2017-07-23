package com.epam.library.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.epam.library.controller.command.Command;
import com.epam.library.controller.command.CommandName;
import com.epam.library.controller.command.impl.AddBook;
import com.epam.library.controller.command.impl.AddSubscription;
import com.epam.library.controller.command.impl.BanUser;
import com.epam.library.controller.command.impl.BookAvailability;
import com.epam.library.controller.command.impl.EditAccess;
import com.epam.library.controller.command.impl.EditBook;
import com.epam.library.controller.command.impl.EditLogin;
import com.epam.library.controller.command.impl.EditOrderBooksList;
import com.epam.library.controller.command.impl.EditPassword;
import com.epam.library.controller.command.impl.Register;
import com.epam.library.controller.command.impl.RemoveSubscription;
import com.epam.library.controller.command.impl.ShowAllBooks;
import com.epam.library.controller.command.impl.SignIn;
import com.epam.library.controller.command.impl.SignOut;
import com.epam.library.controller.command.impl.WrongRequest;
import com.epam.library.controller.session.SessionStorage;
import com.epam.library.controller.utils.parser.Parser;

public class CommandProvider {
	private final static Logger logger = Logger.getLogger(CommandProvider.class);
	
	private final Map<String, Command> repository = new HashMap<>();
	private static final String PATH_TO_XML = ".\\src\\commandList.xml";

	CommandProvider() {
		ParserFactory factory = ParserFactory.getInstance();
		Parser parser = factory.getDOMParser();
		repository = parser.getCommands(PATH_TO_XML);
	}
	
	Command getCommand(String name){
		CommandName commandName = null;
		Command command = null;
		try{
			commandName = CommandName.valueOf(name.toUpperCase());	
				command = repository.get(commandName);
			
			logger.info(""+commandName);
		}catch (IllegalArgumentException | NullPointerException e) {
			
			logger.info(""+commandName);
			command = repository.get(CommandName.WRONG_REQUEST);			
		}
		
		return command;		
	}
}
