package com.cps3230.assignment.webapp.PageObjects;

import com.cps3230.assignment.webapp.utils.BrowserDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class PaymentWebappPageObject {
  private WebDriver driver;

  public PaymentWebappPageObject() {
    this.driver = BrowserDriver.getCurrentDriver();
  }

  public void enterPage() {
    driver.get("localhost:8080");
  }

  public void enterDetails(String customerName, String customerAddress, String cardType,
      String cardNumber, String cardExpiryDate, String cardCvv, String amount) {
    driver.findElement(By.name("customerName")).sendKeys(customerName);
    driver.findElement(By.name("customerAddress")).sendKeys(customerAddress);
    driver.findElement(By.name("cardType")).sendKeys();
    final Select selectBox = new Select(driver.findElement(By.name(cardType)));
    selectBox.deselectByValue(cardType);
    driver.findElement(By.name("cardNumber")).sendKeys(cardNumber);
    driver.findElement(By.name("cardExpiryDate")).sendKeys(cardExpiryDate);
    driver.findElement(By.name("cardCvv")).sendKeys(cardCvv);
    driver.findElement(By.name("amount")).sendKeys(amount);
  }

  public void submitDetails() {
    driver.findElement(By.name("paymentForm")).submit();
  }

}
