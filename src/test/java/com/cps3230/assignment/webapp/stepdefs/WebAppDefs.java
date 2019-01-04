package com.cps3230.assignment.webapp.stepdefs;

import com.cps3230.assignment.webapp.PageObjects.PaymentWebappPageObject;
import cucumber.api.java8.En;

public class WebAppDefs implements En {

  private PaymentWebappPageObject webappPageObject = new PaymentWebappPageObject();

  public WebAppDefs() {
    Given("^I am a user trying to process a payment$", () -> {
      webappPageObject.enterPage();

    });
    When("^I submit correct details$", () -> {

    });
    Then("^I should be told that the payment was successful$", () -> {
    });
  }
}
