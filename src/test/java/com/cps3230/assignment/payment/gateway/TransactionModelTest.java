package com.cps3230.assignment.payment.gateway;

import com.cps3230.assignment.payment.gateway.models.TransactionModel;
import nz.ac.waikato.modeljunit.GreedyTester;
import nz.ac.waikato.modeljunit.StopOnFailureListener;
import nz.ac.waikato.modeljunit.Tester;
import nz.ac.waikato.modeljunit.VerboseListener;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import org.junit.jupiter.api.Test;

public class TransactionModelTest {

  @Test
  void runModelTests() {
    TransactionModel model = new TransactionModel();
    Tester tester = new GreedyTester(model);
    tester.buildGraph();
    tester.addListener(new VerboseListener());
    tester.addListener(new StopOnFailureListener());
    tester.addCoverageMetric(new TransitionCoverage());
    tester.addCoverageMetric(new StateCoverage());
    tester.addCoverageMetric(new ActionCoverage());
    tester.generate(100);
    tester.printCoverage();
  }

}
