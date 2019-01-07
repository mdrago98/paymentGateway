package com.cps3230.assignment.payment.webapp.stepdefs;

import com.cps3230.assignment.payment.webapp.EntryObject;
import com.cps3230.assignment.payment.webapp.pageobjects.PaymentWebappPageObject;
import cucumber.api.CucumberOptions;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;


@Ignore
@CucumberOptions(features = {
    "src/test/resources/features/webapp_features.feature"},
    glue = "com.cps3230.assignment.payment.webapp.stepdefs", plugin = {
    "pretty"})
public class WebAppDefs {

  private PaymentWebappPageObject webappPageObject = new PaymentWebappPageObject();

  /**
   * A step definition for trying to process a payment.
   */
  @Given("I am a user trying to process a payment")
  public void userTryingToProcessAPayment() {
    webappPageObject.enterPage();
  }

  /**
   * A step definition for submitting correct details.
   * @throws InterruptedException if the method is interrupted
   */
  @When("I submit correct details")
  public void submitCorrectDetails() throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS",
        "371449635398431", "10/20", "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  /**
   * A step definition for verifying the payment message.
   */
  @Then("I should be told that the payment was successful")
  public void shouldBeToldThatThePaymentWasSuccessful() {
    String message = webappPageObject.getMessages();
    Assertions.assertEquals("Payment successful", message,
        "Success message should appear on valid payment");
  }

  /**
   * A step definition for submitting correct details.
   * @param fieldName the field name to ignore
   * @throws InterruptedException if the method is interrupted
   */
  @When("I submit a form with all data except {string}")
  public void submitFormWithAllDataExcept(String fieldName) throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS",
        "371449635398431", "10/20", "111", "10");
    webappPageObject.enterDetailsWithEmptyFields(entry, fieldName);
    webappPageObject.submitDetails();
  }

  /**
   * A step definition for verifying the required field message.
   * @param fieldName the required field name
   */
  @Then("I should be told that {string} is required")
  public void shouldBeToldStringIsRequired(String fieldName) {
    String message = webappPageObject.getMessages();
    Assertions.assertEquals("Empty fields " + fieldName, message);
  }

  /**
   * A step definition for submitting all data with an invalid card.
   * @param invalidCard the invalid card
   * @throws InterruptedException if the method is interrupted
   */
  @When("I submit a form with all data but with an {string}")
  public void submitFormWithAllDataButWithAnInvalidCard(String invalidCard)
      throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS",
        invalidCard, "10/20", "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  /**
   * A step definition for verifying the message that the card is invalid.
   */
  @Then("I should be told that the card is invalid")
  public void shouldBeToldThatTheCardIsInvalid() {
    Assertions.assertEquals("Card is invalid", webappPageObject.getMessages(),
        "gateway should indicate if the card is invalid");
  }

  /**
   * A step definition for verifying the message that the card prefix is invalid.
   */
  @Then("I should be told that the card prefix does not match")
  public void shouldBeToldThatTheCardPrefixDoesNotMatch() {
    Assertions.assertEquals("The card number's prefix does not match the card type",
        webappPageObject.getMessages(), "gateway should indicate if the card is invalid");
  }

  /**
   * A step definition for inputting all data but the date is invalid.
   * @param date the invalid date
   * @throws InterruptedException if the method is interrupted
   */
  @When("I submit a form with all data but {string} is invalid")
  public void submitAFormWithAllDataButIsInvalid(String date) throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS",
        "371449635398431", date, "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  /**
   * A step definition for verifying the message that the date is invalid.
   */
  @Then("I should be told date is invalid")
  public void shouldBeToldDateIsInvalid() {
    Assertions.assertEquals("Date is invalid", webappPageObject.getMessages(),
        "Gateway should indicate the date is invalid");
  }

  /**
   * A step definition for inputting card number and card type combinations.
   * @param cardNumber the card number
   * @param cardType the card type
   * @throws InterruptedException if the method is interrupted
   */
  @When("I submit a form with all data with {string} and {string}")
  public void submitAFormWithAllDataWithAnd(String cardNumber, String cardType)
      throws InterruptedException {
    EntryObject entry = new EntryObject("Test User", "Test Address", cardType, cardNumber, "10/20",
        "111", "10");
    webappPageObject.enterDetails(entry);
    webappPageObject.submitDetails();
  }

  @And("click on the clear button")
  public void clickOnTheClearButton() {
    webappPageObject.resetDetails();
  }

  /**
   * A step definition for verifying the form is reset.
   */
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
