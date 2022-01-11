@wip
Feature: Build PlantUML sources to images

    Background:
        Given the "sequence-diagram.puml" file in the "src/main/plantuml/sequences/" folder
        And the file contains code
            """
            @startuml
            Alice -> Bob: Authentication Request
            Bob --> Alice: Authentication Response

            Alice -> Bob: Another authentication Request
            Alice <-- Bob: Another authentication Response
            @enduml
            """

    Rule: Conventions over configuration

        Background:
            Given plugin without configuration
            When the "build" goal is run

        Example: file location conventions
            Then a "png" image is generated

        Example: image name convention
            Then a name of the image is "sequence-diagram.png"

        Example: image location convention
            Then the image is in the "src/main/plantuml/sequences" folder

    Rule: Adjustable plugin

        Scenario Outline: configure output image format
            Given "outputFormat" configuration <outputFormat>
            When the "build" goal is run
            Then a "jpeg" image is generated

            Examples:
                | outputFormat  |
                | jpeg          |
                | cvg           |
            