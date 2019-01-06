package com.cps3230.assignment.payment.gateway.enums;

/**
 * A enumeration representing the transaction states.
 */
public enum TransactionStates {
  AUTHORIZED,
  CAPTURED,
  VOID,
  REFUNDED,
  BANK_DETAILS_INVALID
}
