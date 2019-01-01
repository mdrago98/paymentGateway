package com.cps3230.assignment.payment.gateway.interfaces;

import com.cps3230.assignment.payment.gateway.Transaction;
import java.util.Map;

public interface DatabaseConnection {

  /**
   * A method that saves the transaction to the database.
   *
   * @param transaction an object representation of the transaction.
   * @return true IFF the transaction was saved successfully
   */
  boolean saveTransaction(Transaction transaction);

  /**
   * A method that returns a Map representation of the Transaction Database.
   *
   * @return a Map of transactions
   */
  Map<Long, Transaction> getDatabase();
}
