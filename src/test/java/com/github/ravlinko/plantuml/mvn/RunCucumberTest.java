package com.github.ravlinko.plantuml.mvn;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

import static io.cucumber.junit.CucumberOptions.SnippetType.CAMELCASE;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "summary"}, tags = "(not @wip) and (not @manual)", snippets = CAMELCASE)
public class RunCucumberTest {
}