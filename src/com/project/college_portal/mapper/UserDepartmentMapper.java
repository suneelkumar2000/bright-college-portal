package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.UserPojo;

public class UserDepartmentMapper implements RowMapper<UserPojo> {
	public UserPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserPojo userPojo = new UserPojo();
		String department = rs.getString("department");
		userPojo.setDepartment(department);
		return userPojo;
	}
}
