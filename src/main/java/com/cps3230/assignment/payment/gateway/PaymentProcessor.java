package com.cps3230.assignment.payment.gateway;

import com.cps3230.assignment.payment.gateway.enums.CardBrands;
import com.cps3230.assignment.payment.gateway.enums.CardValidationStatuses;
import com.cps3230.assignment.payment.gateway.enums.TransactionStates;

import com.cps3230.assignment.payment.gateway.interfaces.BankProxy;
import com.cps3230.assignment.payment.gateway.interfaces.DatabaseConnection;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentProcessor {

  private static Logger LOGGER = LogManager.getRootLogger();
  private BankProxy bankProxy;
  private DatabaseConnection connection;


  public PaymentProcessor() {
  }

  /**
   * Gets the bank proxy.
   *
   * @return an object representation of the bank proxy
   */
  public BankProxy getBankProxy() {
    return bankProxy;
  }

  /**
   * Sets the bank proxy.
   *
   * @param bankProxy an implementation of the bank proxy
   */
  public void setBankProxy(BankProxy bankProxy) {
    this.bankProxy = bankProxy;
  }

  //TODO: create a wrapper to get executable service

  /**
   * A wrapper that gets a database connection.
   *
   * @return An object that inherits from DatabaseConnection
   */
  public DatabaseConnection getConnection() {
    DatabaseConnection connection = this.connection;
    if (connection == null) {
      connection = new TransactionDatabase();
    }
    return connection;
  }

  /**
   * Sets a database interface.
   *
   * @param connection An object representing the
   */
  public void setConnection(
      DatabaseConnection connection) {
    this.connection = connection;
  }

  /**
   * A helper function that returns a callable thread to authorize a transaction.
   *
   * @param cardInfo An object representation of the card details
   * @param amount A long representing the amount to transfer
   * @return A Callable wrapper for the bank proxy authorization.
   */
  private Callable<Transaction> authoriseCallable(CcInfo cardInfo, long amount) {
    return () -> {
      Transaction transaction = null;
      long transactionId = getBankProxy().auth(cardInfo, amount);
      if (transactionId > 0) {
        transaction = new Transaction(transactionId, cardInfo, amount, TransactionStates.AUTHORIZED);
      } else {
        LOGGER.info("Bank error - bank returned status code {} - payment rejected", transactionId);
      }
      return transaction;
    };
  }

  /**
   * A wrapper function that authorizes a bank transaction.
   * @param cardInfo An object representing card info
   * @param amount The long representation of the amount to charge
   * @return An authorized transaction IFF auth is successful
   * @throws ExecutionException An execution exception
   * @throws InterruptedException An interrupted exception if the execution is interrupted
   */
  public Transaction authorize(CcInfo cardInfo, long amount)
      throws ExecutionException, InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(1);
    Future<Transaction> authorizeFuture = executor.submit(authoriseCallable(cardInfo, amount));
    return authorizeFuture.get();
  }

  /**
   * A wrapper function that authorizes a bank transaction.
   *
   * @param transaction An object representing a transaction
   * @return An authorized transaction IFF auth is successful
   * @throws ExecutionException An execution exception
   * @throws InterruptedException An interrupted exception if the execution is interrupted
   */
  public Transaction capture(Transaction transaction)
      throws ExecutionException, InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(1);
    Future<Transaction> authorizeFuture = executor.submit(captureCallable(transaction));
    return authorizeFuture.get();
  }

  /**
   * A helper function that returns a callable thread to captureCallable a transaction.
   *
   * @param transaction An object representation of the transaction
   * @return A callable wrapper for the bank proxy captureCallable method.
   */
  Callable<Transaction> captureCallable(Transaction transaction) {
    return () -> {
      int status = getBankProxy().capture(transaction.getId());
      switch (status) {
        case 0: {
          transaction.setState(TransactionStates.CAPTURED.toString());
          break;
        }
        case -1: {
          transaction.setState(TransactionStates.BANK_DETAILS_INVALID.toString());
          break;
        }
        case -2: {
          transaction.setState(TransactionStates.CAPTURED.toString());
          break;
        }
        case -3: {
          transaction.setState(TransactionStates.VOID.toString());
          break;
        }
        default: {
          transaction.setState(TransactionStates.AUTHORIZED.toString());
        }
      }
      return transaction;
    };
  }

  /**
   * An overridden method for updating the AuthTransactions in the database.
   *
   * @param executorService An executor the dispatch async tasks
   * @return null
   */
  Callable<Void> updateTransactions(ExecutorService executorService) {
    return updateTransactions(executorService, new CountDownLatch(1));
  }

  /**
   * An asynchronous task that gets all authorized transactions from the database and tries to
   * captureCallable them.
   *
   * @param executorService An executor to dispatch async tasks
   * @param latch A countdown latch that synchronizes tasks
   * @return null
   */
  Callable<Void> updateTransactions(ExecutorService executorService, CountDownLatch latch) {
    return () -> {
      Map<Long, Transaction> authorizedTransactions = getConnection().getDatabase()
          .entrySet().stream()
          .filter(x -> x.getValue().getState().equals(TransactionStates.AUTHORIZED.toString()))
          .collect(
              Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      authorizedTransactions.forEach((transId, transaction) -> {
        try {
          Future<Transaction> captureFuture = executorService.submit(captureCallable(transaction));
          Transaction capturedTransaction = captureFuture.get();
          getConnection().saveTransaction(capturedTransaction);
        } catch (InterruptedException | ExecutionException e) {
          LOGGER.error(e.getLocalizedMessage());
        }
      });
      latch.countDown();
      return null;
    };
  }

  /**
   * A method that asynchronously gets all transactions that are authorised and captures them at
   * set intervals.
   *
   * @param delay an integer representing the interval between executions
   * @param unit TimeUnit representation of the time units.
   * @throws InterruptedException When the method is interrupted.
   */
  public void updateDatabaseTransactions(int delay, TimeUnit unit) throws InterruptedException {
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
    CountDownLatch latch = new CountDownLatch(1);
    service.schedule(updateTransactions(service, latch), delay, unit);
    latch.await();
  }

  public int processPayment(CcInfo cardInfo, long amount)
      throws ExecutionException, InterruptedException {
    return processPayment(cardInfo, amount, new Date());
  }

  /**
   * A method that processes card payments.
   * @param cardInfo
   * @param amount
   * @param date
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public int processPayment(CcInfo cardInfo, long amount, Date date)
      throws ExecutionException, InterruptedException {
    return processPayment(cardInfo, amount, date, Executors.newFixedThreadPool(2), 7200);
  }

  /**
   * A method that processes card payments.
   *
   * @param cardInfo an object representation of the card details
   * @param amount a long representation of the card details
   * @param date a data object to check the expiry of the card
   * @return an integer representation of the transaction status code
   */
  int processPayment(CcInfo cardInfo, long amount, Date date, ExecutorService  executor, int timeout)
      throws ExecutionException, InterruptedException {
    int status = 0;
    executor.submit(updateTransactions(executor));
    int offlineVerificationStatus = verifyOffline(cardInfo, date);
    if (offlineVerificationStatus == 0) {
      Future<Transaction> authorizationFuture = executor.submit(authoriseCallable(cardInfo, amount));
      Transaction transaction;
      try {
        transaction = authorizationFuture.get(timeout, TimeUnit.SECONDS);
        if (transaction != null) {
          getConnection().saveTransaction(transaction);
          Future<Transaction> captureFuture = executor.submit(captureCallable(transaction));
          Transaction capturedTransaction = captureFuture.get(timeout, TimeUnit.SECONDS);
          if (capturedTransaction.getState().equals(TransactionStates.CAPTURED.toString())) {
            getConnection().saveTransaction(capturedTransaction);
          } else {
            status = 2;
            LOGGER.info("Could not captureCallable transaction");
          }
        } else {
          status = 2;
          LOGGER.error("Auth request failed");
        }
      } catch (TimeoutException e) {
        status = 2;
        LOGGER.info("Bank error Requests timed out");
      }
    } else {
      status = 1;
      LOGGER.info("The card details are invalid with status code {} - payment rejected",
          offlineVerificationStatus);
    }
    return status;
  }

  /**
   * A method that refunds a transaction and updates a database.
   *
   * @param transaction The transaction to refund
   * @return true IFF the refund is successful
   */
  public Transaction refund(Transaction transaction) {
    if (transaction.getState().equals(TransactionStates.CAPTURED.toString())) {
      bankProxy.refund(transaction.getId(), transaction.getAmount());
      transaction.setState(TransactionStates.REFUNDED.toString());
      getConnection().saveTransaction(transaction);
    } else {
      LOGGER.info("Tried to refund an un captured transaction with ID {}", transaction.getId());
    }
    return transaction;
  }

  /**
   * A method that verifies card details.
   * @param cardInfo an object representation of the card details.
   * @return An integer representation of the status code.
   */
  public int verifyOffline(CcInfo cardInfo) {
    return verifyOfflineEnum(cardInfo, new Date()).ordinal();
  }

  public int verifyOffline(CcInfo cardInfo, Date date) {
    return verifyOfflineEnum(cardInfo, date).ordinal();
  }

  /**
   * A method that verifies the credit card details offline.
   *
   * @param cardInfo an object representation of the card details
   * @return An enum representation of the verification status code; -1: failed luhn; -2 invalid
   *         prefix; -3
   */
  public CardValidationStatuses verifyOfflineEnum(CcInfo cardInfo) {
    return verifyOfflineEnum(cardInfo, new Date());
  }

  /**
   * A method that verifies the credit card details offline.
   *
   * @param cardInfo an object representation of the card details
   * @param date an object representation of the date
   * @return an enum representation of the verification status code
   */
  public CardValidationStatuses verifyOfflineEnum(CcInfo cardInfo, Date date) {
    CardValidationStatuses cardValidityStatus = CardValidationStatuses.VALID;
    if (!cardInfo.verifyAllDetailsAreEntered()) {
      cardValidityStatus = CardValidationStatuses.EMPTY_FIELDS;
    } else if (!verifyLuhn(cardInfo.getCardNumber())) {
      cardValidityStatus = CardValidationStatuses.LUHN_FAILURE;
    } else if (cardInfo.getCardTypeEnum() == CardBrands.INVALID) {
      cardValidityStatus = CardValidationStatuses.CARD_BRAND_NOT_VALID;
    } else if (!cardInfo.validatePrefix()) {
      cardValidityStatus = CardValidationStatuses.PREFIX_NOT_VALID;
    } else {
      try {
        if (cardInfo.validateCardIsNotExpired(date) == CardValidationStatuses.CARD_EXPIRED) {
          cardValidityStatus = CardValidationStatuses.CARD_EXPIRED;
        }
      } catch (ParseException e) {
        cardValidityStatus = CardValidationStatuses.DATE_PARSE_FAILURE;
      }
    }
    return cardValidityStatus;
  }

  /**
   * A method that implements Luhn's checksum algorithm.
   *
   * @param card a string representation of the card number
   * @return true IFF card is valid by Luhn's algorithm
   */
  boolean verifyLuhn(String card) {
    boolean validity = false;
    if (card != null) {
      card = card.replaceAll("\\s+", "");
    }
    if (card != null && !card.isEmpty()) {
      int digits = card.length();
      int sum = 0;
      boolean isSecond = false;
      for (int i = digits - 1; i >= 0; i--) {
        int d = card.charAt(i) - '0';
        if (isSecond) {
          d = d * 2;
        }
        sum += d / 10;
        sum += d % 10;
        isSecond = !isSecond;
      }
      validity = (sum % 10 == 0);
    }
    return validity;
  }
}
