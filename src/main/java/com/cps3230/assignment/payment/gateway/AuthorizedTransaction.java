package com.cps3230.assignment.payment.gateway;

public class AuthorizedTransaction {

  boolean authPassed;
  Transaction transaction;
  long reason;

  AuthorizedTransaction(Boolean authPassed, Transaction transaction, Long reason){
    if (authPassed && transaction != null) {
      this.authPassed = authPassed;
      this.transaction = transaction;
    } else if (!authPassed && reason != null) {
      this.authPassed = authPassed;
      this.reason = reason;
    }
  }
}
