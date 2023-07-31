package com.project.college_portal.model;

public class ResultPojo {
	private int examId ;
	private int userId ;
	private int marks ;
	private boolean isActive ;
	
	public ResultPojo() {}
	
	public ResultPojo(int examId, int userId, int marks, boolean isActive) {
		super();
		this.examId = examId;
		this.userId = userId;
		this.marks = marks;
		this.isActive = isActive;
	}

	public int getExamId() {
		return examId;
	}

	public void setExamId(int examId) {
		this.examId = examId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMarks() {
		return marks;
	}

	public void setMarks(int marks) {
		this.marks = marks;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Result [examId=" + examId + ", userId=" + userId + ", marks=" + marks + ", isActive=" + isActive
				+ "]";
	}
}
