package org.schooldb;

import org.schooldb.dao.DBInterface;
import org.schooldb.util.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Logger;
import static org.schooldb.util.FileUtil.getFile;


public class Main {

    private static final String CONNECTION_SUCCESS = "Connection established\n";
    private static final String MENU = "1. Find all groups with less or equals number of students\n" +
                                        "2. Find all students related to course with given name\n" +
                                        "3. Add new student\n" +
                                        "4. Delete student by STUDENT_ID\n" +
                                        "5. Add a student to the course (from a list)\n" +
                                        "6. Remove the student from one of his or her courses\n" +
                                        "7. Exit\n";
    private static final String CONNECTION_ERR = "Connection cannot be established\n";
    private static final String DRIVER_ERR = "JDBC Driver have not been found\n";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            Class.forName(Config.getProperty("DB_DRIVER"));
            launchDBInterface();
        } catch (ClassNotFoundException e) {
            LOGGER.warning(DRIVER_ERR);
        }
    }

    private static void launchDBInterface() {
        try (Connection connection = DriverManager.getConnection(Config.getProperty("DB_URL"),
                Config.getProperty("DB_USERNAME"), Config.getProperty("DB_PASSWORD"))) {
            System.out.println(CONNECTION_SUCCESS);
            DBInterface.configureDB(connection, getFile(Config.getArrayProperty("SQL_TABLE_FILES")),
                    getFile(Config.getArrayProperty("TEST_DATA_FILES")));
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
            LOGGER.warning(CONNECTION_ERR);
        }
    }

    private static void printMenu() {
        System.out.println(MENU);
    }
}
