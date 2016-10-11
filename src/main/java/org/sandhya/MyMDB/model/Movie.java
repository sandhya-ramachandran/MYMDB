package org.sandhya.MyMDB.model;

import java.util.Date;

public class Movie {
	
	private int id;
	private String imdbId;
	private String title;
	private Date releaseDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImdbId() {
		return imdbId;
	}
	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	

}
