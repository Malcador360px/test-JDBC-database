CREATE TABLE students (
    student_id SERIAL,
	group_id INT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	PRIMARY KEY (student_id),
	FOREIGN KEY (group_id) REFERENCES student_groups(group_id)
	ON DELETE CASCADE
);