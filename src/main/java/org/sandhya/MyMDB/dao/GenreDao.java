package org.sandhya.MyMDB.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sandhya.MyMDB.model.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


@Repository(value = "genreDao")
public class GenreDao {
	
	public final String INSERT_SQL = "INSERT INTO genre_master(name) values (?)"; 
	@Autowired  JdbcTemplate jdbcTemplate;
	@Autowired  NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public int save(final Genre genre) {
		
		if (genre.getId() == 0) {
			try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int update = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(
					INSERT_SQL, new String[] { "id" });
					ps.setString(1, genre.getName());
					return ps;
				}
			}, keyHolder);
			Number unId = keyHolder.getKey();
			genre.setId(unId.intValue());
		} catch (Exception e) {
			if (e.getLocalizedMessage().indexOf("Duplicate entry") > -1) {
				String sql = "select id from genre_master where name='"+ genre.getName() + "'";
				genre.setId(jdbcTemplate.queryForObject(sql,Integer.class));
			} else{
				e.printStackTrace();
			}
		}
		} else {
			return genre.getId();
		}
		return genre.getId();
	}
	
	public String getGenreIdByName(List<String> genreNames) {
		String sql = "select group_concat(id) from genre_master where name in (:genreNameList)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("genreNameList", genreNames);
		try {
			return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
		} catch (Exception e) {
			return null ;
		}
	}


}
