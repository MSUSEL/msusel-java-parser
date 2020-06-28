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
package edu.montana.gsoc.msusel.datamodel.parsers

import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import edu.isu.isuese.datamodel.File
import edu.isu.isuese.datamodel.FileType
import edu.isu.isuese.datamodel.Project
import edu.isu.isuese.datamodel.util.DBCredentials
import edu.isu.isuese.datamodel.util.DBManager
import org.apache.logging.log4j.Logger

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class JavaArtifactIdentifier implements ArtifactIdentifier {

    final List<String> buildFileTypes = ImmutableList.of("pom.xml", "build.gradle")
    Project project
    Logger log

    List<File> files = Lists.newArrayList()
    int binCount = 0
    DBCredentials credentials

    JavaArtifactIdentifier(Logger log, DBCredentials credentials) {
        this.log = log
        this.credentials = credentials
    }

    @Override
    void identify(String root) {
        Path rootPath = Paths.get(root)
        this.log.atInfo().log("Root Path: " + rootPath)
        this.log.atInfo().log("Absolute Path: " + rootPath.toAbsolutePath())
        try {
            Files.walkFileTree(rootPath.toAbsolutePath(), new JavaFileVisitor())
        } catch (IOException e) {
            log.atWarn().log("Could not walk the file tree")
        }
        this.log.atInfo().log("Binary File Count: " + binCount)
        this.log.atInfo().log("File Found: " + files.size())
    }

    @Override
    void setProj(Project proj) {
        this.project = proj
    }

    private class JavaFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            FileType type = null
            if (file.toString().endsWith(".java")) {
                type = FileType.SOURCE
//                if (file.toString().contains("test"))
//                    type = FileType.TEST
            } else if (file.getFileName().toString().endsWith(".class")) {
                type = FileType.BINARY
                binCount++
            } else if (buildFileTypes.contains(file.getFileName().toString())) {
                type = FileType.BUILD
            } else if (file.endsWith(".jar") || file.endsWith(".war") || file.endsWith(".ear")) {
                type = FileType.MODULE
            } else if (file.getFileName().toString().equalsIgnoreCase("readme.md") ||
                    file.getFileName().toString().startsWith("LICENSE") ||
                    file.getFileName().toString().startsWith("CHANGELOG")) {
                type = FileType.DOC
            }
            if (type != null) {
                DBManager.instance.open(credentials)
                File f = File.builder()
                        .fileKey(file.toString())
                        .name(file.toString())
                        /*.language("Java")*/
                        .type(type)
                        .create()
                files.add(f)
                project.addFile(f)
                log.atInfo().log("Identified " + type + " file: " + file.getFileName())
                DBManager.instance.close()
            }

            return FileVisitResult.CONTINUE
        }

        @Override
        FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            log.atInfo().log("Checking Path: " + dir.toString())

            return FileVisitResult.CONTINUE
        }

        @Override
        FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (dir.getFileName().toString().equals(".gradle") || dir.getFileName().toString().equals(".git") ||
                    dir.getFileName().toString().equals(".m2") || dir.getFileName().toString().equals(".mvn") ||
                    dir.getFileName().toString().equals(".idea") || dir.getFileName().toString().equals("gradle"))
                return FileVisitResult.SKIP_SUBTREE

            return FileVisitResult.CONTINUE
        }
    }
}
