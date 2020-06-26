/**
 * The MIT License (MIT)
 *
 * MSUSEL Java Parser
 * Copyright (c) 2015-2019 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory and Idaho State University, Informatics and
 * Computer Science, Empirical Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.datamodel.parsers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.montana.gsoc.msusel.datamodel.parsers.ArtifactIdentifier;
import edu.isu.isuese.datamodel.*;
import edu.isu.isuese.datamodel.Module;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
public class JavaArtifactIdentifier implements ArtifactIdentifier {

    private final List<String> buildFileTypes = ImmutableList.of("pom.xml", "build.gradle");
    private Project project;
    private Logger log;
    @Getter
    private List<File> files = Lists.newArrayList();
    private int binCount = 0;

    public JavaArtifactIdentifier(Logger log) {
        this.log = log;
    }

    @Override
    public void identify(String root) {
        Path rootPath = Paths.get(root);
        this.log.atInfo().log("Root Path: " + rootPath);
        this.log.atInfo().log("Absolute Path: " + rootPath.toAbsolutePath());
        try {
            Files.walkFileTree(rootPath.toAbsolutePath(), new JavaFileVisitor());
        } catch (IOException e) {
            log.atWarn().log("Could not walk the file tree");
        }
        this.log.atInfo().log("Binary File Count: " + binCount);
        this.log.atInfo().log("File Found: " + files.size());
    }

    @Override
    public void setProjectPaths() {
        this.log.atInfo().log("Setting Project Paths");
        files = project.getFilesByType(FileType.BINARY);
        project.setSrcPath(setPaths(FileType.SOURCE).toArray(new String[0]));
        this.log.atInfo().log("Source Path: " + project.getSrcPath());
        project.setTestPath(setPaths(FileType.TEST).toArray(new String[0]));
        this.log.atInfo().log("Test Path: " + project.getTestPath());
        project.setBinPath(setPaths(FileType.BINARY).toArray(new String[0]));
        this.log.atInfo().log("Binary Path: " + project.getBinaryPath());
        this.log.atInfo().log("Binary File Count: " + binCount);
    }

    private List<String> setPaths(FileType type) {
        List<File> files = project.getFilesByType(type);
        FileTree tree = new FileTree();
        for (File f : files) {
            String path = f.getName().replace(project.getFullPath(), "");
            log.atInfo().log("Added Path: " + path);
            tree.addPath(path);
        }
        Set<String> rootNamespaces = Sets.newHashSet();
        for (Namespace ns : project.getNamespaces())
            rootNamespaces.add(ns.getName());
        List<String> srcPaths = Lists.newArrayList(tree.findPaths(rootNamespaces));

        for (File f : files) {
            String relPath = f.getName().replace(project.getFullPath(), "");
            int index;
            for (String p : srcPaths) {
                if (relPath.startsWith(p)) {
                    index = srcPaths.indexOf(p);
                    relPath = relPath.replace(p, "");
                    f.setPathIndex(index);
                }
            }
        }
        return srcPaths;
    }

    @Override
    public void setProj(Project proj) {
        this.project = proj;
    }

    private class JavaFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            FileType type = null;
            if (file.toString().endsWith(".java")) {
                type = FileType.SOURCE;
//                if (file.toString().contains("test"))
//                    type = FileType.TEST;
            } else if (file.getFileName().toString().endsWith(".class")) {
                type = FileType.BINARY;
                binCount++;
            } else if (buildFileTypes.contains(file.getFileName().toString())) {
                type = FileType.BUILD;
            } else if (file.endsWith(".jar") || file.endsWith(".war") || file.endsWith(".ear")) {
                type = FileType.MODULE;
            } else if (file.getFileName().toString().equalsIgnoreCase("readme.md") ||
                    file.getFileName().toString().startsWith("LICENSE") ||
                    file.getFileName().toString().startsWith("CHANGELOG")) {
                type = FileType.DOC;
            }
            if (type != null) {
                File f = File.builder()
                        .fileKey(file.toString())
                        .name(file.toString())
                        /*.language("Java")*/
                        .type(type)
                        .create();
                files.add(f);
                project.addFile(f);
                log.atInfo().log("Identified " + type + " file: " + file.getFileName());
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            log.atInfo().log("Checking Path: " + dir.toString());

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (dir.getFileName().toString().equals(".gradle") || dir.getFileName().toString().equals(".git") ||
                    dir.getFileName().toString().equals(".m2") || dir.getFileName().toString().equals(".mvn") ||
                    dir.getFileName().toString().equals(".idea") || dir.getFileName().toString().equals("gradle"))
                return FileVisitResult.SKIP_SUBTREE;

            return FileVisitResult.CONTINUE;
        }
    }
}
