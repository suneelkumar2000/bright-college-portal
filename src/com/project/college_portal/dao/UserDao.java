package com.project.college_portal.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.college_portal.connection.ConnectionUtil;
import com.project.college_portal.exception.AttendanceUserIdException;
import com.project.college_portal.exception.ExistMailIdException;
import com.project.college_portal.exception.ForgotPasswordException;
import com.project.college_portal.exception.InvalidMailIdException;
import com.project.college_portal.interfaces.UserInterface;
import com.project.college_portal.mapper.ApprovingMapper;
import com.project.college_portal.mapper.AttendanceMapper;
import com.project.college_portal.mapper.ForgotPasswordMapper;
import com.project.college_portal.mapper.LoginMapper;
import com.project.college_portal.mapper.UserMapper;
import com.project.college_portal.mapper.StudentResultMapper;
import com.project.college_portal.model.AttendancePojo;
import com.project.college_portal.model.SemesterPojo;
import com.project.college_portal.model.StudentResultPojo;
import com.project.college_portal.model.UserPojo;
import com.project.college_portal.validation.Validation;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Repository
public class UserDao implements UserInterface {
	JdbcTemplate jdbcTemplate = ConnectionUtil.getJdbcTemplate();
	StaffDao staffDao = new StaffDao();

	String student = "student";
	String staff = "staff";
	String updatePasswordQuery = "update user set Password =?  where Email=?";
	String updateSemesterQuery = "update user set semester =? where id=?";

	// --------- user method ---------

	// user registration method
	public int save(UserPojo saveUser) throws ExistMailIdException {
		String password = saveUser.getPassword();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(password);

		Validation val = new Validation();
		List<UserPojo> listUsers = listUsers();

		String userList = listUsers.toString();
		String email = saveUser.getEmail();
		boolean emailContains = userList.contains(email);

		if (emailContains) {
			throw new ExistMailIdException("Exist Email Exception");
		} else {
			String sql = "insert into user(first_name,last_name,dob,gender,phone_number,email,Password,roll) values(?,?,?,?,?,?,?,?)";
			Object[] params = { saveUser.getFirstName(), saveUser.getLastName(), saveUser.getDOB(),
					saveUser.getGender(), saveUser.getPhone(), email, encodedPassword, saveUser.getRoll() };

			boolean emailval = val.emailValidation(email);
			boolean phoneval = val.phoneNumberValidation(saveUser.getPhone());
			boolean firstNameVal = val.nameValidation(saveUser.getFirstName());
			boolean adminval = val.adminEmailValidation(email);
			if (emailval && phoneval && firstNameVal) {
				jdbcTemplate.update(sql, params);
				if (adminval) {
					String approve = "update user set status ='approved'  where email=?";
					Object[] params1 = { email };
					jdbcTemplate.update(approve, params1);
					return 1;
				}
				return 1;
			} else {
				return 0;
			}
		}

	}

	// method for user login
	public int login(UserPojo loginUser) throws InvalidMailIdException {
		String email = loginUser.getEmail();

		String password = loginUser.getPassword();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String login = "Select Email,Password,roll from user";
		List<UserPojo> userLogin = jdbcTemplate.query(login, new LoginMapper());

		List<UserPojo> user1 = userLogin.stream().filter(email1 -> email1.getEmail().equals(email))
				.filter(roll1 -> roll1.getRoll().equals(student)).collect(Collectors.toList());

		List<UserPojo> user2 = userLogin.stream().filter(email2 -> email2.getEmail().equals(email))
				.filter(roll2 -> roll2.getRoll().equals(staff)).collect(Collectors.toList());

		for (UserPojo userModel1 : user1) {
			if (userModel1 != null) {
				String dbpass = userModel1.getPassword();
				boolean match = encoder.matches(password, dbpass);
				if (match)
					return 1;
			}

		}
		for (UserPojo userModel2 : user2) {
			if (userModel2 != null) {
				String dbpass = userModel2.getPassword();
				boolean match = encoder.matches(password, dbpass);
				if (match)
					return 2;
			}

		}
		throw new InvalidMailIdException("Email dosen't exist");
	}

	// method to show user list
	public List<UserPojo> listUsers() {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user";
		return jdbcTemplate.query(select, new UserMapper());
	}

	// forgotPassword method
	public int forgotPassword(UserPojo userPojo) throws ForgotPasswordException {
		String email = userPojo.getEmail();
		long phone = userPojo.getPhone();
		String password = userPojo.getPassword();

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePassword = encoder.encode(password);

		String select = "Select Email,Password,phone_number,roll from user";
		List<UserPojo> userLogin = jdbcTemplate.query(select, new ForgotPasswordMapper());

		List<UserPojo> user1 = userLogin.stream().filter(email1 -> email1.getEmail().equals(email))
				.filter(phone1 -> phone1.getPhone().equals(phone)).filter(roll1 -> roll1.getRoll().equals(student))
				.collect(Collectors.toList());

		List<UserPojo> user2 = userLogin.stream().filter(email2 -> email2.getEmail().equals(email))
				.filter(phone2 -> phone2.getPhone().equals(phone)).filter(roll2 -> roll2.getRoll().equals(staff))
				.collect(Collectors.toList());

		for (UserPojo userModel1 : user1) {
			if (userModel1 != null) {
				String changePassword = updatePasswordQuery;
				Object[] params = { encodePassword, email };
				jdbcTemplate.update(changePassword, params);
				return 1;
			}
		}
		for (UserPojo userModel2 : user2) {
			if (userModel2 != null) {
				String changePassword = updatePasswordQuery;
				Object[] params = { encodePassword, email };
				jdbcTemplate.update(changePassword, params);
				return 2;
			}

		}
		throw new ForgotPasswordException("Email dosen't exist");
	}

	// method to find user ID by email
	public int findIdByEmail(String email) {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where email=?";
		List<UserPojo> userDetails = jdbcTemplate.query(select, new UserMapper(), email);
		for (UserPojo userPojo : userDetails) {
			if (userPojo != null) {
				return userPojo.getUserId();
			}
		}
		return 0;
	}

	// method to set User Session By Id
	public int setUserSessionById(int userId, HttpSession session) {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where (id=?)";
		List<UserPojo> userDetails = jdbcTemplate.query(select, new UserMapper(), userId);
		for (UserPojo userModel : userDetails) {
			if (userModel != null) {
				session.setAttribute("firstName", userModel.getFirstName());
				session.setAttribute("lastName", userModel.getLastName());
				session.setAttribute("dob", userModel.getDOB());
				session.setAttribute("gender", userModel.getGender());
				session.setAttribute("phone", userModel.getPhone());
				session.setAttribute("email", userModel.getEmail());
				session.setAttribute("roll", userModel.getRoll());
				session.setAttribute("department", userModel.getDepartment());
				session.setAttribute("parentName", userModel.getParentName());
				session.setAttribute("joiningYear", userModel.getJoiningYear());
				session.setAttribute("status", userModel.getStatus());
				session.setAttribute("isActive", userModel.isActive());
				return 1;
			}
		}
		return 0;
	}

	// --------- student method ---------

	// method to find student details by Email
	public List<UserPojo> findByEmail(String email) {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where (roll='student' and email=?)";
		return jdbcTemplate.query(select, new UserMapper(), email);
	}

	// method to update student details
	public int studentsave(UserPojo userPojo) {
		String select = "Select id,roll,status,is_active from user";
		List<UserPojo> user = jdbcTemplate.query(select, new ApprovingMapper());
		List<UserPojo> user1 = user.stream().filter(id -> id.getUserId() == (userPojo.getUserId()))
				.filter(roll1 -> roll1.getRoll().equals(student)).collect(Collectors.toList());
		for (UserPojo userModel : user1) {
			LocalDate currentDate = LocalDate.now();
			int year = currentDate.getYear();
			if ((userPojo.getJoiningYear() <= year) && (userModel != null)) {

				String update = "update user set dob=?, phone_number=?,department=?,parent_name=?,year_of_joining=?  where (roll='student' and id=?)";
				Object[] params = { userPojo.getDOB(), userPojo.getPhone(), userPojo.getDepartment(),
						userPojo.getParentName(), userPojo.getJoiningYear(), userPojo.getUserId() };
				jdbcTemplate.update(update, params);
				return 1;

			}
		}
		return 0;
	}

	// method to update Student semester
	public void updateStudentSemester(Model model) throws JsonProcessingException {
		String select = "Select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where roll='student'";
		List<UserPojo> userList = jdbcTemplate.query(select, new UserMapper());
		for (UserPojo userModel : userList) {
			if (userModel != null) {
				int joiningYear = userModel.getJoiningYear();
				LocalDate currentDate = LocalDate.now();
				int month = currentDate.getMonthValue();
				int year = currentDate.getYear();
				staffDao.activeOrInactiveSemester();
				List<SemesterPojo> semesterList = staffDao.activeSemesterList(model);
				for (SemesterPojo semesterModel : semesterList) {
					int semesterId = semesterModel.getId();
					int yearDifference = year - joiningYear;
					if ((yearDifference) < 5) {
						if (month > 5 && month < 12) {
							if (yearDifference == 0) {
								if (semesterId <= 2) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else if ((yearDifference < 2 && yearDifference >= 1)) {
								if ((semesterId <= 4) && (semesterId > 2)) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else if (yearDifference < 3 && yearDifference >= 2) {
								if ((semesterId <= 6) && (semesterId > 4)) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else if (yearDifference < 4 && yearDifference >= 3) {
								if ((semesterId <= 8) && (semesterId > 6)) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else {
								String update = updateSemesterQuery;
								Object[] params = { -1, userModel.getUserId() };
								jdbcTemplate.update(update, params);
							}
						} else {
							if ((yearDifference == 1 && month != 12) && (yearDifference == 0 && month == 12)) {
								if (semesterId <= 2) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else if ((yearDifference == 2 && month != 12) && (yearDifference == 1 && month == 12)) {
								if ((semesterId <= 4) && (semesterId > 2)) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else if ((yearDifference == 3 && month != 12) && (yearDifference == 2 && month == 12)) {
								if ((semesterId <= 6) && (semesterId > 4)) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else if ((yearDifference == 4 && month != 12) && (yearDifference == 3 && month == 12)) {
								if ((semesterId <= 8) && (semesterId > 6)) {
									String update = updateSemesterQuery;
									Object[] params = { semesterId, userModel.getUserId() };
									jdbcTemplate.update(update, params);
								}
							} else {
								String update = updateSemesterQuery;
								Object[] params = { -1, userModel.getUserId() };
								jdbcTemplate.update(update, params);
							}
						}
					} else if (joiningYear == 0) {
						String update = updateSemesterQuery;
						Object[] params = { null, userModel.getUserId() };
						jdbcTemplate.update(update, params);
					} else {
						String update = updateSemesterQuery;
						Object[] params = { -1, userModel.getUserId() };
						jdbcTemplate.update(update, params);
					}
				}
			}
		}
	}

	// method to find Student semester
	public int findStudentSemesterById(int userid, Model model) throws JsonProcessingException {
		String select = "Select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where id=?";
		List<UserPojo> userList = jdbcTemplate.query(select, new UserMapper(), userid);
		for (UserPojo userModel : userList) {
			if (userModel != null) {
				int semester = userModel.getSemester();
				if (semester > 0) {
					return semester;
				} else if (semester == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		}
		return -1;
	}

	// method to find Student result
	public List<StudentResultPojo> findStudentResult(int userid, Model model) throws JsonProcessingException {
		String select = "select result.exam_id,exam.name as exam_name,exam.type,exam.subject_id,subjects.name,subjects.semester_id,result.marks from result left join exam left join subjects on exam.subject_id = subjects.id on result.exam_id = exam.id  where (result.user_id=?) ;";
		List<StudentResultPojo> resultList = jdbcTemplate.query(select, new StudentResultMapper(), userid);
		ObjectMapper object = new ObjectMapper();
		String result = object.writeValueAsString(resultList);
		model.addAttribute("listOfResult", result);
		return resultList;

	}

	// method to find Student attendance
	public List<AttendancePojo> findStudentAttendance(int userId, Model model, HttpSession session)
			throws JsonProcessingException, AttendanceUserIdException {
		
		int semester = findStudentSemesterById(userId, model);
		if (semester <=0 ) {
			throw new AttendanceUserIdException("User Id dosen't exist");
		}
		String select = "Select user_id,semester,total_days,days_attended,days_leave,attendance,is_active from attendance";
		List<AttendancePojo> attendanceList = jdbcTemplate.query(select, new AttendanceMapper());
		List<AttendancePojo> attendanceList1 = attendanceList.stream().filter(userid -> userid.getUserId() == (userId))
				.filter(semesterid -> semesterid.getSemester() == (semester))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		for (AttendancePojo attendanceModel1 : attendanceList1) {
			if (attendanceModel1 != null) {
				String select1 = "select user_id,semester,total_days,days_attended,days_leave,attendance,is_active from attendance where (is_active =true and user_id=? and semester=?)";
				return jdbcTemplate.query(select1, new AttendanceMapper(), userId, semester);
			}
		}
		throw new AttendanceUserIdException("User Id dosen't exist");
	}
}
