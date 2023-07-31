package com.project.college_portal.model;

public class AttendancePojo {

	private int userId;
	private int semester;
	private int totalDays;
	private int daysAttended;
	private int daysLeave;
	private int attendance;
	private boolean isActive;

	public AttendancePojo() {
	}

	public AttendancePojo(int userId, int semester, int totalDays, int daysAttended, int daysLeave, int attendance,
			boolean isActive) {
		super();
		this.userId = userId;
		this.totalDays = totalDays;
		this.daysAttended = daysAttended;
		this.daysLeave = daysLeave;
		this.attendance = attendance;
		this.isActive = isActive;
		this.setSemester(semester);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public int getDaysAttended() {
		return daysAttended;
	}

	public void setDaysAttended(int daysAttended) {
		this.daysAttended = daysAttended;
	}

	public int getDaysLeave() {
		return daysLeave;
	}

	public void setDaysLeave(int daysLeave) {
		this.daysLeave = daysLeave;
	}

	public int getAttendance() {
		return attendance;
	}

	public void setAttendance(int attendance) {
		this.attendance = attendance;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	@Override
	public String toString() {
		return "AttendancePojo [userId=" + userId + ", semester=" + semester + ", totalDays=" + totalDays
				+ ", daysAttended=" + daysAttended + ", daysLeave=" + daysLeave + ", attendance=" + attendance
				+ ", isActive=" + isActive + "]";
	}

}
