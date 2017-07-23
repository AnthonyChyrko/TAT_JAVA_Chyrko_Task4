package com.epam.library.controller.utils.parser;

import java.util.Map;

import com.epam.library.controller.command.Command;

public interface Parser {
	Map<String, Command> getCommands(String xmlPath);
}
