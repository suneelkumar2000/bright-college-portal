package com.project.college_portal.model;

public class DepartmentPojo {
	private int id;
	private String department;
	private boolean isActive;

	public DepartmentPojo() {
	}

	public DepartmentPojo(int id, String department, boolean isActive) {
		super();
		this.id = id;
		this.department = department;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public boolean isIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Department [id=" + id + ", department=" + department + ", isActive=" + isActive + "]";
	}

}
