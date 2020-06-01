package org.schooldb.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {

    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String ARRAY_DELIMITER = ", ";
    private static final String PROPERTIES_ERR = "Properties file cannot be read!";
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    private Config() {}

    public static String[] getArrayProperty(String key) {
        return getProperty(key).split(ARRAY_DELIMITER);
    }

    public static String getProperty(String key) {
        Properties properties = new Properties();
        try {
            FileInputStream in = new FileInputStream(FileUtil.getFile(CONFIG_FILE_NAME));
            properties.load(in);
            return properties.getProperty(key);
        } catch (IOException e) {
            LOGGER.warning(PROPERTIES_ERR);
        }
        return "";
    }
}
