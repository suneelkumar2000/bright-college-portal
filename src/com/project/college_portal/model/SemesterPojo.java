package com.project.college_portal.model;

public class SemesterPojo {
	private int id;
	private boolean isActive;
	
	public SemesterPojo() {}

	public SemesterPojo(int id, boolean isActive) {
		super();
		this.id = id;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Semester [id=" + id + ", isActive=" + isActive + "]";
	}
}
