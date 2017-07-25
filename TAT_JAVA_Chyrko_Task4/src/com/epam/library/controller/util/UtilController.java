package com.epam.library.controller.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.epam.library.bean.Book;
import com.epam.library.bean.OrderBooksList;

public class UtilController {	
	private final static Logger logger = Logger.getLogger(UtilController.class);
	private OrderBooksList orderBooksList = OrderBooksList.getInstance();
	
	public String recognizeParam(Enum<?> comName, String[] param){	
		
		for (int i = 0; i < param.length; i++) {
			String[] params = param[i].split("=");	
			if(comName.toString().equals(params[0].toUpperCase())){
				try {
					return params[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					logger.error("No such parameter!");
					return null;
				}
				
			}
		}		
		return null;	
	}		

	public Book prepareBook(String[] param) {
		int yearInt;
		int quantityInt;		
		long bookId;
		
		String bookIdStr = recognizeParam(BookParam.BOOK_ID, param);
		String title = recognizeParam(BookParam.TITLE, param);
		String author = recognizeParam(BookParam.AUTHOR, param);
		String genre = recognizeParam(BookParam.GENRE, param);
		String year = recognizeParam(BookParam.YEAR, param);
		String quantity = recognizeParam(BookParam.QUANTITY, param);
		String availability = recognizeParam(BookParam.AVAILABILITY, param);
		if(bookIdStr==null || bookIdStr.isEmpty()){
			bookId = 0;
		}else{
			bookId = Integer.parseInt(bookIdStr);
		}
		if(year==null || year.isEmpty()){
			yearInt = 0;
		}else{
			yearInt = Integer.parseInt(year);
		}
		if(quantity==null || quantity.isEmpty()){
			quantityInt = 0;
		}else{
			quantityInt = Integer.parseInt(quantity);
		}
		return new Book(bookId, title, author, genre, yearInt, quantityInt, availability);		
	}

	public OrderBooksList prepareOrderBooksList(String[] param) {
		long bookId;
		String actionCommand = recognizeParam(OrderBooksListParam.ACTION_COMMAND, param);
		String bookIdStr = recognizeParam(OrderBooksListParam.B_ID, param);
		
		if(bookIdStr==null || bookIdStr.isEmpty()){
			bookId = 0;
		}else{
			bookId = Long.parseLong(bookIdStr);
		}

		orderBooksList.setActionCommand(actionCommand);
		orderBooksList.setBookId(bookId);

		return orderBooksList;
	}
	
	public Book getBookFromXML(String pathXML){
		if(isValidXML(pathXML)){
			File file = new File(pathXML);
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(Book.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Book book = (Book) jaxbUnmarshaller.unmarshal(file);		
				return book;
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return null;
	}
	
	public boolean isValidXML(String pathXML){		
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		File schemaLocation = new File(".\\src\\test\\java\\resources\\book.xsd");
		try {
			Schema schema = factory.newSchema(schemaLocation);
			Validator validator = schema.newValidator();		
			Source source = new StreamSource(pathXML);		
			validator.validate(source);		
			System.out.println(" is valid.");
			return true;
		} catch (SAXException ex) {
			System.out.println(" is not valid because ");
			System.out.println(ex.getMessage());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
