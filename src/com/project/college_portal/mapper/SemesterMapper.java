package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.SemesterPojo;

public class SemesterMapper implements RowMapper<SemesterPojo>{
	public SemesterPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		SemesterPojo semesterPojo = new SemesterPojo();
		int id = rs.getInt("id");
		boolean isActive = rs.getBoolean("is_active");
		semesterPojo.setId(id);
		semesterPojo.setActive(isActive);
		return semesterPojo;
	}

}
