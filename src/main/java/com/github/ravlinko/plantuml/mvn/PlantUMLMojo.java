package com.github.ravlinko.plantuml.mvn;

import com.github.ravlinko.plantuml.mvn.exceptions.DirectoryCanNotBeCreatedException;
import com.github.ravlinko.plantuml.mvn.exceptions.IsNotValidPathException;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.Option;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.preproc.Defines;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Mojo(name = "build")
public class PlantUMLMojo extends AbstractMojo {
    private final Option option = new Option();

    /**
     * Fileset to search plantuml diagrams in.
     * <p>
     * default-value="${basedir}/src/main/plantuml&#47;**&#47;*.puml"
     */
    @Parameter(property = "plantuml.sourceFiles", required = true)
    private FileSet sourceFiles;

    /**
     * Directory where images are generated.
     * <p>
     * default-value="${basedir}/target/plantuml"
     */
    @Parameter(property = "plantuml.outputDirectory", required = true)
    private File outputDirectory;

    /**
     * Whether or not to generate images in same directory as the source file.
     * This is useful for using PlantUML diagrams in Javadoc,
     * as described here:
     * <a href="http://plantuml.sourceforge.net/javadoc.html">http://plantuml.sourceforge.net/javadoc.html</a>.
     * <p>
     * If this is set to true then outputDirectory is ignored.
     */
    @Parameter(property = "plantuml.outputInSourceDirectory", defaultValue = "false")
    private boolean outputInSourceDirectory;

    /**
     * Charset used during generation.
     */
    @Parameter(property = "plantuml.charset")
    private String charset;

    /**
     * External configuration file location.
     */
    @Parameter(property = "plantuml.config")
    private String config;

    /**
     * Specify output format.
     *
     * @enum {net.sourceforge.plantuml.FileFormat}
     */
    @Parameter(property = "plantuml.format", defaultValue = "PNG")
    private FileFormat format;

    /**
     * Fully qualified path to Graphviz home directory.
     */
    @Parameter(property = "plantuml.graphvizDot")
    private String graphvizDot;

    /**
     * Wether or not to output details during generation.
     */
    @Parameter(property = "plantuml.verbose", defaultValue = "false")
    private boolean verbose;

    /**
     * Specify to include metadata in the output files.
     */
    @Parameter(property = "plantuml.withMetadata", defaultValue = "false")
    private boolean withMetadata;

    /**
     * Specify to overwrite any output file, also if the target file is newer as the input file.
     */
    @Parameter(property = "plantuml.overwrite", defaultValue = "false")
    private boolean overwrite;

    private void validateSourceDirectory(FileSet sourceFiles) throws IsNotValidPathException {
        if (Objects.isNull(sourceFiles.getDirectory()) || sourceFiles.getDirectory().isEmpty()) {
            getLog().warn("Source files directory is not valid");
            throw new IsNotValidPathException(sourceFiles.getDirectory());
        }
    }

    private File getDirectory(String path) {
        File directory = null;
        try {
            directory = new File(path);
        } catch (Exception e) {
            getLog().debug(e);
        }
        if (isExistedDirectory(directory)) {
            return directory;
        } else {
            getLog().warn("Source files directory is not valid");
            throw new IsNotValidPathException(sourceFiles.getDirectory());
        }
    }

    private boolean isExistedDirectory(File directory) {
        return Objects.nonNull(directory) && directory.exists() && directory.isDirectory();
    }

    private void populateOption() throws IOException {
        if (!outputInSourceDirectory) {
            option.setOutputDir(outputDirectory);
        }
        if (Objects.nonNull(charset)) {
            option.setCharset(charset);
        }
        if (Objects.nonNull(config)) {
            option.initConfig(config);
        }
        if (Objects.nonNull(graphvizDot)) {
            OptionFlags.getInstance().setDotExecutable(graphvizDot);
        }
        if (Objects.nonNull(format)) {
            option.setFileFormat(format);
        }
        OptionFlags.getInstance().setVerbose(verbose);
    }

    public void execute() throws MojoExecutionException {
        validateSourceDirectory(sourceFiles);
        File baseDir = getDirectory(sourceFiles.getDirectory());
        outputDirectory();

        try {
            populateOption();
            generateImagesFromPlantUmlFiles(baseDir, getPlantUMLFiles(baseDir));
        } catch (Exception e) {
            throw new MojoExecutionException("Exception during plantuml process", e);
        }
    }

    private void generateImagesFromPlantUmlFiles(File baseDir, List<File> files) throws IOException {
        for (final File file : files) {
            File outDir = getOutputDirectory(baseDir, file);
            this.option.setOutputDir(outDir);

            FileFormatOption fileFormatOption = getFileFormatOption();
            if (!overwrite) {
                String newName = fileFormatOption.getFileFormat().changeName(file.getName(), 0);
                File targetFile = new File(outDir, newName);
                if (isTargetNewer(file, targetFile)) {
                    getLog().debug("Skip file <" + file + "> because target <" + targetFile + "> is newer");
                    continue;
                }
            }

            getLog().info("Processing file <" + file + ">");
            generateImages(file, fileFormatOption);
        }
    }

    private void generateImages(File file, FileFormatOption fileFormatOption) throws IOException {
        final SourceFileReader sourceFileReader =
                new SourceFileReader(
                        new Defines(), file, option.getOutputDir(),
                        option.getConfig(), option.getCharset(),
                        fileFormatOption);
        for (final GeneratedImage image : sourceFileReader.getGeneratedImages()) {
            getLog().debug(image + " " + image.getDescription());
        }
    }

    private boolean isTargetNewer(File file, File targetFile) {
        return targetFile.exists() && targetFile.lastModified() > file.lastModified();
    }

    private File getOutputDirectory(File baseDir, File plantUMLFile) {
        if (outputInSourceDirectory) {
            return plantUMLFile.getParentFile();
        } else {
            return outputDirectory.toPath().resolve(
                    baseDir.toPath().relativize(plantUMLFile.toPath().getParent())).toFile();
        }
    }

    private List<File> getPlantUMLFiles(File baseDir) throws IOException {
        return FileUtils.getFiles(
                baseDir,
                String.join(",", sourceFiles.getIncludes()),
                String.join(",", sourceFiles.getExcludes())
        );
    }

    private void outputDirectory() {
        if (!this.outputInSourceDirectory) {
            getOrCreateDirectory();
            if (!outputDirectory.isDirectory()) {
                throw new IllegalArgumentException("<" + outputDirectory + "> is not a valid directory.");
            }
        }
    }

    private void getOrCreateDirectory() {
        if (outputDirectory.exists()) {
            getLog().info(outputDirectory.getName() + " will be used.");
        } else {
            if (outputDirectory.mkdirs()) {
                getLog().info("Created: " + outputDirectory.getName());
            } else {
                throw new DirectoryCanNotBeCreatedException("Can't create directories: " + outputDirectory.getName());
            }
        }
    }

    private FileFormatOption getFileFormatOption() {
        FileFormatOption formatOptions = new FileFormatOption(option.getFileFormat(), withMetadata);
        if (formatOptions.isWithMetadata() != withMetadata) {
            // Workarround to error in plantUML where the withMetadata flag is not correctly applied.
            return new FileFormatOption(option.getFileFormat());
        }
        return formatOptions;
    }
}