package project.todoList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToDoListMapper {
    public static List<ToDoList> mapToToDoList(ResultSet resultSet) {
        List<ToDoList> todolist = new ArrayList<>();
        try {
            while(resultSet.next()){
                todolist.add(new ToDoList(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getBoolean(5)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return todolist;
    }
     static List<ToDoList> getAllTasksFromToDoList(String URLaddress, String password, String username, String query) {
         List<ToDoList> todolist;
        try (Connection connection = DriverManager.getConnection(URLaddress, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            try (
                    ResultSet resultSet = statement.executeQuery()
            ) {
                todolist = ToDoListMapper.mapToToDoList(resultSet);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return todolist;
    }
    public static String formattedCommandTOAddTasks(String command) {
        return command.
                replace("CREATE;", "INSERT INTO TODO(NAME,DESCRIPTION,DEADLINE,PRIORITY) VALUES(")
                .replace("NAME=", "'")
                .replace(";DESCRIPTION=", "', '")
                .replace(";DEADLINE=", "', '")
                .replace(";COMPLETED=", "', '")
                .replace(";PRIORITY=", "', '")+"');";
    }
    public static String commandUpdatingGivenTaskFormatter(String command) {
        String[] split = command.split(";");
        return split[0]
                .replace("UPDATE", "UPDATE TODO")+ split[2]
                .replace("DESCRIPTION="," SET DESCRIPTION = '")+"', "+split[3]
                .replace("DEADLINE=", " DEADLINE= '")+"', "+split[4]
                .replace("PRIORITY=", " PRIORITY= '")+"' "+split[1].replace("NAME=","WHERE NAME = '")+"';";
    }

}
