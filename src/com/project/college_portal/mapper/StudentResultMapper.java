package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.StudentResultPojo;

public class StudentResultMapper implements RowMapper<StudentResultPojo>{
	public StudentResultPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		StudentResultPojo studentResultPojo=new StudentResultPojo();
		
		int examId = rs.getInt("exam_id");
		String examName = rs.getString("exam_name");
		String examType = rs.getString("type");
		String subjectId = rs.getString("subject_id");
		String name = rs.getString("name");
		int semesterId = rs.getInt("semester_id");
		int marks = rs.getInt("marks");

		studentResultPojo.setExamId(examId);
		studentResultPojo.setExamName(examName);
		studentResultPojo.setExamType(examType);
		studentResultPojo.setSubjectId(subjectId);
		studentResultPojo.setSubjectName(name);
		studentResultPojo.setSemesterId(semesterId);
		studentResultPojo.setMarks(marks);
		return studentResultPojo ;
		
	}
}
