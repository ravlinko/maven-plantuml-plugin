package com.github.ravlinko.plantuml.mvn;


import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PlantUMLMojoIntegrationTest {

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };


    @Test
    public void testMojoGoal() throws Exception {
        File pom = new File( "src/test/resources/unit/basic-test/basic-test-plugin-config.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        PlantUMLMojo myMojo = (PlantUMLMojo) rule.lookupMojo( "build", pom );
        assertNotNull( myMojo );
        myMojo.execute();

    }
}
