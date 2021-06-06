package com.example.function.triggers;

import com.example.function.TodoTableStorageClient;
import com.example.function.TodoTableStorageClient.TodoTableException;
import com.example.function.entities.Todo;
import com.example.function.helpers.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.StorageException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger for updating a specific To-do
 */
@SuppressWarnings("unused") // Directly called by Azure Functions
public class PatchTodo {

  private Logger logger;

  /**
   * PATCH /api/todos/{id}
   */
  @FunctionName("PatchTodo")
  public HttpResponseMessage run(
      // HTTP Method is actually PATCH but Java library does not have this in the HttpMethod enum.
      @HttpTrigger(route = "todos/{id}", name = "req", methods = {
          HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context, @BindingName("id") String id)
      throws URISyntaxException, InvalidKeyException, StorageException, TodoTableException {
    context.getLogger().info("Java HTTP trigger processed a request.");

    this.logger = context.getLogger();
    TodoTableStorageClient client = new TodoTableStorageClient(context);
    Optional<Todo> fetchedTodo = client.getById(id);
    if (fetchedTodo.isEmpty()) {
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("No such todo").build();
    }

    Todo oldTodo = fetchedTodo.get();

    Optional<String> body = request.getBody();
    if (body.isEmpty()) {
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Body is empty").build();
    }

    updateWithNewValues(oldTodo, body.get());

    client.replace(oldTodo);
    GsonHelper gsonHelper = new GsonHelper();
    String json = gsonHelper.convertToString(oldTodo);
    return request.createResponseBuilder(HttpStatus.OK).body(json).build();
  }

  private void updateWithNewValues(Todo oldTodo, String json) {
    JsonObject object = new Gson().fromJson(json, JsonObject.class);
    if (object.has("order")) {
      int order = object.get("order").getAsInt();
      logger.fine(() -> String.format("Order: %s -> %s", oldTodo.getOrder(), order));
      oldTodo.setOrder(order);
    }
    if (object.has("title")) {
      String title = object.get("title").getAsString();
      logger.fine(() -> String.format("title: %s -> %s", oldTodo.getTitle(), title));
      oldTodo.setTitle(title);
    }
    if (object.has("completed")) {
      boolean completed = object.get("completed").getAsBoolean();
      logger.fine(() -> String.format("completed: %s -> %s", oldTodo.getCompleted(), completed));
      oldTodo.setCompleted(completed);
    }
  }
}
