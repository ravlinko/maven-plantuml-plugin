package com.github.ravlinko.plantuml.mvn;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.Option;
import net.sourceforge.plantuml.SourceFileReader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Mojo(name="build")
public class PlantUMLMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException {
        getLog().info( "Hello, world." );
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("test-sequence-diagram.puml");
        String path = url.getPath();
        File file = new File(path);

        Option option = new Option();
        try {
            SourceFileReader sourceFileReader = new SourceFileReader(option.getDefaultDefines(), file,
                    null, option.getConfig(), option.getCharset(),
                    new FileFormatOption(FileFormat.PNG));
            sourceFileReader.getGeneratedImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}