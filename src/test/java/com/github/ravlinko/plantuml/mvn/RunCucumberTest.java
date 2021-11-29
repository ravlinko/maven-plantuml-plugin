package com.github.ravlinko.plantuml.mvn;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import static io.cucumber.junit.CucumberOptions.SnippetType.CAMELCASE;

@Ignore
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "summary"}, snippets = CAMELCASE)
public class RunCucumberTest {
}