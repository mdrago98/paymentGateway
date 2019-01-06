package com.cps3230.assignment.payment.webapp;

public class EntryObject {

  String customerName;
  String customerAddress;
  String cardType;
  String cardNumber;
  String cardExpiryDate;
  String cardCvv;
  String amount;

  public EntryObject(String customerName, String customerAddress, String cardType,
      String cardNumber, String cardExpiryDate, String cardCvv, String amount) {
    this.customerName = customerName;
    this.customerAddress = customerAddress;
    this.cardType = cardType;
    this.cardNumber = cardNumber;
    this.cardExpiryDate = cardExpiryDate;
    this.cardCvv = cardCvv;
    this.amount = amount;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCardExpiryDate() {
    return cardExpiryDate;
  }

  public void setCardExpiryDate(String cardExpiryDate) {
    this.cardExpiryDate = cardExpiryDate;
  }

  public String getCardCvv() {
    return cardCvv;
  }

  public void setCardCvv(String cardCvv) {
    this.cardCvv = cardCvv;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }
}
