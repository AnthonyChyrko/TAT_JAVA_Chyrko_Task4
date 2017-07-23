package com.epam.library.controller.utils.parser.factory;

import com.epam.library.controller.utils.parser.Parser;
import com.epam.library.controller.utils.parser.impl.DOMParser;
import com.epam.library.controller.utils.parser.impl.SAXParser;
import com.epam.library.controller.utils.parser.impl.StAXParser;

public final class ParserFactory {
    private static ParserFactory instance;
    private final Parser domParser = new DOMParser();
    private final Parser saxParser = new SAXParser();
    private final Parser staxParser = new StAXParser();


    private ParserFactory(){}

    public static synchronized ParserFactory getInstance(){
        if (instance == null){
            instance = new ParserFactory();
        }
        return instance;
    }

    public Parser getDomParser() {
        return domParser;
    }

    public Parser getSaxParser() {
        return saxParser;
    }

    public Parser getStaxParser() {
        return staxParser;
    }
}
