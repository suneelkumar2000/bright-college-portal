package com.project.college_portal.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.college_portal.exception.ExistDepartmentNameException;
import com.project.college_portal.exception.ExistExamException;
import com.project.college_portal.exception.ExistSemesterIdException;
import com.project.college_portal.exception.ExistSubjectNameException;
import com.project.college_portal.exception.ExistMailIdException;
import com.project.college_portal.exception.InvalidMailIdException;
import com.project.college_portal.exception.AttendanceUserIdException;
import com.project.college_portal.exception.DepartmentException;
import com.project.college_portal.exception.ExamIdException;
import com.project.college_portal.exception.MarkException;
import com.project.college_portal.exception.SubjectIdException;
import com.project.college_portal.exception.UserIdException;
import com.project.college_portal.service.StaffService;
import com.project.college_portal.service.UserService;
import com.project.college_portal.exception.HigherAuthorityException;

@Controller
public class HomeController {

	String sessionUserId = "userId";
	String sessionDepartment = "department";
	String sessionSemester = "semester";
	String modelUserId = "userId";
	String modelSemester = "semester";
	String errorpopup = "errorpopup";
	String errorMessage = "ErrorMessage";
	String modeldepartmentList = "departmentList";
	String modelsubjectList = "subjectList";
	String modelAttendanceDetails = "attendanceDetails";

	Logger logger = LoggerFactory.getLogger(HomeController.class);
	UserService userService = new UserService();
	StaffService staffService = new StaffService();
	@Value("${email}")
	String email;

	// method to get index page
	@GetMapping(path = "/index")
	public String index() {
		logger.info("Email : ", email);
		return "index";
	}

	// method to get register form
	@GetMapping(path = "/register")
	public String getSignUpForm() {
		return "signup";
	}

	// method to get admin home
	@GetMapping(path = "/adminHome")
	public String adminHome(Model model) throws JsonProcessingException {
		userService.updateStudentSemester(model);
		return "adminHome";
	}

	// method to get student home
	@GetMapping(path = "/studentHome")
	public String studentHome(ModelMap map, Model model, HttpSession session) throws JsonProcessingException {
		userService.updateStudentSemester(model);
		int userId = (int) session.getAttribute(sessionUserId);
		userService.setUserSessionById(userId, session);
		int value = userService.findStudentSemesterById(userId, model);
		if (value > 0) {
			String department = (String) session.getAttribute(sessionDepartment);
			session.setAttribute(sessionSemester, value);
			map.addAttribute(modelSemester, value);
			map.addAttribute(modelsubjectList, staffService.findSubjectList(value, department, model));
		} else {
			model.addAttribute(modelSemester, value);
		}
		return "Home";
	}

	// method to get studentRegistration form
	@GetMapping(path = "/studentRegistration")
	public String studentProfile(Model model) throws JsonProcessingException {
		model.addAttribute(modeldepartmentList, staffService.departmentList(model));
		return "studentRegistrationForm";
	}

	// method to get student attendance
	@GetMapping(path = "/attendance")
	public String attendance(Model model, HttpSession session)
			throws JsonProcessingException, AttendanceUserIdException {
		int userId = (int) session.getAttribute(sessionUserId);
		model.addAttribute(modelAttendanceDetails, userService.findStudentAttendance(userId, model, session));
		return "attendance";
	}

	// method to get Attendance Admin page
	@GetMapping(path = "/attendanceAdmin")
	public String adminAttendance(Model model) throws JsonProcessingException {
		model.addAttribute("studentList", staffService.approvedStudentList(model));
		return "attendanceAdmin";
	}

	// method to get logout confirmation
	@GetMapping(path = "/logout")
	public String logout() {
		return "logoutpopup";
	}

	// method to get logout
	@GetMapping(path = "/confirmlogout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "index";
	}

	// method to get result Admin page
	@GetMapping(path = "/resultAdmin")
	public String adminResult(Model model) throws JsonProcessingException {
		model.addAttribute("approvedStudentList", staffService.approvedStudentList(model));
		return "resultAdmin";
	}

	// method to get result popup page
	@GetMapping(path = "/resultPopup")
	public String resultPopup(@RequestParam("userId") int userId, ModelMap map, Model model)
			throws JsonProcessingException {
		staffService.resultPopup(userId, map, model);
		return "resultPopup";
	}

	// method to get Subject Details
	@GetMapping(path = "/subjectDetails")
	public String subjectDetails() {
		return "redirect:/subjectlist";
	}

	// method to get department form
	@GetMapping(path = "/insertDepartmentForm")
	public String departmentForm(HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		return "departmentForm";
	}

	// method to get semester form
	@GetMapping(path = "/insertSemesterForm")
	public String semesterForm(HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		return "semesterForm";
	}

	// method to get subject form
	@GetMapping(path = "/insertSubjectForm")
	public String subjectForm(ModelMap map, Model model, HttpSession session)
			throws JsonProcessingException, HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		map.addAttribute(modeldepartmentList, staffService.departmentList(model));
		map.addAttribute("semesterList", staffService.semesterList(model));
		return "subjectForm";
	}

	// method to get exam form
	@GetMapping(path = "/insertExamForm")
	public String examForm(Model model) throws JsonProcessingException {
		model.addAttribute(modelsubjectList, staffService.subjectList(model));
		return "examForm";
	}

	// ----------Exception methods---------

	// method to handle ExistDepartmentNameException
	@ExceptionHandler(value = ExistDepartmentNameException.class)
	public String existDepartmentNameException(ExistDepartmentNameException exception, Model model) {
		model.addAttribute(errorMessage, "Department Already Exist");
		return errorpopup;
	}

	// method to handle ExistExamException
	@ExceptionHandler(value = ExistExamException.class)
	public String existExamException(ExistExamException exception, Model model) {
		model.addAttribute(errorMessage, "Exam Already Exist");
		return errorpopup;
	}

	// method to handle ExistMailIdException
	@ExceptionHandler(value = ExistMailIdException.class)
	public String existMailIdException(ExistMailIdException exception, Model model) {
		model.addAttribute(errorMessage, "Sorry! This Email Id Already Exist");
		return errorpopup;
	}

	// method to handle InvalidMailIdException
	@ExceptionHandler(value = InvalidMailIdException.class)
	public String invalidMailIdException(InvalidMailIdException exception, Model model) {
		model.addAttribute(errorMessage, "Sorry! Invalid Email Id And Password");
		return errorpopup;
	}

	// method to handle ExamIdException
	@ExceptionHandler(value = ExamIdException.class)
	public String examIdException(ExamIdException exception, Model model) {
		model.addAttribute(errorMessage, "Exam Id dosen't Exist");
		return errorpopup;
	}

	// method to handle MarkException
	@ExceptionHandler(value = MarkException.class)
	public String markException(MarkException exception, Model model) {
		model.addAttribute(errorMessage, "Invalid Marks ,Marks should be between 0 to 100");
		return errorpopup;
	}

	// method to handle ExistSemesterIdException
	@ExceptionHandler(value = ExistSemesterIdException.class)
	public String existSemesterIdException(ExistSemesterIdException exception, Model model) {
		model.addAttribute(errorMessage, "Semester Already Exist");
		return errorpopup;
	}

	// method to handle SubjectIdException
	@ExceptionHandler(value = SubjectIdException.class)
	public String subjectIdException(SubjectIdException exception, Model model) {
		model.addAttribute(errorMessage, "Subject Id dosen't Exist");
		return errorpopup;
	}

	// method to handle UserIdException
	@ExceptionHandler(value = UserIdException.class)
	public String userIdException(UserIdException exception, Model model) {
		model.addAttribute(errorMessage, "User dosen't Exist");
		return errorpopup;
	}

	// method to handle HigherAuthorityException
	@ExceptionHandler(value = HigherAuthorityException.class)
	public String higherAuthorityException(HigherAuthorityException exception, Model model) {
		model.addAttribute(errorMessage, "opps sorry! only HigherAuthority can do this Process");
		return errorpopup;
	}

	// method to handle DepartmentException
	@ExceptionHandler(value = DepartmentException.class)
	public String departmentException(DepartmentException exception, Model model) {
		model.addAttribute(errorMessage, "Department dosen't Exist");
		return errorpopup;
	}

	// method to handle ExistSubjectNameException
	@ExceptionHandler(value = ExistSubjectNameException.class)
	public String existSubjectNameException(ExistSubjectNameException exception, Model model) {
		model.addAttribute(errorMessage, "Subject Already Exist");
		return errorpopup;
	}

	// method to handle AttendanceUserIdException
	@ExceptionHandler(value = AttendanceUserIdException.class)
	public String attendanceUserIdException(AttendanceUserIdException exception, Model model) {
		model.addAttribute(errorMessage, "Sorry! There Is No Attendance Details To Show");
		return errorpopup;
	}
}
