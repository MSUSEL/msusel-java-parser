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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import edu.isu.isuese.ArtifactIdentifier;
import edu.isu.isuese.datamodel.File;
import edu.isu.isuese.datamodel.FileType;
import edu.isu.isuese.datamodel.Project;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Slf4j
public class JavaArtifactIdentifier implements ArtifactIdentifier {

    private List<String> buildFileTypes = Lists.newArrayList("pom.xml", "build.gradle");
    private Project project;

    @Override
    public void identify(String root) {
        Path rootPath = Paths.get(root);

        try {
            Files.walkFileTree(rootPath, new JavaFileVisitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            } else if (file.toString().endsWith(".class")) {
                type = FileType.BINARY;
            } else if (buildFileTypes.contains(file.getFileName().toString())) {
                type = FileType.BUILD;
            } else if (file.endsWith(".jar") || file.endsWith(".war") || file.endsWith(".ear")) {
                type = FileType.MODULE;
            }
            if (type != null) {
                File f = File.builder().fileKey(file.toString()).name(file.toString())/*.language("Java")*/.type(type).create();
                project.addFile(f);
            }


            return FileVisitResult.CONTINUE;
        }
    }
}
