package com.project.college_portal.controller;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.college_portal.dao.StaffDao;
import com.project.college_portal.exception.DepartmentException;
import com.project.college_portal.exception.ExamIdException;
import com.project.college_portal.exception.ExistDepartmentNameException;
import com.project.college_portal.exception.ExistExamException;
import com.project.college_portal.exception.ExistMailIdException;
import com.project.college_portal.exception.ExistSemesterIdException;
import com.project.college_portal.exception.ExistSubjectNameException;
import com.project.college_portal.exception.HigherAuthorityException;
import com.project.college_portal.exception.InvalidMailIdException;
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
import com.project.college_portal.service.StaffService;

@Controller
public class StaffController {

	StaffDao staffDao = new StaffDao();
	StaffService staffService = new StaffService();
	
	String sessionUserId = "userId";
	String errorMessage = "ErrorMessage";
	String errorpopup = "errorpopup";
	String modeldepartmentList="departmentList";
	String modelattendanceList="attendanceList";
	String modelsemesterList="semesterList";
	String modelsubjectList="subjectList";
	String returnDepartment="department";
	String returnAttendanceAdmin="attendanceAdmin";
	String returnSemester="semester";
	String returnSubjectDetails="subjectDetails";
	String returnExamDetails="examDetails";
	String returnResultAdmin="resultAdmin";
	String redirectDepartmentlist="redirect:/departmentlist";
	String redirectAttendanceAdmin="redirect:/attendanceAdmin";
	String redirectSemesterlist="redirect:/semesterlist";
	String redirectSubjectlist="redirect:/subjectlist";
	String redirectExamlist="redirect:/examlist";
	String redirectResultlist="redirect:/resultlist";
	String redirectInactiveResultlist="redirect:/inactiveResultlist";
	String redirectlistofusers="redirect:/listofusers";

	// method to get student list
	@GetMapping(path = "/listofusers")
	public String getAllUser(Model model) throws JsonProcessingException {
		List<UserPojo> userPojos = staffService.studentList(model);
		model.addAttribute("USER_LIST", userPojos);
		return "listusers";
	}

	// method to approve student
	@GetMapping(path = "/approve")
	public String approve(@RequestParam("userID") int userID, HttpSession session)
			throws UserIdException, HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		UserPojo userPojo = new UserPojo();
		userPojo.setUserId(userID);
		staffService.approve(staffId, userPojo);
		return redirectlistofusers;
	}
	
	// method to reject student
		@GetMapping(path = "/reject")
		public String reject(@RequestParam("userID") int userID, HttpSession session)
				throws UserIdException, HigherAuthorityException {
			int staffId = (int) session.getAttribute(sessionUserId);
			UserPojo userPojo = new UserPojo();
			userPojo.setUserId(userID);
			staffService.reject(staffId, userPojo);
			return redirectlistofusers;
		}

	// --------- Department methods ------------

	// method to get department list
	@GetMapping(path = "/departmentlist")
	public String departmentList(Model model) throws JsonProcessingException {
		model.addAttribute(modeldepartmentList, staffService.departmentList(model));
		return returnDepartment;
	}

	// method to get inactiveDepartment list
	@GetMapping(path = "/inactiveDepartmentlist")
	public String inactiveDepartmentList(Model model) throws JsonProcessingException {
		model.addAttribute(modeldepartmentList, staffService.inactiveDepartmentList(model));
		return returnDepartment;
	}

	// method to add department
	@PostMapping(path = "/insertdepartment")
	public String addDepartment(@RequestParam("department") String department, Model model, HttpSession session)
			throws ExistDepartmentNameException, HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		DepartmentPojo depart = new DepartmentPojo();
		depart.setDepartment(department);
		int value = staffService.addDepartment(staffId, depart);
		if (value == 1) {
			return redirectDepartmentlist;
		} else
			return "departmentForm";
	}

	// method to activate/Deactivate Department
	@GetMapping(path = "/activateDeactivateDepartment")
	public String activateOrDeactivateDepartment(@RequestParam("name") String name, Model model,HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		DepartmentPojo departmentPojo = new DepartmentPojo();
		departmentPojo.setDepartment(name);
		int value = staffService.activateOrDeactivateDepartment(departmentPojo);
		if (value == 1) {
			return redirectDepartmentlist;
		} else if (value == 2) {
			return "redirect:/inactiveDepartmentlist";
		} else
			return returnDepartment;
	}

	// --------- Attendance methods ------------

	// method to get attendance List
	@GetMapping(path = "/attendancelist")
	public String attendanceList(Model model) {
		model.addAttribute(modelattendanceList, staffService.attendanceList());
		return returnAttendanceAdmin;
	}

	// method to get inactiveAttendance List
	@GetMapping(path = "/inactiveAttendancelist")
	public String inactiveAttendanceList(Model model) {
		model.addAttribute(modelattendanceList, staffService.inactiveAttendanceList());
		return returnAttendanceAdmin;
	}

	// method to add present
	@GetMapping(path = "/addUpdatePresentbyone")
	public String addOrUpdatePresentByOne(@RequestParam("userId") int userId,@RequestParam("semester") int semester, Model model)
			throws UserIdException, JsonProcessingException {
		int value = staffService.addOrUpdatePresentByOne(userId,semester);
		if (value == 1) {
			return redirectAttendanceAdmin;
		} else
			return returnAttendanceAdmin;
	}

	// method to add absent
	@GetMapping(path = "/addUpdateAbsentbyone")
	public String addOrUpdateAbsentByOne(@RequestParam("userId") int userId,@RequestParam("semester") int semester, Model model)
			throws UserIdException, JsonProcessingException {

		int value = staffService.addOrUpdateAbsentByOne(userId,semester);
		if (value == 1) {
			return redirectAttendanceAdmin;
		} else
			return returnAttendanceAdmin;
	}

	// method to activate Or Deactivate Attendance
	@GetMapping(path = "/activateDeactivateAttendance/{userId}")
	public String activateOrDeactivateAttendance(@PathVariable(value = "userId") int userId, Model model) {
		AttendancePojo attendancePojo = new AttendancePojo();
		attendancePojo.setUserId(userId);
		int value = staffService.activateOrDeactivateAttendance(attendancePojo);
		if (value == 1) {
			return "redirect:/attendancelist";
		} else
			return returnAttendanceAdmin;
	}

	// --------- Semester methods ------------

	// method to get Semester List
	@GetMapping(path = "/semesterlist")
	public String semesterList(Model model) throws JsonProcessingException {
		staffService.activeOrInactiveSemester();
		model.addAttribute(modelsemesterList, staffService.semesterList(model));
		return returnSemester;
	}

	// method to get active Semester List
	@GetMapping(path = "/activeSemesterlist")
	public String activeSemesterList(Model model) throws JsonProcessingException {
		staffService.activeOrInactiveSemester();
		model.addAttribute(modelsemesterList, staffService.activeSemesterList(model));
		return returnSemester;
	}

	// method to get inactive Semester List
	@GetMapping(path = "/inactiveSemesterlist")
	public String inactiveSemesterList(Model model) throws JsonProcessingException {
		staffService.activeOrInactiveSemester();
		model.addAttribute(modelsemesterList, staffService.inactiveSemesterList(model));
		return returnSemester;
	}

	// method to add Semester
	@PostMapping(path = "/addsemester")
	public String addSemester(@RequestParam("semesterId") int semesterId, Model model) throws ExistSemesterIdException {
		SemesterPojo semesterPojo = new SemesterPojo();
		semesterPojo.setId(semesterId);
		int value = staffService.addSemester(semesterPojo);

		if (value == 1) {
			return redirectSemesterlist;
		} else
			return returnSemester;
	}

	// method to activate Or Deactivate Semester
	@GetMapping(path = "/activateDeactivateSemester/{semesterId}")
	public String activateOrDeactivateSemester(@PathVariable(value = "semesterId") int semesterId, Model model) {
		SemesterPojo semesterPojo = new SemesterPojo();
		semesterPojo.setId(semesterId);
		int value = staffService.activateOrDeactivateSemester(semesterPojo);
		if (value == 1) {
			return redirectSemesterlist;
		} else if (value == 2) {
			return "redirect:/inactiveSemesterlist";
		} else
			return returnSemester;
	}

	// --------- Subject methods ------------

	// method to get subject list
	@GetMapping(path = "/subjectlist")
	public String subjectList(Model model) throws JsonProcessingException {
		model.addAttribute(modelsubjectList, staffService.subjectList(model));
		return returnSubjectDetails;
	}

	// method to get inactive subject list
	@GetMapping(path = "/inactiveSubjectlist")
	public String inactivesubjectList(Model model) throws JsonProcessingException {
		model.addAttribute(modelsubjectList, staffService.inactivesubjectList(model));
		return returnSubjectDetails;
	}

	// method to add subject
	@PostMapping(path = "/addsubject")
	public String addSubject(@RequestParam("name") String name, @RequestParam("semesterId") int semesterId,
			@RequestParam("department") String department, Model model)
			throws SemesterIdException, DepartmentException, ExistSubjectNameException {
		SubjectPojo subjectPojo = new SubjectPojo();
		subjectPojo.setName(name);
		subjectPojo.setSemesterId(semesterId);
		subjectPojo.setDepartment(department);
		int value = staffService.addSubject(subjectPojo);
		if (value == 1) {
			return redirectSubjectlist;
		} else
			return returnSubjectDetails;
	}

	// method to find Subject By ID

	// method to activate/Deactivate Subject
	@GetMapping(path = "/activateDeactivateSubject")
	public String activateOrDeactivateSubject(@RequestParam("subjectId") String subjectId, Model model,HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		SubjectPojo subjectPojo = new SubjectPojo();
		subjectPojo.setId(subjectId);
		int value = staffService.activateOrDeactivateSubject(subjectPojo);
		if (value == 1) {
			return redirectSubjectlist;
		} else if (value == 2) {
			return "redirect:/inactiveSubjectlist";
		} else
			return returnSubjectDetails;
	}

	// --------- Exam methods ------------

	// method to get exam list
	@GetMapping(path = "/examlist")
	public String examList(Model model) throws JsonProcessingException {
		model.addAttribute("examList", staffService.examList(model));
		return returnExamDetails;
	}

	// method to get inactive exam list
	@GetMapping(path = "/inactiveExamlist")
	public String inactiveExamList(Model model) throws JsonProcessingException {
		model.addAttribute("inactiveExamList", staffService.inactiveExamList(model));
		return returnExamDetails;
	}

	// method to add exam
	@PostMapping(path = "/addexam")
	public String addExam(@RequestParam("name") String name, @RequestParam("date") Date date,
			@RequestParam("subjectId") String subjectId, @RequestParam("type") String type, Model model)
			throws SubjectIdException, ExistExamException {
		ExamPojo examPojo = new ExamPojo();
		examPojo.setName(name);
		examPojo.setSubjectId(subjectId);
		examPojo.setDate(date);
		examPojo.setType(type);
		int value = staffService.addExam(examPojo);
		if (value == 1) {
			return redirectExamlist;
		} else
			return returnExamDetails;
	}

	// method to activate Or Deactivate Exam
	@GetMapping(path = "/activateDeactivateExam")
	public String activateOrDeactivateExam(@RequestParam("examId") int examId, Model model,HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		ExamPojo examPojo = new ExamPojo();
		examPojo.setId(examId);
		int value = staffService.activateOrDeactivateExam(examPojo);
		if (value == 1) {
			return redirectExamlist;
		} else if (value == 2) {
			return "redirect:/inactiveExamlist";
		} else
			return returnExamDetails;
	}

	// --------- Result methods ------------

	// method to get result List
	@GetMapping(path = "/resultlist")
	public String resultList(Model model) throws JsonProcessingException {
		model.addAttribute("resultList", staffService.resultList(model));
		return returnResultAdmin;
	}

	// method to get inactive Result List
	@GetMapping(path = "/inactiveResultlist")
	public String inactiveResultList(Model model) throws JsonProcessingException {
		model.addAttribute("inactiveResultList", staffService.inactiveResultList(model));
		return returnResultAdmin;
	}

	// method to Add Or Update Result
	@GetMapping(path = "/addUpdateResult")
	public String addOrUpdateResult(@RequestParam("subject") String subjectName, @RequestParam("exam") String examName,
			@RequestParam("examType") String examType, @RequestParam("userId") int userId,
			@RequestParam("marks") int marks, Model model) throws MarkException, UserIdException, ExamIdException, SubjectIdException {
		ResultPojo resultPojo = new ResultPojo();
		resultPojo.setUserId(userId);
		resultPojo.setMarks(marks);

		List<UserPojo> userPojo = staffDao.findStudentById(userId, model);
		for (UserPojo userModel : userPojo) {
			if (userModel != null) {
				String department = userModel.getDepartment();
				int semester = userModel.getSemester();
				List<SubjectPojo> subjectPojo = staffDao.findSubjectID(department, semester, subjectName);
				for (SubjectPojo subjectModel : subjectPojo) {
					if (subjectModel != null) {
						String subjectId = subjectModel.getId();
						List<ExamPojo> examPojo = staffDao.findExam(examName, examType, subjectId);
						for (ExamPojo examModel : examPojo) {
							if (examModel != null) {
								int examId = examModel.getId();
								resultPojo.setExamId(examId);
								int value = staffService.addOrUpdateResult(resultPojo);
								if (value == 1) {
									return "redirect:/resultAdmin";
								}
							}
						}throw new ExamIdException("Exam Id dosen't exist");
					}
				}throw new SubjectIdException("Subject Id dosen't exist");
			}
		}
		return "redirect:/resultAdmin";
	}

	// method to Activate Or Deactivate one Result of particular exam and user
	@GetMapping(path = "/activateDeactivateOneResult/{examId}/{userId}")
	public String activateOrDeactivateOneResult(@RequestParam("examId") int examId,
			@RequestParam("userId") int userId, Model model,HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		ResultPojo resultPojo = new ResultPojo();
		resultPojo.setExamId(examId);
		resultPojo.setUserId(userId);
		int value = staffService.activateOrDeactivateOneResult(resultPojo);
		if (value == 1) {
			return "redirect:/resultlist";
		} else if (value == 2) {
			return redirectInactiveResultlist;
		} else
			return returnResultAdmin;
	}

	// method to Activate Or Deactivate Result of one whole exam
	@GetMapping(path = "/activateDeactivateWholeExamresult/{examId}")
	public String activateOrDeactivateWholeExamResult(@RequestParam("examId") int examId, Model model,HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		ResultPojo resultPojo = new ResultPojo();
		resultPojo.setExamId(examId);
		int value = staffService.activateOrDeactivateWholeExamResult(resultPojo);
		if (value == 1) {
			return redirectResultlist;
		} else if (value == 2) {
			return redirectInactiveResultlist;
		} else
			return returnResultAdmin;
	}

	// method to Activate Or Deactivate one Result of one whole user
	@GetMapping(path = "/activateDeactivateWholeUserresult/{userId}")
	public String activateOrDeactivateWholeUserResult(@RequestParam("userId") int userId, Model model,HttpSession session) throws HigherAuthorityException {
		int staffId = (int) session.getAttribute(sessionUserId);
		staffService.checkHigherAuthority(staffId);
		ResultPojo resultPojo = new ResultPojo();
		resultPojo.setUserId(userId);
		int value = staffService.activateOrDeactivateWholeUserResult(resultPojo);
		if (value == 1) {
			return redirectResultlist;
		} else if (value == 2) {
			return redirectInactiveResultlist;
		} else
			return returnResultAdmin;
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
}
