package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.ResultPojo;

public class ResultMapper implements RowMapper<ResultPojo>{
	public ResultPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultPojo resultPojo = new ResultPojo();
		int examId = rs.getInt("exam_id");
		int userId = rs.getInt("user_id");
		int marks = rs.getInt("marks");
		boolean isActive = rs.getBoolean("is_active");
		resultPojo.setExamId(examId);
		resultPojo.setUserId(userId);
		resultPojo.setMarks(marks);
		resultPojo.setIsActive(isActive);
		return resultPojo;
		}
}
