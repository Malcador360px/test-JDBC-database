package org.schooldb.dao;

import java.sql.*;


final class DBRetriever {

    private static final String COLUMN_SEPARATOR = " | ";
    private static final String NEW_LINE = "\n";
    private static final String WHITESPACE = " ";

    private DBRetriever() {}

    static String retrieveGroups(Connection connection, int studentsNumber) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT group_name, COUNT(students.group_id) FROM student_groups " +
                "INNER JOIN students ON students.group_id = student_groups.group_id " +
                "GROUP BY students.group_id, student_groups.group_name HAVING COUNT(students.group_id) <= ?")) {
            statement.setInt(1, studentsNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return convertResultToString(resultSet);
            }
        }
    }

    static String retrieveStudents(Connection connection, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT students.student_id, students.first_name, students.last_name FROM courses " +
                "INNER JOIN student_courses ON courses.course_id = student_courses.course_id " +
                "INNER JOIN students ON student_courses.student_id = students.student_id " +
                "WHERE courses.course_name = ?")) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return convertResultToString(resultSet);
            }
        }
    }
    static String retrieveAllCourses(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT course_name, course_description FROM courses")) {
            return convertResultToString(resultSet);
        }
    }

    static String retrieveStudentName(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT first_name, last_name FROM students WHERE student_id = ?")) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getRow(resultSet);
            }
        }
    }

    static String retrieveStudentId(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT student_id FROM students WHERE first_name = ? AND last_name = ?")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getRow(resultSet);
            }
        }
    }

    static String retrieveCourseId(Connection connection, String courseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT course_id FROM courses WHERE course_name = ?")) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return getRow(resultSet);
            }
        }
    }

    static String retrieveStudentCourses(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT courses.course_name FROM student_courses " +
                "INNER JOIN courses ON student_courses.course_id = courses.course_id " +
                "WHERE student_courses.student_id = ?")) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return convertResultToString(resultSet);
            }
        }
    }

    private static String convertResultToString(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        StringBuilder output = new StringBuilder();
        int columnsNumber = metaData.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            if (i > 1) {
                output.append(COLUMN_SEPARATOR);
            }
            output.append(metaData.getColumnName(i));
        }
        output.append(NEW_LINE);
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) {
                    output.append(COLUMN_SEPARATOR);
                }
                output.append(resultSet.getString(i));
            }
            output.append(NEW_LINE);
            }
        return output.toString();
    }

    private static String getRow(ResultSet resultSet) throws SQLException{
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
}
