package org.schooldb.dao;

import org.apache.ibatis.jdbc.ScriptRunner;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


final class DBConfigurator {

    private static final int NUMBER_OF_STUDENTS = 200;
    private static final int NUMBER_OF_GROUPS = 10;
    private static final int MINIMUM_GROUPS = 5;
    private static final int MINIMUM_PER_GROUP = 10;
    private static final int MINIMUM_COURSES = 1;
    private static final int ID_GENERATION_ADJUSTER = 1;
    private static final String GROUP_NAME_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String GROUP_NAME_DIGITS = "0123456789";
    private static final String LETTERS_DIGITS_SEPARATOR = "-";
    private static final int LETTERS_DIGITS_IN_NAME = 2;
    private static final String WHITESPACE = " ";
    private static final String INSERT_GROUPS = "INSERT INTO groups(group_name) VALUES(?)";
    private static final String INSERT_COURSES = "INSERT INTO courses(course_name, course_description) VALUES(?, ?)";
    private static final String INSERT_STUDENTS = "INSERT INTO students(first_name, last_name) VALUES(?, ?)";
    private static final String ASSIGN_TO_COURSE = "INSERT INTO students_courses(student_id,course_id) VALUES(?, ?)";
    private static final String ASSIGN_TO_GROUP = "UPDATE students SET group_id = ? WHERE student_id = ?";

    private DBConfigurator() {}

    static void createTables(Connection connection, File[] tableFiles) throws SQLException, IOException {
        try {
            for (File tableFile : tableFiles) {
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                scriptRunner.setLogWriter(null);
                scriptRunner.runScript(new FileReader(tableFile));
            }
            connection.commit();
        } catch (NullPointerException e) {
            throw new IOException();
        }
    }

    static void createTestData(Connection connection, File[] testDataFiles) throws SQLException, IOException {
        List<String> courses = readCourses(testDataFiles[0]);
        List<String> courseDescription = readCoursesDescription(testDataFiles[1]);
        File firstNamesFile = testDataFiles[2];
        File lastNamesFile = testDataFiles[3];

        insertGroups(connection);
        insertStudents(connection, firstNamesFile, lastNamesFile);
        insertCourses(connection, courses, courseDescription);
        relateTestData(connection, courses);
    }

    private static void relateTestData(Connection connection, List<String> courses) throws SQLException {
        List<Integer> groupIds = IntStream.range(1, NUMBER_OF_GROUPS + ID_GENERATION_ADJUSTER).boxed().collect(Collectors.toList());
        List<Integer> studentIds = IntStream.range(1, NUMBER_OF_STUDENTS + ID_GENERATION_ADJUSTER).boxed().collect(Collectors.toList());
        List<Integer> courseIds = IntStream.range(1, courses.size() + ID_GENERATION_ADJUSTER).boxed().collect(Collectors.toList());

        assignStudentsToCourse(connection, studentIds, courseIds);
        assignStudentsToGroups(connection, studentIds, groupIds);
    }

    private static void assignStudentsToCourse(Connection connection,
                                               List<Integer> studentIds, List<Integer> courseIds) throws SQLException {

        for (int studentId : studentIds) {
            int coursesNumberForStudent = new Random().nextInt(2) + MINIMUM_COURSES;
            List<Integer> availableCourses = new ArrayList<>(courseIds);
            for (int i = 1; i <= coursesNumberForStudent; i++) {
                int courseId = availableCourses.remove(new Random().nextInt(availableCourses.size()));
                try (PreparedStatement statement = connection.prepareStatement(ASSIGN_TO_COURSE)) {
                    statement.setInt(1, studentId);
                    statement.setInt(2, courseId);
                    statement.executeUpdate();
                }
            }
        }
        connection.commit();
    }

    private static void assignStudentsToGroups(Connection connection, List<Integer> studentIds, List<Integer> groupIds) throws SQLException {
        for (int counter = 0; counter < new Random().nextInt(5) + MINIMUM_GROUPS; counter++) {
            int groupId = groupIds.remove(new Random().nextInt(groupIds.size()));
            int studentsInGroup = new Random().nextInt(20) + MINIMUM_PER_GROUP;
            for (int student = 0; student < studentsInGroup ; student++) {
                int studentId = studentIds.remove(new Random().nextInt(studentIds.size()));
                try (PreparedStatement statement = connection.prepareStatement(ASSIGN_TO_GROUP)) {
                    statement.setInt(1, groupId);
                    statement.setInt(2, studentId);
                    statement.executeUpdate();
                }
            }
        }
        connection.commit();
    }

    private static void insertGroups(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_GROUPS)) {
            for (String groupName : generateGroups()) {
                statement.setString(1, groupName);
                statement.executeUpdate();
            }
        }
        connection.commit();
    }

    private static void insertStudents(Connection connection, File firstNamesFile, File lastNamesFile) throws SQLException, IOException {
        int firstNamePos = 0;
        int lastNamePos = 1;
        try (PreparedStatement statement = connection.prepareStatement(INSERT_STUDENTS)) {
            for (String student : generateStudents(firstNamesFile, lastNamesFile)) {
                statement.setString(1, student.split(WHITESPACE)[firstNamePos]);
                statement.setString(2, student.split(WHITESPACE)[lastNamePos]);
                statement.executeUpdate();
            }
        }
        connection.commit();
    }

    private static void insertCourses(Connection connection,
                                      List<String> courses, List<String> courseDescriptions) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_COURSES)) {
            for (int index = 0; index < courses.size(); index++) {
                statement.setString(1, courses.get(index));
                statement.setString(2, courseDescriptions.get(index));
                statement.executeUpdate();
            }
        }
        connection.commit();
    }

    private static List<String> generateGroups() {
        List<String> groups = new ArrayList<>();
        for (int group = 0; group < NUMBER_OF_GROUPS; group++) {
            StringBuilder groupName = new StringBuilder();
            for (int i = 0; i < LETTERS_DIGITS_IN_NAME; i++) {
                groupName.append(GROUP_NAME_LETTERS
                        .charAt(new Random().nextInt(GROUP_NAME_LETTERS.length())));
            }
            groupName.append(LETTERS_DIGITS_SEPARATOR);
            for (int i = 0; i < LETTERS_DIGITS_IN_NAME; i++) {
                groupName.append(GROUP_NAME_DIGITS
                        .charAt(new Random().nextInt(GROUP_NAME_DIGITS.length())));
            }
            groups.add(groupName.toString());
        }
        return groups;
    }

    private static List<String> readCoursesDescription(File courseDescription) throws IOException {
        return Files.readAllLines(courseDescription.toPath());
    }

    private static List<String> readCourses(File courses) throws IOException {
        return Files.readAllLines(courses.toPath());
    }

    private static List<String> generateStudents(File firstNames, File lastNames) throws IOException {
        List<String> students = new ArrayList<>();
        List<String> firstNamesList = Files.readAllLines(firstNames.toPath());
        List<String> lastNamesList = Files.readAllLines(lastNames.toPath());
        for (int student = 0; student < NUMBER_OF_STUDENTS; student++) {
            String fullName = String.format("%s %s", firstNamesList.get(new Random().nextInt(firstNamesList.size())),
                    lastNamesList.get(new Random().nextInt(lastNamesList.size())));
            if (students.contains(fullName)) {
                student--;
            } else {
                students.add(fullName);
            }
        }
        return students;
    }
}
