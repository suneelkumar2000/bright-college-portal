package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.UserPojo;

public class ApprovingMapper implements RowMapper<UserPojo>{
	public UserPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserPojo userPojo = new UserPojo();
		int userId = rs.getInt("id");
		String roll = rs.getString("roll");
		String status = rs.getString("status");
		boolean isActive = rs.getBoolean("is_active");
		userPojo.setUserId(userId);
		userPojo.setRoll(roll);
		userPojo.setStatus(status);
		userPojo.setActive(isActive);
		return userPojo;
		
	}
}
