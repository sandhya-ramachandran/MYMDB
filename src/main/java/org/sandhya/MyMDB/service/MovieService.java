package org.sandhya.MyMDB.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.sandhya.MyMDB.dao.ActorDao;
import org.sandhya.MyMDB.dao.GenreDao;
import org.sandhya.MyMDB.dao.MovieCastDao;
import org.sandhya.MyMDB.dao.MovieDao;
import org.sandhya.MyMDB.dao.MovieGenreDao;
import org.sandhya.MyMDB.dao.RatingsDao;
import org.sandhya.MyMDB.dao.ReviewCommentsDao;
import org.sandhya.MyMDB.dao.UserDao;
import org.sandhya.MyMDB.exce.MyMDBDbException;
import org.sandhya.MyMDB.exception.MyMDBBadRequestException;
import org.sandhya.MyMDB.exception.MyMDBCustomException;
import org.sandhya.MyMDB.model.Actor;
import org.sandhya.MyMDB.model.Movie;
import org.sandhya.MyMDB.model.Ratings;
import org.sandhya.MyMDB.model.ReviewComments;
import org.sandhya.MyMDB.model.User;
import org.sandhya.MyMDB.util.MyMDBConstants;
import org.sandhya.MyMDB.util.MyMDBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class MovieService {
	
	@Autowired MovieDao movieDao;
	@Autowired MovieGenreDao movieGenreDao;
	@Autowired GenreDao genreDao;
	@Autowired MovieCastDao movieCastDao;
	@Autowired ActorDao actorDao;
	@Autowired UserDao userDao;
	@Autowired RatingsDao ratingsDao;
	@Autowired ReviewCommentsDao reviewCommentsDao;
	@Autowired SolrHelper solrHelper;
	
	@Transactional
	public int saveMovieDetails(Map<String,Object> movieDetails) throws MyMDBBadRequestException, MyMDBCustomException {
		
		Movie movie = new Movie();
		
		if(MyMDBHelper.isEmpty(movieDetails.get("Title"))) {
			throw new MyMDBBadRequestException("Title of the movie cannot be null");
		}
		
		movie.setTitle(String.valueOf(movieDetails.get("Title")));
		
		if(MyMDBHelper.isEmpty(movieDetails.get("Released"))) {
			throw new MyMDBBadRequestException("release date of the movie cannot be null");
		}
		
		try {
			 Date releaseDate = MyMDBHelper.parseDate(String.valueOf(movieDetails.get("Released")));
			 movie.setReleaseDate(releaseDate);
		} catch (Exception e) {
			throw new MyMDBBadRequestException("release date cannot be parsed.");
		}
		
		if(MyMDBHelper.isEmpty(movieDetails.get("imdbID"))) {
			throw new MyMDBBadRequestException("imdbId the movie cannot be null");
		}
		String imdbId = String.valueOf(movieDetails.get("imdbID"));
		boolean alreadyExists = movieDao.checkIfPresent(imdbId);
		
		if(alreadyExists)
			throw new MyMDBBadRequestException("movie with given imdbId already present");

		movie.setImdbId(imdbId);	
		
		if(MyMDBHelper.isEmpty(movieDetails.get("Genre"))) {
			throw new MyMDBBadRequestException("Genre the movie cannot be null");
		}
		
		String genre = String.valueOf(movieDetails.get("Genre"));
		List<String> genreNameList = Arrays.asList(genre.split(", "));
		String genreId = genreDao.getGenreIdByName(genreNameList);
		if(MyMDBHelper.isEmpty(genreId)) {
			throw new MyMDBBadRequestException("Bad genre details passed");
		}
		
		int movieId = movieDao.save(movie);
		if(movieId <= 0) {
			throw new MyMDBCustomException("Failed to save movie details");
		}
		List<String> genreIdList = Arrays.asList(genreId.split(","));
		movieGenreDao.addGenreList(movieId, genreIdList);
		try {
			JSONObject movieJson = new JSONObject();
			movieJson.put("id", String.valueOf(movie.getId()));
			movieJson.put("movie_id", movie.getId());
			movieJson.put("genre", genreIdList);
			movieJson.put("imdb_id", movie.getImdbId());
			movieJson.put("title", movie.getTitle());
			System.out.println(movieJson);
			solrHelper.movieSolrSave(movieJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return movieId ;
	}

	@Transactional
	public void editMovieDetails(int id , Map<String, Object> movieDetails) throws MyMDBBadRequestException, MyMDBDbException{
		
		if (id <= 0) {
			throw new MyMDBBadRequestException("movie id should be a positive integer");
		}
		
		Movie movie = movieDao.getMovieById(id);
		if(MyMDBHelper.isEmpty(movie)) {
			throw new MyMDBBadRequestException("movie does not exist");
		}
		
		if(MyMDBHelper.isNotEmpty(movieDetails.get("Title"))) {
			String title = String.valueOf(movieDetails.get("Title"));
			if(!title.equalsIgnoreCase(movie.getTitle())) {
				movie.setTitle(title);
			}
		}
		
		if(MyMDBHelper.isNotEmpty(movieDetails.get("Released"))) {
			try {
				 Date releaseDate = MyMDBHelper.parseDate(String.valueOf(movieDetails.get("releaseDate")));
				 movie.setReleaseDate(releaseDate);
			} catch (Exception e) {
			}
		}
		
		if(MyMDBHelper.isNotEmpty(movieDetails.get("imdbID"))) {
			String imdbId = String.valueOf(movieDetails.get("imdbID"));
			boolean alreadyExists = movieDao.checkIfPresent(imdbId);
			
			if(alreadyExists)
				throw new MyMDBBadRequestException("movie with given imdbId already present");
			movie.setImdbId(imdbId);
		}
		
		String genre = String.valueOf(movieDetails.get(movieDetails.get("Genre")));
		List<String> genreNameList = Arrays.asList(genre.split(","));
		String genreId = genreDao.getGenreIdByName(genreNameList);
		if(MyMDBHelper.isEmpty(genreId)) {
			throw new MyMDBBadRequestException("Bad genre details passed");
		}
		movieDao.save(movie);
		List<String> genreIdList = null ;
		try {
			genreIdList = new ArrayList<String> (Arrays.asList(genreId.split(",")));
			movieGenreDao.deleteAllGenreForMovie(movie.getId());
			movieGenreDao.addGenreList(movie.getId(), genreIdList);
		} catch (Exception e) {
			throw new MyMDBDbException("failed to update genre for movie");
		}
		
		try {
			JSONObject movieJson = new JSONObject();
			movieJson.put("id", String.valueOf(movie.getId()));
			movieJson.put("movie_id", movie.getId());
			movieJson.put("genre", genreIdList);
			movieJson.put("imdb_id", movie.getImdbId());
			movieJson.put("title", movie.getTitle());
			System.out.println(movieJson);
			solrHelper.movieUpdateSolr(movieJson, MyMDBConstants.SOLR_UPDATE_ACTION_SET);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void updateMovieCast(int movieId, List<String> actorNames, boolean isEdit ) throws MyMDBBadRequestException {
		
		int solrUpdateAction =  MyMDBConstants.SOLR_UPDATE_ACTION_ADD; 
		
		if (movieId <= 0) {
			throw new MyMDBBadRequestException("movie id should be a positive integer");
		}
		
		if(MyMDBHelper.isEmpty(actorNames) || actorNames.size() == 0) {
			throw new MyMDBBadRequestException("cast list is null or empty");
		}
		
		Movie movie = movieDao.getMovieById(movieId);
		if(MyMDBHelper.isEmpty(movie)) {
			throw new MyMDBBadRequestException("movie does not exist");
		}
		// find ids of actors in db
		List<Map<String,Object>> actorDetailsList = actorDao.getActorIdByName(actorNames);
		List<Integer> actorIdList = new ArrayList<Integer>();
		
		/* if returned size is not the same as sent list size
		 * it means that we may need to add some new actors
		*/
		if(actorDetailsList.size() != actorNames.size()) {
			
			List<String> actorNamesInDb = new ArrayList<String>();
			for (Map<String, Object> actorDetails : actorDetailsList) {
				actorIdList.add(MyMDBHelper.returnInt(String
						.valueOf(actorDetails.get("id"))));
				actorNamesInDb.add(String.valueOf(actorDetails.get("name")));
			}
			// find all the names of actors that need to be added to db
			actorNames.removeAll(actorNamesInDb);
			
			// add each one to the db
			for (String name : actorNames) {
				Actor newActor = new Actor();
				newActor.setName(name);
				int newId = actorDao.save(newActor);
				if(newId > 0)
					actorIdList.add(newId);
			}
		} 
		else {
			for (Map<String, Object> actorDetails : actorDetailsList) {
				actorIdList.add(MyMDBHelper.returnInt(String
						.valueOf(actorDetails.get("id"))));
			}
		}
		// if edit mode then we will delete and add 
		if(isEdit) {
			solrUpdateAction = MyMDBConstants.SOLR_UPDATE_ACTION_SET;
			movieCastDao.deleteAllActorsForMovie(movie.getId());
		}
		movieCastDao.addActorList(movieId, actorIdList);
		
		if(MyMDBHelper.isNotEmpty(actorIdList) && actorIdList.size() > 0) {
			try {
			//Map<String,Object> solrActorDetails = new HashMap<String, Object>();
			JSONObject json = new JSONObject();
			json.put("id", String.valueOf(movie.getId()));
			json.put("actor", actorIdList);
			solrHelper.movieUpdateSolr(json, solrUpdateAction);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}

	public List<Actor> getCastForMovie(int id) throws MyMDBBadRequestException {
		if (id <= 0) {
			throw new MyMDBBadRequestException("movie id should be a positive integer");
		}
		List<Actor> actorList = movieCastDao.getMovieCastByMovieId(id);
		return actorList;
	}

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

	public Map<String, Object> getMovieRatings(int movieId) throws MyMDBBadRequestException {
		Movie movie = getMovie(movieId);
		Map<String,Object> ratings = ratingsDao.getMovieAvgRating(movie.getId());
		return ratings;
	}

	public Map<String, Object> getMovieReviews(int id, int offset, int limit) throws MyMDBBadRequestException{
		Map<String,Object> reviewDetails = new HashMap<String, Object>();
		Movie movie = getMovie(id);
		if(offset < 0 ) {
			offset = 0;
		}
		
		if(limit < 0) {
			limit = 1;
		}
		
		if (limit > MyMDBConstants.MAX_ALLOWED_LIMIT) {
			limit = 10;
		}
		int totalCount = ratingsDao.getTotalReviewCountForMovie(movie.getId());
		List<Map<String,Object>> reviews = ratingsDao.getMovieReviews(id, offset, limit);
		reviewDetails.put("totalCount", totalCount);
		reviewDetails.put("reviewList", reviews);
		return reviewDetails;
	}
	
	public Movie getMovie(int movieId) throws MyMDBBadRequestException {
		if (movieId <=0) {
			throw new MyMDBBadRequestException("movie id should be a positive integer");
		}
		Movie movie = movieDao.getMovieById(movieId);
		if(MyMDBHelper.isEmpty(movie)) {
			throw new MyMDBBadRequestException("movie does not exist");
		}
		
		return movie;
	}

	public void voteCommentOnReview(ReviewComments comment) throws MyMDBBadRequestException{
		if (comment.getRatingsId() <=0) {
			throw new MyMDBBadRequestException("review id should be a positive integer");
		}
		
		User user = userDao.getById(comment.getUserId());
		if (MyMDBHelper.isEmpty(user)) {
			throw new MyMDBBadRequestException("User does not exist");
		}
		Ratings review = ratingsDao.getById(comment.getRatingsId());
		if(MyMDBHelper.isEmpty(review)) {
			throw new MyMDBBadRequestException("review does not exist");
		}
		
		if(MyMDBHelper.isEmpty(comment.getComments()) && comment.getVote() == 0 ) {
			throw new MyMDBBadRequestException("no comments or vote"); 
		}
		
		if(comment.getVote() != -1 && comment.getVote() != 0 && comment.getVote() != 1 ) {
			throw new MyMDBBadRequestException("Vote can either be a 1 (like), 0 (neutral), -1(dislike)"); 
		}
		
		if(comment.getComments().length() > 999 ) {
			throw new MyMDBBadRequestException("Comment should not be more than 1000 characters");
		}
		//comment.setRatingsId(reviewId);
		reviewCommentsDao.save(comment);
		
	}

	public boolean reviewMovie(int id, Ratings rating) throws MyMDBBadRequestException {
		Movie movie = getMovie(id);
		
		if(rating.getRating() < 0 || rating.getRating() > 5) {
			throw new MyMDBBadRequestException("incorrect rate values");
		}
		if(MyMDBHelper.isEmpty(rating.getReview()) && rating.getRating() == 0) {
			throw new MyMDBBadRequestException("no review or rating"); 
		}
		
		User user = userDao.getById(rating.getUserId());
		if (MyMDBHelper.isEmpty(user)) {
			throw new MyMDBBadRequestException("User does not exist");
		}
		
		if(rating.getReview().length() > 999 ) {
			throw new MyMDBBadRequestException("Review comment should not be more than 1000 characters");
		}
		rating.setMovieId(movie.getId());
		boolean isNew = ratingsDao.save(rating);
		//Update SOLR
		try {
			if(isNew) {
				JSONObject json = new JSONObject();
				json.put("id", String.valueOf(rating.getMovieId()));
				json.put("raters", rating.getUserId());
				solrHelper.movieUpdateSolr(json,  MyMDBConstants.SOLR_UPDATE_ACTION_ADD);
			}
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return isNew;
	}
	
	

	public void process(String jsonLine) throws Exception {
		
		Map<String, Object> retMap = new Gson().fromJson(jsonLine, new TypeToken<HashMap<String, Object>>() {}.getType());
		if(MyMDBHelper.isEmpty(retMap.get("Actors")) || String.valueOf(retMap.get("Actors")).equalsIgnoreCase("N/A")) {
			return ;
		}
		if(MyMDBHelper.isEmpty(retMap.get("Genre")) || String.valueOf(retMap.get("Genre")).equalsIgnoreCase("N/A")) {
			return ;
		}
		System.out.println(String.valueOf(retMap.get("imdbID")));
		int movieId = saveMovieDetails(retMap);
		String actorNamesStr = String.valueOf(retMap.get("Actors"));
		ArrayList<String> actorNames = new ArrayList<String>(Arrays.asList(actorNamesStr.split(",")));
		updateMovieCast(movieId, actorNames, false);
		
	}

	public List<String> getMovieRecommendation(int userId) throws MyMDBBadRequestException {
		if (userId <=0) {
			throw new MyMDBBadRequestException("user id should be a positive integer");
		}
		User dbUser = userDao.getById(userId);
		if(MyMDBHelper.isEmpty(dbUser)) {
			throw new MyMDBBadRequestException("user does not exist");
		}
		List<String> movieIds = solrHelper.getRecommendedMovieIds(userId);
		return movieIds;

	}

	public Map<String, Object> getMovieDetails(int id) throws MyMDBBadRequestException{
		if (id <=0) {
			throw new MyMDBBadRequestException("movie id should be a positive integer");
		}
		
		Map<String,Object> movieDetails = movieDao.getMovieDetailsById(id);
		return movieDetails;
	}

}

	
