Feature: convert plantuml into image

  Scenario: Default configuration
    Given Apache Maven Project Object Model (POM) file at 'src/test/resources/unit/basic-test/basic-test-plugin-config.xml'
    And target goal is 'build'
    When execute the goal for pom