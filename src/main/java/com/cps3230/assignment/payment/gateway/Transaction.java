package com.cps3230.assignment.payment.gateway;

import com.cps3230.assignment.payment.gateway.enums.TransactionStates;

public class Transaction {

  private long id;
  private CcInfo ccInfo;
  private long amount;
  private String state;

  public Transaction(long id, CcInfo ccInfo, long amount, TransactionStates state) {
    setId(id);
    setCcInfo(ccInfo);
    setAmount(amount);
    setState(state.toString());
  }

  public Transaction(long id, CcInfo ccInfo, long amount, String state) {
    setId(id);
    setCcInfo(ccInfo);
    setAmount(amount);
    setState(state);
  }

  /**
   * Gets the id of the transaction.
   *
   * @return the transaction id
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the transaction id.
   *
   * @param id a long representation the transaction id
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the Credit card info.
   *
   * @return an object representing the Credit card info
   */
  public CcInfo getCcInfo() {
    return ccInfo;
  }

  /**
   * Sets the Credit card info.
   *
   * @param ccInfo An object representation the credit card info
   */
  public void setCcInfo(CcInfo ccInfo) {
    this.ccInfo = ccInfo;
  }

  /**
   * Gets the transaction amount.
   *
   * @return a long integer representation of the amount
   */
  public long getAmount() {
    return amount;
  }

  /**
   * Sets the transaction amount.
   *
   * @param amount a long representation of the amount
   */
  public void setAmount(long amount) {
    this.amount = amount;
  }

  /**
   * Gets the state of the transaction.
   *
   * @return a string representation of the  transaction state.
   */
  public String getState() {
    return state;
  }

  /**
   * Sets the state of the transaction.
   *
   * @param state a string representation of the transaction state
   */
  public void setState(String state) {
    this.state = state;
  }
}
