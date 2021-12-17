package com.github.ravlinko.plantuml.mvn.test.extensions;

import com.github.ravlinko.plantuml.mvn.test.MojoTestCaseWrapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Field;
import java.util.List;

public class MojoExtension extends MojoTestCaseWrapper implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        List<Field> mojoFields = AnnotationSupport.findAnnotatedFields(testInstance.getClass(), TestMojo.class);
        for (Field mojoField : mojoFields) {
            TestMojo testMojoAnnotation = mojoField.getAnnotation(TestMojo.class);
            try {
                mojoField.setAccessible(true);
                mojoField.set(testInstance, lookupTestMojo(testMojoAnnotation.pom(), testMojoAnnotation.goal()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
