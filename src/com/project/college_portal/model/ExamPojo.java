package com.project.college_portal.model;

import java.sql.Date;

public class ExamPojo {
	private int id;
	private String subjectId;
	private String name;
	private String type;
	private Date date;
	private boolean isActive ;
	
	public ExamPojo() {}

	public ExamPojo(int id, String subjectId, String name, String type,Date date, boolean isActive) {
		super();
		this.id = id;
		this.subjectId = subjectId;
		this.name = name;
		this.type = type;
		this.date=date;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Exam [id=" + id + ", subjectId=" + subjectId + ", name=" + name + ", type=" + type + ", date=" + date
				+ ", isActive=" + isActive + "]";
	}
	
}
