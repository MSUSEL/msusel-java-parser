/**
 * The MIT License (MIT)
 *
 * MSUSEL Java Parser
 * Copyright (c) 2015-2018 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
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
package edu.montana.gsoc.msusel.codetree.parsers;

import edu.montana.gsoc.msusel.codetree.ArtifactIdentifier;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * @author Isaac Griffith
 * @version 1.2.0
 */
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

    /**
     * {@inheritDoc}
     */
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

        /**
         * {@inheritDoc}
         */
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
}
