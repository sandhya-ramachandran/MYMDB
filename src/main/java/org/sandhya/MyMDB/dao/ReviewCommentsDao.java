package org.sandhya.MyMDB.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.sandhya.MyMDB.model.Ratings;
import org.sandhya.MyMDB.model.ReviewComments;
import org.sandhya.MyMDB.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository (value = "reviewCommentsDao")
public class ReviewCommentsDao {
	
	public final String INSERT_SQL = "INSERT INTO review_comments(ratings_id,user_id,vote,comments) values (?,?,?,?)"; 
	public final String UPDATE_SQL	= "UPDATE review_comments set comments = ? , vote = ? where id = ?";
	public final String UPDATE_DUPLICATE_SQL = "UPDATE review_comments set vote = ? , comments = ? where ratings_id = ? and user_id = ?";

	@Autowired
	public JdbcTemplate jdbcTemplate;
	
	public void save(final ReviewComments reviewComments) {
		if (reviewComments.getId() == 0) {
			try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int update = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(
					INSERT_SQL, new String[] { "id" });
					ps.setInt(1, reviewComments.getRatingsId());
					ps.setInt(2, reviewComments.getUserId());
					ps.setInt(3, reviewComments.getVote());
					ps.setString(4, reviewComments.getComments());
					return ps;
				}
			}, keyHolder);
			Number unId = keyHolder.getKey();
			reviewComments.setId(unId.intValue());
			}catch(org.springframework.dao.DuplicateKeyException e) {
					jdbcTemplate.update(UPDATE_DUPLICATE_SQL,
							new Object[] { 
									reviewComments.getVote(),reviewComments.getComments(), 
									reviewComments.getRatingsId(), reviewComments.getUserId() });
		} catch (Exception e) {
			e.printStackTrace();
			return 	;
		}
		} else {
			jdbcTemplate.update(UPDATE_SQL,
					new Object[] { reviewComments.getComments(), reviewComments.getVote(),
					reviewComments.getId() });
			 return ;
		}
		
	}
	
	
	

}
