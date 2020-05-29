package org.schooldb.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.IllegalCharsetNameException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Logger;


public final class DBInterface {

    private static final String STUDENT_ID_REQUEST = "Type in student ID :";
    private static final String FIRST_NAME_REQUEST = "Type in student first name :";
    private static final String LAST_NAME_REQUEST = "Type in student last name :";
    private static final String COURSE_NAME_REQUEST = "Type in course name :";
    private static final String STUDENT_NUMBER_REQUEST = "Type in number of students :";
    private static final String REPORT_REMINDER = "Please, report to your SYS admin\n";
    private static final String SQL_ERROR_WARNING = "An error in database has occurred!\n" +
            REPORT_REMINDER;
    private static final String RECORD_NOT_FOUND = "Such record is not present in database\n";
    private static final String IO_ERROR_WARNING = "Could not read data from file\n" +
            REPORT_REMINDER;
    private static final String UNABLE_TO_CLOSE_CONNECTION = "Unable to close connection\n" +
            REPORT_REMINDER;
    private static final String STUDENT_ALREADY_IN_DB = "Student is already present in database\n";
    private static final String STUDENT_WRONG_COURSE = "Student does not attend this course\n";
    private static final String STUDENT_ADDED = "New student added successfully! ID : %s\n";
    private static final String STUDENT_REMOVED = "Student %s removed successfully!\n";
    private static final String STUDENT_ADDED_TO_COURSE = "Student added to course successfully!\n";
    private static final String STUDENT_REMOVED_FROM_COURSE = "Student removed from course successfully!\n";
    private static final Logger LOGGER = Logger.getLogger(DBInterface.class.getName());

    private DBInterface() {}

    public static void configureDB(Connection connection, File[] tableFiles, File[] testDataFiles) {
        try {
            DBConfigurator.createTables(connection, tableFiles);
            DBConfigurator.generateTestData(connection, testDataFiles);
        } catch (SQLException e) {
            LOGGER.warning(SQL_ERROR_WARNING);
        } catch (IOException e) {
            LOGGER.warning(IO_ERROR_WARNING);
        }
    }

    public static void findGroupsByStudentsNumber(Connection connection) {
        System.out.println(STUDENT_NUMBER_REQUEST);
        try {
            System.out.println(DBRetriever.retrieveGroups(connection, new Scanner(System.in).nextInt()));
        } catch (SQLException e) {
         LOGGER.warning(SQL_ERROR_WARNING);
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
        } catch (SQLException e) {
            LOGGER.warning(SQL_ERROR_WARNING);
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
            System.out.println(String.format(STUDENT_ADDED,
                    DBRetriever.retrieveStudentId(connection, firstName, lastName)));
        } catch (SQLException e) {
            LOGGER.warning(SQL_ERROR_WARNING);
        } catch (IllegalArgumentException e) {
            System.err.println(STUDENT_ALREADY_IN_DB);
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
            System.out.println(String.format(STUDENT_REMOVED, removedStudent));
        } catch (SQLException e) {
            LOGGER.warning(SQL_ERROR_WARNING);
        } catch (IllegalArgumentException e) {
            System.err.println(RECORD_NOT_FOUND);
        }
    }

    public static void addStudentToCourse(Connection connection) {
        try {
            System.out.println(DBRetriever.retrieveAllCourses(connection));
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
            System.out.println(STUDENT_ADDED_TO_COURSE);
        } catch (SQLException e) {
            LOGGER.warning(SQL_ERROR_WARNING);
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
            String studentCourses = DBRetriever.retrieveStudentCourses(connection, studentId);
            System.out.println(studentCourses);
            System.out.println(COURSE_NAME_REQUEST);
            String courseName = new Scanner(System.in).nextLine();
            if (checkCourse(connection, courseName)) {
                throw new IllegalArgumentException();
            }
            if (!studentCourses.contains(courseName)) {
                throw new IllegalCharsetNameException("");
            }
            DBOperator.removeStudentFromCourse(connection, studentId, courseName);
            System.out.println(STUDENT_REMOVED_FROM_COURSE);
        } catch (SQLException e) {
            LOGGER.warning(SQL_ERROR_WARNING);
        } catch (IllegalCharsetNameException e) {
            System.err.println(STUDENT_WRONG_COURSE);
        } catch (IllegalArgumentException e) {
            System.err.println(RECORD_NOT_FOUND);
        }
    }

    public static void exitDB(Connection connection) {
        try {
            connection.close();
            System.exit(0);
        } catch (SQLException e) {
            LOGGER.warning(UNABLE_TO_CLOSE_CONNECTION);
            System.exit(-1);
        }
    }

    private static boolean checkStudentId(Connection connection, int studentId) throws SQLException {
        return DBRetriever.retrieveStudentName(connection, studentId).isEmpty();
    }

    private static boolean checkCourse(Connection connection, String courseName) throws SQLException {
        return DBRetriever.retrieveCourseId(connection, courseName).isEmpty();
    }
}
