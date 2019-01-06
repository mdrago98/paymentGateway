package com.cps3230.assignment.payment.webapp;

import com.cps3230.assignment.payment.gateway.utils.StringUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

public class PaymentModel {
  private String customerName;
  private String customerAddress;
  private String cardType;
  private String cardNumber;
  private String cardExpiryDate;
  private String cardCvv;
  @Value("0")
  private String amount;
  private String errorMsg;

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public String getCardExpiryDate() {
    return cardExpiryDate;
  }

  public void setCardExpiryDate(String cardExpiryDate) {
    this.cardExpiryDate = cardExpiryDate;
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
    if (!amount.trim().isEmpty()) {
      this.amount = amount;
    } else {
      this.amount = "";
    }
  }

  public List<String> getEmptyFields() throws IllegalAccessException {
    List<String> emptyStrings = new ArrayList<>();
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field f : fields) {
      String value = String.valueOf(f.get(this));
      if ((StringUtils.isNullOrEmpty(value) || value.trim().equalsIgnoreCase("null") || value.trim().equalsIgnoreCase("0")) && !f.getName().equalsIgnoreCase("errorMsg")) {
        emptyStrings.add(f.getName());
      }
    }
    return emptyStrings;
  }

}
