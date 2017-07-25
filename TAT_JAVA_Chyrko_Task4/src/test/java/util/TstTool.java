package test.java.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

import com.epam.library.bean.Book;

public class TstTool {
	public boolean isValidXML(){		
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		File schemaLocation = new File(".\\src\\test\\java\\resources\\book.xsd");
		try {
			Schema schema = factory.newSchema(schemaLocation);
			Validator validator = schema.newValidator();		
			Source source = new StreamSource(".\\src\\test\\java\\resources\\book.xml");		
			validator.validate(source);		
			System.out.println(" is valid.");
			return true;
		} catch (SAXException ex) {
			System.out.println(" is not valid because ");
			System.out.println(ex.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void createBookXML(){
		JAXBContext context;		
		try {			
			context = JAXBContext.newInstance(Book.class);
			Marshaller m = context.createMarshaller();
			Book book = new Book();
			book.setTitle("NewBook");
			book.setAuthor("Sema's Prodaction");
			book.setYear(2017);
			book.setGenre("fantastik and PROGRAMMING");
			book.setQuantity(1);
			book.setAvailability("Y");
			File file = new File(".\\src\\test\\java\\resources\\book.xml");
			m.marshal(book, new FileOutputStream(file));
			m.marshal(book, System.out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
