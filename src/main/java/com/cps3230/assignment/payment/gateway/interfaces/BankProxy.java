package com.cps3230.assignment.payment.gateway.interfaces;

import com.cps3230.assignment.payment.gateway.CcInfo;

public interface BankProxy {

  /**
   * A method that processes an auth request.
   *
   * @param creditCardDetails an object representation of the card details
   * @param amount a long representation of the amount to charge
   * @return a long representation of th transaction id
   */
  long auth(CcInfo creditCardDetails, long amount);

  /**
   * A method that captures funds.
   *
   * @param transactionId a long representation of th transaction id
   * @return an int representation of the capture status code; 0 is successful; -1 if the
   *         transaction does not exist; -2 transaction exists but already captured; -3 exists but
   *         capture period expired; -4 unknown error
   */
  int capture(long transactionId);

  /**
   * A method that requests a refund for a transaction.
   *
   * @param transactionId a long representation of the transaction id
   * @param amount a long representation of the amount to refund
   * @return an int representation of the refund status
   */
  int refund(long transactionId, long amount);
}
