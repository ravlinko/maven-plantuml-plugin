package com.github.ravlinko.plantuml.mvn;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name="PlantUML")
public class PlantUMLMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException {
        getLog().info( "Hello, world." );
    }
}