package com.github.ravlinko.plantuml.mvn;


import com.github.ravlinko.plantuml.mvn.test.extensions.MojoExtension;
import com.github.ravlinko.plantuml.mvn.test.extensions.TestMojo;
import org.apache.maven.plugin.Mojo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MojoExtension.class)
class PlantUMLMojoIntegrationTest {
    @TestMojo(pom = "src/test/resources/unit/basic-test/basic-test-plugin-config.xml")
    Mojo mojo;

    @Test
    void testMojoGoal() throws Exception {
        assertNotNull(mojo);
        mojo.execute();
    }
}
