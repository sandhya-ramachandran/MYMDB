package org.sandhya.MyMDB.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sandhya.MyMDB.model.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository (value = "actorDao")
public class ActorDao {
	public final String INSERT_SQL = "INSERT INTO actor_master (name) values (?)"; 
	@Autowired  JdbcTemplate jdbcTemplate;
	@Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public int save(final Actor actor) {

		if (actor.getId() == 0) {
			try {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				int update = jdbcTemplate.update(
						new PreparedStatementCreator() {
							public PreparedStatement createPreparedStatement(
									Connection connection) throws SQLException {
								PreparedStatement ps = connection
										.prepareStatement(INSERT_SQL,
												new String[] { "id" });
								ps.setString(1, actor.getName());
								return ps;
							}
						}, keyHolder);
				Number unId = keyHolder.getKey();
				actor.setId(unId.intValue());
			} catch (org.springframework.dao.DuplicateKeyException e) {
				String sql = "select id from actor where name= ?";
				actor.setId(jdbcTemplate.queryForObject(sql,
						new Object[] { actor.getName() }, Integer.class));

			} catch (Exception e) {
				return -1;
			}
		} else {
			return actor.getId();
		}
		return actor.getId();
	}
	
	public List<Map<String,Object>> getActorIdByName(List<String> names) {
		String sql = "select * from actor_master where name in (:actorList)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("actorList", names);
		try {
			return namedParameterJdbcTemplate.queryForList(sql, params);
		} catch (Exception e) {
			return null ;
		}
	}
}
