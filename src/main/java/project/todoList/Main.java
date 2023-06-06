package project.todoList;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static project.todoList.ToDoList.query;
import static project.todoList.ToDoListMapper.*;

public class Main {
    public static void main(String[] args) {
        String URLaddress = "jdbc:postgresql://localhost:5432/zajavka_project";
        String password = "postgres";
        String username = "postgres";
        Scanner scanner = new Scanner(System.in);

        mainMethod(URLaddress, password, username, scanner);

    }

    private static void mainMethod(String URLaddress, String password, String username, Scanner scanner) {
        while (scanner.hasNext()) {
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("break;")) {
                break;
            } else if (command.contains("READ ALL") && command.contains("SORT")) {
                readSortedBySomeValue(URLaddress, password, username, command);
                continue;

            } else if (command.equalsIgnoreCase("READ ALL")) {

                printToDoList(URLaddress, password, username);
                continue;
            } else if (command.contains("READ;NAME=")) {
                List<ToDoList> list = getAllTasksFromToDoList(URLaddress, password, username, query);
                readSelectedTaskByName(command, list);
                continue;
            } else if (command.equalsIgnoreCase("print completed tasks")) {
                printCompletedTasksToMotivateYou(URLaddress, password, username);
                continue;
            }
            else if(command.equalsIgnoreCase("READ GROUPED;")){
                readGroupedByDeadline(URLaddress, password, username, command);
                continue;
            }

            try (Connection connection = DriverManager.getConnection(URLaddress, username, password);
                 Statement statement = connection.createStatement()) {
                if (command.contains("CREATE")) {
                    String commandToAddGivenTaskFormatted = formattedCommandTOAddTasks(command);
                    try {
                        statement.executeUpdate(commandToAddGivenTaskFormatted);
                    } catch (SQLException e) {
                        System.err.printf("SQL STATE %s ERROR CODE: %s GET MESSAGE: %s",
                                e.getSQLState(),
                                e.getErrorCode(),
                                e.getMessage());
                    }
                    continue;
                }
                if (command.contains("COMPLETED;NAME=")) {
                    try {
                        String updateToCompleted = command.replace("COMPLETED;NAME=", "UPDATE TODO SET COMPLETED = TRUE WHERE NAME= '") + "'";
                        statement.executeUpdate(updateToCompleted);
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                } else if (command.contains("DELETE;NAME=")) {
                    String deleteByName = command.replace("DELETE;NAME=", "DELETE FROM TODO WHERE NAME='") + "'";
                    int i = statement.executeUpdate(deleteByName);
                    if (i > 0) {
                        System.out.println("deleting went successfully");
                    }
                } else if (command.contains("UPDATE;")) {
                    String commandFormattedToUpdateTask = commandUpdatingGivenTaskFormatter(command);
                    try {
                        statement.executeUpdate(commandFormattedToUpdateTask);
                    } catch (SQLException e) {
                        System.err.printf("SQL STATE %s ERROR CODE: %s GET MESSAGE: %s",
                                e.getSQLState(),
                                e.getErrorCode(),
                                e.getMessage());
                    }


                } else {
                    try {
                        statement.executeUpdate(command);
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }


    }

    private static void readGroupedByDeadline(String URLaddress, String password, String username, String command) {
        String groupingByDate = command.replace("READ GROUPED;", "SELECT deadline, array_agg(name), array_agg(description)," +
                " array_agg(priority)  FROM TODO GROUP BY deadline;").replace("read grouped;", "SELECT deadline, array_agg(name), array_agg(description)," +
                " array_agg(priority)  FROM TODO GROUP BY deadline;");
        Map<LocalDate, ToDoListSimplified> map = MapperToDoList.getAllTasksFromToDoMap(URLaddress, password, username, groupingByDate);
        MapperToDoList.MapPrinter(map);
    }

    private static void readSortedBySomeValue(String URLaddress, String password, String username, String command) {
        String sortedBy = command
                .replace("READ ALL;SORT=PRIORITY,", "SELECT * FROM TODO ORDER BY PRIORITY ")
                .replace("READ ALL;SORT=NAME,", "SELECT * FROM TODO ORDER BY NAME ")
                .replace("READ ALL;SORT=DESCRIPTION,", "SELECT * FROM TODO ORDER BY DESCRIPTION ")
                .replace("READ ALL;SORT=DEADLINE,", "SELECT * FROM TODO ORDER BY DEADLINE")
                .replace("READ ALL;SORT=COMPLETED,", "SELECT * FROM TODO ORDER BY COMPLETED");
        List<ToDoList> allTasksFromToDoList = getAllTasksFromToDoList(URLaddress, password, username, sortedBy);
        for (ToDoList todolist : allTasksFromToDoList) {
            System.out.println(todolist);
        }
    }


    private static void readSelectedTaskByName(String command, List<ToDoList> list) {
        String enteredTaskName = command.replace("READ;NAME=", "");
        for (ToDoList element : list) {
            String[] split = element.toString().split(",");
            String taskName = split[0].replace("'", "").replace("TODOLIST{task=", "");
            if (taskName.equals(enteredTaskName)) {
                System.out.println(element);
            }
        }
    }

    private static void printToDoList(String URLaddress, String password, String username) {
        List<ToDoList> list = getAllTasksFromToDoList(URLaddress, password, username, query);
        list.forEach(System.out::println);
    }

    private static void printCompletedTasksToMotivateYou(String URLaddress, String password, String username) {
        List<ToDoList> list = getAllTasksFromToDoList(URLaddress, password, username, query);
        for (ToDoList todolist : list) {
            if (todolist.toString().contains("completed=true")) {
                System.out.println(todolist);
            }
        }
    }


}