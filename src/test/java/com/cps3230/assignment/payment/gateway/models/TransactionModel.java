package com.cps3230.assignment.payment.gateway.models;

import com.cps3230.assignment.payment.gateway.Transaction;
import com.cps3230.assignment.payment.gateway.adaptors.PaymentProcessorAdaptor;
import java.util.concurrent.ExecutionException;
import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import org.junit.jupiter.api.Assertions;

public class TransactionModel implements FsmModel {

  PaymentProcessorAdaptor adaptor = new PaymentProcessorAdaptor();
  private States state;

  private boolean isValidated, isAuthorized, isCaptured;

  @Override
  public Object getState() {
    return state;
  }

  @Override
  public void reset(boolean testing) {
    isValidated = false;
    isAuthorized = false;
    isCaptured = false;
    state = States.OFFLINE_VERIFICATION;

    if (testing) {
      adaptor = new PaymentProcessorAdaptor();
    }
  }

  public boolean validateGuard() {
    return getState().equals(States.OFFLINE_VERIFICATION) && !isValidated;
  }

  @Action
  public void validate() {

    isValidated = adaptor.validate();
    state = States.AUTHORISE;

    Assertions.assertTrue(isValidated, "The model should pass validation");
  }

  public boolean invalidateGuard() {
    return getState().equals(States.OFFLINE_VERIFICATION) && !isValidated;
  }

  @Action
  public void invalidate() {
    isValidated = adaptor.invalidate();
    state = States.OFFLINE_VERIFICATION;

    Assertions.assertFalse(isValidated);
  }

  public boolean invalidateLuhnGuard() {
    return getState().equals(States.OFFLINE_VERIFICATION) && !isValidated;
  }

  @Action
  public void invalidateLuhn() {
    isValidated = adaptor.invalidateLuhn();
    state = States.OFFLINE_VERIFICATION;

    Assertions.assertFalse(isValidated);
  }

  public boolean invalidateDateGuard() {
    return getState().equals(States.OFFLINE_VERIFICATION) && !isValidated;
  }

  @Action
  public void invalidateDate() {
    isValidated = adaptor.invalidateDate();
    state = States.OFFLINE_VERIFICATION;

    Assertions.assertFalse(isValidated);
  }

  public boolean invalidateCardBrandGuard() {
    return getState().equals(States.OFFLINE_VERIFICATION) && !isValidated;
  }

  @Action
  public void invalidateCardBrand() {
    isValidated = adaptor.invalidateCardBrand();
    state = States.OFFLINE_VERIFICATION;

    Assertions.assertFalse(isValidated);
  }

  public boolean invalidatePrefixGuard() {
    return getState().equals(States.OFFLINE_VERIFICATION) && !isValidated;
  }

  @Action
  public void invalidatePrefix() {
    isValidated = adaptor.invalidatePrefix();
    state = States.OFFLINE_VERIFICATION;

    Assertions.assertFalse(isValidated);
  }

  public boolean authorizeGuard() {
    return getState().equals(States.AUTHORISE) && isValidated && !isAuthorized;
  }

  @Action
  public void authorize() {
    try {
      Transaction transaction = adaptor.authorize();
      isAuthorized = true;
      state = States.CAPTURE;
      Assertions.assertNotNull(transaction);
      Assertions.assertEquals("AUTHORIZED", transaction.getState(), "State should be AUTHORIZED");
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public boolean authFailBankErrorGuard() {
    return getState().equals(States.AUTHORISE) && isValidated && !isAuthorized;
  }

  @Action
  public void authFailBankError() {
    try {
      Transaction transaction = adaptor.authFailBankError();
      isAuthorized = false;
      state = States.OFFLINE_VERIFICATION;
      Assertions.assertNull(transaction);
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public boolean bankDetailsInvalidGuard() {
    return getState().equals(States.AUTHORISE) && isValidated && !isAuthorized;
  }

  @Action
  public void bankDetailsInvalid() {
    try {
      Transaction transaction = adaptor.authFailBankError();
      isValidated = true;
      isAuthorized = false;
      state = States.OFFLINE_VERIFICATION;
      Assertions.assertNull(transaction, "Authorization should have failed");
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public boolean tryToRecaptureBecauseBankErrorGuard() {
    return getState().equals(States.CAPTURE) && isAuthorized && isValidated && !isCaptured;
  }

  @Action
  public void tryToRecaptureBecauseBankError() throws ExecutionException, InterruptedException {
    Transaction transaction = adaptor.tryToRecaptureBecauseBankError();
    isValidated = true;
    isAuthorized = true;
    isCaptured = false;
    state = States.CAPTURE;
    Assertions.assertEquals("AUTHORIZED", transaction.getState());
  }

  public boolean transactionIdDoesNotExistGuard() {
    return getState().equals(States.CAPTURE) && isAuthorized && isValidated && !isCaptured;
  }

  @Action
  public void transactionIdDoesNotExist() throws ExecutionException, InterruptedException {
    Transaction transaction = adaptor.transactionIdDoesNotExist();
    isValidated = true;
    isAuthorized = true;
    isCaptured = false;
    state = States.BANK_ERROR;
    Assertions.assertEquals("BANK_DETAILS_INVALID", transaction.getState());
  }

  public boolean sevenDayPeriodExpiredGuard() {
    return getState().equals(States.CAPTURE) && isAuthorized && isValidated && !isCaptured;
  }

  @Action
  public void sevenDayPeriodExpired() throws ExecutionException, InterruptedException {
    Transaction transaction = adaptor.sevenDayPeriodExpired();
    isValidated = true;
    isAuthorized = true;
    isCaptured = false;
    state = States.VOID;
    Assertions.assertEquals("VOID", transaction.getState());
  }

  public boolean isAlreadyCapturedGuard() {
    return getState().equals(States.CAPTURE) && isAuthorized && isValidated && !isCaptured;
  }

  @Action
  public void isAlreadyCaptured() throws ExecutionException, InterruptedException {
    Transaction transaction = adaptor.isAlreadyCaptured();
    isValidated = true;
    isAuthorized = true;
    isCaptured = true;
    state = States.CAPTURED;
    Assertions.assertEquals("CAPTURED", transaction.getState());
  }

  public boolean captureGuard() {
    return getState().equals(States.CAPTURE) && isAuthorized && isValidated && !isCaptured;
  }

  @Action
  public void capture() throws ExecutionException, InterruptedException {
    Transaction transaction = adaptor.capture();
    isValidated = true;
    isAuthorized = true;
    isCaptured = true;
    state = States.CAPTURED;
    Assertions.assertEquals("CAPTURED", transaction.getState());
  }

  public boolean refundGuard() {
    return getState().equals(States.CAPTURED) && isAuthorized && isValidated && isCaptured;
  }

  @Action
  public void refund() {
    Transaction transaction = adaptor.refund();
    isValidated = true;
    isAuthorized = true;
    isCaptured = true;
    state = States.REFUND;
    Assertions.assertEquals("REFUNDED", transaction.getState());
  }

  private enum States {OFFLINE_VERIFICATION, AUTHORISE, VOID, CAPTURE, CAPTURED, REFUND, BANK_ERROR}


}
