package com.github.ravlinko.plantuml.mvn;

import net.sourceforge.plantuml.*;
import net.sourceforge.plantuml.preproc.Defines;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

@Mojo(name = "build")
public class PlantUMLMojo extends AbstractMojo {
    private final Option option = new Option();

    /**
     * Fileset to search plantuml diagrams in.
     *
     * @parameter property="plantuml.sourceFiles" default-value="${basedir}/src/main/plantuml&#47;**&#47;*.puml"
     * @required
     */
    @Parameter(property = "plantuml.sourceFiles")
    private FileSet sourceFiles;

    /**
     * Directory where images are generated.
     *
     * @parameter property="plantuml.outputDirectory" default-value="${basedir}/target/plantuml"
     * @required
     */
    @Parameter(property = "plantuml.outputDirectory")
    private File outputDirectory;

    /**
     * Whether or not to generate images in same directory as the source file.
     * This is useful for using PlantUML diagrams in Javadoc,
     * as described here:
     * <a href="http://plantuml.sourceforge.net/javadoc.html">http://plantuml.sourceforge.net/javadoc.html</a>.
     * <p>
     * If this is set to true then outputDirectory is ignored.
     *
     * @parameter property="plantuml.outputInSourceDirectory" default-value="false"
     */
    @Parameter(property = "plantuml.outputInSourceDirectory", defaultValue = "false")
    private boolean outputInSourceDirectory;

    /**
     * Charset used during generation.
     *
     * @parameter property="plantuml.charset"
     */
    @Parameter(property = "plantuml.charset")
    private String charset;

    /**
     * External configuration file location.
     *
     * @parameter property="plantuml.config"
     */
    @Parameter(property = "plantuml.config")
    private String config;

    /**
     * Specify output format.
     *
     * @enum {net.sourceforge.plantuml.FileFormat}
     * @parameter property="plantuml.format"
     */
    @Parameter(property = "plantuml.format", defaultValue = "PNG")
    private FileFormat format;

    /**
     * Fully qualified path to Graphviz home directory.
     *
     * @parameter property="plantuml.graphvizDot"
     */
    @Parameter(property = "plantuml.graphvizDot")
    private String graphvizDot;

    /**
     * Wether or not to output details during generation.
     *
     * @parameter property="plantuml.verbose" default-value="false"
     */
    @Parameter(property = "plantuml.verbose", defaultValue = "false")
    private boolean verbose;

    /**
     * Specify to include metadata in the output files.
     *
     * @parameter property="plantuml.withMetadata"
     */
    @Parameter(property = "plantuml.withMetadata", defaultValue = "false")
    private boolean withMetadata;

    /**
     * Specify to overwrite any output file, also if the target file is newer as the input file.
     *
     * @parameter property="plantuml.overwrite"
     */
    @Parameter(property = "plantuml.overwrite", defaultValue = "false")
    private boolean overwrite;

    public void execute() throws MojoExecutionException {
        final String invalidSourceFilesDirectoryWarnMsg = this.sourceFiles.getDirectory() + " is not a valid path";
        if (null == this.sourceFiles.getDirectory() || this.sourceFiles.getDirectory().isEmpty()) {
            getLog().warn(invalidSourceFilesDirectoryWarnMsg);
            return;
        }
        File baseDir = null;
        try {
            baseDir = new File(this.sourceFiles.getDirectory());
        } catch (Exception e) {
            getLog().debug(invalidSourceFilesDirectoryWarnMsg, e);
        }
        if (null == baseDir || !baseDir.exists() || !baseDir.isDirectory()) {
            getLog().warn(invalidSourceFilesDirectoryWarnMsg);
            return;
        }
        if (!this.outputInSourceDirectory) {
            if (!this.outputDirectory.exists()) {
                // If output directoy does not exist yet create it.
                this.outputDirectory.mkdirs();
            }
            if (!this.outputDirectory.isDirectory()) {
                throw new IllegalArgumentException("<" + this.outputDirectory + "> is not a valid directory.");
            }
        }

        try {
            if (!this.outputInSourceDirectory) {
                this.option.setOutputDir(this.outputDirectory);
            }
            if (this.charset != null) {
                this.option.setCharset(this.charset);
            }
            if (this.config != null) {
                this.option.initConfig(this.config);
            }
            if (this.graphvizDot != null) {
                OptionFlags.getInstance().setDotExecutable(this.graphvizDot);
            }
            if (this.format != null) {
                option.setFileFormat(this.format);
            }
            if (this.verbose) {
                OptionFlags.getInstance().setVerbose(true);
            }

            final List<File> files = FileUtils.getFiles(
                    baseDir,
                    getCommaSeparatedList(this.sourceFiles.getIncludes()),
                    getCommaSeparatedList(this.sourceFiles.getExcludes())
            );
            for (final File file : files) {
                File outDir;
                if (this.outputInSourceDirectory) {
                    outDir = file.getParentFile();
                } else {
                    outDir = outputDirectory.toPath().resolve(
                            baseDir.toPath().relativize(file.toPath().getParent())).toFile();
                }
                this.option.setOutputDir(outDir);

                FileFormatOption fileFormatOption = getFileFormatOption();
                if (!overwrite) {
                    String newName = fileFormatOption.getFileFormat().changeName(file.getName(), 0);
                    File targetFile = new File(outDir, newName);
                    if (targetFile.exists() && targetFile.lastModified() > file.lastModified()) {
                        getLog().debug("Skip file <" + file + "> because target <" + targetFile + "> is newer");
                        continue;
                    }
                }

                getLog().info("Processing file <" + file + ">");
                final SourceFileReader sourceFileReader =
                        new SourceFileReader(
                                new Defines(), file, this.option.getOutputDir(),
                                this.option.getConfig(), this.option.getCharset(),
                                fileFormatOption);
                for (final GeneratedImage image : sourceFileReader.getGeneratedImages()) {
                    getLog().debug(image + " " + image.getDescription());
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Exception during plantuml process", e);
        }
    }

    protected String getCommaSeparatedList(final List<String> list) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            final Object object = it.next();
            builder.append(object.toString());
            if (it.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private FileFormatOption getFileFormatOption() {
        FileFormatOption formatOptions = new FileFormatOption(this.option.getFileFormat(), this.withMetadata);
        if (formatOptions.isWithMetadata() != withMetadata) {
            // Workarround to error in plantUML where the withMetadata flag is not correctly applied.
            return new FileFormatOption(this.option.getFileFormat());
        }
        return formatOptions;
    }

}