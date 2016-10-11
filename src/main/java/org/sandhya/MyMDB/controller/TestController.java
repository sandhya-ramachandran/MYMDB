package org.sandhya.MyMDB.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TestController {
	
	@RequestMapping(value="api/greetings", method = RequestMethod.GET)
	public ResponseEntity<String> helloWorld() {
		 
		return new ResponseEntity<String>("Finally I did it", HttpStatus.OK);
	}

}
