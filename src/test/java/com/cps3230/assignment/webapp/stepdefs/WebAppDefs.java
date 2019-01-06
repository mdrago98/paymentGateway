package com.cps3230.assignment.webapp.stepdefs;

import com.cps3230.assignment.webapp.EntryObject;
import com.cps3230.assignment.webapp.PageObjects.PaymentWebappPageObject;
import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.junit.Cucumber;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;


public class WebApp {

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
    Assertions.assertEquals("", message, "Success message should appear on valid payment");
  }

  @When("I submit a form with all data except <fieldName>")
  public void iSubmitAFormWithAllDataExceptFieldName(String fieldName) {
    EntryObject entry = new EntryObject("Test User", "Test Address", "AMERICAN_EXPRESS", "371449635398431", "10/20", "111", "10");
    webappPageObject.enterDetailsWithEmptyFields(entry, fieldName);
  }

  @Then("I should be told that <fieldName> is required")
  public void iShouldBeToldThatFieldNameIsRequired(String fieldName) {
    String message = webappPageObject.getMessages();
    Assertions.assertEquals("Empty fields " + fieldName, message);
  }
}
