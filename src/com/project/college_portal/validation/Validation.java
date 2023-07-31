package com.project.college_portal.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
	public boolean nameValidation(String name) {
		Pattern p = Pattern.compile("[a-zA-Z]+");
		Matcher m = p.matcher(name);
		boolean name1 = m.matches();
		if (name1)
			return true;
		else
			return false;
	}

	public boolean emailValidation(String email) {
		Pattern p = Pattern.compile("^(.+)@(.+)$");
		Matcher m = p.matcher(email);
		boolean mail = m.matches();
		if (mail)
			return true;
		else
			return false;
	}

	public boolean phoneNumberValidation(Long phoneNumber) {
		String phone=String.valueOf(phoneNumber);
		Pattern p = Pattern.compile("(0|91)?[6-9]\\d{9}");
		Matcher m = p.matcher(phone);
		boolean phone1 = m.matches();
		if (phone1)
			return true;
		else
			return false;
	}
	
	public boolean adminEmailValidation(String email) {
		Pattern p = Pattern.compile("^(.+)(hod)@(brightcollege)(.+)$");
		Matcher m = p.matcher(email);
		boolean mail = m.matches();
		if (mail)
			return true;
		else
			return false;
	}
}