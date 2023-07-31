package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.ExamPojo;

public class ExamIdMapper implements RowMapper<ExamPojo>{
	public ExamPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ExamPojo examPojo = new ExamPojo();
		int id = rs.getInt("id");
		examPojo.setId(id);
		return examPojo;
	}
}
