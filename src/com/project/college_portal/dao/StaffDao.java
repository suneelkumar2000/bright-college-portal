package com.project.college_portal.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import com.project.college_portal.connection.ConnectionUtil;
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
import com.project.college_portal.interfaces.StaffInterface;
import com.project.college_portal.mapper.ApprovingMapper;
import com.project.college_portal.mapper.AttendanceMapper;
import com.project.college_portal.mapper.DepartmentMapper;
import com.project.college_portal.mapper.ExamMapper;
import com.project.college_portal.mapper.ExamTypeMapper;
import com.project.college_portal.mapper.ExamnameMapper;
import com.project.college_portal.mapper.ExamIdMapper;
import com.project.college_portal.mapper.ResultMapper;
import com.project.college_portal.mapper.SemesterMapper;
import com.project.college_portal.mapper.SubjectIdMapper;
import com.project.college_portal.mapper.SubjectMapper;
import com.project.college_portal.mapper.SubjectNameMapper;
import com.project.college_portal.mapper.UserDepartmentMapper;
import com.project.college_portal.mapper.UserMapper;
import com.project.college_portal.model.AttendancePojo;
import com.project.college_portal.model.DepartmentPojo;
import com.project.college_portal.model.ExamPojo;
import com.project.college_portal.model.ResultPojo;
import com.project.college_portal.model.SemesterPojo;
import com.project.college_portal.model.SubjectPojo;
import com.project.college_portal.model.UserPojo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class StaffDao implements StaffInterface {
	JdbcTemplate jdbcTemplate = ConnectionUtil.getJdbcTemplate();

	String modelUserId = "userId";
	String staff = "staff";
	String student = "student";
	String approved = "approved";
	String selectdepartment = "Select id,department,is_active from classroom";
	String selectIdRollStatus = "Select id,roll,status,is_active from user";
	String updateSemesterActivate = "update semester set is_active =true where id=?";
	String updateSemesterDeactivate = "update semester set is_active =false where id=?";
	String selectSubjects = "Select id,name,semester_id,department,is_active from subjects";
	String selectAttendance = "Select user_id,semester,total_days,days_attended,days_leave,attendance,is_active from attendance";
	String selectSemester = "Select id,is_active from semester";
	String selectExam = "Select id,subject_id,name,date_,type,is_active from exam";
	String selectResult = "Select exam_id,user_id,marks,is_active from result";
	String listOfDepartment="listOfDepartment";
	String listOfSemester="listOfSemester";
	String listOfResult="listOfResult";
	String listOfExam="listOfExam";
	String listOfSubject="listOfSubject";
	String listOfSubjectbySemesterId="listOfSubjectbySemesterId";
	String higherAuthorityException="HigherAuthority Exception";
	String userIdDosenotexist="User Id dosen't exist";

	// --------- Students methods ------------

	public int checkHigherAuthority(int staffId) throws HigherAuthorityException {
		String selectStaff = selectIdRollStatus;
		List<UserPojo> userPojo = jdbcTemplate.query(selectStaff, new ApprovingMapper());
		List<UserPojo> user1 = userPojo.stream().filter(id -> id.getUserId() == (staffId))
				.filter(roll -> roll.getRoll().equals(staff)).filter(status -> status.getStatus().equals(approved))
				.collect(Collectors.toList());
		for (UserPojo userModel : user1) {
			if (userModel != null) {
				return 0;
			}
		}
		throw new HigherAuthorityException(higherAuthorityException);
	}

	public List<UserPojo> findStudentById(int userId, Model model) {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where (roll='student' and id=?)";
		return jdbcTemplate.query(select, new UserMapper(), userId);
	}

	public UserPojo findStudentDepartmentById(int userId) {
		String select = "select department from user where (roll='student' and id=?)";
		return jdbcTemplate.queryForObject(select, new UserDepartmentMapper(), userId);
	}

	public List<UserPojo> studentList(Model model) throws JsonProcessingException {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where (roll='student' and is_active =true)";
		List<UserPojo> userList = jdbcTemplate.query(select, new UserMapper());
		ObjectMapper object = new ObjectMapper();
		String user = object.writeValueAsString(userList);
		model.addAttribute("listOfStudents", user);
		return userList;
	}

	public int approve(int staffId, UserPojo approveUser) throws UserIdException, HigherAuthorityException {
		String selectStaff = selectIdRollStatus;
		List<UserPojo> userPojo = jdbcTemplate.query(selectStaff, new ApprovingMapper());
		List<UserPojo> user1 = userPojo.stream().filter(id -> id.getUserId() == (staffId))
				.filter(roll -> roll.getRoll().equals(staff)).filter(status -> status.getStatus().equals(approved))
				.collect(Collectors.toList());
		for (UserPojo userModel : user1) {
			if (userModel != null) {

				String select = selectIdRollStatus;
				List<UserPojo> user2 = jdbcTemplate.query(select, new ApprovingMapper());
				List<UserPojo> user3 = user2.stream().filter(id -> id.getUserId() == (approveUser.getUserId()))
						.filter(roll1 -> roll1.getRoll().equals(student)).collect(Collectors.toList());
				for (UserPojo userModel2 : user3) {
					if (userModel2 != null) {
						String approve = "update user set status='approved'  where (roll='student' and id=?)";
						Object[] params = { approveUser.getUserId() };
						jdbcTemplate.update(approve, params);
						return 1;
					}
				}
				throw new UserIdException("User Id dosen't exist to approve");
			}
		}
		throw new HigherAuthorityException(higherAuthorityException);

	}
	
	public int reject(int staffId, UserPojo approveUser) throws UserIdException, HigherAuthorityException {
		String selectStaff = selectIdRollStatus;
		List<UserPojo> userPojo = jdbcTemplate.query(selectStaff, new ApprovingMapper());
		List<UserPojo> user1 = userPojo.stream().filter(id -> id.getUserId() == (staffId))
				.filter(roll -> roll.getRoll().equals(staff)).filter(status -> status.getStatus().equals(approved))
				.collect(Collectors.toList());
		for (UserPojo userModel : user1) {
			if (userModel != null) {

				String select = selectIdRollStatus;
				List<UserPojo> user2 = jdbcTemplate.query(select, new ApprovingMapper());
				List<UserPojo> user3 = user2.stream().filter(id -> id.getUserId() == (approveUser.getUserId()))
						.filter(roll1 -> roll1.getRoll().equals(student)).collect(Collectors.toList());
				for (UserPojo userModel2 : user3) {
					if (userModel2 != null) {
						String approve = "update user set status='not approved'  where (roll='student' and id=?)";
						Object[] params = { approveUser.getUserId() };
						jdbcTemplate.update(approve, params);
						return 1;
					}
				}
				throw new UserIdException("User Id dosen't exist to reject");
			}
		}
		throw new HigherAuthorityException(higherAuthorityException);

	}

	public List<UserPojo> approvedStudentList(Model model) throws JsonProcessingException {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where (roll='student' and status='approved' and is_active =true)";
		List<UserPojo> userList = jdbcTemplate.query(select, new UserMapper());
		List<UserPojo> userList1 = userList.stream().filter(semester -> semester.getSemester() > 0)
				.collect(Collectors.toList());

		ObjectMapper object = new ObjectMapper();
		String user = object.writeValueAsString(userList1);
		model.addAttribute("listOfApprovedStudents", user);
		return userList1;
	}

	public List<UserPojo> notApprovedStudentList() {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where (roll='student' and status='not approved' and is_active =true)";
		return jdbcTemplate.query(select, new UserMapper());
	}

	public int activateOrDeactivateStudent(UserPojo userPojos) {
		String select = selectIdRollStatus;
		List<UserPojo> userPojo = jdbcTemplate.query(select, new ApprovingMapper());
		List<UserPojo> user1 = userPojo.stream().filter(id -> id.getUserId() == (userPojos.getUserId()))
				.filter(roll1 -> roll1.getRoll().equals(student)).filter(isActive -> isActive.isActive() == (true))
				.collect(Collectors.toList());
		List<UserPojo> user2 = userPojo.stream().filter(id -> id.getUserId() == (userPojos.getUserId()))
				.filter(roll1 -> roll1.getRoll().equals(student)).filter(isActive -> isActive.isActive() == (false))
				.collect(Collectors.toList());
		for (UserPojo userModel1 : user1) {
			if (userModel1 != null) {
				String deactivate = "update user set is_active = false  where (roll='student' and id=?)";
				Object[] params = { userPojos.getUserId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (UserPojo userModel2 : user2) {
			if (userModel2 != null) {
				String activate = "update user set is_active = true where (roll='student' and id=?)";
				Object[] params = { userPojos.getUserId() };
				jdbcTemplate.update(activate, params);
				return 1;
			}
		}
		return 0;
	}

	public List<UserPojo> inactiveStudentList() {
		String select = "select id,first_name,last_name,dob,gender,phone_number,email,password,roll,department,parent_name,year_of_joining,semester,status,image,is_active from user where (roll='student' and is_active =false)";
		return jdbcTemplate.query(select, new UserMapper());
	}

	// --------- Department methods ------------

	public int addDepartment(int staffId, DepartmentPojo departmentPojo)
			throws ExistDepartmentNameException, HigherAuthorityException {
		String selectStaff = selectIdRollStatus;
		List<UserPojo> userPojo = jdbcTemplate.query(selectStaff, new ApprovingMapper());
		List<UserPojo> user1 = userPojo.stream().filter(id -> id.getUserId() == (staffId))
				.filter(roll -> roll.getRoll().equals(staff)).filter(status -> status.getStatus().equals(approved))
				.collect(Collectors.toList());
		for (UserPojo userModel : user1) {
			if (userModel != null) {
				String name = departmentPojo.getDepartment();
				String select = selectdepartment;
				List<DepartmentPojo> department1 = (jdbcTemplate.query(select, new DepartmentMapper())).stream()
						.filter(dep -> ((dep.getDepartment()).toLowerCase()).equals((name).toLowerCase()))
						.collect(Collectors.toList());
				for (DepartmentPojo departmentModel1 : department1) {
					if (departmentModel1 != null) {
						throw new ExistDepartmentNameException("Exist Department Exception");
					}
				}
				String add = "insert into classroom(department) values(?)";
				Object[] params = { name };
				int noOfRows = jdbcTemplate.update(add, params);
				if (noOfRows > 0) {
					return 1;
				} else
					return 0;
			}
		}
		throw new HigherAuthorityException(higherAuthorityException);
	}

	public int activateOrDeactivateDepartment(DepartmentPojo departmentPojo) {
		String select = selectdepartment;
		List<DepartmentPojo> department = jdbcTemplate.query(select, new DepartmentMapper());
		List<DepartmentPojo> department1 = department.stream()
				.filter(dep -> dep.getDepartment().equals(departmentPojo.getDepartment()))
				.filter(isActive -> isActive.isIsActive() == (true)).collect(Collectors.toList());
		List<DepartmentPojo> department2 = department.stream()
				.filter(dep -> dep.getDepartment().equals(departmentPojo.getDepartment()))
				.filter(isActive -> isActive.isIsActive() == (false)).collect(Collectors.toList());
		for (DepartmentPojo departmentModel1 : department1) {
			if (departmentModel1 != null) {
				String deactivate = "update classroom set is_active =false where department=?";
				Object[] params = { departmentPojo.getDepartment() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (DepartmentPojo departmentModel2 : department2) {
			if (departmentModel2 != null) {
				String activate = "update classroom set is_active =true where department=?";
				Object[] params = { departmentPojo.getDepartment() };
				jdbcTemplate.update(activate, params);
				return 2;
			}
		}
		return 0;
	}

	public List<DepartmentPojo> departmentList(Model model) throws JsonProcessingException {
		String select = "select id,department,is_active from classroom where (is_active =true and department !='not selected')";
		List<DepartmentPojo> departmentList = jdbcTemplate.query(select, new DepartmentMapper());
		ObjectMapper object = new ObjectMapper();
		String department = object.writeValueAsString(departmentList);
		model.addAttribute(listOfDepartment, department);
		return departmentList;
	}

	public List<DepartmentPojo> inactiveDepartmentList(Model model) throws JsonProcessingException {
		String select = "select id,department,is_active from classroom where (is_active =false and department !='not selected')";
		List<DepartmentPojo> departmentList = jdbcTemplate.query(select, new DepartmentMapper());
		ObjectMapper object = new ObjectMapper();
		String department = object.writeValueAsString(departmentList);
		model.addAttribute(listOfDepartment, department);
		return departmentList;
	}

	// --------- Attendance methods ------------

	public int addOrUpdatePresentByOne(int userId,int semester) throws UserIdException {
		String select1 = selectIdRollStatus;
		List<UserPojo> userPojo = (jdbcTemplate.query(select1, new ApprovingMapper())).stream()
				.filter(id -> id.getUserId() == userId).filter(roll1 -> roll1.getRoll().equals(student))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		for (UserPojo userModel1 : userPojo) {
			if (userModel1 != null) {

				String select = selectAttendance;
				List<AttendancePojo> attendanceList = jdbcTemplate.query(select, new AttendanceMapper());
				List<AttendancePojo> attendanceList1 = attendanceList.stream()
						.filter(userid -> userid.getUserId() == (userId))
						.filter(semesterid -> semesterid.getSemester() == (semester))
						.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
				for (AttendancePojo attendanceModel1 : attendanceList1) {
					if (attendanceModel1 != null) {
						int daysAttended = attendanceModel1.getDaysAttended() + 1;
						int daysLeave = attendanceModel1.getDaysLeave();
						int totalDays = daysAttended + daysLeave;
						double daysAttended2 = daysAttended;
						double totalDays2 = totalDays;
						double attendance = (daysAttended2 / totalDays2);
						double attendancePercentage = attendance * 100;
						String update = "update attendance set total_days=?,days_attended=?,days_leave=?,attendance=? where user_id=? and semester=?";
						Object[] params = { totalDays, daysAttended, daysLeave, attendancePercentage, userId,semester };
						jdbcTemplate.update(update, params);
						return 1;
					}
				}
				int daysAttended = 1;
				int daysLeave = 0;
				int totalDays = daysAttended + daysLeave;
				double daysAttended2 = daysAttended;
				double totalDays2 = totalDays;
				double attendance = (daysAttended2 / totalDays2);
				double attendancePercentage = attendance * 100;
				String add = "insert into attendance(user_id,semester,total_days,days_attended,days_leave,attendance) values(?,?,?,?,?,?)";
				Object[] params = { userId,semester, totalDays, daysAttended, daysLeave, attendancePercentage };
				jdbcTemplate.update(add, params);
				return 1;
			}
		}
		throw new UserIdException(userIdDosenotexist);
	}

	public int addOrUpdateAbsentByOne(int userId,int semester) throws UserIdException {
		String select1 = selectIdRollStatus;
		List<UserPojo> userPojo = (jdbcTemplate.query(select1, new ApprovingMapper())).stream()
				.filter(id -> id.getUserId() == userId).filter(roll1 -> roll1.getRoll().equals(student))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		for (UserPojo userModel1 : userPojo) {
			if (userModel1 != null) {

				String select = selectAttendance;
				List<AttendancePojo> attendanceList = jdbcTemplate.query(select, new AttendanceMapper());
				List<AttendancePojo> attendanceList1 = attendanceList.stream()
						.filter(userid -> userid.getUserId() == (userId))
						.filter(semesterid -> semesterid.getSemester() == (semester))
						.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
				for (AttendancePojo attendanceModel1 : attendanceList1) {
					if (attendanceModel1 != null) {
						int daysAttended = attendanceModel1.getDaysAttended();
						int daysLeave = attendanceModel1.getDaysLeave() + 1;
						int totalDays = daysAttended + daysLeave;
						double daysAttended2 = daysAttended;
						double totalDays2 = totalDays;
						double attendance = (daysAttended2 / totalDays2);
						double attendancePercentage = attendance * 100;
						String update = "update attendance set total_days=?,days_attended=?,days_leave=?,attendance=? where user_id=? and semester=?";
						Object[] params = { totalDays, daysAttended, daysLeave, attendancePercentage, userId,semester };
						jdbcTemplate.update(update, params);
						return 1;
					}
				}
				int daysAttended = 0;
				int daysLeave = 1;
				int totalDays = daysAttended + daysLeave;
				double daysAttended2 = daysAttended;
				double totalDays2 = totalDays;
				double attendance = (daysAttended2 / totalDays2);
				double attendancePercentage = attendance * 100;
				String add = "insert into attendance(user_id,semester,total_days,days_attended,days_leave,attendance) values(?,?,?,?,?,?)";
				Object[] params = { userId,semester, totalDays, daysAttended, daysLeave, attendancePercentage };
				jdbcTemplate.update(add, params);
				return 1;
			}
		}
		throw new UserIdException(userIdDosenotexist);
	}

	public int activateOrDeactivateAttendance(AttendancePojo attendancePojo) {
		String select = selectAttendance;
		List<AttendancePojo> attendanceList = jdbcTemplate.query(select, new AttendanceMapper());
		List<AttendancePojo> attendanceList1 = attendanceList.stream()
				.filter(userid -> userid.getUserId() == (attendancePojo.getUserId()))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		List<AttendancePojo> attendanceList2 = attendanceList.stream()
				.filter(userid -> userid.getUserId() == (attendancePojo.getUserId()))
				.filter(isActive -> isActive.isActive() == (false)).collect(Collectors.toList());
		for (AttendancePojo attendanceModel1 : attendanceList1) {
			if (attendanceModel1 != null) {
				String deactivate = "update attendance set is_active =false where user_id=?";
				Object[] params = { attendancePojo.getUserId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (AttendancePojo attendanceModel2 : attendanceList2) {
			if (attendanceModel2 != null) {
				String activate = "update attendance set is_active =true where user_id=?";
				Object[] params = { attendancePojo.getUserId() };
				jdbcTemplate.update(activate, params);
				return 1;
			}
		}
		return 0;
	}

	public List<AttendancePojo> attendanceList() {
		String select = "Select user_id,semester,total_days,days_attended,days_leave,attendance,is_active from attendance where (is_active =true)";
		return jdbcTemplate.query(select, new AttendanceMapper());
	}

	public List<AttendancePojo> inactiveAttendanceList() {
		String select = "Select user_id,semester,total_days,days_attended,days_leave,attendance,is_active from attendance where (is_active =false)";
		return jdbcTemplate.query(select, new AttendanceMapper());
	}

	// --------- Semester methods ------------

	public int addSemester(SemesterPojo semesterPojo) throws ExistSemesterIdException {
		int semesterId = semesterPojo.getId();
		String select = selectSemester;
		List<SemesterPojo> semester1 = (jdbcTemplate.query(select, new SemesterMapper())).stream()
				.filter(id -> ((id.getId()) == (semesterId))).collect(Collectors.toList());
		for (SemesterPojo semesterModel1 : semester1) {
			if (semesterModel1 != null) {
				throw new ExistSemesterIdException("Exist Semester Exception");
			}
		}

		String add = "insert into semester(id) values(?)";
		Object[] params = { semesterPojo.getId() };
		int noOfRows = jdbcTemplate.update(add, params);
		if (noOfRows > 0) {
			return 1;
		} else
			return 0;
	}

	public int activateOrDeactivateSemester(SemesterPojo semesterPojo) {
		String select = selectSemester;
		List<SemesterPojo> semester = jdbcTemplate.query(select, new SemesterMapper());
		List<SemesterPojo> semester1 = semester.stream().filter(id -> id.getId() == (semesterPojo.getId()))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		List<SemesterPojo> semester2 = semester.stream().filter(id -> id.getId() == (semesterPojo.getId()))
				.filter(isActive -> isActive.isActive() == (false)).collect(Collectors.toList());
		for (SemesterPojo semesterModel1 : semester1) {
			if (semesterModel1 != null) {
				String deactivate = updateSemesterDeactivate;
				Object[] params = { semesterPojo.getId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (SemesterPojo semesterModel2 : semester2) {
			if (semesterModel2 != null) {
				String activate = updateSemesterActivate;
				Object[] params = { semesterPojo.getId() };
				jdbcTemplate.update(activate, params);
				return 2;
			}
		}
		return 0;
	}

	public int activeOrInactiveSemester() {
		String select = selectSemester;
		List<SemesterPojo> semester = jdbcTemplate.query(select, new SemesterMapper());

		for (SemesterPojo semesterModel1 : semester) {
			if (semesterModel1 != null) {
				int semesterId = semesterModel1.getId();
				LocalDate currentDate = LocalDate.now();
				int month = currentDate.getMonthValue();
				if (month > 5 && month < 12) {
					if (semesterId % 2 == 0) {
						String deactivate = updateSemesterDeactivate;
						Object[] params = { semesterId };
						jdbcTemplate.update(deactivate, params);
					} else {
						String activate = updateSemesterActivate;
						Object[] params = { semesterId };
						jdbcTemplate.update(activate, params);
					}
				} else {
					if (semesterId % 2 == 0) {
						String activate = updateSemesterActivate;
						Object[] params = { semesterId };
						jdbcTemplate.update(activate, params);
					} else {
						String deactivate = updateSemesterDeactivate;
						Object[] params = { semesterId };
						jdbcTemplate.update(deactivate, params);
					}
				}

			}
		}
		return 0;

	}

	public List<SemesterPojo> semesterList(Model model) throws JsonProcessingException {
		String select = selectSemester;
		List<SemesterPojo> semesterList = jdbcTemplate.query(select, new SemesterMapper());
		ObjectMapper object = new ObjectMapper();
		String semester = object.writeValueAsString(semesterList);
		model.addAttribute(listOfSemester, semester);
		return semesterList;
	}

	public List<SemesterPojo> activeSemesterList(Model model) throws JsonProcessingException {
		String select = "Select id,is_active from semester where (is_active =true)";
		List<SemesterPojo> semesterList = jdbcTemplate.query(select, new SemesterMapper());
		ObjectMapper object = new ObjectMapper();
		String semester = object.writeValueAsString(semesterList);
		model.addAttribute(listOfSemester, semester);
		return semesterList;
	}

	public List<SemesterPojo> inactiveSemesterList(Model model) throws JsonProcessingException {
		String select = "Select id,is_active from semester where (is_active =false)";
		List<SemesterPojo> semesterList = jdbcTemplate.query(select, new SemesterMapper());
		ObjectMapper object = new ObjectMapper();
		String semester = object.writeValueAsString(semesterList);
		model.addAttribute(listOfSemester, semester);
		return semesterList;
	}

	// --------- Subject methods ------------

	public int addSubject(SubjectPojo subjectPojo)
			throws SemesterIdException, DepartmentException, ExistSubjectNameException {
		int semesterId = subjectPojo.getSemesterId();
		String select = selectSemester;
		List<SemesterPojo> semesterPojo = jdbcTemplate.query(select, new SemesterMapper());
		List<SemesterPojo> semester1 = semesterPojo.stream().filter(id -> id.getId() == (semesterId))
				.collect(Collectors.toList());
		for (SemesterPojo semesterModel1 : semester1) {
			if (semesterModel1 != null) {

				String department = subjectPojo.getDepartment();
				String select1 = selectdepartment;
				List<DepartmentPojo> depart = jdbcTemplate.query(select1, new DepartmentMapper());
				List<DepartmentPojo> department1 = depart.stream().filter(dep -> dep.getDepartment().equals(department))
						.collect(Collectors.toList());
				for (DepartmentPojo departmentModel1 : department1) {
					if (departmentModel1 != null) {

						String subjectName = subjectPojo.getName();
						String select2 = selectSubjects;
						List<SubjectPojo> sub = jdbcTemplate.query(select2, new SubjectMapper());
						List<SubjectPojo> subjectName1 = sub.stream().filter(name -> name.getName().equals(subjectName))
								.filter(dep -> dep.getDepartment().equals(department))
								.filter(sem -> sem.getSemesterId() == (semesterId)).collect(Collectors.toList());
						for (SubjectPojo subjectModel1 : subjectName1) {
							if (subjectModel1 != null) {
								throw new ExistSubjectNameException("Subject Alredy exist");
							}
						}

						String add = "insert into subjects(name,semester_id,department) values(?,?,?)";
						Object[] params = { subjectPojo.getName(), semesterId, department };
						jdbcTemplate.update(add, params);
						String update = "update subjects set id=(concat(SUBSTR(department, 1, 2),SUBSTR(name, 1, 2),semester_id)) where (name=? and semester_id=? and department=?)";
						Object[] param = { subjectPojo.getName(), semesterId, department };
						int noOfRows = jdbcTemplate.update(update, param);
						if (noOfRows > 0) {
							return 1;
						} else
							return 0;
					}

				}
				throw new DepartmentException("Department dosen't exist");
			}
		}
		throw new SemesterIdException("Semester Id dosen't exist");
	}

	public int activateOrDeactivateSubject(SubjectPojo subjectPojo) {
		String select = selectSubjects;
		List<SubjectPojo> subject = jdbcTemplate.query(select, new SubjectMapper());
		List<SubjectPojo> subject1 = subject.stream().filter(id -> id.getId().equals(subjectPojo.getId()))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		List<SubjectPojo> subject2 = subject.stream().filter(id -> id.getId().equals(subjectPojo.getId()))
				.filter(isActive -> isActive.isActive() == (false)).collect(Collectors.toList());
		for (SubjectPojo subjectModel1 : subject1) {
			if (subjectModel1 != null) {
				String deactivate = "update subjects set is_active =false where id=?";
				Object[] params = { subjectPojo.getId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (SubjectPojo subjectModel2 : subject2) {
			if (subjectModel2 != null) {
				String activate = "update subjects set is_active =true where id=?";
				Object[] params = { subjectPojo.getId() };
				jdbcTemplate.update(activate, params);
				return 2;
			}
		}
		return 0;
	}

	public SubjectPojo findByID(int id) {
		String find = "select id,name,semester_id,department,is_active from subjects where (is_active =true and id =?)";
		return jdbcTemplate.queryForObject(find, new SubjectMapper(), id);
	}

	public List<SubjectPojo> findSubjectID(String department, int semester, String name) {
		String find = "select id from subjects where (is_active =true and department =? and semester_id=? and name=?)";
		return jdbcTemplate.query(find, new SubjectIdMapper(), department, semester, name);
	}

	public List<SubjectPojo> findSubjectNameByDepartmentSemester(String department, int semester) {
		String find = "select name from subjects where (is_active =true and department =? and semester_id=?)";
		return jdbcTemplate.query(find, new SubjectNameMapper(), department, semester);
	}

	public List<SubjectPojo> findSubjectIdByName(String name) {
		String find = "select id from subjects where (is_active =true and name =?)";
		return jdbcTemplate.query(find, new SubjectIdMapper(), name);
	}

	public List<SubjectPojo> findSubjectListBySemester(int semesterId, Model model) throws JsonProcessingException {
		String find = "select id,name,semester_id,department,is_active from subjects where (is_active =true and semester_id =?)";
		List<SubjectPojo> subjectList = jdbcTemplate.query(find, new SubjectMapper(), semesterId);
		ObjectMapper object = new ObjectMapper();
		String subject = object.writeValueAsString(subjectList);
		model.addAttribute(listOfSubjectbySemesterId, subject);
		return subjectList;
	}

	public List<SubjectPojo> findSubjectList(int semesterId, String department, Model model)
			throws JsonProcessingException {
		String find = "select id,name,semester_id,department,is_active from subjects where (is_active =true and semester_id =? and department=?)";
		List<SubjectPojo> subjectList = jdbcTemplate.query(find, new SubjectMapper(), semesterId, department);
		ObjectMapper object = new ObjectMapper();
		String subject = object.writeValueAsString(subjectList);
		model.addAttribute(listOfSubjectbySemesterId, subject);
		return subjectList;
	}

	public List<SubjectPojo> subjectList(Model model) throws JsonProcessingException {
		String select = "select id,name,semester_id,department,is_active from subjects where (is_active =true)";
		List<SubjectPojo> subjectList = jdbcTemplate.query(select, new SubjectMapper());
		ObjectMapper object = new ObjectMapper();
		String subject = object.writeValueAsString(subjectList);
		model.addAttribute(listOfSubject, subject);
		return subjectList;
	}

	public List<SubjectPojo> inactivesubjectList(Model model) throws JsonProcessingException {
		String select = "select id,name,semester_id,department,is_active from subjects where (is_active =false)";
		List<SubjectPojo> subjectList = jdbcTemplate.query(select, new SubjectMapper());
		ObjectMapper object = new ObjectMapper();
		String subject1 = object.writeValueAsString(subjectList);
		model.addAttribute(listOfSubject, subject1);
		return subjectList;
	}

	// --------- Exam methods ------------

	public int addExam(ExamPojo examPojo) throws SubjectIdException, ExistExamException {
		String subjectId = examPojo.getSubjectId();
		String select = selectSubjects;
		List<SubjectPojo> subjectPojo = jdbcTemplate.query(select, new SubjectMapper());
		List<SubjectPojo> subject1 = subjectPojo.stream().filter(id -> id.getId().equals(subjectId))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		for (SubjectPojo subjectModel1 : subject1) {
			if (subjectModel1 != null) {
				String select1 = selectExam;
				List<ExamPojo> exam1 = jdbcTemplate.query(select1, new ExamMapper());
				List<ExamPojo> exam2 = exam1.stream().filter(subjectid -> (subjectid.getSubjectId()).equals(subjectId))
						.filter(name -> name.getName().equals(examPojo.getName()))
						.filter(type -> type.getType().equals(examPojo.getType())).collect(Collectors.toList());
				for (ExamPojo examModel1 : exam2) {
					if (examModel1 != null) {
						throw new ExistExamException("Exist Exam Exception");
					}
				}
				String add = "insert into exam(subject_id,name,date_,type) values(?,?,?,?)";
				Object[] params = { subjectId, examPojo.getName(), examPojo.getDate(), examPojo.getType() };
				int noOfRows = jdbcTemplate.update(add, params);
				if (noOfRows > 0) {
					return 1;
				} else {
					return 0;
				}
			}
		}
		throw new SubjectIdException("Subject Id dosen't exist");
	}

	public int activateOrDeactivateExam(ExamPojo examPojo) {
		String select = selectExam;
		List<ExamPojo> exam = jdbcTemplate.query(select, new ExamMapper());
		List<ExamPojo> exam1 = exam.stream().filter(id -> id.getId() == (examPojo.getId()))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		List<ExamPojo> exam2 = exam.stream().filter(id -> id.getId() == (examPojo.getId()))
				.filter(isActive -> isActive.isActive() == (false)).collect(Collectors.toList());
		for (ExamPojo examModel1 : exam1) {
			if (examModel1 != null) {
				String deactivate = "update exam set is_active =false where id=?";
				Object[] params = { examPojo.getId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (ExamPojo examModel2 : exam2) {
			if (examModel2 != null) {
				String activate = "update exam set is_active =true where id=?";
				Object[] params = { examPojo.getId() };
				jdbcTemplate.update(activate, params);
				return 2;
			}
		}
		return 0;
	}

	public List<ExamPojo> examList(Model model) throws JsonProcessingException {
		String select = "select id,subject_id,name,date_,type,is_active from exam where (is_active =true)";
		List<ExamPojo> examList = jdbcTemplate.query(select, new ExamMapper());
		ObjectMapper object = new ObjectMapper();
		String exam = object.writeValueAsString(examList);
		model.addAttribute(listOfExam, exam);
		return examList;
	}

	public List<ExamPojo> inactiveExamList(Model model) throws JsonProcessingException {
		String select = "select id,subject_id,name,date_,type,is_active from exam where (is_active =false)";
		List<ExamPojo> examList = jdbcTemplate.query(select, new ExamMapper());
		ObjectMapper object = new ObjectMapper();
		String exam = object.writeValueAsString(examList);
		model.addAttribute(listOfExam, exam);
		return examList;
	}

	public List<ExamPojo> findExamNameBySubjectID(String subjectID) {
		String find = "select name from exam where (subject_id=?)";
		return jdbcTemplate.query(find, new ExamnameMapper(), subjectID);
	}

	public List<ExamPojo> findExamTypeBySubjectID(String subjectID) {
		String find = "select type from exam where (subject_id=?)";
		return jdbcTemplate.query(find, new ExamTypeMapper(), subjectID);
	}

	public List<ExamPojo> findExam(String name, String type, String subjectID) {
		String find = "select id from exam where (name=? and type=? and subject_id=?)";
		return jdbcTemplate.query(find, new ExamIdMapper(), name, type, subjectID);
	}

	// --------- Result methods ------------

	public int addOrUpdateResult(ResultPojo resultPojo) throws MarkException, UserIdException, ExamIdException {
		int examid = resultPojo.getExamId();
		String select = selectExam;
		List<ExamPojo> examPojo = jdbcTemplate.query(select, new ExamMapper());
		List<ExamPojo> exam1 = examPojo.stream().filter(id -> id.getId() == (examid))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		for (ExamPojo examModel1 : exam1) {
			if (examModel1 != null) {

				int userId = resultPojo.getUserId();
				String select1 = selectIdRollStatus;
				List<UserPojo> userPojo = jdbcTemplate.query(select1, new ApprovingMapper());
				List<UserPojo> user1 = userPojo.stream().filter(id -> id.getUserId() == userId)
						.filter(roll1 -> roll1.getRoll().equals(student))
						.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
				for (UserPojo userModel1 : user1) {
					if (userModel1 != null) {

						int marks = resultPojo.getMarks();
						if (marks >= 0 && marks <= 100) {
							String select2 = "Select exam_id,user_id,marks,is_active from result";
							List<ResultPojo> result = jdbcTemplate.query(select2, new ResultMapper());
							List<ResultPojo> result1 = result.stream()
									.filter(examId -> examId.getExamId() == (resultPojo.getExamId()))
									.filter(userId1 -> userId1.getUserId() == (resultPojo.getUserId()))
									.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
							for (ResultPojo resultModel1 : result1) {
								if (resultModel1 != null) {
									String update = "update result set marks =? where (exam_id=? and user_id=?)";
									Object[] params = { marks, resultPojo.getExamId(), userId };
									jdbcTemplate.update(update, params);
									return 1;
								}
							}
							String add = "insert into result(exam_id,user_id,marks) values(?,?,?)";
							Object[] params = { resultPojo.getExamId(), resultPojo.getUserId(), marks };
							jdbcTemplate.update(add, params);
							return 2;
						} else {
							throw new MarkException("Invalid Marks");
						}
					}
				}
				throw new UserIdException(userIdDosenotexist);
			}
		}
		throw new ExamIdException("Exam Id dosen't exist");
	}

	public int activateOrDeactivateOneResult(ResultPojo resultPojo) {
		String select = selectResult;
		List<ResultPojo> result = jdbcTemplate.query(select, new ResultMapper());
		List<ResultPojo> result1 = result.stream().filter(examId -> examId.getExamId() == (resultPojo.getExamId()))
				.filter(userId -> userId.getUserId() == (resultPojo.getUserId()))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		List<ResultPojo> result2 = result.stream().filter(examId -> examId.getExamId() == (resultPojo.getExamId()))
				.filter(userId -> userId.getUserId() == (resultPojo.getUserId()))
				.filter(isActive -> isActive.isActive() == (false)).collect(Collectors.toList());
		for (ResultPojo resultModel1 : result1) {
			if (resultModel1 != null) {
				String deactivate = "update result set is_active =false where (exam_id=? and user_id=?)";
				Object[] params = { resultPojo.getExamId(), resultPojo.getUserId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (ResultPojo resultModel2 : result2) {
			if (resultModel2 != null) {
				String activate = "update result set is_active =true where (exam_id=? and user_id=?)";
				Object[] params = { resultPojo.getExamId(), resultPojo.getUserId() };
				jdbcTemplate.update(activate, params);
				return 2;
			}
		}
		return 0;
	}

	public int activateOrDeactivateWholeExamResult(ResultPojo resultPojo) {
		String select = selectResult;
		List<ResultPojo> result = jdbcTemplate.query(select, new ResultMapper());
		List<ResultPojo> result1 = result.stream().filter(examId -> examId.getExamId() == (resultPojo.getExamId()))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		List<ResultPojo> result2 = result.stream().filter(examId -> examId.getExamId() == (resultPojo.getExamId()))
				.filter(isActive -> isActive.isActive() == (false)).collect(Collectors.toList());
		for (ResultPojo resultModel1 : result1) {
			if (resultModel1 != null) {
				String deactivate = "update result set is_active =false where exam_id=?";
				Object[] params = { resultPojo.getExamId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (ResultPojo resultModel2 : result2) {
			if (resultModel2 != null) {
				String activate = "update result set is_active =true where exam_id=?";
				Object[] params = { resultPojo.getExamId() };
				jdbcTemplate.update(activate, params);
				return 2;
			}
		}
		return 0;
	}

	public int activateOrDeactivateWholeUserResult(ResultPojo resultPojo) {
		String select = selectResult;
		List<ResultPojo> result = jdbcTemplate.query(select, new ResultMapper());
		List<ResultPojo> result1 = result.stream().filter(userId -> userId.getUserId() == (resultPojo.getUserId()))
				.filter(isActive -> isActive.isActive() == (true)).collect(Collectors.toList());
		List<ResultPojo> result2 = result.stream().filter(userId -> userId.getUserId() == (resultPojo.getUserId()))
				.filter(isActive -> isActive.isActive() == (false)).collect(Collectors.toList());
		for (ResultPojo resultModel1 : result1) {
			if (resultModel1 != null) {
				String deactivate = "update result set is_active =false where user_id=?";
				Object[] params = { resultPojo.getUserId() };
				jdbcTemplate.update(deactivate, params);
				return 1;
			}
		}
		for (ResultPojo resultModel2 : result2) {
			if (resultModel2 != null) {
				String activate = "update result set is_active =true where user_id=?";
				Object[] params = { resultPojo.getUserId() };
				jdbcTemplate.update(activate, params);
				return 2;
			}
		}
		return 0;
	}

	public List<ResultPojo> resultList(Model model) throws JsonProcessingException {
		String select = "select exam_id,user_id,marks,is_active from result where (is_active =true)";
		List<ResultPojo> resultList = jdbcTemplate.query(select, new ResultMapper());
		ObjectMapper object = new ObjectMapper();
		String result = object.writeValueAsString(resultList);
		model.addAttribute(listOfResult, result);
		return resultList;
	}

	public List<ResultPojo> inactiveResultList(Model model) throws JsonProcessingException {
		String select = "select exam_id,user_id,marks,is_active from result where (is_active =false)";
		List<ResultPojo> resultList = jdbcTemplate.query(select, new ResultMapper());
		ObjectMapper object = new ObjectMapper();
		String result = object.writeValueAsString(resultList);
		model.addAttribute(listOfResult, result);
		return resultList;
	}

	public void resultPopup(int userId, ModelMap map, Model model) throws JsonProcessingException {
		List<UserPojo> userPojo = findStudentById(userId, model);
		for (UserPojo userModel : userPojo) {
			if (userModel != null) {
				map.addAttribute(modelUserId, userId);
				map.addAttribute("userName", userModel.getFirstName());
				String department = userModel.getDepartment();
				int semester = userModel.getSemester();
				List<SubjectPojo> subjectPojo = findSubjectNameByDepartmentSemester(department, semester);
				for (SubjectPojo subjectModel : subjectPojo) {
					if (subjectModel != null) {
						String name = subjectModel.getName();
						map.addAttribute("subjectName", subjectPojo);
						List<SubjectPojo> id = findSubjectIdByName(name);
						for (SubjectPojo subjectModel2 : id) {
							if (subjectModel2 != null) {
								String subjectID = subjectModel2.getId();
								map.addAttribute("Exam", findExamNameBySubjectID(subjectID));
								map.addAttribute("ExamType", findExamTypeBySubjectID(subjectID));
							}
						}
					}
				}
			}
		}
	}

}