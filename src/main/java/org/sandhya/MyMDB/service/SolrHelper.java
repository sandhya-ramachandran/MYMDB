package org.sandhya.MyMDB.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.sandhya.MyMDB.dao.MovieCastDao;
import org.sandhya.MyMDB.dao.MovieGenreDao;
import org.sandhya.MyMDB.util.MyMDBConstants;
import org.sandhya.MyMDB.util.MyMDBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SolrHelper {
	
	@Autowired MovieGenreDao movieGenreDao;
	@Autowired MovieCastDao movieCastDao;
	
	private BooleanQuery buildListTermQuery(String field,
			List<String> values, List<String> boost, BooleanClause.Occur innerOccur) {
		BooleanQuery listQuery = new BooleanQuery();
		if (values != null) {
			if (values.size() != 0) {
				int i = 0;
				for (Object singleValue : values) {
					String value = singleValue.toString();

					if (MyMDBHelper.isEmpty(value)) {
						continue;
					}
					TermQuery tq = new TermQuery(new Term(field, value
							.toLowerCase().trim()));
					tq.setBoost(Float.valueOf(boost.get(i)));
					i++;
					listQuery.add(tq, innerOccur);
				}
				
			}
		}
		return listQuery;
	}
	
	private BooleanQuery buildRecommenderQuery(int userId, int count) {
		BooleanQuery topQuery = new BooleanQuery();
		List<Map<String,Object>> topGenres = movieGenreDao.getTopPreferredGenreForUser(userId, count);
		List<Map<String,Object>> topActors = movieCastDao.getTopPreferredActorsForUser(userId, count);
		List<String> genres = new ArrayList<String>();
		List<String> genreBoost = new ArrayList<String>();
		for(Map<String,Object> result : topGenres) {
			String genre  = String.valueOf(result.get("gid"));
			genres.add(genre);
			String preference = String.valueOf(result.get("count"));
			genreBoost.add(preference);
		}
		
		List<String> actors = new ArrayList<String>();
		List<String> actorBoost = new ArrayList<String>();
		for(Map<String,Object> result : topActors) {
			String actor  = String.valueOf(result.get("aid"));
			actors.add(actor);
			String preference = String.valueOf(result.get("count"));
			actorBoost.add(preference);
		}
		
		BooleanQuery genreQuery = buildListTermQuery(MyMDBConstants.SOLR_DOC_FIELD_GENRE, genres, genreBoost, Occur.SHOULD);
		BooleanQuery actorQuery = buildListTermQuery(MyMDBConstants.SOLR_DOC_FIELD_ACTOR, actors, actorBoost, Occur.SHOULD);
		
		genreQuery.add(actorQuery, Occur.SHOULD);
		topQuery.add(genreQuery, Occur.MUST);
		
		
		TermQuery tq = new TermQuery(new Term(MyMDBConstants.SOLR_DOC_FIELD_USER, String.valueOf(userId)));
		topQuery.add(tq,Occur.MUST_NOT);
		
		return topQuery;
		
	}
	
	public List<String> getDocumentMatchesSolr(Query query, String solrBaseUrl, int start, int limit, String output){
		
		List<String> matchedMovieIdList = new ArrayList<String>();
		
		try{
			JSONObject solrResponseJson = getDocMatchResultsSolr(query, solrBaseUrl ,start, limit, output);
			JSONObject responseHeaders = solrResponseJson.getJSONObject("responseHeader");
			JSONObject responseObj = solrResponseJson.getJSONObject("response");
			
			JSONArray matchedDocResults = responseObj.getJSONArray("docs");
			if(matchedDocResults != null && matchedDocResults.length()>0){
				for(int i = 0 ; i < matchedDocResults.length(); i++) { 
					JSONObject movieObject = matchedDocResults.getJSONObject(i);
					String id = movieObject.getString(MyMDBConstants.SOLR_DOC_FIELD_ID);
					matchedMovieIdList.add(id);
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		
		return matchedMovieIdList;
	}

	private JSONObject getDocMatchResultsSolr(Query query, String solrBaseUrl,
			int start, int limit, String output) {
		try {
			JSONObject matchedDocs = null;
			String solrRequestUrl = solrBaseUrl + "?" + String.format("q=%s" + "&start=" + start + "&rows=" + limit + "&wt=json&fl=" + output,URLEncoder.encode(query.toString(), "UTF-8"));
			HttpURLConnection connection = (HttpURLConnection) (new URL(
					solrRequestUrl).openConnection());
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			if (connection.getResponseCode() != 200) {
				throw new Exception(
						"Response from solr is non 200. Actual response code:"
								+ connection.getResponseCode());
			}
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuffer solrResponse = new StringBuffer();
			String rdline = "";
			while ((rdline = rd.readLine()) != null) {
				solrResponse.append(rdline);
			}
			rd.close();
			matchedDocs = new JSONObject(solrResponse.toString());
			return matchedDocs;
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<String> getRecommendedMovieIds(int userId) {
		BooleanQuery bq = buildRecommenderQuery(userId, MyMDBConstants.DEFAULT_PREFERENCE_COUNT);
		return getDocumentMatchesSolr(bq, MyMDBConstants.SOLR_BASE_URL, 0, 20, MyMDBConstants.SOLR_DOC_FIELD_ID);
	}
	
	public void movieSolrSave(JSONObject json) {

		try {
			String jobsSolrUpdateUrl = MyMDBConstants.SOLR_BASE_URL.replace("select",
					"update/json/docs?wt=json&commit=true");
			HttpURLConnection connection = (HttpURLConnection) (new URL(
					jobsSolrUpdateUrl).openConnection());
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes("[" + json.toString()+"]");
			wr.flush();
			wr.close();
			StringBuffer OBResponse = new StringBuffer();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String rdline = "";
			while ((rdline = rd.readLine()) != null) {
				OBResponse.append(rdline);
			}
			System.out.println(OBResponse.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//public void movieUpdateSolr(Map<String, Object> updateSolrParams, int action) {
	public void movieUpdateSolr(JSONObject inputJson, int action) {
		try {
			if(MyMDBHelper.isEmpty(inputJson.get(MyMDBConstants.SOLR_DOC_FIELD_ID))){
				return;
			}
			JSONObject json = new JSONObject();
			json.put(MyMDBConstants.SOLR_DOC_FIELD_ID, inputJson.get(MyMDBConstants.SOLR_DOC_FIELD_ID));
			inputJson.remove(MyMDBConstants.SOLR_DOC_FIELD_ID);
			
			//Set<String> solrFields = updateSolrParams.keySet();
			Iterator<String> solrFields = inputJson.keys();
			String actionStr = getActionStringFromId(action);
			while (solrFields.hasNext()) {
				String field = solrFields.next();
				json.put(field, new JSONObject().put(actionStr , inputJson.get(field)));
			}
			String jsonStr = "["+json.toString()+"]";
			String jobsSolrUpdateUrl = MyMDBConstants.SOLR_BASE_URL.replace("select", "update?wt=json&commit=true");
			HttpURLConnection connection = (HttpURLConnection) (new URL(jobsSolrUpdateUrl).openConnection());
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(jsonStr);
			wr.flush();
			wr.close();
			StringBuffer OBResponse = new StringBuffer();
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String rdline = "";
			while ((rdline = rd.readLine()) != null) {
				OBResponse.append(rdline);
			}
//			System.out.println(OBResponse.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getActionStringFromId(int action) {
		switch(action) {
		case MyMDBConstants.SOLR_UPDATE_ACTION_SET: return "set"; 
		case MyMDBConstants.SOLR_UPDATE_ACTION_ADD: return "add"; 
		case MyMDBConstants.SOLR_UPDATE_ACTION_REMOVE: return "remove";
		}
		return "set";
	}
	
	
	public static void main(String[] args) throws JSONException {
		Map<String,Object> solrMovieDetails = new HashMap<String,Object>();
		solrMovieDetails.put("id", "11107");
		solrMovieDetails.put("movie_id", 11107);
		ArrayList<String> gList = new ArrayList<String>();
		gList.add("1");
		gList.add("2");
		gList.add("3");
		
		solrMovieDetails.put("genre", gList);
		solrMovieDetails.put("imdb_id", "tt123456");
		JSONObject json = new JSONObject(solrMovieDetails);
		json.put("genre", gList);
		System.out.println(json);
		new SolrHelper().movieSolrSave(json);
		
		
		JSONObject newJSON = new JSONObject();
		newJSON.put("id", "11107");
		ArrayList<String> aList = new ArrayList<String>();
		aList.add("11");
		aList.add("12");
		aList.add("13");
		
		newJSON.put("actor", aList);
		new SolrHelper().movieUpdateSolr(newJSON, 1);
		
		
		
		
	}
}
