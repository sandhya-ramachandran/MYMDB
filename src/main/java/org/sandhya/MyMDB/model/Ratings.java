package org.sandhya.MyMDB.model;

import java.util.Date;

public class Ratings {
	
	private int id;
	private int movieId;
	private int userId;
	private Date createdTime;
	private String review;
	private float rating;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	
}
