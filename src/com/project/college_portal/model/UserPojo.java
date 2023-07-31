package com.project.college_portal.model;

import java.sql.Date;

public class UserPojo {
	private int userId;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private Long phone;
	private Date dob;
	private String gender;
	private String roll;
	private String status;
	private String department;
	private String parentName;
	private int joiningYear;
	private int semester;
	private String image;
	private boolean isActive;
	
	public UserPojo() {}

	public UserPojo(int userId, String firstName, String lastName, String email, String password, Long phone, Date dOB,
			String gender, String roll, String status, String department, String parentName, int joiningYear,
			int semester, String image, boolean isActive) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.dob = dOB;
		this.gender = gender;
		this.roll = roll;
		this.status = status;
		this.department = department;
		this.parentName = parentName;
		this.joiningYear = joiningYear;
		this.semester = semester;
		this.image = image;
		this.isActive = isActive;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	public Date getDOB() {
		return dob;
	}

	public void setDOB(Date dOB) {
		dob = dOB;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRoll() {
		return roll;
	}

	public void setRoll(String roll) {
		this.roll = roll;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public int getJoiningYear() {
		return joiningYear;
	}

	public void setJoiningYear(int joiningYear) {
		this.joiningYear = joiningYear;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", phone=" + phone + ", DOB=" + dob + ", gender=" + gender + ", roll="
				+ roll + ", status=" + status + ", department=" + department + ", parentName=" + parentName
				+ ", joiningYear=" + joiningYear + ", semester=" + semester + ", image=" + image + ", isActive="
				+ isActive + "]";
	}
	
}
