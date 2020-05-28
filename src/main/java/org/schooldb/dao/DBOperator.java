package org.schooldb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


final class DBOperator {

    private static final String ADD_STUDENT = "INSERT INTO students(first_name, last_name) VALUES(?, ?)";
    private static final String DELETE_STUDENT = "DELETE FROM students WHERE student_id = ?";
    private static final String ADD_STUDENT_TO_COURSE = "INSERT INTO student_courses(course_id, student_id) " +
            "SELECT courses.course_id, students.student_id FROM courses, students " +
            "WHERE courses.course_name = ? AND students.student_id = ?";
    private static final String DELETE_STUDENT_FROM_COURSE = "DELETE FROM student_courses " +
            "WHERE student_id = ? AND course_id = (" +
            "SELECT course_id FROM courses WHERE course_name = ?)";

    private DBOperator() {}

    static void addNewStudent(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(ADD_STUDENT)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.executeUpdate();
        }
        connection.commit();
    }

    static void removeStudent(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT)) {
            statement.setInt(1, studentId);
            statement.executeUpdate();
        }
        connection.commit();
    }

    static void addStudentToCourse(Connection connection, int studentId, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(ADD_STUDENT_TO_COURSE)) {
            statement.setString(1, courseName);
            statement.setInt(2, studentId);
            statement.executeUpdate();
        }
        connection.commit();
    }

    static void removeStudentFromCourse(Connection connection, int studentId, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_STUDENT_FROM_COURSE)) {
            statement.setInt(1, studentId);
            statement.setString(2, courseName);
            statement.executeUpdate();
        }
        connection.commit();
    }
}
