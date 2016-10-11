package org.sandhya.MyMDB.model;

public class ReviewComments {
	int id;
	int ratingsId;
	String comments;
	int vote;
	int userId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRatingsId() {
		return ratingsId;
	}
	public void setRatingsId(int ratingsId) {
		this.ratingsId = ratingsId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getVote() {
		return vote;
	}
	public void setVote(int vote) {
		this.vote = vote;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
