package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.ArtifactIdentifier;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@Slf4j
public class JavaArtifactIdentifier implements ArtifactIdentifier {

    @Getter
    @Singular
    private List<Path> buildFiles = Lists.newArrayList();
    @Getter
    @Singular
    private List<Path> sourceFiles = Lists.newArrayList();
    @Getter
    @Singular
    private List<Path> binaryFiles = Lists.newArrayList();
    @Getter
    @Singular
    private List<Path> moduleFiles = Lists.newArrayList();

    private List<String> knownFileTypes = Lists.newArrayList("java", "class", "jar");
    private List<String> buildFileTypes = Lists.newArrayList("pom.xml", "build.gradle");

    @Override
    public void identify(String root) {
        Path rootPath = Paths.get(root);

        try {
            Files.walkFileTree(rootPath, new JavaFileVisitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class JavaFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.toString().endsWith(".java")) {
                sourceFiles.add(file);
            } else if (file.toString().endsWith(".class")) {
                binaryFiles.add(file);
            } else if (file.getFileName().toString().equals("pom.xml") || file.getFileName().toString().equals("build.gradle")) {
                buildFiles.add(file);
            } else if (file.endsWith(".jar") || file.endsWith(".war") || file.endsWith(".ear")) {
                moduleFiles.add(file);
            }

            return FileVisitResult.CONTINUE;
        }
    }

    public static void main(String[] args) {
        JavaArtifactIdentifier jai = new JavaArtifactIdentifier();
        jai.identify("/home/git/msusel/msusel-codetree/data/java-test-project");

        System.out.println("Found the following Source files: ");
        jai.getSourceFiles().forEach(path -> System.out.printf("\t%s\n", path.getFileName().toString()));
    }
}
