package com.cps3230.assignment.payment.gateway;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cps3230.assignment.payment.gateway.interfaces.BankProxy;
import com.cps3230.assignment.payment.gateway.interfaces.DatabaseConnection;
import com.cps3230.assignment.payment.gateway.spies.DatabaseSpy;
import com.cps3230.assignment.payment.gateway.stubs.DateIn2018Stub;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class PaymentProcessorTests {

  private PaymentProcessor processor = null;

  private static Stream<Arguments> generateCreditCardInfoWithEmptyFields() {
    return Stream.of(Arguments
            .of(new CcInfo("", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20", "111")),
        Arguments
            .of(new CcInfo("  ", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
                "111")), Arguments
            .of(new CcInfo("Test Name", "", "AMERICAN_EXPRESS", "371449635398431", "10/20", "111")),
        Arguments
            .of(new CcInfo("Test Name", "Test Address", " ", "", "10/20", "111")),
        Arguments.of(new CcInfo("", "", "VISA", "", "10/20", "")),
        Arguments.of(new CcInfo("Test Name", "Test Address", "VISA", "", "", "")),
        Arguments.of(new CcInfo("Test Name", "", "VISA", "371449635398431", "", "")),
        Arguments.of(new CcInfo("Test Name", "", "VISA", "371449635398431", "", "")),
        Arguments.of(new CcInfo("Test Name", "", "", "", "", "")),
        Arguments
            .of(new CcInfo("Test Name", "Test address", "AMERICAN_EXPRESS", "371449635398431", "",
                "111")),
        Arguments
            .of(new CcInfo("Test Name", "Test Address", "AMERICAN_EXPRESS", "371449635398431",
                "10/20", "")),
        Arguments
            .of(new CcInfo("Test Name", "Test Address", "", "371449635398431", "10/20", "111")));
  }

  private static Stream<Arguments> generateValidCreditCardInfo() {
    return Stream.of(
        Arguments.of(new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431",
            "10/20", "111")),
        Arguments.of(new CcInfo("Test User", "Test address", "VISA", "4557 3727 3979 3901", "10/20",
            "111")),
        Arguments.of(new CcInfo("Test User", "Test address", "MASTER_CARD", "5124 0021 8814 1135",
            "10/20", "111"))
    );
  }

  private static Stream<Arguments> generateCreditCardDetailsWithInvalidCardTypesAndPrefixes() {
    return Stream.of(
        Arguments.of(new CcInfo("Test User", "", "AMERICAN_EXPRESS", "371449635398431",
            "10/20", "111")),
        Arguments.of(new CcInfo("Test User", "Test address", "amer", "371449635398431",
            "10/20", "111")),
        Arguments.of(new CcInfo("Test User", "Test address", "VISA", "371449635398431",
            "10/20", "111")),
        Arguments.of(new CcInfo("Test User", "Test address", "VISA", "5286529599000892",
            "10/20", "111"))
    );
  }

  @BeforeEach
  void setup() {
    processor = new PaymentProcessor();
  }

  @AfterEach
  void teardown() {
    processor = null;
  }

  /**
   * A parameterized test that verifies the luhn algorithm by checking that these valid card numbers
   * from visa, mastercard and american express return valid.
   */
  @ParameterizedTest
  @ValueSource(strings = {"371449635398431", "4230 6885 1339 5737", "3404 769161 31932",
      "4024 0071 5598 4809", "5423 5299 5706 1290"})
  void checkLuhnWorksCorrectlyWithValidCards(
      String creditCards) {
    Assertions.assertTrue(processor.verifyLuhn(creditCards),
        String.format("Card number %s should be valid", creditCards));
  }

  /**
   * A parameterized test that checks luhn algorithm against invalid cards.
   */
  @ParameterizedTest
  @ValueSource(strings = {"79927398710", "79927398712", "79927398719", ""})
  void checkLuhnValidationFailsWithInvalidCards(String cardNumber) {
    Assertions.assertFalse(processor.verifyLuhn(cardNumber),
        String.format("Card number %s should be invalid", cardNumber));
  }

  @Test
  void checkLuhnFailsWhenCardIsNull() {
    Assertions.assertFalse(processor.verifyLuhn(null),
        "Null cards should return false");
  }

  @ParameterizedTest
  @ValueSource(strings = {"7992739I871o", "79927398712", "79927398719"})
  void checkOfflineVerificationWithInvalidCardNumbers(String cardNumber) {
    DateIn2018Stub date = new DateIn2018Stub();
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", cardNumber, "10/20",
            "111");
    Assertions.assertEquals(1, processor.verifyOffline(testCreditCardDetails, date.getTime()));
  }

  @Test
  void checkOfflineValidationWhenCardIsExpired() {
    DateIn2018Stub date = new DateIn2018Stub();
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "04/17",
            "111");
    SimpleDateFormat format = new SimpleDateFormat("MM/YY");
    Assertions.assertEquals(5, processor.verifyOffline(testCreditCardDetails, date.getTime()),
        String
            .format("Card details should be invalid as it is expired, CARD DATE:%s, TEST DATE: %s",
                testCreditCardDetails.getCardExpiryDate(), format.format(date.getTime())));
  }

  @Test
  void checkOfflineVerificationWithInvalidCardBrands() {
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "ameri", "371449635398431", "04/17", "111");
    DateIn2018Stub date = new DateIn2018Stub();
    Assertions.assertEquals(2, processor.verifyOffline(testCreditCardDetails, date.getTime()),
        String
            .format("Card Brand %s should be invalid",
                testCreditCardDetails.getCardType()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"2/", "2.20", "114/20", "2005"})
    //TODO: replace the date stub with mock object
  void testCreditCardDetailsWithInvalidDates(String cardExpiry) {
    CcInfo testDetails = new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS",
        "371449635398431", cardExpiry, "111");
    DateIn2018Stub date = new DateIn2018Stub();
    Assertions.assertEquals(6, processor.verifyOffline(testDetails, date.getTime()),
        "Offline verification should fail with error code 2 (invalid date)");
  }

  @ParameterizedTest
  @ValueSource(strings = {"5124 0021 8814 1135", "5286529599000892"})
  void testOfflineVerificationWithInvvalidPrefixes(String creditCardNumber) {
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "VISA", creditCardNumber, "04/17", "111");
    DateIn2018Stub date = new DateIn2018Stub();
    Assertions.assertEquals(3, processor.verifyOffline(testCreditCardDetails, date.getTime()));
  }

  @ParameterizedTest
  @MethodSource("generateCreditCardInfoWithEmptyFields")
  void checkOfflineValidationWhenObjectHasEmptyFields(CcInfo creditCardDetails) {
    DateIn2018Stub date = new DateIn2018Stub();
    Assertions.assertEquals(6, processor.verifyOffline(creditCardDetails, date.getTime()));
  }

  @ParameterizedTest
  @MethodSource("generateValidCreditCardInfo")
  void checkOfflineValidationWithValidCreditCardDetails(CcInfo creditCardDetails) {
    DateIn2018Stub date = new DateIn2018Stub();
    Assertions.assertEquals(0, processor.verifyOffline(creditCardDetails, date.getTime()));
  }

  @ParameterizedTest
  @MethodSource("generateCreditCardDetailsWithInvalidCardTypesAndPrefixes")
  void checkProcessPaymentVerifiesCreditCardDetailsWithInvalidCardTypeAndCardNumber(CcInfo card)
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    Assertions.assertEquals(1, processor.processPayment(card, 10, date.getTime()));
  }

  @Test
  void checkProcessPaymentFailsWhenAuthorisationFailsBecauseBankDetailsInvalid()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) -1);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
  void checkProcessPaymentWhenAuthFailsBecauseUserDoesNotHaveFunds()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) -2);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2, processor.processPayment(testCard, 10, date.getTime()));
  }


  @Test
  void checkProcessPaymentWhenAuthFailsBecauseOfSomeBankError()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) -3);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
  void checkProcessPaymentWhenAuthFailsBecauseOfRandomError()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) -4);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
  void checkProcessPaymentWithValidTransactionAndCaptureFailureTransactionDoesNotExist()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    when(proxy.capture(111)).thenReturn(-1);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
    // Already captured transactions should not fail the process payment
  void checkProcessPaymentWithValidTransactionAndCaptureFailureTransactionExistsButAlreadyCaptured()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    when(proxy.capture(111)).thenReturn(-2);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(0, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
  void checkProcessPaymentWithValidTransactionAndCaptureFailureTransactionExistsButSevenDayPeriodExpired()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    when(proxy.capture(111)).thenReturn(-3);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
  void checkProcessPaymentWithValidTransactionAndCaptureFailureTransactionExistsButUnknownCaptureError()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    when(proxy.capture(111)).thenReturn(-4);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
  void checkIfBankProxyAuthIsBeingCalledFromProcessPayment()
      throws ExecutionException, InterruptedException {
    BankProxy proxy = spy(BankProxy.class);
    DateIn2018Stub date = new DateIn2018Stub();
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    processor.setBankProxy(proxy);
    processor.processPayment(testCard, 10, date.getTime());
    verify(proxy).auth(testCard, 10);
  }

  @Test
  void checkProcessPaymentReturnsStatusCodeTwoWhenAuthorizeIsDelayedPastTimeOut()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = spy(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenAnswer(new Answer<Long>() {
      @Override
      public Long answer(InvocationOnMock invocation) throws InterruptedException {
        Thread.sleep(2000);
        return (long) 111;
      }
    });
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2,
        processor.processPayment(testCard, 10, date.getTime(), Executors.newFixedThreadPool(2), 1));
  }

  @Test
  void checkProcessPaymentReturnsStatusCodeTwoWhenCaptureIsDelayedPastTimeOut()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = spy(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    when(proxy.capture(111)).thenAnswer(new Answer<Long>() {
      @Override
      public Long answer(InvocationOnMock invocation) throws InterruptedException {
        Thread.sleep(2000);
        return (long) 0;
      }
    });
    processor.setBankProxy(proxy);
    Assertions.assertEquals(2,
        processor.processPayment(testCard, 10, date.getTime(), Executors.newFixedThreadPool(2), 1));
  }

  @Test
  void checkProcessPaymentReturnsOkWhenPaymentCleared()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    BankProxy proxy = spy(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    when(proxy.capture(111)).thenReturn(0);
    processor.setBankProxy(proxy);
    Assertions.assertEquals(0, processor.processPayment(testCard, 10, date.getTime()));
  }

  @Test
  void verifyTheTransactionDatabaseIsAccessedWhenPaymentAuthorized()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    DatabaseSpy databaseSpy = new DatabaseSpy();
    BankProxy proxy = spy(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    processor.setBankProxy(proxy);
    processor.setConnection(databaseSpy);
    processor.processPayment(testCard, 10, date.getTime());
    Assertions.assertTrue(databaseSpy.isAccessed(), "Transaction should be accessed");
  }

  @Test
  void verifyTheTransactionDatabaseIsAccessedWhenPaymentCleared()
      throws ExecutionException, InterruptedException {
    DateIn2018Stub date = new DateIn2018Stub();
    DatabaseSpy databaseSpy = new DatabaseSpy();
    BankProxy proxy = spy(BankProxy.class);
    CcInfo testCard = new CcInfo("Test User", "test address", "AMERICAN_EXPRESS", "371449635398431",
        "10/20", "111");
    when(proxy.auth(testCard, 10)).thenReturn((long) 111);
    when(proxy.capture(111)).thenReturn(0);
    processor.setBankProxy(proxy);
    processor.setConnection(databaseSpy);
    processor.processPayment(testCard, 10, date.getTime());
    Assertions.assertTrue(databaseSpy.isAccessed(), "Transaction should be accessed");
  }


  @Test
  void assertGetConnectionReturnsADatabaseConnection() {
    DatabaseConnection connection = new TransactionDatabase();
    processor.setConnection(connection);
    Assertions.assertEquals(connection, processor.getConnection(),
        "Get connection should return Database connection");
  }

  @Test
  void assertUpdateTransactionsCapturesAuthorizedTransactions() throws InterruptedException {
    Map<Long, Transaction> mockDb = new HashMap<>();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    mockDb.put((long) 11, new Transaction(11, testCreditCardDetails, 10, "AUTHORIZED"));
    TransactionDatabase testConnection = new TransactionDatabase();
    testConnection.database = mockDb;
    when(proxy.capture(11)).thenReturn(0);

    processor.setConnection(testConnection);
    processor.setBankProxy(proxy);
    processor.updateDatabaseTransactions(1, TimeUnit.NANOSECONDS);

    Assertions.assertEquals("CAPTURED", mockDb.get((long) 11).getState());
  }

  @Test
  void assertUpdateTransactionsVoidsAuthorizedTransactions() throws InterruptedException {
    Map<Long, Transaction> fakeDatabase = new HashMap<>();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    fakeDatabase.put((long) 11, new Transaction(11, testCreditCardDetails, 10, "AUTHORIZED"));
    TransactionDatabase testConnection = new TransactionDatabase();
    testConnection.database = fakeDatabase;
    when(proxy.capture(11)).thenReturn(-3);

    processor.setConnection(testConnection);
    processor.setBankProxy(proxy);
    processor.updateDatabaseTransactions(1, TimeUnit.NANOSECONDS);

    Assertions.assertEquals("VOID", fakeDatabase.get((long) 11).getState());
  }

  @Test
  void assertUpdateTransactionsWithAlreadyCapturedTransactions() throws InterruptedException {
    Map<Long, Transaction> fakeDatabase = new HashMap<>();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    fakeDatabase.put((long) 11, new Transaction(11, testCreditCardDetails, 10, "CAPTURED"));
    TransactionDatabase testConnection = new TransactionDatabase();
    testConnection.database = fakeDatabase;
    when(proxy.capture(11)).thenReturn(-2);

    processor.setConnection(testConnection);
    processor.setBankProxy(proxy);
    processor.updateDatabaseTransactions(1, TimeUnit.NANOSECONDS);

    Assertions.assertEquals("CAPTURED", fakeDatabase.get((long) 11).getState());
  }

  @Test
  void checkAuthorizedValuesStayAuthorizedOnUnknownCaptureError() throws InterruptedException {
    Map<Long, Transaction> fakeDatabase = new HashMap<>();
    BankProxy proxy = mock(BankProxy.class);
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    fakeDatabase.put((long) 11, new Transaction(11, testCreditCardDetails, 10, "AUTHORISE"));
    TransactionDatabase testConnection = new TransactionDatabase();
    testConnection.database = fakeDatabase;
    when(proxy.capture(11)).thenReturn(-4);

    processor.setConnection(testConnection);
    processor.setBankProxy(proxy);
    processor.updateDatabaseTransactions(1, TimeUnit.NANOSECONDS);

    Assertions.assertEquals("AUTHORISE", fakeDatabase.get((long) 11).getState());
  }

  @Test
  void checkRefundFailsIfTransactionNotCaptured() {
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    Transaction transaction = new Transaction(11, testCreditCardDetails, 10, "AUTHORIZED");
    BankProxy proxy = mock(BankProxy.class);
    when(proxy.refund((long) 11, 10)).thenReturn(0);
    processor.setBankProxy(proxy);
    Assertions.assertEquals("AUTHORIZED", processor.refund(transaction).getState());
  }

  @Test
  void assertRefundWorksWithCapturedTransaction() {
    CcInfo testCreditCardDetails =
        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
            "111");
    Transaction transaction = new Transaction(11, testCreditCardDetails, 10, "CAPTURED");
    BankProxy proxy = mock(BankProxy.class);
    when(proxy.refund((long) 11, 10)).thenReturn(0);
    processor.setBankProxy(proxy);
    Assertions.assertEquals("REFUNDED", processor.refund(transaction).getState());
  }

}
