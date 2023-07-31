package com.project.college_portal.controller;

import java.sql.Date;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.college_portal.dao.UserDao;
import com.project.college_portal.exception.DepartmentException;
import com.project.college_portal.exception.ExamIdException;
import com.project.college_portal.exception.ExistDepartmentNameException;
import com.project.college_portal.exception.ExistExamException;
import com.project.college_portal.exception.ExistMailIdException;
import com.project.college_portal.exception.ExistSemesterIdException;
import com.project.college_portal.exception.ExistSubjectNameException;
import com.project.college_portal.exception.ForgotPasswordException;
import com.project.college_portal.exception.HigherAuthorityException;
import com.project.college_portal.exception.InvalidMailIdException;
import com.project.college_portal.exception.MarkException;
import com.project.college_portal.exception.SubjectIdException;
import com.project.college_portal.exception.UserIdException;
import com.project.college_portal.exception.AttendanceUserIdException;
import com.project.college_portal.model.UserPojo;
import com.project.college_portal.service.StaffService;
import com.project.college_portal.service.UserService;

@Controller
public class UserController {

	String errorMessage = "ErrorMessage";
	String sessionUserId = "userId";
	String errorpopup = "errorpopup";
	String redirectStudentHome = "redirect:/studentHome";
	String index = "index";

	UserDao userDao = new UserDao();
	UserService userService = new UserService();
	StaffService staffService = new StaffService();

	// --------- user method ---------

	// method to save register details
	@PostMapping(path = "/signupSubmit")
	public String saveUser(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("phone") Long phone, @RequestParam("gender") String gender, @RequestParam("roll") String roll,
			@RequestParam("DOB") Date dob) throws ExistMailIdException {
		UserPojo userPojo = new UserPojo();
		userPojo.setFirstName(firstName);
		userPojo.setLastName(lastName);
		userPojo.setEmail(email);
		userPojo.setPassword(password);
		userPojo.setPhone(phone);
		userPojo.setDOB(dob);
		userPojo.setGender(gender);
		userPojo.setRoll(roll);
		int value = userService.saveUser(userPojo);

		if (value == 1) {
			return "sucesspopup";
		}
		return "signup";

	}

	// method to get Login success
	@PostMapping(path = "/loginSubmit")
	public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model,
			HttpSession session) throws InvalidMailIdException, JsonProcessingException {
		UserPojo userPojo = new UserPojo();
		userPojo.setEmail(email);
		userPojo.setPassword(password);
		int value = userService.loginUser(userPojo, session);

		if (value == 1) {
			return redirectStudentHome;
		} else if (value == 2) {
			return "redirect:/adminHome";
		} else
			return index;
	}

	// method to get forgot password
	@PostMapping(path = "/forgotPassword")
	public String forgotPassword(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("phone") Long phone, HttpSession session) throws ForgotPasswordException {
		UserPojo userPojo = new UserPojo();
		userPojo.setEmail(email);
		userPojo.setPhone(phone);
		userPojo.setPassword(password);
		int value = userService.forgotPassword(userPojo, session);

		if (value == 1) {
			return redirectStudentHome;
		} else if (value == 2) {
			return "adminHome";
		} else
			return index;
	}

	// --------- student method ---------

	// method to update student Registration details
	@PostMapping(path = "/studentsave")
	public String studentsave(@RequestParam("phone") Long phone, @RequestParam("DOB") Date dob,
			@RequestParam("department") String department, @RequestParam("year") int year,
			@RequestParam("parentName") String parentName, HttpSession session) {
		int userId = (int) session.getAttribute(sessionUserId);
		UserPojo userPojo = new UserPojo();
		userPojo.setUserId(userId);
		userPojo.setPhone(phone);
		userPojo.setDOB(dob);
		userPojo.setDepartment(department);
		userPojo.setParentName(parentName);
		userPojo.setJoiningYear(year);

		int value = userService.studentsave(userPojo);

		if (value == 1) {
			return redirectStudentHome;
		}
		return "redirect:/studentRegistration";
	}

	// method to find Subject By semester
	@GetMapping(path = "/findsubjectListbySemester")
	public String findSubjectListBySemester(Model model, HttpSession session) throws JsonProcessingException {
		int semesterId = (int) session.getAttribute("semester");
		model.addAttribute("subjectList", staffService.findSubjectListBySemester(semesterId, model));
		return "subjectDetails";
	}

	// method to view student result
	@GetMapping(path = "/studentResult")
	public String findStudentResult(Model model, HttpSession session) throws JsonProcessingException {
		int userId = (int) session.getAttribute(sessionUserId);
		model.addAttribute("resultList", userDao.findStudentResult(userId, model));
		return "studentResult";
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

	// method to handle ForgotPasswordException
	@ExceptionHandler(value = ForgotPasswordException.class)
	public String forgotPasswordException(ForgotPasswordException exception, Model model) {
		model.addAttribute(errorMessage, "Sorry! Invalid Email Id or Phone Number");
		return errorpopup;
	}

	// method to handle AttendanceUserIdException
	@ExceptionHandler(value = AttendanceUserIdException.class)
	public String attendanceUserIdException(AttendanceUserIdException exception, Model model) {
		model.addAttribute(errorMessage, "Sorry! There Is No Attendance Details To Show");
		return errorpopup;
	}
}
