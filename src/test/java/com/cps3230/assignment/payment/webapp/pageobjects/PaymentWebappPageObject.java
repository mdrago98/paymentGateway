package com.cps3230.assignment.payment.webapp.pageobjects;

import com.cps3230.assignment.payment.webapp.EntryObject;
import com.cps3230.assignment.payment.webapp.utils.BrowserDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaymentWebappPageObject {

  private final WebDriver driver;

  public PaymentWebappPageObject() {
    this.driver = BrowserDriver.getCurrentDriver();
  }

  public void enterPage() {
    driver.get("localhost:8080");
  }

  /**
   * A method that facilitates form entry.
   *
   * @param entry an Entry object representation of the form details.
   */
  public void enterDetails(EntryObject entry) {
    driver.findElement(By.name("customerName")).sendKeys(entry.getCustomerName());
    driver.findElement(By.name("customerAddress")).sendKeys(entry.getCustomerAddress());
    final Select selectBox = new Select(driver.findElement(By.name("cardType")));
    selectBox.selectByValue(entry.getCardType());
    driver.findElement(By.name("cardNumber")).sendKeys(entry.getCardNumber());
    driver.findElement(By.name("cardExpiryDate")).sendKeys(entry.getCardExpiryDate());
    driver.findElement(By.name("cardCvv")).sendKeys(entry.getCardCvv());
    driver.findElement(By.name("amount")).sendKeys(entry.getAmount());
  }

  /**
   * A method that facilitates form submission.
   *
   * @throws InterruptedException thrown when the method was interrupted
   */
  public void submitDetails() throws InterruptedException {
    driver.findElement(By.name("submit")).submit();
    WebDriverWait wait = new WebDriverWait(driver, 60);
    wait.until((input) -> ((JavascriptExecutor) input).executeScript("return document.readyState")
        .equals("complete"));
  }

  /**
   * A method that facilitates form entry with empty fields.
   *
   * @param entry an Entry object representation of the form details.
   * @param fieldNameToIgnore a String representation of the field name that will be left blank
   */
  public void enterDetailsWithEmptyFields(EntryObject entry, String fieldNameToIgnore) {
    enterDetails(entry);
    driver.findElement(By.name(fieldNameToIgnore)).clear();
  }

  /**
   * A method that facilitates form reset.
   */
  public void resetDetails() {
    driver.findElement(By.name("reset")).click();
  }

  /**
   * A method that gets form messages.
   *
   * @return a String representation of the message.
   */
  public String getMessages() {
    return driver.findElement(By.name("status")).getText();
  }

  /**
   * A method that facilitates getting entered details in a form.
   *
   * @return an object representing the entered details
   */
  public EntryObject getFieldInfo() {
    return new EntryObject(
        driver.findElement(By.name("customerName")).getText(),
        driver.findElement(By.name("customerAddress")).getText(),
        driver.findElement(By.name("cardType")).getText(),
        driver.findElement(By.name("cardNumber")).getText(),
        driver.findElement(By.name("cardExpiryDate")).getText(),
        driver.findElement(By.name("cardCvv")).getText(),
        driver.findElement(By.name("amount")).getText()
    );
  }

}
