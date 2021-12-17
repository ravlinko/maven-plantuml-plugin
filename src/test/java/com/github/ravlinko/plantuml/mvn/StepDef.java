package com.github.ravlinko.plantuml.mvn;

import com.github.ravlinko.plantuml.mvn.test.MojoTestCaseWrapper;
import io.cucumber.java.en.Given;
import org.apache.maven.plugin.MojoExecutionException;


public final class StepDef extends MojoTestCaseWrapper {
    PlantUMLMojo plantUMLMojo;

    @Given("hello")
    public void hello() throws MojoExecutionException {
        try {
            plantUMLMojo = (PlantUMLMojo) lookupTestMojo("src/test/resources/unit/basic-test/basic-test-plugin-config.xml", "build");
        } catch (Exception e) {
            e.printStackTrace();
        }
        plantUMLMojo.execute();
    }
}
