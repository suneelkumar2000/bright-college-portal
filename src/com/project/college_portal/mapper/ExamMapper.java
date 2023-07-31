package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.ExamPojo;

public class ExamMapper implements RowMapper<ExamPojo>{
	public ExamPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ExamPojo examPojo = new ExamPojo();
		int id = rs.getInt("id");
		String name = rs.getString("name");
		String subjectId = rs.getString("subject_id");
		String type = rs.getString("type");
		Date date = rs.getDate("date_");
		boolean isActive = rs.getBoolean("is_active");
		examPojo.setId(id);
		examPojo.setName(name);
		examPojo.setSubjectId(subjectId);
		examPojo.setType(type);
		examPojo.setDate(date);
		examPojo.setActive(isActive);
		return examPojo;
	}

}
