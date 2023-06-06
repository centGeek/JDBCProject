package project.todoList;

import lombok.Value;
@Value
public class ToDoList {
     String task;
     String description;
     String deadline;
     int priority;
     boolean completed;public static String query = "SELECT * FROM TODO";
}
