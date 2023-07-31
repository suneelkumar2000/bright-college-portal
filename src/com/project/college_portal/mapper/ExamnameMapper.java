package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.ExamPojo;

public class ExamnameMapper implements RowMapper<ExamPojo>{
	public ExamPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ExamPojo examPojo = new ExamPojo();
		String name = rs.getString("name");
		
		examPojo.setName(name);
		
		return examPojo;
	}
}
