package org.schooldb;

import org.schooldb.dao.DBInterface;
import org.schooldb.util.FileFinderUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


public class Main {

    private static final String[] TABLE_NAMES = {"student_groups", "students", "courses", "student_courses"};
    private static final String[] TABLE_FILE_NAMES = {"student_groups.sql", "students.sql", "courses.sql", "student_courses.sql"};
    private static final String[] TEST_DATA_FILE_NAMES = {"courses.txt", "first_names.txt", "last_names.txt"};
    private static final String URL = "jdbc:postgresql://localhost:5432/school";
    private static final String USERNAME = "school";
    private static final String PASSWORD = "1";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String CONNECTION_SUCCESS = "Connection established";
    private static final String MENU = "1. Find all groups with less or equals number of students\n" +
                                        "2. Find all students related to course with given name\n" +
                                        "3. Add new student\n" +
                                        "4. Delete student by STUDENT_ID\n" +
                                        "5. Add a student to the course (from a list)\n" +
                                        "6. Remove the student from one of his or her courses\n" +
                                        "7. Exit";
    private static final String CONNECTION_ERR = "Connection cannot be established";
    private static final String FILE_NOT_FOUND_ERR = "File %s have not been found";
    private static final String DRIVER_ERR = "JDBC Driver have not been found";

    public static void main(String[] args) {
        try {
            Class.forName(DRIVER);
            launchDBInterface();
        } catch (ClassNotFoundException e) {
            System.err.println(DRIVER_ERR);
            e.printStackTrace();
        }
    }

    private static void launchDBInterface() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println(CONNECTION_SUCCESS);
            DBInterface.configureDB(connection, TABLE_NAMES, getFiles(TABLE_FILE_NAMES), getFiles(TEST_DATA_FILE_NAMES));
            while (true) {
                printMenu();
                int i = new Scanner(System.in).nextInt();
                if (i == 1) {
                    DBInterface.findGroupsByStudentsNumber(connection);
                } else if (i == 2) {
                    DBInterface.findStudentsByCourse(connection);
                } else if (i == 3) {
                    DBInterface.addNewStudent(connection);
                } else if (i == 4) {
                    DBInterface.removeStudent(connection);
                } else if (i == 5) {
                    DBInterface.addStudentToCourse(connection);
                } else if (i == 6) {
                    DBInterface.removeStudentFromCourse(connection);
                } else if (i == 7) {
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println(CONNECTION_ERR);
        }
    }

    private static File[] getFiles(String[] fileNames) {
        File[] tableFiles = new File[fileNames.length];
        for (int index = 0; index < fileNames.length; index++) {
            try {
                tableFiles[index] = FileFinderUtil.getFile(fileNames[index]);
            } catch (FileNotFoundException e) {
                System.err.println(String.format(FILE_NOT_FOUND_ERR, fileNames[index]));
            }
        }
        return tableFiles;
    }

    private static void printMenu() {
        System.out.println(MENU);
        System.out.println();
    }
}
