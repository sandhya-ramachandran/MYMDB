package org.sandhya.MyMDB.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jettison.json.JSONObject;
import org.sandhya.MyMDB.exception.MyMDBExceptionHandler;
import org.sandhya.MyMDB.model.Actor;
import org.sandhya.MyMDB.model.Ratings;
import org.sandhya.MyMDB.model.ReviewComments;
import org.sandhya.MyMDB.model.User;
import org.sandhya.MyMDB.service.MovieService;
import org.sandhya.MyMDB.util.MyMDBConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MovieController {
	
	@Autowired MovieService movieService;
	
	@RequestMapping(value="api/movies", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<Map<String, Object>> addMovie(
			@RequestBody Map<String, Object> movieDetailsParams,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			int movieId = movieService.saveMovieDetails(movieDetailsParams);
			apiResponse.put(MyMDBConstants.JSON_KEY_MOVIE_ID, movieId);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/{id}", method = RequestMethod.PUT)
	@ResponseBody ResponseEntity<Map<String, Object>> editMovie(
			@RequestBody Map<String, Object> movieDetailsParams, @PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			movieService.editMovieDetails(id, movieDetailsParams);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/{id}", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<Map<String,Object>> getMovie(
			@PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			Map<String,Object> movie = movieService.getMovieDetails(id);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_MOVIE_DETAILS, movie);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
	}
	
	@RequestMapping(value="api/users/", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<Map<String, Object>> addUser(
			@RequestBody User userDetails,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			int userId = movieService.addUser(userDetails);
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
			int userId = movieService.editUser(userDetails, id);
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
			User user = movieService.getUserDetails(id);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_USER, user);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
	}
	
	@RequestMapping(value="api/recommendation/users/{id}", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<Map<String, Object>> getMovieRecommendationUser(
			@PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			List<String> movieIds = movieService.getMovieRecommendation(id);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_MOVIE_ID_LIST, movieIds);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
	}
	
	@RequestMapping(value="api/movies/{id}/cast", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<Map<String, Object>> addMovieCast(
			@RequestBody List<String> movieCastDetails, @PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			movieService.updateMovieCast(id, movieCastDetails, false);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/{id}/cast", method = RequestMethod.PUT)
	@ResponseBody ResponseEntity<Map<String, Object>> editMovieCast(
			@RequestBody List<String> movieCastDetails, @PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			movieService.updateMovieCast(id, movieCastDetails, true);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/{id}/cast", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<Map<String, Object>> getMovieCast( @PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			List<Actor> actorList = movieService.getCastForMovie(id);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_MOVIE_CAST, actorList);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/{id}/reviews", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<Map<String, Object>> rateReviewMovie(
			@RequestBody Ratings rating, @PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			movieService.reviewMovie(id, rating);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/{id}/ratings", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<Map<String, Object>> getMovieRatings( @PathVariable ("id") int id,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			Map<String,Object> ratings = movieService.getMovieRatings(id);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_MOVIE_RATING, ratings);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/{id}/reviews", method = RequestMethod.GET)
	@ResponseBody ResponseEntity<Map<String, Object>> getMovieReviews( @PathVariable ("id") int id,
			@RequestParam("offset") int offset, @RequestParam("limit") int limit,
			HttpServletRequest req, HttpServletResponse response,
			HttpSession session) { 
		
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			Map<String,Object> reviews = movieService.getMovieReviews(id, offset,limit);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			apiResponse.put(MyMDBConstants.JSON_KEY_MOVIE_REVIEWS, reviews);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/movies/review/comment", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<Map<String, Object>> comment( //@PathVariable ("id") int reviewId,
			@RequestBody ReviewComments comment,HttpServletResponse response,HttpSession session) { 
		try {
			Map<String, Object> apiResponse = new HashMap<String, Object>();
			movieService.voteCommentOnReview(comment);
			apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
			return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			MyMDBExceptionHandler handler = new MyMDBExceptionHandler();
			return handler.handleExceptionAsEntity(e);
			
		}
		 
	}
	
	@RequestMapping(value="api/populate", method = RequestMethod.POST)
	@ResponseBody ResponseEntity<Map<String, Object>> populate(HttpServletRequest request , HttpServletResponse response,HttpSession session) { 
		Map<String, Object> apiResponse = new HashMap<String, Object>();
		File file = new File("/Users/sandhyaramachandran/PP/project/movieDetails3.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = br.readLine()) != null) {
		    	try {
		    		if(line.endsWith(",")) {
		    			line = line.substring(0, line.lastIndexOf(","));
		    		} else {
		    			continue ;
		    		}
		    		movieService.process(line);
		    	} catch (Exception e) {
		    		continue;
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		apiResponse.put(MyMDBConstants.JSON_KEY_STATUS, MyMDBConstants.STATUS_SUCCESS);
		return new ResponseEntity<Map<String,Object>>(apiResponse, HttpStatus.NO_CONTENT);

	}

}
