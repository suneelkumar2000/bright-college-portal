package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.UserPojo;

public class LoginMapper implements RowMapper<UserPojo>{
	public UserPojo mapRow(ResultSet rs,int rowNum) throws SQLException {
		UserPojo userPojo = new UserPojo();
		String email = rs.getString("email");
		String password = rs.getString("password");
		String roll = rs.getString("roll");
		userPojo.setEmail(email);
		userPojo.setPassword(password);
		userPojo.setRoll(roll);
		return userPojo;
	}
}

