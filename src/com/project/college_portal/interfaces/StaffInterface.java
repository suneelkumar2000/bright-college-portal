package com.project.college_portal.interfaces;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.core.JsonProcessingException;
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

public interface StaffInterface {
	public int checkHigherAuthority(int staffId) throws HigherAuthorityException;
	public UserPojo findStudentDepartmentById(int userId);
	public List<UserPojo> findStudentById(int userId,Model model) ;
	public List<UserPojo> studentList(Model model) throws JsonProcessingException;
	public int approve(int staffId,UserPojo approveUser) throws UserIdException, HigherAuthorityException;
	public int reject(int staffId, UserPojo approveUser) throws UserIdException, HigherAuthorityException;
	public List<UserPojo> approvedStudentList(Model model) throws JsonProcessingException;
	public List<UserPojo> notApprovedStudentList();
	public int activateOrDeactivateStudent(UserPojo userPojo);
	public List<UserPojo> inactiveStudentList();
	
	public int addDepartment(int staffId,DepartmentPojo departmentPojo) throws ExistDepartmentNameException, HigherAuthorityException;
	public int activateOrDeactivateDepartment(DepartmentPojo departmentPojo);
	public List<DepartmentPojo> departmentList(Model model) throws JsonProcessingException;
	public List<DepartmentPojo> inactiveDepartmentList(Model model) throws JsonProcessingException;
	
	public int addOrUpdatePresentByOne(int userId,int semester) throws UserIdException;
	public int addOrUpdateAbsentByOne(int userId,int semester) throws UserIdException;
	public int activateOrDeactivateAttendance(AttendancePojo attendancePojo);
	public List<AttendancePojo> attendanceList();
	public List<AttendancePojo> inactiveAttendanceList();
	
	public int addSemester(SemesterPojo semesterPojo) throws ExistSemesterIdException;
	public int activateOrDeactivateSemester(SemesterPojo semesterPojo);
	public int activeOrInactiveSemester();
	public List<SemesterPojo> semesterList(Model model) throws JsonProcessingException;
	public List<SemesterPojo> activeSemesterList(Model model) throws JsonProcessingException;
	public List<SemesterPojo> inactiveSemesterList(Model model) throws JsonProcessingException;
	
	public int addSubject(SubjectPojo subjectPojo) throws SemesterIdException,DepartmentException, ExistSubjectNameException;
	public int activateOrDeactivateSubject(SubjectPojo subjectPojo);
	public SubjectPojo findByID(int id);
	public List<SubjectPojo> findSubjectID(String department, int semester,String name);
	public List<SubjectPojo> findSubjectIdByName(String name);
	public List<SubjectPojo> findSubjectNameByDepartmentSemester(String department,int semester);
	public List<SubjectPojo> findSubjectListBySemester(int semesterId,Model model) throws JsonProcessingException;
	public List<SubjectPojo> findSubjectList(int semesterId,String department,Model model) throws JsonProcessingException;
	public List<SubjectPojo> subjectList(Model model) throws JsonProcessingException;
	public List<SubjectPojo> inactivesubjectList(Model model) throws JsonProcessingException;
	
	public int addExam(ExamPojo examPojo) throws SubjectIdException, ExistExamException;
	public int activateOrDeactivateExam(ExamPojo examPojo);
	public List<ExamPojo> examList(Model model) throws JsonProcessingException;
	public List<ExamPojo> inactiveExamList(Model model) throws JsonProcessingException;
	public List<ExamPojo> findExamNameBySubjectID(String subjectID);
	public List<ExamPojo> findExamTypeBySubjectID(String subjectID);
	
	public int addOrUpdateResult(ResultPojo resultPojo) throws MarkException, UserIdException, ExamIdException;
	public int activateOrDeactivateOneResult(ResultPojo resultPojo);
	public int activateOrDeactivateWholeExamResult(ResultPojo resultPojo);
	public int activateOrDeactivateWholeUserResult(ResultPojo resultPojo);
	public List<ResultPojo> resultList(Model model) throws JsonProcessingException;
	public List<ResultPojo> inactiveResultList(Model model) throws JsonProcessingException;
	public void resultPopup(int userId, ModelMap map, Model model) throws JsonProcessingException;
	
}
