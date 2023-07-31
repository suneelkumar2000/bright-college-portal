package com.project.college_portal.service;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.college_portal.dao.StaffDao;
import com.project.college_portal.dao.UserDao;
import com.project.college_portal.exception.DepartmentException;
import com.project.college_portal.exception.ExamIdException;
import com.project.college_portal.exception.ExistDepartmentNameException;
import com.project.college_portal.exception.ExistExamException;
import com.project.college_portal.exception.ExistSemesterIdException;
import com.project.college_portal.exception.ExistSubjectNameException;
import com.project.college_portal.exception.HigherAuthorityException;
import com.project.college_portal.exception.MarkException;
import com.project.college_portal.exception.SemesterIdException;
import com.project.college_portal.exception.SubjectIdException;
import com.project.college_portal.exception.UserIdException;
import com.project.college_portal.model.AttendancePojo;
import com.project.college_portal.model.DepartmentPojo;
import com.project.college_portal.model.ExamPojo;
import com.project.college_portal.model.ResultPojo;
import com.project.college_portal.model.SemesterPojo;
import com.project.college_portal.model.SubjectPojo;
import com.project.college_portal.model.UserPojo;

public class StaffService {
	UserDao userDao = new UserDao();
	StaffDao staffDao = new StaffDao();

	// method to find Subject List By Semester
	public List<SubjectPojo> findSubjectListBySemester(int semesterId, Model model) throws JsonProcessingException {
		return staffDao.findSubjectListBySemester(semesterId, model);

	}

	// method to get student list
	public List<UserPojo> studentList(Model model) throws JsonProcessingException {
		return staffDao.studentList(model);
	}

	// method to approve student
	public int approve(int staffId, UserPojo userPojo) throws UserIdException, HigherAuthorityException {
		return staffDao.approve(staffId, userPojo);
	}

	// method to reject student
	public int reject(int staffId, UserPojo userPojo) throws UserIdException, HigherAuthorityException {
		return staffDao.reject(staffId, userPojo);
	}

	// method to get department list
	public List<DepartmentPojo> departmentList(Model model) throws JsonProcessingException {
		return staffDao.departmentList(model);
	}

	// method to get inactiveDepartment list
	public List<DepartmentPojo> inactiveDepartmentList(Model model) throws JsonProcessingException {
		return staffDao.inactiveDepartmentList(model);
	}

	// method to add department
	public int addDepartment(int staffId, DepartmentPojo depart)
			throws ExistDepartmentNameException, HigherAuthorityException {
		return staffDao.addDepartment(staffId, depart);
	}

	// method to activate/Deactivate Department
	public int activateOrDeactivateDepartment(DepartmentPojo depart) {
		return staffDao.activateOrDeactivateDepartment(depart);
	}

	// method to get approvedStudentList
	public List<UserPojo> approvedStudentList(Model model) throws JsonProcessingException {
		return staffDao.approvedStudentList(model);
	}

	// method to get attendance List
	public List<AttendancePojo> attendanceList() {
		return staffDao.attendanceList();
	}

	// method to get inactiveAttendance List
	public List<AttendancePojo> inactiveAttendanceList() {
		return staffDao.inactiveAttendanceList();
	}

	// method to add Or Update Present By One
	public int addOrUpdatePresentByOne(int userId,int semester) throws UserIdException {
		return staffDao.addOrUpdatePresentByOne(userId,semester);
	}

	// method to add Or Update Absent By One
	public int addOrUpdateAbsentByOne(int userId,int semester) throws UserIdException {
		return staffDao.addOrUpdateAbsentByOne(userId,semester);
	}

	// method to activate Or Deactivate Attendance
	public int activateOrDeactivateAttendance(AttendancePojo attendancePojo) {
		return staffDao.activateOrDeactivateAttendance(attendancePojo);
	}

	// method to get active Or Inactive Semester
	public int activeOrInactiveSemester() {
		return staffDao.activeOrInactiveSemester();
	}

	// method to get semester List
	public List<SemesterPojo> semesterList(Model model) throws JsonProcessingException {
		return staffDao.semesterList(model);
	}

	// method to get active Semester List
	public List<SemesterPojo> activeSemesterList(Model model) throws JsonProcessingException {
		return staffDao.activeSemesterList(model);
	}

	// method to get inactive Semester List
	public List<SemesterPojo> inactiveSemesterList(Model model) throws JsonProcessingException {
		return staffDao.inactiveSemesterList(model);
	}

	// method to add Semester
	public int addSemester(SemesterPojo semesterPojo) throws ExistSemesterIdException {
		return staffDao.addSemester(semesterPojo);
	}

	// method to activate Or Deactivate Semester
	public int activateOrDeactivateSemester(SemesterPojo semesterPojo) {
		return staffDao.activateOrDeactivateSemester(semesterPojo);
	}

	// method to get subject list
	public List<SubjectPojo> subjectList(Model model) throws JsonProcessingException {
		return staffDao.subjectList(model);
	}

	// method to get inactive subject list
	public List<SubjectPojo> inactivesubjectList(Model model) throws JsonProcessingException {
		return staffDao.inactivesubjectList(model);
	}

	// method to add subject
	public int addSubject(SubjectPojo subjectPojo)
			throws SemesterIdException, DepartmentException, ExistSubjectNameException {
		return staffDao.addSubject(subjectPojo);
	}

	// method to activate Or Deactivate Subject
	public int activateOrDeactivateSubject(SubjectPojo subjectPojo) {
		return staffDao.activateOrDeactivateSubject(subjectPojo);
	}

	// method to get exam list
	public List<ExamPojo> examList(Model model) throws JsonProcessingException {
		return staffDao.examList(model);
	}

	// method to get inactive exam list
	public List<ExamPojo> inactiveExamList(Model model) throws JsonProcessingException {
		return staffDao.inactiveExamList(model);
	}

	// method to add exam
	public int addExam(ExamPojo examPojo) throws SubjectIdException, ExistExamException {
		return staffDao.addExam(examPojo);
	}

	// method to add exam
	public int activateOrDeactivateExam(ExamPojo examPojo) {
		return staffDao.activateOrDeactivateExam(examPojo);
	}

	// method to get result List
	public List<ResultPojo> resultList(Model model) throws JsonProcessingException {
		return staffDao.resultList(model);
	}

	// method to get inactive Result List
	public List<ResultPojo> inactiveResultList(Model model) throws JsonProcessingException {
		return staffDao.inactiveResultList(model);
	}

	// method to Add Or Update Result
	public int addOrUpdateResult(ResultPojo resultPojo) throws MarkException, UserIdException, ExamIdException {
		return staffDao.addOrUpdateResult(resultPojo);
	}

	// method to Activate Or Deactivate one Result of particular exam and user
	public int activateOrDeactivateOneResult(ResultPojo resultPojo) {
		return staffDao.activateOrDeactivateOneResult(resultPojo);
	}

	// method to Activate Or Deactivate Result of one whole exam
	public int activateOrDeactivateWholeExamResult(ResultPojo resultPojo) {
		return staffDao.activateOrDeactivateWholeExamResult(resultPojo);
	}

	// method to Activate Or Deactivate one Result of one whole user
	public int activateOrDeactivateWholeUserResult(ResultPojo resultPojo) {
		return staffDao.activateOrDeactivateWholeUserResult(resultPojo);
	}

	// method to find Subject List
	public List<SubjectPojo> findSubjectList(int value, String department, Model model) throws JsonProcessingException {
		return staffDao.findSubjectList(value, department, model);
	}

	// method to check Higher Authority
	public int checkHigherAuthority(int staffId) throws HigherAuthorityException {
		return staffDao.checkHigherAuthority(staffId);
	}

	// method to get result popup page
	public void resultPopup(int userId, ModelMap map, Model model) throws JsonProcessingException {
		staffDao.resultPopup(userId, map, model);
	}
}