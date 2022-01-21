package com.github.ravlinko.plantuml.mvn;

import com.github.ravlinko.plantuml.mvn.test.MojoTestCaseWrapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.maven.plugin.MojoExecutionException;


public final class StepDef extends MojoTestCaseWrapper {
    PlantUMLMojo plantUMLMojo;

    private String pomFilePath;
    private String mavenGoal;

    @Given("Apache Maven Project Object Model \\(POM) file at {string}")
    public void apache_maven_pom_file_at(String pathToPom) {
        this.pomFilePath = pathToPom;
    }

    @Given("target goal is {string}")
    public void target_goal_is(String goal) {
        this.mavenGoal = goal;
    }

    @When("execute the goal for pom")
    public void execute_the_goal_for_pom() throws MojoExecutionException {
        try {
            plantUMLMojo = (PlantUMLMojo) lookupTestMojo(pomFilePath, mavenGoal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        plantUMLMojo.execute();
    }
}
