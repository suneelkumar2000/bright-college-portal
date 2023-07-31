package com.project.college_portal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.project.college_portal.model.DepartmentPojo;

public class DepartmentMapper implements RowMapper<DepartmentPojo> {
	public DepartmentPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
		DepartmentPojo departmentPojo = new DepartmentPojo();
		int id = rs.getInt("id");
		String department1 = rs.getString("department");
		boolean isActive = rs.getBoolean("is_active");
		departmentPojo.setId(id);
		departmentPojo.setDepartment(department1);
		departmentPojo.setIsActive(isActive);
		return departmentPojo;
	}
}
