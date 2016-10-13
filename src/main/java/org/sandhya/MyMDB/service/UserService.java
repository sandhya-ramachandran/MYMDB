package org.sandhya.MyMDB.service;

import org.sandhya.MyMDB.dao.UserDao;
import org.sandhya.MyMDB.exception.MyMDBBadRequestException;
import org.sandhya.MyMDB.model.User;
import org.sandhya.MyMDB.util.MyMDBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
	
	@Autowired UserDao userDao;
	
	public int addUser(User user) throws MyMDBBadRequestException {
		if(MyMDBHelper.isEmpty(user.getEmail())) {
			throw new MyMDBBadRequestException("email is mandatory"); 
		}
		boolean userExists = userDao.isUserPresent(user.getEmail());
		if(userExists) {
			throw new MyMDBBadRequestException("user already present"); 
		}
		return userDao.save(user);
		
	}
	
	public int editUser(User user, int id) throws MyMDBBadRequestException {
		User dbUser = getUserDetails(id);
		if(MyMDBHelper.isEmpty(dbUser)) {
			throw new MyMDBBadRequestException("user does not exist");
		}
		user.setId(id);
		return userDao.save(user);
		
	}

	public User getUserDetails(int id) throws MyMDBBadRequestException{
		if (id <=0) {
			throw new MyMDBBadRequestException("user id should be a positive integer");
		}
		User dbUser = userDao.getById(id);
		return dbUser; 
	}

}
