package org.sandhya.MyMDB.util;

public class MyMDBConstants {
	
	public final static String STATUS_SUCCESS = "Success"; 
	public final static String STATUS_FAILURE = "Failure"; 
	
	/** JSON RESPONSE KEYS **/
	public final static String JSON_KEY_ERROR_MESSAGE = "ErrorMsg";
	public final static String JSON_KEY_STATUS = "Status";
	public final static String JSON_KEY_MOVIE_ID = "MovieId";
	public final static String JSON_KEY_USER_ID = "UserId";
	public final static String JSON_KEY_MOVIE_CAST_ID = "MovieCastId";
	public final static String JSON_KEY_MOVIE_CAST = "MovieCast";
	public static final String JSON_KEY_USER = "User";
	public static final String JSON_KEY_MOVIE_DETAILS = "MovieDetails";
	public static final String JSON_KEY_MOVIE_ID_LIST = "MovieIdList";
	public static final String JSON_KEY_MOVIE_RATING = "MovieRatings";
	public static final String JSON_KEY_MOVIE_REVIEWS = "MovieReviews";
	public static final String JSON_KEY_MOVIE_REVIEW_COMMENT = "ReviewComment";


	public final static String JSON_KEY_HTTP_STATUS_CODE = "HTTPStatusCode";
	public static final int MAX_ALLOWED_LIMIT = 10;
	public static final int DEFAULT_PREFERENCE_COUNT = 5;
	public static final String SOLR_BASE_URL = "http://localhost:8983/solr/neonate_shard1_replica1/select";
	public static final String SOLR_DOC_FIELD_ID = "id";
	public static final String SOLR_DOC_FIELD_GENRE = "genre";
	public static final String SOLR_DOC_FIELD_ACTOR = "actor";
	public static final String SOLR_DOC_FIELD_USER = "raters";
	
	public static final int SOLR_UPDATE_ACTION_SET = 1;
	public static final int SOLR_UPDATE_ACTION_ADD = 2;
	public static final int SOLR_UPDATE_ACTION_REMOVE = 3;



}
