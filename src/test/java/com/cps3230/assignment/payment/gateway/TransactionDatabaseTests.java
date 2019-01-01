//package com.cps3230.assignment.payment.gateway;
//
//import static org.mockito.Mockito.spy;
//import static org.mockito.Mockito.verify;
//
//import java.util.Map;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class TransactionDatabaseTests {
//
//  private TransactionDatabase database = null;
//
//  @BeforeEach
//  void setup() {
//    database = new TransactionDatabase();
//  }
//
//  @AfterEach
//  void teardown() {
//    database = null;
//  }
//
//  @Test
//  void verifySaveTransactionSavesATransaction() {
//    Map<Long, Transaction> databaseSpy = spy(database.database);
//    CcInfo testCreditCardDetails =
//        new CcInfo("Test User", "Test address", "AMERICAN_EXPRESS", "371449635398431", "10/20",
//            "111");
//    Transaction transaction = new Transaction(12, testCreditCardDetails, 10, "AUTHORIZED");
//    database.saveTransaction(transaction);
//    verify(databaseSpy).put(transaction.getId(), transaction);
//
//  }
//
//}
