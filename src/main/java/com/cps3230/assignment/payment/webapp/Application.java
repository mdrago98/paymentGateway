package com.cps3230.assignment.payment.webapp;

import com.cps3230.assignment.payment.gateway.PaymentProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  @Bean
  public PaymentProcessor getPaymentProcessor() {
    return new PaymentProcessor();
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
