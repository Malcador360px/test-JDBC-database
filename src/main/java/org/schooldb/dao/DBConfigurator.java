package org.schooldb.dao;

import org.apache.ibatis.jdbc.ScriptRunner;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


final class DBConfigurator {

    private static final int TABLE_NAMES_META_DATA_POS = 3;
    private static final String ALL_NAME_FORMATS_WILDCARD = "%";
    private static final int NUMBER_OF_STUDENTS = 200;
    private static final int NUMBER_OF_GROUPS = 10;
    private static final int MINIMUM_PER_GROUP = 10;

    private DBConfigurator() {}

    static void createTables(Connection connection, String[] tableNames, File[] tableFiles) throws SQLException, IOException {
        if (!checkExistingTables(connection, tableNames)) {
            dropConflictingTables(connection, tableNames);
        }

        for (File tableFile : tableFiles) {
            new ScriptRunner(connection).runScript(new BufferedReader(new FileReader(tableFile)));
        }
        connection.commit();
    }

    private static boolean checkExistingTables(Connection connection, String[] tableNames) throws SQLException {
        try (ResultSet tablesInDB = connection.getMetaData().getTables(null, null,ALL_NAME_FORMATS_WILDCARD, null)) {
            while (tablesInDB.next()) {
                if (Arrays.asList(tableNames).contains(tablesInDB.getString(TABLE_NAMES_META_DATA_POS))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void dropConflictingTables(Connection connection, String[] tableNames) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String tableName : tableNames) {
                statement.execute("DROP TABLE IF EXISTS " + tableName + " CASCADE");
            }
        }
    }

    static void generateTestData(Connection connection, File[] testDataFiles) throws SQLException, IOException {
        List<String> courses = readCourses(testDataFiles[0]);
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO student_groups(group_name) VALUES(?)")) {
            for (String groupName : generateGroups()) {
                statement.setString(1, groupName);
                statement.execute();
            }
        }
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO courses(course_name) VALUES(?)")) {
            for (String course : courses) {
                statement.setString(1, course);
                statement.execute();
            }
        }
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO students(first_name, last_name) VALUES(?, ?)")) {
            for (String student : generateStudents(testDataFiles[1], testDataFiles[2])) {
                statement.setString(1, student.split(" ")[0]);
                statement.setString(2, student.split(" ")[1]);
                statement.execute();
            }
        }
        relateTestData(connection, courses);
        connection.commit();
    }

    private static void relateTestData(Connection connection, List<String> courses) throws SQLException {
        List<Integer> groupIds = IntStream.range(1, NUMBER_OF_GROUPS + 1).boxed().collect(Collectors.toList());
        List<Integer> studentIds = IntStream.range(1, NUMBER_OF_STUDENTS + 1).boxed().collect(Collectors.toList());
        for (int studentId : studentIds) {
            for (int i = 0; i <= new Random().nextInt(2); i++) {
                int courseId = new Random().nextInt(courses.size()) + 1;
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO student_courses(student_id,course_id) VALUES(?, ?)")) {
                    statement.setInt(1, studentId);
                    statement.setInt(2, courseId);
                    statement.execute();
                }
            }
        }
        for (int counter = 0; counter < new Random().nextInt(NUMBER_OF_GROUPS); counter++) {
            int groupId = groupIds.remove(new Random().nextInt(groupIds.size()));
            int studentsInGroup = new Random().nextInt(20) + MINIMUM_PER_GROUP;
            for (int student = 0; student < studentsInGroup ; student++) {
                int studentId = studentIds.remove(new Random().nextInt(studentIds.size()));
                try (PreparedStatement statement = connection.prepareStatement("UPDATE students SET group_id = ? WHERE student_id = ?")) {
                    statement.setInt(1, groupId);
                    statement.setInt(2, studentId);
                    statement.execute();
                }
            }
        }
    }

    private static List<String> generateGroups() {
        List<String> groups = new ArrayList<>();
        for (int group = 0; group < NUMBER_OF_GROUPS; group++) {
            StringBuilder groupName = new StringBuilder();
            String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String digits = "0123456789";
            for (int i = 0; i < 2; i++) {
                groupName.append(letters.charAt(new Random().nextInt(letters.length())));
            }
            groupName.append("-");
            for (int i = 0; i < 2; i++) {
                groupName.append(digits.charAt(new Random().nextInt(digits.length())));
            }
            groups.add(groupName.toString());
        }
        return groups;
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
