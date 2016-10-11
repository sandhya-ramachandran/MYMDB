package org.sandhya.MyMDB.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.sandhya.MyMDB.model.Genre;
import org.sandhya.MyMDB.util.MyMDBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository (value = "movieGenreDao" )
public class MovieGenreDao {
	
	@Autowired JdbcTemplate jdbcTemplate;

	public void addGenreList(final int movieId, final List<String> genreIdList) {
		String sql = "INSERT INTO movie_genre (movie_id, genre_id) VALUES (?,?)";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int genreId = MyMDBHelper.returnInt(genreIdList.get(i));
				ps.setInt(1, movieId);
				ps.setInt(2, genreId);
			}

			public int getBatchSize() {
				return genreIdList.size();
			}	
	    });
	}
	
	public List<Genre> getMovieGenreByMovieId(int id) {
		String sql = "select g.* from genre g inner join movie_genre mg on mg.genre_id = g.id inner join movie m on m.id = mg.movie_id where mc.id = ?";
		List<Genre> retlist = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(Genre.class));
		if (retlist.size() > 0)
			return retlist ;
		return null;
	}

	public void deleteAllGenreForMovie(int id) {
		String sql = "delete * from movie_genre where movie_id = ?";
		jdbcTemplate.update(sql, new Object[]{id});
	}

	public List<Map<String, Object>> getTopPreferredGenreForUser(int userId, int count) {
		String sql = "select count(*) as count, genre_id as gid from movie_genre mg inner join ratings r on r.`movie_id`=mg.movie_id where r.user_id = ?"
				+ " group by genre_id order by count(*) desc limit 0, ?";
		List<Map<String,Object>> topGenres = jdbcTemplate.queryForList(sql, new Object[]{userId, count});
		return topGenres;
	}
}
