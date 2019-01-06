package com.cps3230.assignment.payment.gateway;

import com.cps3230.assignment.payment.gateway.models.TransactionModel;
import nz.ac.waikato.modeljunit.AllRoundTester;
import nz.ac.waikato.modeljunit.GreedyTester;
import nz.ac.waikato.modeljunit.RandomTester;
import nz.ac.waikato.modeljunit.StopOnFailureListener;
import nz.ac.waikato.modeljunit.Tester;
import nz.ac.waikato.modeljunit.VerboseListener;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

public class TransactionModelTest {

  @Test
  void runModelTestsWithGreedyTester() {
    TransactionModel model = new TransactionModel();
    Tester tester = new GreedyTester(model);
    tester.buildGraph();
    tester.addListener(new VerboseListener());
    tester.addListener(new StopOnFailureListener());
    tester.addCoverageMetric(new TransitionCoverage());
    tester.addCoverageMetric(new StateCoverage());
    tester.addCoverageMetric(new ActionCoverage());
    tester.generate(150);
    tester.printCoverage();
  }

  @Test
  @Ignore
  void runModelTestsWithAllRoundTester() {
    TransactionModel model = new TransactionModel();
    Tester tester = new AllRoundTester(model);
    tester.buildGraph();
    tester.addListener(new VerboseListener());
    tester.addListener(new StopOnFailureListener());
    tester.addCoverageMetric(new TransitionCoverage());
    tester.addCoverageMetric(new StateCoverage());
    tester.addCoverageMetric(new ActionCoverage());
    tester.generate(400);
    tester.printCoverage();
  }

  @Test
  void runModelTestsWithRandomTester() {
    TransactionModel model = new TransactionModel();
    Tester tester = new RandomTester(model);
    tester.buildGraph();
    tester.addListener(new VerboseListener());
    tester.addListener(new StopOnFailureListener());
    tester.addCoverageMetric(new TransitionCoverage());
    tester.addCoverageMetric(new StateCoverage());
    tester.addCoverageMetric(new ActionCoverage());
    tester.generate(400);
    tester.printCoverage();
  }


}
