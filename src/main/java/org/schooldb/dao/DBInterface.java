package org.schooldb.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;


public final class DBInterface {

    private static final String STUDENT_ID_REQUEST = "Type in student ID :";
    private static final String FIRST_NAME_REQUEST = "Type in student first name :";
    private static final String LAST_NAME_REQUEST = "Type in student last name :";
    private static final String COURSE_NAME_REQUEST = "Type in course name :";
    private static final String STUDENT_NUMBER_REQUEST = "Type in number of students :";
    private static final String ERROR_WARNING = "An error in database has occurred!" +
            "\nPlease, report to your nearest SYS admin";
    private static final String RECORD_NOT_FOUND = "Such record is not present in database";

    private DBInterface() {}

    public static void configureDB(Connection connection, String[] tableNames, File[] tableFiles, File[] testDataFiles) {
        try {
            DBConfigurator.createTables(connection, tableNames, tableFiles);
            DBConfigurator.generateTestData(connection, testDataFiles);
        } catch (SQLException e) {
            System.err.println(ERROR_WARNING);
        } catch (IOException e) {
            System.err.println();
        }
    }

    public static void findGroupsByStudentsNumber(Connection connection) {
        System.out.println(STUDENT_NUMBER_REQUEST);
        try {
            System.out.println(DBRetriever.retrieveGroups(connection, new Scanner(System.in).nextInt()));
            System.out.println();
        } catch (SQLException e) {
         System.err.println(ERROR_WARNING);
        }
    }

    public static void findStudentsByCourse(Connection connection) {
        System.out.println(COURSE_NAME_REQUEST);
        String courseName = new Scanner(System.in).nextLine();
        try {
            if (checkCourse(connection, courseName)) {
                throw new IllegalArgumentException();
            }
            System.out.println(DBRetriever.retrieveStudents(connection, courseName));
            System.out.println();
        } catch (SQLException e) {
            System.err.println(ERROR_WARNING);
        } catch (IllegalArgumentException e) {
            System.err.println(RECORD_NOT_FOUND);
        }
    }

    public static void addNewStudent(Connection connection) {
        try {
            System.out.println(FIRST_NAME_REQUEST);
            String firstName = new Scanner(System.in).nextLine();
            System.out.println(LAST_NAME_REQUEST);
            String lastName = new Scanner(System.in).nextLine();
            if (!DBRetriever.retrieveStudentId(connection, firstName, lastName).isEmpty()) {
                throw new IllegalArgumentException();
            }
            DBOperator.addNewStudent(connection, firstName, lastName);
            System.out.println(String.format("New student added successfully! ID : %s",
                    DBRetriever.retrieveStudentId(connection, firstName, lastName)));
            System.out.println();
        } catch (SQLException e) {
            System.err.println(ERROR_WARNING);
        } catch (IllegalArgumentException e) {
            System.err.println("Student is already in database");
        }
    }

    public static void removeStudent(Connection connection) {
        System.out.println(STUDENT_ID_REQUEST);
        int studentId = new Scanner(System.in).nextInt();
        try {
            if (checkStudentId(connection, studentId)) {
                throw new IllegalArgumentException();
            }
            String removedStudent= DBRetriever.retrieveStudentName(connection, studentId);
            DBOperator.removeStudent(connection, studentId);
            System.out.println(String.format("Student %s removed successfully!", removedStudent));
            System.out.println();
        } catch (SQLException e) {
            System.err.println(ERROR_WARNING);
        } catch (IllegalArgumentException e) {
            System.err.println(RECORD_NOT_FOUND);
        }
    }

    public static void addStudentToCourse(Connection connection) {
        try {
            System.out.println(DBRetriever.retrieveCourses(connection));
            System.out.println();
            System.out.println(STUDENT_ID_REQUEST);
            int studentId = new Scanner(System.in).nextInt();
            if (checkStudentId(connection, studentId)) {
                throw new IllegalArgumentException();
            }
            System.out.println(COURSE_NAME_REQUEST);
            String courseName = new Scanner(System.in).nextLine();
            if (checkCourse(connection, courseName)) {
                throw new IllegalArgumentException();
            }
            DBOperator.addStudentToCourse(connection, studentId, courseName);
            System.out.println("Student added to course successfully!");
            System.out.println();
        } catch (SQLException e) {
            System.err.println(ERROR_WARNING);
        } catch (IllegalArgumentException e) {
            System.err.println(RECORD_NOT_FOUND);
        }
    }

    public static void removeStudentFromCourse(Connection connection) {
        try {
            System.out.println(STUDENT_ID_REQUEST);
            int studentId = new Scanner(System.in).nextInt();
            if (checkStudentId(connection, studentId)) {
                throw new IllegalArgumentException();
            }
            System.out.println(COURSE_NAME_REQUEST);
            String courseName = new Scanner(System.in).nextLine();
            if (checkCourse(connection, courseName)) {
                throw new IllegalArgumentException();
            }
            DBOperator.removeStudentFromCourse(connection, studentId, courseName);
            System.out.println("Student removed from course successfully!");
            System.out.println();
        } catch (SQLException e) {
            System.err.println(ERROR_WARNING);
        } catch (IllegalArgumentException e) {
            System.err.println(RECORD_NOT_FOUND);
        }
    }

    private static boolean checkStudentId(Connection connection, int studentId) throws SQLException {
        return DBRetriever.retrieveStudentName(connection, studentId).isEmpty();
    }

    private static boolean checkCourse(Connection connection, String courseName) throws SQLException {
        return DBRetriever.retrieveCourseId(connection, courseName).isEmpty();
    }
}
