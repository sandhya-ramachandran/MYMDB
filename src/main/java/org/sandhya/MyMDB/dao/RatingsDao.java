package org.sandhya.MyMDB.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.sandhya.MyMDB.model.Ratings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository (value = "ratingsDao")
public class RatingsDao {
	public final String INSERT_SQL = "INSERT INTO ratings(movie_id,user_id,rating,review) values (?,?,?,?)"; 
	public final String UPDATE_SQL	= "UPDATE ratings set movie_id = ?, user_id= ?, rating = ?, review= ? where id = ?";
	public final String UPDATE_DUPLICATE_SQL = "UPDATE ratings set rating = ? , review = ? where movie_id = ? and user_id = ?";
	@Autowired
	public JdbcTemplate jdbcTemplate;
	
	public boolean save(final Ratings movieRatings) {
		if (movieRatings.getId() == 0) {
			try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int update = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(
					INSERT_SQL, new String[] { "id" });
					ps.setInt(1, movieRatings.getMovieId());
					ps.setInt(2, movieRatings.getUserId());
					ps.setFloat(3, movieRatings.getRating());
					ps.setString(4, movieRatings.getReview());
					return ps;
				}
			}, keyHolder);
			Number unId = keyHolder.getKey();
			movieRatings.setId(unId.intValue());
			
			} catch(org.springframework.dao.DuplicateKeyException e) {
				jdbcTemplate.update(UPDATE_DUPLICATE_SQL,
						new Object[] { 
								movieRatings.getRating(), movieRatings.getReview(),
								movieRatings.getMovieId(), movieRatings.getUserId() });
				 return false;	
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
		} else {
			jdbcTemplate.update(UPDATE_SQL,
					new Object[] { movieRatings.getMovieId(), movieRatings.getUserId(),
							movieRatings.getRating(), movieRatings.getReview(),
							movieRatings.getId() });
			 return false;
		}
		return true;
	}
	
	public Map<String,Object> getMovieAvgRating(int movieId) {
		String sql = "select avg(rating) as avgRating, max(rating) as maxRating, min(rating) as minRating, count(rating) as totalCount from ratings where movie_id = ?";
		Map<String,Object> ratings = jdbcTemplate.queryForMap(sql, new Object[]{movieId});
		return ratings;
	}
	
	public List<Map<String,Object>> getMovieReviews(int movieId, int offset, int limit ) {
		String sql = "select u.name, r.review from ratings r inner join user u on r.user_id = u.id where r.movie_id = ? limit ? offset ?";
		List<Map<String,Object>> reviewList = jdbcTemplate.queryForList(sql, new Object[] {movieId, limit, offset});
		return reviewList;
	}
	
	public int getTotalReviewCountForMovie(int movieId) {
		String sql = "select count(id) from ratings where movie_id = ? and review is not null";
		int count = 0 ;
		try {
			count = jdbcTemplate.queryForObject(sql, new Object[] {movieId}, Integer.class);
		} catch (Exception e) {
			count = 0 ;
		}
		return count ;
	}
	
	public Ratings getById(int ratingsId) {
		String sql = "select * from ratings where id = ?";
		List<Ratings> retlist = jdbcTemplate.query(sql, new Object[] { ratingsId },
				BeanPropertyRowMapper.newInstance(Ratings.class));
		if (retlist.size() > 0)
			return retlist.get(0);
		return null;
	}

}
