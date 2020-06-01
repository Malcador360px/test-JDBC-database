package org.schooldb.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    private static final String[] NON_ARRAY_PROPERTIES = {
            "jdbc:postgresql://localhost:5432/school", "school", "1", "org.postgresql.Driver"};
    private static final String[][] ARRAY_PROPERTIES = {
            {"groups.sql", "students.sql", "courses.sql", "students_courses.sql"},
            {"courses.txt", "courses_desc.txt", "first_names.txt", "last_names.txt"}};

    @Test
    void getArrayProperty() {
        String[][] actual = {Config.getArrayProperty("SQL_TABLE_FILES"),
                Config.getArrayProperty("TEST_DATA_FILES")};
        assertArrayEquals(ARRAY_PROPERTIES, actual);
    }

    @Test
    void getProperty() {
        String[] actual = {Config.getProperty("DB_URL"), Config.getProperty("DB_USERNAME"),
        Config.getProperty("DB_PASSWORD"), Config.getProperty("DB_DRIVER")};
        assertArrayEquals(NON_ARRAY_PROPERTIES, actual);
    }
}