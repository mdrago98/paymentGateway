package com.cps3230.assignment.webapp;

import com.cps3230.assignment.payment.gateway.PaymentProcessor;
import com.cps3230.assignment.payment.webapp.Application;
import com.cps3230.assignment.payment.webapp.PaymentProcessorController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = {TestApplicationConfiguration.class})
public class SpringBootBaseIntegrationTest {

  @LocalServerPort
  protected int port;

  @Autowired
  PaymentProcessor getPaymentProcessor;

  @Autowired
  private PaymentProcessorController controller;

  @Before
  public void before() {
    controller.setPaymentProcessor(getPaymentProcessor);
  }

  @Test
  public void runCucumberTests() {
    cucumber.api.cli.Main.main(new String[]{
        "--glue",
        "com.cps3230.assignment.webapp.stepdefs",
        "src/test/resources/features/webapp_features.feature"
    });
  }
}
