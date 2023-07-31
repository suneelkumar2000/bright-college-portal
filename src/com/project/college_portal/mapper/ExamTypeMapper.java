package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.ExamPojo;

public class ExamTypeMapper implements RowMapper<ExamPojo>{
	public ExamPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ExamPojo examPojo = new ExamPojo();
		
		String type = rs.getString("type");
		
		examPojo.setType(type);
		
		return examPojo;
	}

}
