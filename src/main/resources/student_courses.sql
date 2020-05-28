DROP TABLE IF EXISTS student_courses CASCADE;
CREATE TABLE student_courses (
    course_id INT,
    student_id INT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
    ON DELETE CASCADE
);