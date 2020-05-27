package org.schooldb.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileFinderUtil {

    private FileFinderUtil() {}

    private static final String NO_SUCH_FILE_ERR_MESSAGE = "File %s not found!";
    private static final Logger LOGGER = Logger.getLogger(FileFinderUtil.class.getName());

    public static File getFile(String fileName) throws FileNotFoundException {
        try {
            return new File(ClassLoader.getSystemResource(fileName).toURI());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, String.format(NO_SUCH_FILE_ERR_MESSAGE, fileName));
        }
        throw new FileNotFoundException(NO_SUCH_FILE_ERR_MESSAGE);
    }
}
