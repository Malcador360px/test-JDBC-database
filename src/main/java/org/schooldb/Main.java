package org.schooldb;

import org.schooldb.commands.Commander;
import org.schooldb.dao.DBConnector;
import org.schooldb.dao.DBInterface;
import org.schooldb.util.Config;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import static org.schooldb.util.FileUtil.getFile;


public class Main {

    private static final String CONNECTION_ERR = "Connection cannot be established\n";
    private static final String MENU = "1. Find all groups with less or equals number of students\n" +
                                        "2. Find all students related to course with given name\n" +
                                        "3. Add new student\n" +
                                        "4. Delete student by STUDENT_ID\n" +
                                        "5. Add a student to the course (from a list)\n" +
                                        "6. Remove the student from one of his or her courses\n" +
                                        "7. Exit\n";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try (Connection connection = DBConnector.getConnection()) {
            DBInterface.configureDB(connection, getFile(Config.getArrayProperty("SQL_TABLE_FILES")),
                    getFile(Config.getArrayProperty("TEST_DATA_FILES")));
            while (true) {
                System.out.println(MENU);
                Commander.executeCommand(connection);
            }
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_ERR);
        }

    }
}
