package com.cps3230.assignment.payment.webapp.pageobjects;


import com.cps3230.assignment.payment.webapp.EntryObject;
import com.cps3230.assignment.payment.webapp.utils.BrowserDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaymentWebappPageObject {
  private WebDriver driver;

  public PaymentWebappPageObject() {
    this.driver = BrowserDriver.getCurrentDriver();
  }

  public void enterPage() {
    driver.get("localhost:8080");
  }

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

  public void submitDetails() throws InterruptedException {
    driver.findElement(By.name("submit")).submit();
    WebDriverWait wait = new WebDriverWait(driver, 60);
    wait.until((input) -> ((JavascriptExecutor) input).executeScript("return document.readyState").equals("complete"));
  }

  public void enterDetailsWithEmptyFields(EntryObject entry, String fieldNameToIgnore) {
    enterDetails(entry);
    driver.findElement(By.name(fieldNameToIgnore)).clear();
  }


  public void resetDetails() {
    driver.findElement(By.name("reset")).click();
  }

  public String getMessages() {
    return driver.findElement(By.name("status")).getText();
  }

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
