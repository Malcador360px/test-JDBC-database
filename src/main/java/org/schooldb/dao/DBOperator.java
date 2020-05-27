package org.schooldb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


final class DBOperator {

    private DBOperator() {}

    static void addNewStudent(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO students(first_name, last_name) VALUES(?, ?)")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.execute();
        }
        connection.commit();
    }

    static void removeStudent(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
            statement.setInt(1, studentId);
            statement.execute();
        }
        connection.commit();
    }

    static void addStudentToCourse(Connection connection, int studentId, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO student_courses(course_id, student_id) " +
                "SELECT courses.course_id, students.student_id FROM courses, students " +
                "WHERE courses.course_name = ? AND students.student_id = ?")) {
            statement.setString(1, courseName);
            statement.setInt(2, studentId);
            statement.execute();
        }
        connection.commit();
    }

    static void removeStudentFromCourse(Connection connection, int studentId, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM student_courses " +
                "WHERE student_id = ? AND course_id = (" +
                "SELECT course_id FROM courses WHERE course_name = ?)")) {
            statement.setInt(1, studentId);
            statement.setString(2, courseName);
            statement.execute();
        }
        connection.commit();
    }
}
