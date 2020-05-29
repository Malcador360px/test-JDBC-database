package org.schooldb.dao;

import org.schooldb.util.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;


public final class DBConnector {

    private DBConnector() {}

    private static final String CONNECTION_SUCCESS = "Connection established\n";
    private static final String DRIVER_ERR = "JDBC Driver have not been found\n";
    private static final Logger LOGGER = Logger.getLogger(DBConnector.class.getName());

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(Config.getProperty("DB_DRIVER"));
            Connection connection =  DriverManager.getConnection(Config.getProperty("DB_URL"),
                    Config.getProperty("DB_USERNAME"), Config.getProperty("DB_PASSWORD"));
            System.out.println(CONNECTION_SUCCESS);
            return connection;
        } catch (ClassNotFoundException e) {
            LOGGER.warning(DRIVER_ERR);
        }
        return null;
    }
}
