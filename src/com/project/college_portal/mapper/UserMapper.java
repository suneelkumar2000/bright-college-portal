package com.project.college_portal.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.UserPojo;

public class UserMapper implements RowMapper<UserPojo> {

	@Override
	public UserPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserPojo userPojo = new UserPojo();
		int userId = rs.getInt("id");
		String email = rs.getString("email");
		String firstName = rs.getString("first_name");
		String lastName = rs.getString("last_name");
		String password = rs.getString("password");
		Long phone = rs.getLong("phone_number");
		Date dob = rs.getDate("dob");
		String gender = rs.getString("gender");
		String roll = rs.getString("roll");
		String status = rs.getString("status");
		String department = rs.getString("department");
		String parentName = rs.getString("parent_name");
		int joiningYear = rs.getInt("year_of_joining");
		int semester = rs.getInt("semester");
		String image = rs.getString("image");
		boolean isActive = rs.getBoolean("is_active");
		
		userPojo.setUserId(userId);
		userPojo.setFirstName(firstName);
		userPojo.setLastName(lastName);
		userPojo.setEmail(email);
		userPojo.setPassword(password);
		userPojo.setPhone(phone);
		userPojo.setDOB(dob);
		userPojo.setGender(gender);
		userPojo.setRoll(roll);
		userPojo.setStatus(status);
		userPojo.setDepartment(department);
		userPojo.setParentName(parentName);
		userPojo.setJoiningYear(joiningYear);
		userPojo.setSemester(semester);
		userPojo.setImage(image);
		userPojo.setActive(isActive);
		
		return userPojo;
	}
}
