package org.schooldb.util;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.*;


class FileUtilTest {

    private static final String NON_EXISTENT_FILE = "does_not_exist.txt";
    private static final String TEST_FILE = "config.properties";
    private static final String SOME_FILE = "some_file.txt";
    private static final String[] TEST_FILE_NAMES = {SOME_FILE, TEST_FILE};
    private static final String TEST_FILE_IN_FOLDER = "test_file.png";
    private static final String FOLDER = "test_file\\";

    @Test
    void getFile() {
        try {
            assertEquals(new File(ClassLoader.getSystemResource(TEST_FILE).toURI()), FileUtil.getFile(TEST_FILE),
                    "File loaded incorrectly");
        } catch (FileNotFoundException | URISyntaxException e) {
            fail("File should be loaded");
        }
    }

    @Test
    void getArrayOfFiles() {
        try {
            File[] expected = {new File(ClassLoader.getSystemResource(SOME_FILE).toURI()),
                    new File(ClassLoader.getSystemResource(TEST_FILE).toURI())};

            assertArrayEquals(expected, FileUtil.getFile(TEST_FILE_NAMES),
                    "Files returned incorrectly");
        } catch (URISyntaxException e) {
            fail("Method should return array of multiple loaded files");
        }
    }

    @Test
    void getNonExistentFile() {
        try {
            FileUtil.getFile(NON_EXISTENT_FILE);
            fail("File that don't exist can't be loaded");
        } catch (FileNotFoundException ignore) {
        }
    }

    @Test
    void getFileFromFolder() {
        try {
            assertEquals(new File(ClassLoader.getSystemResource(FOLDER + TEST_FILE_IN_FOLDER).toURI()),
                    FileUtil.getFile(TEST_FILE_IN_FOLDER, FOLDER), "File loaded incorrectly from folder");
        } catch (FileNotFoundException | URISyntaxException e) {
            fail("File should be loaded from folder properly");
        }
    }
}