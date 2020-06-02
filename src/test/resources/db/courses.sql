DROP TABLE IF EXISTS courses CASCADE;
CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
	course_name VARCHAR(50) NOT NULL UNIQUE,
	course_description VARCHAR(50)
);