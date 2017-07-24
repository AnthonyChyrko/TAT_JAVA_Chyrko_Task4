package com.epam.library.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import java.sql.PreparedStatement;

import com.epam.library.bean.OrderBooksList;
import com.epam.library.bean.User;
import com.epam.library.controller.session.SessionStorage;
import com.epam.library.dao.UserDAO;
import com.epam.library.dao.exception.DAOException;
import com.epam.library.dao.pool.ConnectionPool;
import com.epam.library.dao.pool.exception.ConnectionPoolException;

public class SQLUserDAO implements UserDAO {
	private final static Logger logger = Logger.getLogger(SQLUserDAO.class);
	private static final String GET_USER = "SELECT `u_id`, `u_login`, `u_password`, `u_access`, `u_signIn` FROM `users` WHERE u_id = ?;";
	private static final String GET_USERS = "SELECT `u_id`, `u_login`, `u_password`, `u_access`, `u_signIn` FROM `users`;";
	private static final String SIGN_IN_USER = "UPDATE users SET u_signIn='IN' WHERE u_id = ?;";
	private static final String SIGN_OUT_USER = "UPDATE users SET u_signIn='OUT' WHERE u_id = ?;";
	private static final String ADD_NEW_USER = "INSERT INTO `users` (`u_login`, `u_password`, `u_access`, `u_signIn`) VALUES (?, ?, ?, ?);";
	private static final String EDIT_LOGIN = "UPDATE users SET u_login= ? WHERE u_id = ?;";
	private static final String EDIT_PASSWORD = "UPDATE users SET u_password= ? WHERE u_id = ?;";
	private static final String EDIT_ACCESS = "UPDATE users SET u_access= ? WHERE u_login = ?;";
	private static final String BAN_USER =    "UPDATE users SET u_signIn= ? WHERE u_login = ?;";

	private SessionStorage session = SessionStorage.getInstance();
	private OrderBooksList orderBooksList = OrderBooksList.getInstance();	
	
	@Override
	public void signIn(String login, String password) throws DAOException{	
		User user;
		ResultSet rs;
		PreparedStatement ps;
		boolean isSignInSuccessful = false;
		try {
			ConnectionPool connectionPool = ConnectionPool.getInstance();		
			Connection connection = connectionPool.getConnection();				
			user = session.getUserFromSession(Thread.currentThread().hashCode());
		
			ps = connection.prepareStatement(GET_USERS);
			rs = ps.executeQuery();
			
			while(rs.next()){					
				if(login.equals(rs.getString(2))){	
					if(password.equals(rs.getString(3))){
						if("BAN".equals(rs.getString(5))){
							logger.warn("User banned!");
							throw new DAOException("User banned!");
						}else if("OUT".equals(rs.getString(5))){	
							isSignInSuccessful = true;
							ps = connection.prepareStatement(SIGN_IN_USER);
							ps.setInt(1, rs.getInt(1));
							ps.executeUpdate();								
							
							ps = connection.prepareStatement(GET_USER);							
							ps.setInt(1, rs.getInt(1));			
							user = setUserParam(user, ps.executeQuery());
							logger.debug(Thread.currentThread().hashCode()+" - "+user.toString()+" FROM SIGN_IN");
							
						}else{			
							logger.warn("User already SignIn!");
							throw new DAOException("User already SignIn!");
						}
					}else{
						logger.warn("Wrong login or password!");
						throw new DAOException("Wrong login or password!");						
					}
				}
			}		
			if(!isSignInSuccessful){
				user.nullifyUser();
				logger.warn("Such a user does not exist yet! You can register!");
				throw new DAOException("Such a user does not exist yet! You can register!");
			}
		} catch (SQLException e) {
			logger.error("SQLException!",e);
			throw new DAOException("SQLException!",e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException!",e);
			throw new DAOException("ConnectionPoolException!",e);
		}	
	}
	
	@Override
	public void signOut(String login) throws DAOException {			
		User user;
		ResultSet rs;
		PreparedStatement ps;
		try {
			ConnectionPool connectionPool = ConnectionPool.getInstance();		
			Connection connection = connectionPool.getConnection();	
			user = session.getUserFromSession(Thread.currentThread().hashCode());			
			ps = connection.prepareStatement(GET_USERS);
			rs = ps.executeQuery();
			
			while(rs.next()){					
				if(login.equals(rs.getString(2))){

					if("IN".equals(rs.getString(5))){								
						ps = connection.prepareStatement(SIGN_OUT_USER);
						ps.setInt(1, rs.getInt(1));
						ps.executeUpdate();						
									
						user.nullifyUser();				
						orderBooksList.clearOrderBooksList();
						continue;
					}else{						
						logger.warn("User already SignOut!");
						throw new DAOException("User already SignOut!");
					}
				}
			}			
		} catch (SQLException  e) {			
			logger.error("SQLException!",e);
			throw new DAOException("SQLException!",e);
		}catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException!",e);
			throw new DAOException("ConnectionPoolException!",e);
		}
	}

	@Override
	public void registration(String login, String password) throws DAOException{			
		ResultSet rs;
		PreparedStatement ps;
		ConnectionPool connectionPool;
		try {
			connectionPool = ConnectionPool.getInstance();
			Connection connection = connectionPool.getConnection();		
			ps = connection.prepareStatement(GET_USERS);
			rs = ps.executeQuery();
			
			while(rs.next()){				
				if(login.equals(rs.getString(2))){		
					logger.warn("User with such login already exists! Think up another login!");
					throw new DAOException("User with such login already exists! Think up another login!");
				}else if(password.equals(rs.getString(3))){
					logger.warn("User with such password already exists!");
					throw new DAOException("User with such password already exists!");
				}				
			}
			
			ps = connection.prepareStatement(ADD_NEW_USER);
			ps.setString(1, login);
			ps.setString(2, password);
			ps.setString(3, "U");
			ps.setString(4, "OUT");
			ps.executeUpdate();
			
		} catch (SQLException e) {
			logger.error("SQLException!",e);
			throw new DAOException("SQLException!",e);
		
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException!",e);
			throw new DAOException("ConnectionPoolException!",e);
		}			
	}

	@Override
	public void editLogin(String login) throws DAOException {
		User user;
		ResultSet rs;
		PreparedStatement ps;
		boolean isUserFound = false;
		try {
			ConnectionPool connectionPool = ConnectionPool.getInstance();		
			Connection connection = connectionPool.getConnection();
			user = session.getUserFromSession(Thread.currentThread().hashCode());				
			
			ps = connection.prepareStatement(GET_USERS);
			rs = ps.executeQuery();
			while(rs.next()){  //Go through the collection and if such a user is then act
				if(user.getLogin().equals(rs.getString(2)) && user.getPassword().equals(rs.getString(3))&&
				user.getAccess().equals(rs.getString(4)) && user.getSignIn().equals(rs.getString(5))){					
					user.setUserId(Integer.valueOf(rs.getString(1))); //id user for edit					
					isUserFound = true;             
					ps = connection.prepareStatement(EDIT_LOGIN);
					ps.setString(1, login);
					ps.setLong(2, user.getUserId());	
					ps.executeUpdate();
					user.setLogin(login);
				}
			}
			if(!isUserFound){
				logger.warn("User is absent in db! Please registration!");
				throw new DAOException("User is absent in db! Please registration!");
			}			
			
		}catch (SQLException e) {
				logger.error("Can't close connection!",e);
				throw new DAOException("Can't close connection!",e);
		}catch (ConnectionPoolException e) {
				logger.error("ConnectionPoolException!",e);
				throw new DAOException("ConnectionPoolException!",e);
		}
	}		


	@Override
	public void editPassword(String password) throws DAOException {
		User user;
		ResultSet rs;
		PreparedStatement ps;
		try {
			ConnectionPool connectionPool = ConnectionPool.getInstance();		
			Connection connection = connectionPool.getConnection();
			user = session.getUserFromSession(Thread.currentThread().hashCode());			
		
		
			ps = connection.prepareStatement(GET_USERS);
			rs = ps.executeQuery();
			while(rs.next()){  
				if(user.getLogin().equals(rs.getString(2)) && user.getPassword().equals(rs.getString(3))&&
				user.getAccess().equals(rs.getString(4)) && user.getSignIn().equals(rs.getString(5))){					
					user.setUserId(Integer.valueOf(rs.getString(1))); //id user for edit					
					                    
					ps = connection.prepareStatement(EDIT_PASSWORD);
					ps.setString(1, password);
					ps.setLong(2, user.getUserId());	
					ps.executeUpdate();
					user.setPassword(password);
				}
			}			
		} catch (SQLException e) {
			logger.error("SQLException!",e);
			throw new DAOException("SQLException!", e);		
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException!",e);
			throw new DAOException("ConnectionPoolException!",e);
		}	
	}

	@Override
	public void editAccess(String targetLogin, String newAccess) throws DAOException {
		User user;
		ResultSet rs;
		PreparedStatement ps;
		boolean isUserFound = false;		
		
		try {
			ConnectionPool connectionPool = ConnectionPool.getInstance();		
			Connection connection = connectionPool.getConnection();
			user = session.getUserFromSession(Thread.currentThread().hashCode());		
		
			ps = connection.prepareStatement(GET_USERS);
			rs = ps.executeQuery();	
			while(rs.next()){
				if(targetLogin.equals(rs.getString(2))){
					isUserFound = true;
					ps = connection.prepareStatement(EDIT_ACCESS);
					ps.setString(1, newAccess);
					ps.setString(2,targetLogin);	
					ps.executeUpdate();								
				}
			}	
			if(!isUserFound){
				logger.warn("User is absent in db! Please registration!");
				throw new DAOException("User is absent in db! Please registration!");
			}				
		} catch (SQLException e) {
			logger.error("SQLException!",e);
			throw new DAOException("SQLException!",e);
		}catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException!",e);
			throw new DAOException("ConnectionPoolException!",e);
		}
			
	}

	@Override
	public void banUser(String targetlogin, String signIn) throws DAOException {
		ResultSet rs;
		PreparedStatement ps;
		boolean isUserBan = false;		
		try {
			ConnectionPool connectionPool = ConnectionPool.getInstance();		
			Connection connection = connectionPool.getConnection();
		
			ps = connection.prepareStatement(GET_USERS);
			rs = ps.executeQuery();	
			while(rs.next()){
				if(targetlogin.equals(rs.getString(2))){
					if("SA".equals(rs.getString(4))){
						logger.warn("You can not block superadmin!");
						throw new DAOException("You can not block superadmin!");
					}
					if("BAN".equals(rs.getString(5))){
						logger.warn("User already BAN!");
						throw new DAOException("User already BAN!");
					}
					isUserBan = true;
					ps = connection.prepareStatement(BAN_USER);
					ps.setString(1, signIn);
					ps.setString(2,targetlogin);	
					ps.executeUpdate();								
				}
			}	
			if(!isUserBan){
				logger.warn("User is absent in db! Please registration!");
				throw new DAOException("User is absent in db! Please registration!");
			}		
			} catch (SQLException e) {
				logger.error("SQLException!", e);
				throw new DAOException("SQLException!",e);
			}catch (ConnectionPoolException e) {
				logger.error("ConnectionPoolException!",e);
				throw new DAOException("ConnectionPoolException!",e);
			}	
		}		
	
	private User setUserParam(User user, ResultSet rs) throws SQLException{			
			while(rs.next()){
				user.setUserId(rs.getInt(1));			
				user.setLogin(rs.getString(2));
				user.setPassword(rs.getString(3));
				user.setAccess(rs.getString(4));
				user.setSignIn(rs.getString(5));	
			}		
		return user;		
	}
}

	

	


