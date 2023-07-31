package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.AttendancePojo;

public class AttendanceMapper implements RowMapper<AttendancePojo>{
	public AttendancePojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		AttendancePojo attendancePojo = new AttendancePojo();
		int userId = rs.getInt("user_id");
		int semester = rs.getInt("semester");
		int totalDays = rs.getInt("total_days");
		int daysAttended = rs.getInt("days_attended");
		int daysLeave = rs.getInt("days_leave");
		int attendancePercentage = rs.getInt("attendance");
		boolean isActive = rs.getBoolean("is_active");
		attendancePojo.setUserId(userId);
		attendancePojo.setSemester(semester);
		attendancePojo.setTotalDays(totalDays);
		attendancePojo.setDaysAttended(daysAttended);
		attendancePojo.setDaysLeave(daysLeave);
		attendancePojo.setAttendance(attendancePercentage);
		attendancePojo.setActive(isActive);
		return attendancePojo;
		}

}
