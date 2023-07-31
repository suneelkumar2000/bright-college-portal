package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.SubjectPojo;

public class SubjectMapper implements RowMapper<SubjectPojo>{
	public SubjectPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		SubjectPojo subjectPojo = new SubjectPojo();
		String id = rs.getString("id");
		String name = rs.getString("name");
		int semesterId = rs.getInt("semester_id");
		String department = rs.getString("department");
		boolean isActive = rs.getBoolean("is_active");
		subjectPojo.setId(id);
		subjectPojo.setName(name);
		subjectPojo.setSemesterId(semesterId);
		subjectPojo.setDepartment(department);
		subjectPojo.setActive(isActive);
		return subjectPojo;
	}

}
