package com.cps3230.assignment.payment.gateway;

import com.cps3230.assignment.payment.gateway.constants.CardLengths;
import com.cps3230.assignment.payment.gateway.constants.CardPrefixes;
import com.cps3230.assignment.payment.gateway.enums.CardBrands;
import com.cps3230.assignment.payment.gateway.enums.CardValidationStatuses;
import com.cps3230.assignment.payment.gateway.utils.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CcInfo {

  private String customerName;
  private String customerAddress;
  private String cardType;
  private String cardNumber;
  private String cardExpiryDate;
  private String cardCvv;

  /**
   * Initializes CcInfo.
   *
   * @param customerName the customer name
   * @param customerAddress the customer address
   * @param cardType the card type
   * @param cardNumber the card number
   * @param cardExpiryDate the card expiry
   * @param cardCvv the card cvv
   */
  public CcInfo(String customerName, String customerAddress, String cardType, String cardNumber,
      String cardExpiryDate, String cardCvv) {
    setCustomerName(customerName);
    setCustomerAddress(customerAddress);
    setCardType(cardType);
    setCardNumber(cardNumber);
    setCardExpiryDate(cardExpiryDate);
    setCardCvv(cardCvv);
  }

  /**
   * Gets Customer Name.
   *
   * @return customer name
   */
  public String getCustomerName() {
    return customerName;
  }

  /**
   * Sets the customer name.
   *
   * @param customerName the customer name
   */
  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  /**
   * Gets the customer address.
   *
   * @return customer address
   */
  public String getCustomerAddress() {
    return customerAddress;
  }

  /**
   * Sets the customer address.
   *
   * @param customerAddress the customer address
   */
  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  /**
   * gets the card Type.
   *
   * @return the card type
   */
  public String getCardType() {
    return cardType;
  }

  /**
   * Sets the card type.
   *
   * @param cardType the card type
   */
  public void setCardType(String cardType) {
    this.cardType = cardType.toUpperCase().replaceAll("\\s+", "");
  }

  /**
   * A helper method that gets the ENUM representation Card brand from the card details object.
   *
   * @return enum representation of the card brand
   */
  public CardBrands getCardTypeEnum() {
    CardBrands brand;
    try {
      brand = CardBrands.valueOf(getCardType().toUpperCase());
    } catch (IllegalArgumentException argException) {
      brand = CardBrands.INVALID;
    }
    return brand;
  }

  /**
   * Gets the card number.
   *
   * @return the card number
   */
  public String getCardNumber() {
    return cardNumber;
  }

  /**
   * Sets the card Number.
   *
   * @param cardNumber the card number
   */
  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber.replaceAll("\\s+", "");
  }

  public String getCardNumberPrefix() {
    String prefix = "";
    if (getCardTypeEnum() == CardBrands.VISA) {
      prefix = getCardNumber().substring(0, 1);
    } else {
      prefix = getCardNumber().substring(0, 2);
    }
    return prefix;
  }

  /**
   * Gets the card expiry date.
   *
   * @return the card expiry date
   */
  public String getCardExpiryDate() {
    return cardExpiryDate;
  }

  /**
   * Sets the card expiry.
   *
   * @param cardExpiryDate the card expiry
   * @return true IFF card expiry is valid
   */
  public boolean setCardExpiryDate(String cardExpiryDate) {
    boolean valid = true;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/YY");
    simpleDateFormat.setLenient(false);
    try {
      simpleDateFormat.parse(cardExpiryDate);
      this.cardExpiryDate = cardExpiryDate.replaceAll("\\s+", "");
    } catch (ParseException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * gets the card cvv.
   *
   * @return the card cvv
   */
  public String getCardCvv() {
    return cardCvv;
  }

  /**
   * Sets the card cvv.
   *
   * @param cardCvv the card cvv
   */
  public boolean setCardCvv(String cardCvv) {
    boolean cvvValid = cardCvv.length() == 3;
    if (cvvValid) {
      this.cardCvv = cardCvv.replaceAll("\\s+", "");
    }
    return cvvValid;
  }

  /**
   * A method that verifies that all data is entered.
   *
   * @return true IFF no fields are empty
   */
  protected boolean verifyAllDetailsAreEntered() {
    return (!StringUtils.isNullOrEmpty(getCustomerName()) && !StringUtils
        .isNullOrEmpty(getCustomerAddress()) && !StringUtils.isNullOrEmpty(getCardCvv())
        && !StringUtils.isNullOrEmpty(getCardNumber()) && !StringUtils
        .isNullOrEmpty(getCardExpiryDate()) && !StringUtils.isNullOrEmpty(getCardType()));
  }

  protected CardValidationStatuses validateCardIsNotExpired(Date date) throws ParseException {
    CardValidationStatuses validity = CardValidationStatuses.CARD_EXPIRED;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/YY");
    simpleDateFormat.setLenient(false);
    Date expiry = simpleDateFormat.parse(getCardExpiryDate());
    if (expiry.after(date)) {
      validity = CardValidationStatuses.VALID;
    }
    return validity;
  }

  /**
   * A helper method that checks the prefixes and the card length match the card brand.
   *
   * @return true IFF the prefix matches the card brand prefix
   */
  protected boolean validatePrefix() {
    boolean validity = false;
    if (CardLengths.getCardLengths().get(getCardTypeEnum())
        .contains(getCardNumber().length()) && CardPrefixes.validPrefixes().get(getCardTypeEnum())
        .contains(getCardNumberPrefix())) {
      validity = true;
    }
    return validity;
  }
}
