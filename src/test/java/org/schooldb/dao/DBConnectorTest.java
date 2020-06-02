package org.schooldb.dao;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DBConnectorTest {

    @Test
    public void getConnection() {
        try {
            Connection connection = DBConnector.getConnection();
            connection.close();
        } catch (SQLException | NullPointerException e) {
            fail("Connection should be opened and closed properly");
            e.printStackTrace();
        }
    }
}