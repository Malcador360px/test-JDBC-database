DROP TABLE IF EXISTS students_courses CASCADE;
CREATE TABLE students_courses (
    course_id INT NOT NULL,
    student_id INT NOT NULL,
    UNIQUE(course_id, student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
    ON DELETE CASCADE
);