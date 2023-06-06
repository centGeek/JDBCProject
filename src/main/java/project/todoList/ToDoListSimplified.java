package project.todoList;

import lombok.Value;

import java.time.LocalDate;
@Value
public class ToDoListSimplified {

     String task;
     String description;
     LocalDate deadline;
}
