package com.cps3230.assignment.payment.gateway.spies;

import com.cps3230.assignment.payment.gateway.Transaction;
import com.cps3230.assignment.payment.gateway.interfaces.DatabaseConnection;
import java.util.HashMap;
import java.util.Map;

public class DatabaseSpy implements DatabaseConnection {

  private final Map<Long, Transaction> database;

  private int requestCount;

  public DatabaseSpy() {
    database = new HashMap<>();
    requestCount = 0;
  }

  @Override
  public boolean saveTransaction(Transaction transaction) {
    database.put(transaction.getId(), transaction);
    requestCount++;
    return true;
  }

  @Override
  public Map<Long, Transaction> getDatabase() {
    return database;
  }

  public int getRequestCount() {
    return requestCount;
  }

  public boolean isAccessed() {
    return requestCount > 0;
  }

}
