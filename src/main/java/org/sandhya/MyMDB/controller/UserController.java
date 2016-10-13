package org.sandhya.MyMDB.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sandhya.MyMDB.exception.MyMDBExceptionHandler;
import org.sandhya.MyMDB.model.User;
import org.sandhya.MyMDB.service.UserService;
import org.sandhya.MyMDB.util.MyMDBConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
	
	@Autowired UserService userService;

	
	@RequestMapping(value="api/users/", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<Map<String, Object>> addUser(
			@RequestBody User userDetails,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			int userId = userService.addUser(userDetails);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_USER_ID, userId);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
	}
	
	@RequestMapping(value="api/users/{id}", method = RequestMethod.PUT)
	@ResponseBody ResponseEntity<Map<String, Object>> editUser(
			@RequestBody User userDetails, @PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			int userId = userService.editUser(userDetails, id);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_USER_ID, userId);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
	}
	
	@RequestMapping(value="api/users/{id}", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<Map<String, Object>> getUser(
			@PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			User user = userService.getUserDetails(id);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_USER, user);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
	}

}
