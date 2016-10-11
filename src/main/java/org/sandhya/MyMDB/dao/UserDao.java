package org.sandhya.MyMDB.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.sandhya.MyMDB.model.Actor;
import org.sandhya.MyMDB.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository (value = "userDao")
public class UserDao {
	public final String INSERT_SQL = "INSERT INTO user(name,email) values (?,?)"; 
	@Autowired
	public JdbcTemplate jdbcTemplate;

	public int save(final User user) {
		
		if (user.getId() == 0) {
			try{
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int update = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(
					INSERT_SQL, new String[] { "id" });
					ps.setString(1, user.getName());
					ps.setString(2, user.getEmail());
					return ps;
				}
			}, keyHolder);
			Number unId = keyHolder.getKey();
			user.setId(unId.intValue());
		} catch (Exception e) {
			if (e.getLocalizedMessage().indexOf("Duplicate entry") > -1) {
				String sql = "select id from user where email='"+ user.getEmail() + "'";
				user.setId(jdbcTemplate.queryForObject(sql,Integer.class));
			} else{
				e.printStackTrace();
				return -1;	
			}
		}
		} else {
			String sql = "UPDATE user set name = ? where id = ? ";
			 jdbcTemplate.update(sql,
					new Object[] { user.getName(),user.getId() });
			
			return user.getId();
		}
		return user.getId();
	}
	
	public User getById(int id) {
		String sql = "select * from user where id = ?";
		List<User> retlist = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(User.class));
		if (retlist.size() > 0)
			return retlist.get(0);
		return null;
	}
	
	public User getByEmail(String email) {
		String sql = "select * from user where email = ?";
		List<User> retlist = jdbcTemplate.query(sql, new Object[] { email },
				BeanPropertyRowMapper.newInstance(User.class));
		if (retlist.size() > 0)
			return retlist.get(0);
		return null;
	}

	public boolean isUserPresent(String email) {
		String sql = "select count(*) from user where email = ?";
		int count = 0;
		try {
			jdbcTemplate.queryForObject(sql, new Object[] {email}, Integer.class);
		} catch (Exception e) {
			count = 0;
		}
		
		return count > 0 ;
	}
}
