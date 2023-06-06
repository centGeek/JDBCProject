package project.todoList;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class MapperToDoList {

    public static final String GREEN_BOLD = "\033[1;32m";
    public static Map<LocalDate,ToDoListSimplified> mapToToDoMapp(ResultSet resultSet) {
    Map<LocalDate,ToDoListSimplified> todoMap = new HashMap<>();
    try {
        while(resultSet.next()){
            todoMap.put(resultSet.getDate(1).toLocalDate(),new ToDoListSimplified(
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDate(1).toLocalDate()));
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
        return todoMap;
    }
    static Map<LocalDate,ToDoListSimplified> getAllTasksFromToDoMap(String URLaddress, String password, String username, String query) {
         Map<LocalDate, ToDoListSimplified> todoMap;
        try (Connection connection = DriverManager.getConnection(URLaddress, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            try (
                    ResultSet resultSet = statement.executeQuery()
            ) {
                todoMap = mapToToDoMapp(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return todoMap;
    }
    static <T,V> void MapPrinter(Map<T,V> printing){
        printing
                .entrySet()
                .forEach(System.err::println);
    }
}
