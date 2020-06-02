package org.schooldb.commands;

import org.schooldb.dao.DBManager;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class CommandsManager {

    private static final String INPUT_ERR = "Incorrect input, please use only requested sequences\n";

    private CommandsManager() {}

    public static void executeCommand(Connection connection) {
        try {
            Stream.of(new AbstractMap.SimpleEntry<Integer, Runnable>(1, () -> DBManager.findGroupsByStudentsNumber(connection)),
                    new AbstractMap.SimpleEntry<Integer, Runnable>(2, () -> DBManager.findStudentsByCourse(connection)),
                    new AbstractMap.SimpleEntry<Integer, Runnable>(3, () -> DBManager.addNewStudent(connection)),
                    new AbstractMap.SimpleEntry<Integer, Runnable>(4, () -> DBManager.removeStudent(connection)),
                    new AbstractMap.SimpleEntry<Integer, Runnable>(5, () -> DBManager.addStudentToCourse(connection)),
                    new AbstractMap.SimpleEntry<Integer, Runnable>(6, () -> DBManager.removeStudentFromCourse(connection)),
                    new AbstractMap.SimpleEntry<Integer, Runnable>(7, () -> DBManager.exitDB(connection)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).get(new Scanner(System.in).nextInt()).run();
        } catch (InputMismatchException | NullPointerException e) {
            System.err.println(INPUT_ERR);
        }
    }
}
