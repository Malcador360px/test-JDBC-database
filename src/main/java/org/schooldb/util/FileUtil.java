package org.schooldb.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;


public class FileUtil {

    private FileUtil() {}

    private static final String NO_SUCH_FILE_ERR_MESSAGE = "File %s not found!";
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

    public static File[] getFile(String[] fileNames, String path) {
        File[] files = new File[fileNames.length];
        for (int index = 0; index < fileNames.length; index++) {
            try {
                files[index] = getFile(fileNames[index], path);
            } catch (FileNotFoundException ignore) {
            }
        }
        return files;
    }

    public static File[] getFile(String[] fileNames) {
        File[] files = new File[fileNames.length];
        for (int index = 0; index < fileNames.length; index++) {
            try {
                files[index] = getFile(fileNames[index]);
            } catch (FileNotFoundException ignore) {
            }
        }
        return files;
    }

    public static File getFile(String fileName, String path) throws FileNotFoundException {
        try {
            return new File(ClassLoader.getSystemResource(path + fileName).toURI());
        } catch (Exception e) {
            LOGGER.warning(String.format(NO_SUCH_FILE_ERR_MESSAGE, fileName));
        }
        throw new FileNotFoundException(String.format(NO_SUCH_FILE_ERR_MESSAGE, fileName));
    }

    public static File getFile(String fileName) throws FileNotFoundException {
        try {
            return new File(ClassLoader.getSystemResource(fileName).toURI());
        } catch (Exception e) {
            LOGGER.warning(String.format(NO_SUCH_FILE_ERR_MESSAGE, fileName));
        }
        throw new FileNotFoundException(String.format(NO_SUCH_FILE_ERR_MESSAGE, fileName));
    }
}
