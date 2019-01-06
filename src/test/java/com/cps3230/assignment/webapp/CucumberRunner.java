package com.cps3230.assignment.webapp;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features/webapp_features.feature"}, glue = "com.cps3230.assignment.webapp.stepdefs" )
public class CucumberRunner {

}
