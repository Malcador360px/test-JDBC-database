package org.schooldb.commands;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.powermock.reflect.Whitebox;
import org.schooldb.util.Config;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.Assert.*;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;


public class CommandsManagerTest {

    @Rule
    public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule();

    @Test
    public void testIncorrectInput() throws ClassNotFoundException, SQLException {
        systemInMock.provideLines("Incorrect input");
        systemOutRule.enableLog();

        Class.forName(Config.getProperty("DB_DRIVER"));
        CommandsManager.executeCommand(DriverManager.getConnection(Config.getProperty("DB_URL")));

        assertTrue("Incorrect input should be dealt properly",
                systemOutRule.getLog()
                .contains(Whitebox.getInternalState(CommandsManager.class, "INPUT_ERR").toString()));
        systemOutRule.clearLog();
    }
}