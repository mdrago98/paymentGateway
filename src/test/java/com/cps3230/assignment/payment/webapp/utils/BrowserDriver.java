package com.cps3230.assignment.payment.webapp.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class BrowserDriver {

  private static final Logger LOGGER = LogManager.getRootLogger();

  private static WebDriver mDriver;

  /**
   * A method that returns a WebDriver instance.
   * @return a WebDriver instance.
   */
  public static synchronized WebDriver getCurrentDriver() {
    if (mDriver == null) {
      try {
        System.setProperty("webdriver.chrome.driver", "/home/drago/chromedriver");
        mDriver = new ChromeDriver();
      } finally {
        Runtime.getRuntime().addShutdownHook(
            new Thread(new BrowserCleanup()));
      }
    }
    return mDriver;
  }

  /**
   * A method that facilitates closing the WebDriver instance.
   */
  public static void close() {
    try {
      getCurrentDriver().quit();
      mDriver = null;
      LOGGER.info("Closing the browser");
    } catch (UnreachableBrowserException e) {
      LOGGER.info("Browser is unreachable");
    }
  }

  private static class BrowserCleanup implements Runnable {

    public void run() {
      close();
    }
  }
}
