package com.cps3230.assignment.webapp.stepdefs;

import com.cps3230.assignment.webapp.EntryObject;
import com.cps3230.assignment.webapp.pageobjects.PaymentWebappPageObject;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.jupiter.api.Assertions;


public class WebAppDefs {

  private PaymentWebappPageObject webappPageObject = new PaymentWebappPageObject();

  @Given("I am a user trying to process a payment")
  public void iAmAUserTryingToProcessAPayment() {
    webappPageObject.enterPage();
  }

  @When("I submit correct details")
  public void iSubmitCorrectDetails() throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS", "371449635398431", "10/20", "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  @Then("I should be told that the payment was successful")
  public void iShouldBeToldThatThePaymentWasSuccessful() {
    String message = webappPageObject.getMessages();
    Assertions.assertEquals("Payment successful", message, "Success message should appear on valid payment");
  }

  @When("I submit a form with all data except {string}")
  public void iSubmitFormWithAllDataExcept(String fieldName) throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS", "371449635398431", "10/20", "111", "10");
    webappPageObject.enterDetailsWithEmptyFields(entry, fieldName);
    webappPageObject.submitDetails();
  }

  @Then("I should be told that {string} is required")
  public void iShouldBeToldStringIsRequired(String fieldName) {
    String message = webappPageObject.getMessages();
    Assertions.assertEquals("Empty fields " + fieldName, message);
  }

  @When("I submit a form with all data but with an {string}")
  public void iSubmitFormWithAllDataButWithAnInvalidCard(String invalidCard)
      throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS", invalidCard, "10/20", "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  @Then("I should be told that the card is invalid")
  public void iShouldBeToldThatTheCardIsInvalid() {
    Assertions.assertEquals("Card is invalid", webappPageObject.getMessages(), "gateway should indicate if the card is invalid");
  }

  @Then("I should be told that the card prefix does not match")
  public void iShouldBeToldThatTheCardPrefixDoesNotMatch() {
    Assertions.assertEquals("The card number's prefix does not match the card type", webappPageObject.getMessages(), "gateway should indicate if the card is invalid");
  }

  @When("I submit a form with all data but {string} is invalid")
  public void iSubmitAFormWithAllDataButIsInvalid(String date) throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS", "371449635398431", date, "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  @Then("I should be told date is invalid")
  public void iShouldBeToldDateIsInvalid() {
    Assertions.assertEquals("Date is invalid", webappPageObject.getMessages(), "Gateway should indicate the date is invalid");
  }

  @When("I submit a form with all data with {string} and {string}")
  public void iSubmitAFormWithAllDataWithAnd(String cardNumber, String cardType) throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", cardType, cardNumber, "10/20", "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  @And("click on the clear button")
  public void clickOnTheClearButton() {
    webappPageObject.resetDetails();
  }

  @Then("the form data should be cleared")
  public void theFormDataShouldBeCleared() {
    EntryObject entry = webappPageObject.getFieldInfo();
    Assertions.assertEquals("", entry.getCustomerName());
    Assertions.assertEquals("", entry.getCustomerAddress());
    Assertions.assertEquals("", entry.getCardNumber());
    Assertions.assertEquals("", entry.getCardCvv());
    Assertions.assertEquals("", entry.getCardExpiryDate());
    Assertions.assertEquals("", entry.getAmount());
  }
}
