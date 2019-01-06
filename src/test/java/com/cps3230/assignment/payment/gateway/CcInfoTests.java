package com.cps3230.assignment.payment.gateway;

import com.cps3230.assignment.payment.gateway.helpers.StringUtils;
import com.cps3230.assignment.payment.gateway.stubs.DateIn2018Stub;
import com.cps3230.assignment.payment.gateway.stubs.DateIn2021Stub;
import java.text.ParseException;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class CcInfoTests {

  private CcInfo testCreditCard = null;

  private static Stream<Arguments> generateCreditCardInfoWithEmptyFields() {
    return Stream.of(
        Arguments
            .of(new CcInfo("  ", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
                "111")), Arguments
            .of(new CcInfo("Test Name", "", "AMERICAN_EXPRESS", "371449635398431", "10/20", "111")),
        Arguments
            .of(new CcInfo("Test Name", "Test Address", " ", "", "10/20", "111")),
        Arguments.of(new CcInfo("", "", "VISA", "", "10/20", "")),
        Arguments.of(new CcInfo("Test Name", "Test Address", "VISA", "", "", "")),
        Arguments.of(new CcInfo("Test Name", "", "VISA", "371449635398431", "", "")),
        Arguments.of(new CcInfo("Test Name", "", "VISA", "371449635398431", "", "")),
        Arguments.of(new CcInfo("Test Name", "", "", "", "", "")),
        Arguments
            .of(new CcInfo("Test Name", "Test address", "AMERICAN_EXPRESS", "371449635398431", "",
                "111")),
        Arguments
            .of(new CcInfo("Test Name", "Test Address", "AMERICAN_EXPRESS", "371449635398431",
                "10/20", "")),
        Arguments
            .of(new CcInfo("Test Name", "Test Address", "", "371449635398431", "10/20", "111")));
  }

  @BeforeEach
  void setup() {
    testCreditCard = new CcInfo("John Borg", "56, Triq il- Bibla, Attard", "AMERICAN_EXPRESS",
        "371449635398431",
        "10/20", "111");
  }

  @AfterEach
  void teardown() {
    testCreditCard = null;
  }

  @Test
  void verifyCardTypeIsNotStoredWithWhiteSpace() {
    testCreditCard.setCardType(" American_Express ");
    Assertions.assertFalse(!testCreditCard.getCardType().matches("\\S+"),
        "Card Type should not be stored with whitespace");
  }

  @Test
  void verifyCardTypeIsSavedInUpperCase() {
    testCreditCard.setCardType(" American_Express ");
    boolean isUpperCase = StringUtils.isStringUpperCase(testCreditCard.getCardType());
    Assertions.assertTrue(isUpperCase, "Card type should be upper case.");
  }

  @Test
  void verifyCardNumberIsNotStoredWithWhiteSpace() {
    testCreditCard.setCardNumber(" 371 449 635 398 431 ");
    Assertions.assertFalse(!testCreditCard.getCardNumber().matches("\\S+"),
        "Card NUmber should not be stored with whitespace");
  }

  @Test
  void verifyCardCVVIsNotStoredWithWhitSpace() {
    testCreditCard.setCardCvv("123");
    Assertions.assertFalse(!testCreditCard.getCardCvv().matches("\\S+"),
        "Card CVV should not be stored with whitespace");
  }

  @Test
  void verifyCardCvvLongerThanThreeDigitsAreNotStored() {
    boolean valid = testCreditCard.setCardCvv("2222");
    Assertions.assertFalse(valid);
    Assertions.assertEquals("111", testCreditCard.getCardCvv());
  }

  @Test
  void verifyPrefixLengthForAmericanExpressShouldBeThree() {
    Assertions.assertEquals(2, testCreditCard.getCardNumberPrefix().length(),
        "American Express prefix length should be 2");
  }

  @Test
  void verifyPrefixLengthForMasterCardExpressShouldBeThree() {
    testCreditCard.setCardNumber("5368319888467585");
    testCreditCard.setCardType("MASTER_CARD");
    Assertions.assertEquals(2, testCreditCard.getCardNumberPrefix().length(),
        "American Express prefix length should be 2");
  }

  @Test
  void verifyPrefixLengthForVisaShouldBeTwo() {
    testCreditCard.setCardNumber("4539737109859735");
    testCreditCard.setCardType("VISA");
    Assertions.assertEquals(1, testCreditCard.getCardNumberPrefix().length(),
        "Visa prefix length should be 3");
  }

  @Test
  void verifyPrefixForCardNumberWhenCardIsAmericanExpress() {
    Assertions.assertTrue(testCreditCard.validatePrefix());
  }

  @Test
  void verifyPrefixForCardNumberWhenCardIsVisa() {
    testCreditCard.setCardNumber("4539737109859735");
    testCreditCard.setCardType("VISA");
    Assertions.assertTrue(testCreditCard.validatePrefix());
  }

  @Test
  void verifyPrefixForCardNumberWhenCardIsMastercard() {
    testCreditCard.setCardNumber("5368319888467585");
    testCreditCard.setCardType("MASTER_CARD");
    Assertions.assertTrue(testCreditCard.validatePrefix());
  }

  @Test
  void verifyPrefixForCardNumberWhenVisaCardHasInvalidPrefix() {
    testCreditCard.setCardNumber("0539737109859735");
    testCreditCard.setCardType("VISA");
    Assertions.assertFalse(testCreditCard.validatePrefix());
  }

  @Test
  void verifyPrefixForCardNumberWhenCardNumberIsEmpty() {
    testCreditCard.setCardNumber("");
    Assertions.assertFalse(testCreditCard.validatePrefix());
  }

  @Test
  void checkDateSetterCardExpiredWithValidDateInTheFuture() throws ParseException {
    DateIn2018Stub dateStub = new DateIn2018Stub();
    Assertions
        .assertEquals(0, testCreditCard.validateCardIsNotExpired(dateStub.getTime()).ordinal());
  }

  @Test
  void checkDateSetterWithInvalidDate() {
    DateIn2018Stub dateStub = new DateIn2018Stub();
    Assertions.assertFalse(testCreditCard.setCardExpiryDate("2019/12"));
  }

  @Test
  void checkDateSetterCardExpiredWithValidDateInThePast() throws ParseException {
    DateIn2021Stub dateStub = new DateIn2021Stub();
    Assertions
        .assertEquals(5, testCreditCard.validateCardIsNotExpired(dateStub.getTime()).ordinal());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "hh:12", "1234:45"})
  void confirmTheDateSetterDoesNtAllowInvalidDates(String date) {
    DateIn2021Stub dateStub = new DateIn2021Stub();
    Assertions.assertFalse(testCreditCard.setCardExpiryDate(date));
    Assertions.assertEquals("10/20", testCreditCard.getCardExpiryDate(),
        "Invalid dates should not be set");
  }

  @ParameterizedTest
  @MethodSource("generateCreditCardInfoWithEmptyFields")
  void checkOfflineValidationWhenObjectHasEmptyFields(CcInfo creditCardDetails) {
    DateIn2018Stub date = new DateIn2018Stub();
    Assertions.assertFalse(creditCardDetails.verifyAllDetailsAreEntered());
  }

}
