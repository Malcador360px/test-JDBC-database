package org.schooldb.dao;

import java.sql.*;
import java.util.Collections;


final class DBRetriever {

    private static final String COLUMN_SEPARATOR = " | ";
    private static final String NEW_LINE = "\n";
    private static final String WHITESPACE = " ";
    private static final String RETRIEVE_GROUPS = "SELECT group_name, COUNT(students.group_id) FROM groups " +
            "INNER JOIN students ON students.group_id = groups.group_id " +
            "GROUP BY students.group_id, groups.group_name HAVING COUNT(students.group_id) <= ?";
    private static final String RETRIEVE_STUDENTS_BY_COURSE = "SELECT students.student_id, students.first_name, students.last_name FROM courses " +
            "INNER JOIN student_courses ON courses.course_id = student_courses.course_id " +
            "INNER JOIN students ON student_courses.student_id = students.student_id " +
            "WHERE courses.course_name = ?";
    private static final String RETRIEVE_ALL_COURSES = "SELECT course_name, course_description FROM courses";
    private static final String RETRIEVE_STUDENT_NAME = "SELECT first_name, last_name FROM students WHERE student_id = ?";
    private static final String RETRIEVE_STUDENT_ID = "SELECT student_id FROM students WHERE first_name = ? AND last_name = ?";
    private static final String RETRIEVE_COURSE_ID = "SELECT course_id FROM courses WHERE course_name = ?";
    private static final String RETRIEVE_STUDENT_COURSES = "SELECT courses.course_name FROM student_courses " +
            "INNER JOIN courses ON student_courses.course_id = courses.course_id " +
            "WHERE student_courses.student_id = ?";
    private static final int OUTPUT_COLUMN_WIDTH = 50;

    private DBRetriever() {}

    static String retrieveGroups(Connection connection, int studentsNumber) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_GROUPS)) {
            statement.setInt(1, studentsNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getColumns(resultSet);
            }
        }
    }

    static String retrieveStudents(Connection connection, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_STUDENTS_BY_COURSE)) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getColumns(resultSet);
            }
        }
    }
    static String retrieveAllCourses(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(RETRIEVE_ALL_COURSES)) {
            return getColumns(resultSet);
        }
    }

    static String retrieveStudentName(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_STUDENT_NAME)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getAsRow(resultSet);
            }
        }
    }

    static String retrieveStudentId(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_STUDENT_ID)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getAsRow(resultSet);
            }
        }
    }

    static String retrieveCourseId(Connection connection, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_COURSE_ID)) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getAsRow(resultSet);
            }
        }
    }

    static String retrieveStudentCourses(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_STUDENT_COURSES)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getColumns(resultSet);
            }
        }
    }

    private static String getColumns(ResultSet resultSet) throws SQLException {
        StringBuilder output = new StringBuilder();
        writeColumnHeaders(output, resultSet);
        output.append(NEW_LINE);
        writeColumns(output, resultSet);
        return output.toString();
    }

    private static String getAsRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        StringBuilder output = new StringBuilder();
        int columnsNumber = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i == columnsNumber) {
                    output.append(resultSet.getString(i));
                } else {
                    output.append(resultSet.getString(i)).append(WHITESPACE);
                }
            }
        }
        return output.toString();
    }

    private static void writeColumnHeaders(StringBuilder output, ResultSet table) throws SQLException {
        ResultSetMetaData metaData = table.getMetaData();
        int columnsNumber = metaData.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            if (i > 1) {
                output.append(COLUMN_SEPARATOR);
            }
            output.append(metaData.getColumnName(i));
            output.append(repeatString(WHITESPACE,
                    OUTPUT_COLUMN_WIDTH - metaData.getColumnName(i).length()));
        }
    }

    private static void writeColumns(StringBuilder output, ResultSet table) throws SQLException {
        int columnsNumber = table.getMetaData().getColumnCount();
        while (table.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) {
                    output.append(COLUMN_SEPARATOR);
                }
                output.append(table.getString(i));
                if (table.getString(i) != null) {
                    output.append(repeatString(WHITESPACE,
                            OUTPUT_COLUMN_WIDTH - table.getString(i).length()));
                }
            }
            output.append(NEW_LINE);
        }
    }

    private static String repeatString(String str, int copies) {
        return String.join("", Collections.nCopies(copies, str));
    }
}
