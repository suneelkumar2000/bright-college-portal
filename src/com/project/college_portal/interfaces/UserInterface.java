package com.project.college_portal.interfaces;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.college_portal.exception.AttendanceUserIdException;
import com.project.college_portal.exception.ExistMailIdException;
import com.project.college_portal.exception.ForgotPasswordException;
import com.project.college_portal.exception.InvalidMailIdException;
import com.project.college_portal.model.AttendancePojo;
import com.project.college_portal.model.StudentResultPojo;
import com.project.college_portal.model.UserPojo;

public interface UserInterface {
	public int save(UserPojo saveUser) throws ExistMailIdException;

	public int login(UserPojo loginUser) throws InvalidMailIdException;

	public List<UserPojo> listUsers();

	public int forgotPassword(UserPojo userPojo) throws ForgotPasswordException;

	public int findIdByEmail(String email);

	public int setUserSessionById(int userId, HttpSession session);

	public List<UserPojo> findByEmail(String email);

	public int studentsave(UserPojo userPojo);

	public void updateStudentSemester(Model model) throws JsonProcessingException;

	public int findStudentSemesterById(int userid, Model model) throws JsonProcessingException;

	public List<StudentResultPojo> findStudentResult(int userid, Model model) throws JsonProcessingException;

	public List<AttendancePojo> findStudentAttendance(int userId, Model model, HttpSession session)
			throws JsonProcessingException, AttendanceUserIdException;
}
