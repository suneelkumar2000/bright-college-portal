create database Bright_College_Portal;
use Bright_College_Portal;
show tables;

create table classroom(
id integer not null auto_increment primary key,
department varchar(50) not null unique key,
is_active boolean default true
);
select * from classroom;
insert into classroom values(0,'not selected',true);
truncate classroom;
delete from classroom where id = 18;

drop table classroom;
update classroom set is_active = false where department="Computer Science";

create table user(
id integer not null auto_increment primary key,
first_name varchar(30)not null,last_name varchar(30),
dob date not null,gender varchar(20) not null,
phone_number long not null,
email varchar(100) not null,password varchar(100) not null,
roll varchar(20) not null,department varchar(50) DEFAULT 'not selected',
parent_name varchar(50),year_of_joining integer(10),semester integer,
status varchar(20) DEFAULT 'not approved',image blob,is_active boolean default true,
FOREIGN KEY (department) REFERENCES classroom(department)
);

select*from user;
drop table user;
truncate user;

create table attendance(
user_id integer,
total_days integer,
days_attended integer,
days_leave integer,
attendance double,
semester integer,
is_active boolean default true,
FOREIGN KEY (user_id) REFERENCES user(id),
FOREIGN KEY (semester) REFERENCES semester(id)
);
select * from attendance;
delete from attendance where user_id = 4;
drop table attendance;
truncate attendance;

create table leaves(
id integer not null auto_increment primary key,
user_id integer not null,
department varchar(50)DEFAULT 'not selected',
reason varchar(300) not null,
leave_from date,
leave_to date,
status varchar(20)DEFAULT 'not approved', 
is_active boolean default true,
FOREIGN KEY (user_id) REFERENCES user(id),
FOREIGN KEY (department) REFERENCES classroom(department)
);
select * from leaves;
drop table leaves;
truncate leaves;

create table semester(
id integer not null primary key,
is_active boolean default true
);
select * from semester;
drop table semester;
delete from semester where id = 9;

create table subjects(
id varchar(30) DEFAULT 'not set' primary key,
name varchar(30)not null,
semester_id integer,
department varchar(50)DEFAULT 'not selected',
is_active boolean default true,
FOREIGN KEY (semester_id) REFERENCES semester(id),
FOREIGN KEY (department) REFERENCES classroom(department)
);
select * from subjects;
drop table subjects;



create table exam(
id integer auto_increment not null primary key,
subject_id varchar(30) not null,
name varchar(30)not null,
date_ date,
type varchar(30)not null,
is_active boolean default true,
FOREIGN KEY (subject_id) REFERENCES subjects(id)
);
ALTER TABLE exam AUTO_INCREMENT = 1000;
select * from exam;
drop table exam;

create table result(
exam_id integer not null,
user_id integer not null,
marks integer not null,
is_active boolean default true,
FOREIGN KEY (exam_id) REFERENCES exam(id),
FOREIGN KEY (user_id) REFERENCES user(id)
);
select * from result;
drop table result;


-- SET SQL_SAFE_UPDATES=0;


DROP TABLE user_aud;
CREATE TABLE user_aud(
`id` integer NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a user_audit_insert trigger
DELIMITER $$
CREATE TRIGGER user_audit_insert
AFTER INSERT ON user
FOR EACH ROW
BEGIN
INSERT INTO user_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a user_audit_delete trigger
DELIMITER $$
CREATE TRIGGER user_audit_update
AFTER UPDATE ON user
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO user_aud(id, `action`, aud_timestamp)
VALUES (OLD.id, 'DELETE', NOW());
ELSE
INSERT INTO user_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM user_aud;



DROP TABLE classroom_aud;
CREATE TABLE classroom_aud(
id INT PRIMARY KEY AUTO_INCREMENT ,
`department` varchar(50) NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a classroom_audit_insert trigger
DELIMITER $$
CREATE TRIGGER classroom_audit_insert
AFTER INSERT ON classroom
FOR EACH ROW
BEGIN
INSERT INTO classroom_aud(department, `action`, aud_timestamp)
VALUES (NEW.department, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a classroom_audit_delete trigger
DELIMITER $$
CREATE TRIGGER classroom_audit_update
AFTER UPDATE ON classroom
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO classroom_aud(department, `action`, aud_timestamp)
VALUES (OLD.department, 'DELETE', NOW());
ELSE
INSERT INTO classroom_aud(department, `action`, aud_timestamp)
VALUES (NEW.department, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM classroom_aud;



DROP TABLE attendance_aud;
CREATE TABLE attendance_aud(
`user_id` integer NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a attendance_audit_insert trigger
DELIMITER $$
CREATE TRIGGER attendance_audit_insert
AFTER INSERT ON attendance
FOR EACH ROW
BEGIN
INSERT INTO attendance_aud(user_id, `action`, aud_timestamp)
VALUES (NEW.user_id, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a attendance_audit_delete trigger
DELIMITER $$
CREATE TRIGGER attendance_audit_update
AFTER UPDATE ON attendance
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO attendance_aud(user_id, `action`, aud_timestamp)
VALUES (OLD.user_id, 'DELETE', NOW());
ELSE
INSERT INTO attendance_aud(User_ID, `action`, aud_timestamp)
VALUES (NEW.user_id, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM attendance_aud;



DROP TABLE leaves_aud;
CREATE TABLE leaves_aud(
`id` integer NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a leaves_audit_insert trigger
DELIMITER $$
CREATE TRIGGER leaves_audit_insert
AFTER INSERT ON leaves
FOR EACH ROW
BEGIN
INSERT INTO leaves_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a leaves_audit_delete trigger
DELIMITER $$
CREATE TRIGGER leaves_audit_update
AFTER UPDATE ON leaves
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO leaves_aud(id, `action`, aud_timestamp)
VALUES (OLD.id, 'DELETE', NOW());
ELSE
INSERT INTO leaves_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM leaves_aud;



DROP TABLE semester_aud;
CREATE TABLE semester_aud(
`id` integer NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a semester_audit_insert trigger
DELIMITER $$
CREATE TRIGGER semester_audit_insert
AFTER INSERT ON semester
FOR EACH ROW
BEGIN
INSERT INTO semester_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a semester_audit_delete trigger
DELIMITER $$
CREATE TRIGGER semester_audit_update
AFTER UPDATE ON semester
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO semester_aud(id, `action`, aud_timestamp)
VALUES (OLD.id, 'DELETE', NOW());
ELSE
INSERT INTO semester_aud(ID, `action`, aud_timestamp)
VALUES (NEW.id, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM semester_aud;



DROP TABLE subjects_aud;
CREATE TABLE subjects_aud(
`id` VARCHAR(30) NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a subjects_audit_insert trigger
DELIMITER $$
CREATE TRIGGER subjects_audit_insert
AFTER INSERT ON subjects
FOR EACH ROW
BEGIN
INSERT INTO subjects_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a subjects_audit_delete trigger
DELIMITER $$
CREATE TRIGGER subjects_audit_update
AFTER UPDATE ON subjects
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO subjects_aud(id, `action`, aud_timestamp)
VALUES (OLD.id, 'DELETE', NOW());
ELSE
INSERT INTO subjects_aud(ID, `action`, aud_timestamp)
VALUES (NEW.id, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM subjects_aud;



DROP TABLE exam_aud;
CREATE TABLE exam_aud(
`id` integer NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a exam_audit_insert trigger
DELIMITER $$
CREATE TRIGGER exam_audit_insert
AFTER INSERT ON exam
FOR EACH ROW
BEGIN
INSERT INTO exam_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a exam_audit_delete trigger
DELIMITER $$
CREATE TRIGGER exam_audit_update
AFTER UPDATE ON exam
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO exam_aud(id, `action`, aud_timestamp)
VALUES (OLD.ID, 'DELETE', NOW());
ELSE
INSERT INTO exam_aud(id, `action`, aud_timestamp)
VALUES (NEW.id, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM exam_aud;



DROP TABLE result_aud;
CREATE TABLE result_aud(
`user_id` integer NOT NULL,
`action` VARCHAR(10) NOT NULL,
`aud_timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- creating a result_audit_insert trigger
DELIMITER $$
CREATE TRIGGER result_audit_insert
AFTER INSERT ON result
FOR EACH ROW
BEGIN
INSERT INTO result_aud(user_id, `action`, aud_timestamp)
VALUES (NEW.user_id, 'INSERT', NOW());
END;
$$
DELIMITER ;
-- creating a result_audit_delete trigger
DELIMITER $$
CREATE TRIGGER result_audit_update
AFTER UPDATE ON result
FOR EACH ROW
BEGIN
IF NEW.is_active = 0 THEN  -- If is_active is set to 0, consider it as a DELETE operation
INSERT INTO result_aud(user_id, `action`, aud_timestamp)
VALUES (OLD.user_id, 'DELETE', NOW());
ELSE
INSERT INTO result_aud(user_id, `action`, aud_timestamp)
VALUES (NEW.user_id, 'UPDATE', NOW());
END IF;
END;
$$
DELIMITER ;
SELECT * FROM result_aud;