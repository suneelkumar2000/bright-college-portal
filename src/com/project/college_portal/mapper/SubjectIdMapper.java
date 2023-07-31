package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.SubjectPojo;

public class SubjectIdMapper implements RowMapper<SubjectPojo>{
	public SubjectPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		SubjectPojo subjectPojo = new SubjectPojo();
		String id = rs.getString("id");
		subjectPojo.setId(id);
		return subjectPojo;
	}
}
