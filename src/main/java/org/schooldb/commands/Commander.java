package org.schooldb.commands;

import org.schooldb.dao.DBInterface;

import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public final class Commander {

    private static final String INPUT_ERR = "Incorrect input, please use only requested sequences";

    private Commander() {}

    public static void executeCommand(Connection connection) {
        Integer[] commandIds = {1, 2, 3, 4, 5, 6, 7};
        Runnable[] commands = {() -> DBInterface.findGroupsByStudentsNumber(connection),
                () -> DBInterface.findStudentsByCourse(connection), () -> DBInterface.addNewStudent(connection),
                () -> DBInterface.removeStudent(connection), () -> DBInterface.addStudentToCourse(connection),
                () -> DBInterface.removeStudentFromCourse(connection), () -> System.exit(0)};

        try {
            IntStream.range(0, commandIds.length).boxed()
                    .collect(Collectors.toMap(i -> commandIds[i], i -> commands[i]))
                    .get(new Scanner(System.in).nextInt()).run();
        } catch (InputMismatchException | NullPointerException e) {
            System.err.println(INPUT_ERR);
        }
    }
}
