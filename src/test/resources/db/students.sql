DROP TABLE IF EXISTS students CASCADE;
CREATE TABLE students (
    student_id SERIAL,
	group_id INT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	UNIQUE (first_name, last_name),
	PRIMARY KEY (student_id),
	FOREIGN KEY (group_id) REFERENCES groups(group_id)
	ON DELETE CASCADE
);