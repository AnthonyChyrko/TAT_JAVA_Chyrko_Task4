package test.java;

import static org.testng.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.epam.library.controller.Controller;
import com.epam.library.dao.exception.DAOException;
import com.epam.library.dao.pool.ConnectionPool;
import com.epam.library.dao.pool.exception.ConnectionPoolException;
import com.epam.library.server.MultithreadServer;

import test.java.resources.Encoding;
import test.java.resources.PathCommand;
import test.java.util.TstTool;

public class TstMultithreadServer {
	private List<String> pathFillTestDB = new ArrayList<>();
	private List<String> pathCleanTestDB = new ArrayList<>();
	private ConnectionPool connectionPool;
	private PreparedStatement ps;	
	private Connection connection;
	private StringBuilder sb;	 
	private MultithreadServer multithreadServer = MultithreadServer.getInstance();

	@Test(dataProvider = "scenario1")
	  public void scenario1(List<String> requestsList, List<String> expectedResponceList) {	
			String[] requestsArray = new String[requestsList.size()];
			requestsArray = requestsList.toArray(requestsArray);
			List<String> actualResponceList = multithreadServer.executeUserCommands(requestsArray);
			for (int i = 0; i < actualResponceList.size(); i++) {
				assertEquals(actualResponceList.get(i), expectedResponceList.get(i));	
			}			  
	  }  
	
		@Test(dataProvider = "scenario2")
		  public void scenario2(List<String> requestsList, List<String> expectedResponceList) {	
				String[] requestsArray = new String[requestsList.size()];
				requestsArray = requestsList.toArray(requestsArray);
				List<String> actualResponceList = multithreadServer.executeUserCommands(requestsArray);
				for (int i = 0; i < actualResponceList.size(); i++) {
					assertEquals(actualResponceList.get(i), expectedResponceList.get(i));	
				}			  
		  }  
		
		@Test(dataProvider = "scenario3")
		  public void scenario3(List<String> requestsList, List<String> expectedResponceList) {	
				TstTool.createBookXML();
				String[] requestsArray = new String[requestsList.size()];
				requestsArray = requestsList.toArray(requestsArray);
				List<String> actualResponceList = multithreadServer.executeUserCommands(requestsArray);
				for (int i = 0; i < actualResponceList.size(); i++) {
					assertEquals(actualResponceList.get(i), expectedResponceList.get(i));	
				}			  
		  }  
		@DataProvider
		public Object[][] scenario3() {
			  List<String> requestsList = Arrays.asList(
					  "command=sign_in&login=Sema&password=passWord3",
					  "command=add_book&book_path=.\\src\\test\\java\\resources\\book.xml",					  
					  "command=sign_out&login=Sema"
					  );
			  
			  List<String> expectedResponceList = Arrays.asList(
					  "Welcom!",
					  "New Book added!",					  
					  "Good Bye!"
					  );	  
			  
		  return new Object[][] {
		    new Object[] { requestsList, expectedResponceList },     
		  };
		}  
		
		@DataProvider
		public Object[][] scenario2() {
			  List<String> requestsList = Arrays.asList(
					  "command=sign_in&login=Ivan&password=passWord1",
					  "command=edit_Login&login=IvanStoGram",					  
					  "command=sign_out&login=IvanStoGram"
					  );
			  
			  List<String> expectedResponceList = Arrays.asList(
					  "Welcom!",
					  "Login is changed!",					  
					  "Good Bye!"
					  );	  
			  
		  return new Object[][] {
		    new Object[] { requestsList, expectedResponceList },     
		  };
		}  
	
  @DataProvider
  public Object[][] scenario1() {
	  List<String> requestsList = Arrays.asList(
			  "command=sign_in&login=Anton&password=passWord4",
			  "command=ban_user&login=Petr&signIn=BAN",
			  "command=edit_access&login=Sema&access=U",
			  "command=sign_out&login=Anton"
			  );
	  
	  List<String> expectedResponceList = Arrays.asList(
			  "Welcom!",
			  "Profile is changed!",
			  "Access is changed!",
			  "Good Bye!"
			  );	  
	  
    return new Object[][] {
      new Object[] { requestsList, expectedResponceList },     
    };
  }  
  
  @BeforeTest
  public void beforeTest() throws DAOException {
	  
	  pathFillTestDB = PathCommand.getPathFillTestDB();
	  try {
		  
		  Controller controller = new Controller();
		  controller.init();
		  ConnectionPool connectionPool = ConnectionPool.getInstance();
		  Connection connection = connectionPool.getConnection();		  
		  for (int i = 0; i < pathFillTestDB.size(); i++) {
			  
			  sb = Encoding.readFileWithCharset(pathFillTestDB.get(i), PathCommand.CHARSET);					
			  ps = connection.prepareStatement(""+sb);
			  ps.executeUpdate();				
		  }
			
	  } catch (SQLException e) {	
		  e.printStackTrace();
	  } catch (ConnectionPoolException e) {	
		  e.printStackTrace();
	  }
  }

  @AfterTest
  public void afterTest() throws DAOException{
	  pathCleanTestDB = PathCommand.getPathCleanTestDB();
	  try {		  
		  connectionPool = ConnectionPool.getInstance();
		  connection = connectionPool.getConnection();		
		  for (int i = 0; i < pathCleanTestDB.size(); i++) {			  
			  sb = Encoding.readFileWithCharset(pathCleanTestDB.get(i), PathCommand.CHARSET);		
			  ps = connection.prepareStatement(""+sb);
			  ps.executeUpdate();			
		  }
	  } catch (SQLException e) {				
			e.printStackTrace();
	  } catch (ConnectionPoolException e1) {
		  e1.printStackTrace();
	  }	  	
  }
  
}
