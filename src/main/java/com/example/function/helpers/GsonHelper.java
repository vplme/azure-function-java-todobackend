package com.example.function.helpers;

import com.example.function.entities.Todo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;

public class GsonHelper {

  public static final String API_URL_ENV_VAR = "TODOS_API_URL";
  private final Gson gson;
  public GsonHelper() {

    gson = new GsonBuilder()
        .registerTypeAdapter(Todo.class, new TodoSerializer(System.getenv(API_URL_ENV_VAR)))
        .create();
  }

  public Todo convertToTodo(String b) {
    return gson.fromJson(b, Todo.class);
  }

  public String convertToString(Todo todo) {
    return gson.toJson(todo);
  }

  public String convertToString(List<Todo> todos) {
    return gson.toJson(todos);
  }
}
