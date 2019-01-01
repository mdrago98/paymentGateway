package com.cps3230.assignment.payment.gateway.adaptors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cps3230.assignment.payment.gateway.CcInfo;
import com.cps3230.assignment.payment.gateway.PaymentProcessor;
import com.cps3230.assignment.payment.gateway.Transaction;
import com.cps3230.assignment.payment.gateway.enums.TransactionStates;
import com.cps3230.assignment.payment.gateway.interfaces.BankProxy;
import com.cps3230.assignment.payment.gateway.stubs.DateIn2018Stub;
import java.util.concurrent.ExecutionException;


public class PaymentProcessorAdaptor {
  private PaymentProcessor processor = new PaymentProcessor();

  public boolean validate() {
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    return processor.verifyOffline(testCreditCardDetails, new DateIn2018Stub().getTime()) == 0;
  }

  public boolean invalidate() {
    CcInfo testCreditCardDetails =
        new CcInfo("", "", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    return processor.verifyOffline(testCreditCardDetails, new DateIn2018Stub().getTime()) == 0;
  }

  public Transaction authorize() throws ExecutionException, InterruptedException {
    // mock
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.auth(testCreditCardDetails, 10)).thenReturn((long) 1111);
    processor.setBankProxy(mockProxy);
    return processor.authorize(testCreditCardDetails, 10);
  }

  public Transaction authFailBankError() throws ExecutionException, InterruptedException {
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.auth(testCreditCardDetails, 10)).thenReturn((long) -3);
    processor.setBankProxy(mockProxy);
    return processor.authorize(testCreditCardDetails, 10);
  }

  public Transaction bankDetailsInvalid() throws ExecutionException, InterruptedException {
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.auth(testCreditCardDetails, 10)).thenReturn((long) -1);
    processor.setBankProxy(mockProxy);
    return processor.authorize(testCreditCardDetails, 10);
  }

  public Transaction tryToRecaptureBecauseBankError()
      throws ExecutionException, InterruptedException {
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.capture((long) 111)).thenReturn(-4);
    processor.setBankProxy(mockProxy);
    Transaction transaction = new Transaction((long) 111, testCreditCardDetails, 10, "CAPTURE");
    return processor.capture(transaction);
  }

  public Transaction transactionIdDoesNotExist() throws ExecutionException, InterruptedException {
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.capture((long) 111)).thenReturn(-1);
    processor.setBankProxy(mockProxy);
    Transaction transaction = new Transaction((long) 111, testCreditCardDetails, 10, "CAPTURE");
    return processor.capture(transaction);
  }

  public Transaction sevenDayPeriodExpired() throws ExecutionException, InterruptedException {
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.capture((long) 111)).thenReturn(-3);
    processor.setBankProxy(mockProxy);
    Transaction transaction = new Transaction((long) 111, testCreditCardDetails, 10, "CAPTURE");
    return processor.capture(transaction);
  }

  public Transaction capture() throws ExecutionException, InterruptedException {
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.capture((long) 111)).thenReturn(0);
    processor.setBankProxy(mockProxy);
    Transaction transaction = new Transaction((long) 111, testCreditCardDetails, 10, "CAPTURE");
    return processor.capture(transaction);
  }

  public Transaction refund() {
    CcInfo testCreditCardDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
        "111");
    BankProxy mockProxy = mock(BankProxy.class);
    when(mockProxy.refund((long) 111, (long) 10)).thenReturn(0);
    processor.setBankProxy(mockProxy);
    Transaction transaction = new Transaction((long) 111, testCreditCardDetails, 10, "CAPTURE");
    return processor.refund(transaction);
  }

}
