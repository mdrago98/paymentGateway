package com.cps3230.assignment.webapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cps3230.assignment.payment.gateway.CcInfo;
import com.cps3230.assignment.payment.gateway.PaymentProcessor;
import com.cps3230.assignment.payment.gateway.interfaces.BankProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestApplicationConfiguration {

  @Bean
  @Primary
  public PaymentProcessor getPaymentProcessor() {
    PaymentProcessor processor = new PaymentProcessor();
    CcInfo testCard = new CcInfo("Test User", "Test Address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    BankProxy proxy = mock(BankProxy.class);
    when(proxy.auth(any(), eq((long) 10))).thenReturn((long)111);
    when(proxy.capture(111)).thenReturn(0);
    processor.setBankProxy(proxy);
    return processor;
  }
}
