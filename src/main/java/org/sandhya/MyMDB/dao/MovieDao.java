package org.sandhya.MyMDB.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.sandhya.MyMDB.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository (value = "movieDao")
public class MovieDao {

	@Autowired
	public JdbcTemplate jdbcTemplate;
	public final String INSERT_SQL = "INSERT INTO movie(title, imdb_id, release_date) values (?,?,?)";

	public int save(final Movie movie) {

		if (movie.getId() == 0) {
			try {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				int update = jdbcTemplate.update(
						new PreparedStatementCreator() {
							public PreparedStatement createPreparedStatement(
									Connection connection) throws SQLException {
								PreparedStatement ps = connection
										.prepareStatement(INSERT_SQL,
												new String[] { "id" });
								ps.setString(1, movie.getTitle());
								ps.setString(2, movie.getImdbId());
								ps.setTimestamp(3, new Timestamp(movie
										.getReleaseDate().getTime()));
								return ps;
							}
						}, keyHolder);
				Number unId = keyHolder.getKey();
				movie.setId(unId.intValue());
				return unId.intValue();
			} catch(org.springframework.dao.DuplicateKeyException e) {
				return -1;
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		} else {
			String sql = "UPDATE movie  set "
					+ "title = ? ,imdb_id = ? ,"
					+ "release_date = ? "
					+ " where id = ? ";
			 jdbcTemplate.update(sql,
					new Object[] { movie.getTitle(), movie.getImdbId(),
							new Timestamp(movie.getReleaseDate().getTime()),
							movie.getId() });
			 return movie.getId();
			
		}

	}
	
	public Movie getMovieById(int id) {
		String sql = "select * from movie where id = ?";
		List<Movie> retlist = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(Movie.class));
		if (retlist.size() > 0)
			return retlist.get(0);
		return null;
	}

	public boolean checkIfPresent(String imdbId) {
		String sql = "select count(*) from movie where imdb_id = ?";
		try {
			int count = jdbcTemplate.queryForObject(sql, Integer.class);
			return count > 0 ;
		} catch (Exception e) {
			return false;
		}
	}

	public Map<String, Object> getMovieDetailsById(int id) {
		String sql = "select m.title, m.imdb_id, m.release_date, group_concat(gm.name) as genre from movie m inner join movie_genre mg on mg.movie_id = m.id "
				+ "inner join genre_master gm on mg.genre_id = gm.id where m.id = ?";
		List<Map<String,Object>> movieList = jdbcTemplate.queryForList(sql, new Object[] {id});
		if(movieList.size() > 0) {
			return movieList.get(0);
		}
		return null ;
	}
	

}
