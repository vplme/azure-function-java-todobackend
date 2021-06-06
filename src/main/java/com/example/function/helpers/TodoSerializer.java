package com.example.function.helpers;

import com.example.function.entities.Todo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 * Specialized JsonSerializer for the To-do class.
 * This is mainly used so we can dynamically generate and add a <i>url</i> property.
 */
public class TodoSerializer implements JsonSerializer<Todo> {

  private final String url;

  public TodoSerializer(String url) {
    this.url = url;
  }

  @Override
  public JsonElement serialize(Todo src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("title", src.getTitle());
    object.addProperty("completed", src.getCompleted());
    object.addProperty("order", src.getOrder());
    object.addProperty("url", url + "/" + src.getRowKey());

    return object;
  }
}
