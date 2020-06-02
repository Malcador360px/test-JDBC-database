package org.schooldb.dao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.powermock.reflect.Whitebox;
import org.schooldb.util.Config;
import org.schooldb.util.FileUtil;
import java.sql.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static org.junit.Assert.*;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;


public class DBManagerTest {

    private static final String ALL_NAME_FORMATS_WILDCARD = "%";
    private static final int TABLE_NAME_POS = 3;
    private static final String GROUP_NAME_PATTERN = "\\s[A-Z]{2}-[0-9]{2}\\s";
    private static final String MAX_STUDENTS_GROUP = "30";
    private static final String[] EXPECTED_TABLE_NAMES = {"STUDENTS", "GROUPS", "COURSES", "STUDENTS_COURSES"};
    private static final String CONNECTION_NOT_CLOSED = "Unable to close connection";
    private static final String INSERT_ERR = "Unable to insert data into database";
    private static final String TEST_FIRST_NAME = "Mike";
    private static final String TEST_LAST_NAME = "Vasovski";
    private static final int TEST_STUDENT_ID = 201;
    private static final int TEST_COURSE_ID = 1;
    private static final String TEST_COURSE_NAME = "Math";
    private static final String INSERT_TEST_STUDENT = "INSERT INTO students(first_name, last_name) VALUES(?, ?)";
    private static final String ASSIGN_TEST_COURSE = "INSERT INTO students_courses(course_id, student_id) VALUES(?, ?)";
    private static final String RETRIEVE_STUDENT_ID = "SELECT student_id FROM students WHERE first_name = ? AND last_name = ?";
    private static final String ADDED_STUDENT_FIRST_NAME = "Horus";
    private static final String ADDED_STUDENT_LAST_NAME = "Lupercal";
    private static final String ADDED_ID_PATTERN = "\\sID\\s:\\s[0-9]+";
    private static final String REMOVED_STUDENT_ID = "1";
    private static final Logger LOGGER = Logger.getLogger(DBManagerTest.class.getName());

    @Rule
    public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    @Test
    public void configureDB() {
        try (Connection connection = establishConnection()) {

            DBManager.configureDB(connection,
                    FileUtil.getFile(Config.getArrayProperty("SQL_TABLE_FILES"), Config.getProperty("DB_FOLDER")),
                    FileUtil.getFile(Config.getArrayProperty("TEST_DATA_FILES"), Config.getProperty("TEST_DATA_FOLDER")));

            for (String tableName : EXPECTED_TABLE_NAMES) {
                if (!getTableNames(connection).contains(tableName)) {
                    fail("Required tables were not created!");
                }
            }
            createTestSubject(connection);
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        }
    }

    @Test
    public void findGroupsByStudentsNumber() {
        systemInMock.provideLines(MAX_STUDENTS_GROUP);
        systemOutRule.enableLog();
        try (Connection connection = establishConnection()) {

            DBManager.findGroupsByStudentsNumber(connection);

            assertTrue("Groups returned incorrectly",
                    Pattern.compile(GROUP_NAME_PATTERN).matcher(systemOutRule.getLog()).find());
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        } finally {
            systemOutRule.clearLog();
        }
    }

    @Test
    public void findStudentsByCourse() {
        systemInMock.provideLines(TEST_COURSE_NAME);
        systemOutRule.enableLog();
        try (Connection connection = establishConnection()) {

            DBManager.findStudentsByCourse(connection);

            assertTrue("Student assigned to course has not been found",
                    systemOutRule.getLog().contains(TEST_FIRST_NAME) &&
                    systemOutRule.getLog().contains(TEST_LAST_NAME));
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        } finally {
            systemOutRule.clearLog();
        }
    }

    @Test
    public void addNewStudent() {
        systemInMock.provideLines(ADDED_STUDENT_FIRST_NAME, ADDED_STUDENT_LAST_NAME);
        systemOutRule.enableLog();
        try (Connection connection = establishConnection()) {

            DBManager.addNewStudent(connection);

            assertTrue("Incorrect reply, or student wasn't added to database",
                    Pattern.compile(ADDED_ID_PATTERN).matcher(systemOutRule.getLog()).find());
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        } finally {
            systemOutRule.clearLog();
        }
    }

    @Test
    public void removeStudent() {
        systemInMock.provideLines(REMOVED_STUDENT_ID, REMOVED_STUDENT_ID);
        systemOutRule.enableLog();
        try (Connection connection = establishConnection()) {

            DBManager.removeStudent(connection);
            DBManager.removeStudent(connection);

            assertTrue("Incorrect reply, or student wasn't removed",
                    systemOutRule.getLog()
                    .contains(Whitebox.getInternalState(DBManager.class, "RECORD_NOT_FOUND").toString()));
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        } finally {
            systemOutRule.clearLog();
        }
    }

    @Test
    public void addStudentToCourse() {
        systemOutRule.enableLog();
        try (Connection connection = establishConnection()) {
            createTestStudent(connection, "Tummi", "Gummi");
            String studentId =  getCreatedStudentId(connection, "Tummi", "Gummi");
            systemInMock.provideLines(studentId, TEST_COURSE_NAME, studentId, TEST_COURSE_NAME);

            DBManager.addStudentToCourse(connection);
            DBManager.addStudentToCourse(connection);

            assertTrue("Incorrect reply, or student wasn't added to course",
                    systemOutRule.getLog()
                    .contains(Whitebox.getInternalState(DBManager.class, "STUDENT_ALREADY_ASSIGN").toString()));
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        } finally {
            systemOutRule.clearLog();
        }
    }

    @Test
    public void removeStudentFromCourse() {
        systemOutRule.enableLog();
        try (Connection connection = establishConnection()) {
            createTestStudent(connection, "Zummi", "Gummi");
            String studentId = getCreatedStudentId(connection, "Zummi", "Gummi");
            assignStudentToTestCourse(connection, Integer.parseInt(studentId));
            systemInMock.provideLines(studentId, TEST_COURSE_NAME, studentId, TEST_COURSE_NAME);

            DBManager.removeStudentFromCourse(connection);
            DBManager.removeStudentFromCourse(connection);

            assertTrue("Incorrect reply, or student wasn't removed from course",
                    systemOutRule.getLog()
                    .contains(Whitebox.getInternalState(DBManager.class, "STUDENT_WRONG_COURSE").toString()));
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        } finally {
            systemOutRule.clearLog();
        }
    }

    @Test
    public void exitDB() {
        try (Connection connection = establishConnection()) {
            exit.expectSystemExitWithStatus(0);

            DBManager.exitDB(connection);

            assertNull("Connection should be closed", connection);
        } catch (SQLException e) {
            LOGGER.warning(CONNECTION_NOT_CLOSED);
        } finally {
            systemOutRule.clearLog();
        }
    }

    Connection establishConnection() {
        try {
            Class.forName(Config.getProperty("DB_DRIVER"));
            return DriverManager.getConnection(Config.getProperty("DB_URL"));
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Driver for h2 database not found");
        } catch (SQLException e) {
            LOGGER.warning("Unable to establish connection");
        }
        return null;
    }

    String getTableNames(Connection connection) {
        StringBuilder output = new StringBuilder();
        try {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, ALL_NAME_FORMATS_WILDCARD, null);
            while (resultSet.next()) {
                output.append(resultSet.getString(TABLE_NAME_POS));
            }
            return output.toString();
        } catch (SQLException e) {
            LOGGER.warning("Unable to get metadata from database");
        }
        return null;
    }

    void createTestSubject(Connection connection) {
        createTestStudent(connection, TEST_FIRST_NAME, TEST_LAST_NAME);
        assignStudentToTestCourse(connection, TEST_STUDENT_ID);
    }

    void assignStudentToTestCourse(Connection connection, int studentId) {
        try (PreparedStatement statement = connection.prepareStatement(ASSIGN_TEST_COURSE)) {
            statement.setInt(1, TEST_COURSE_ID);
            statement.setInt(2, studentId);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.warning(INSERT_ERR);
        }
    }

    void createTestStudent(Connection connection, String firstName, String lastName) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_TEST_STUDENT)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.warning(INSERT_ERR);
        }
    }

    String getCreatedStudentId(Connection connection, String firstName, String lastName) {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_STUDENT_ID)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            LOGGER.warning("Cannot retrieve data from database");
        }
        return null;
    }
}