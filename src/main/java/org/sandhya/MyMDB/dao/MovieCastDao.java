package org.sandhya.MyMDB.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.sandhya.MyMDB.model.Actor;
import org.sandhya.MyMDB.model.Movie;
import org.sandhya.MyMDB.model.MovieCast;
import org.sandhya.MyMDB.util.MyMDBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository (value = "movieCastDao")
public class MovieCastDao {
	
	@Autowired
	public JdbcTemplate jdbcTemplate;
	public final String INSERT_SQL = "INSERT INTO movie_cast(movie_id, actor_id) values (?,?)";

	public int save(final MovieCast cast) {

		if (cast.getId() == 0) {
			try {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				int update = jdbcTemplate.update(
						new PreparedStatementCreator() {
							public PreparedStatement createPreparedStatement(
									Connection connection) throws SQLException {
								PreparedStatement ps = connection
										.prepareStatement(INSERT_SQL,
												new String[] { "id" });
								ps.setInt(1, cast.getMovieId());
								ps.setInt(2, cast.getActorId());
								
								return ps;
							}
						}, keyHolder);
				Number unId = keyHolder.getKey();
				cast.setId(unId.intValue());
				return unId.intValue();
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		} else {
			String sql = "UPDATE movie_cast  set "
					+ "movie_id = ? ,actor_id = ? "
					+ " where id = ? ";
			 jdbcTemplate.update(sql,
					new Object[] {cast.getMovieId(), cast.getActorId(),
							cast.getId() });
			 return cast.getId();
			
		}

	}
	
	public void addActorList(final int movieId, final List<Integer> actorIdList) {
		String sql = "INSERT INTO movie_cast (movie_id, actor_id) VALUES (?,?)";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int actorId = actorIdList.get(i);
				ps.setInt(1, movieId);
				ps.setInt(2, actorId);
			}

			public int getBatchSize() {
				return actorIdList.size();
			}	
	    });
	}
	
	public Actor getMovieCastById(int id) {
		String sql = "select a.* from actor a inner join movie_cast mc on mc.actor_id = a.id where mc.id = ?";
		List<Actor> retlist = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(Actor.class));
		if (retlist.size() > 0)
			return retlist.get(0);
		return null;
	}
	
	public List<Actor> getMovieCastByMovieId(int id) {
		String sql = "select a.* from actor_master a inner join movie_cast mc on mc.actor_id = a.id inner join movie m on m.id = mc.movie_id where m.id = ?";
		List<Actor> retlist = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(Actor.class));
		if (retlist.size() > 0)
			return retlist ;
		return null;
	}

	public void deleteAllActorsForMovie(int id) {
		String sql = "delete from movie_cast where movie_id = ?";
		jdbcTemplate.update(sql, new Object[]{id});
		
	}
	
	public List<Map<String, Object>> getTopPreferredActorsForUser(int userId, int count) {
		String sql = "select count(*) as count, actor_id as aid from movie_cast mc inner join ratings r on r.movie_id = mc.movie_id where r.user_id = ? "
				+ "group by actor_id order by count(*) desc limit 0, ?";
		List<Map<String,Object>> topActor = jdbcTemplate.queryForList(sql, new Object[]{userId, count});
		return topActor;
	}
		
	

}
