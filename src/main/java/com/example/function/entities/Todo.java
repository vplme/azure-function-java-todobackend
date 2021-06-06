package com.example.function.entities;

import com.microsoft.azure.storage.table.TableServiceEntity;
import java.util.UUID;

public class Todo extends TableServiceEntity {

  public static final String PARTITION_KEY = "todos";

  private String title;

  private boolean completed;

  private int order;

  public Todo(String title) {
    this.title = title;

    this.partitionKey = PARTITION_KEY;
    this.rowKey = UUID.randomUUID().toString();
  }

  public Todo() {
    this(null);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  // Azure Table Service expects getters only to start with 'get' and not 'is'.
  public boolean getCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }
}
