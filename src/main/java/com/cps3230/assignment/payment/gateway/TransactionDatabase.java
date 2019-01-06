package com.cps3230.assignment.payment.gateway;

import com.cps3230.assignment.payment.gateway.interfaces.DatabaseConnection;
import java.util.HashMap;
import java.util.Map;

public class TransactionDatabase implements DatabaseConnection {

  Map<Long, Transaction> database;

  TransactionDatabase() {
    database = new HashMap<>();
  }

  /**
   * A method that saves the transaction to the database.
   *
   * @param transaction an object representation of the transaction.
   * @return true IFF the transaction was saved successfully
   */
  @Override
  public boolean saveTransaction(Transaction transaction) {
    database.put(transaction.getId(), transaction);
    return true;
  }

  /**
   * A method that returns a map instance of the database.
   * @return a map representation of the items in the database.
   */
  @Override
  public Map<Long, Transaction> getDatabase() {
    return database;
  }

}
