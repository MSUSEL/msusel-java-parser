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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.flogger.FluentLogger;
import com.google.inject.Inject;
import edu.isu.isuese.ArtifactIdentifier;
import edu.isu.isuese.datamodel.*;
import edu.isu.isuese.datamodel.Module;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
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

    public JavaArtifactIdentifier(Logger log) {
        this.log = log;
    }

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
    public void setProjectPaths() {
        Module mod = project.getModules().get(0);
        files = mod.getFilesByType(FileType.BINARY);
        mod.setSrcPath(setPaths(FileType.SOURCE).toArray(new String[0]));
        mod.setTestPath(setPaths(FileType.TEST).toArray(new String[0]));
        mod.setBinPath(setPaths(FileType.BINARY).toArray(new String[0]));
    }

    private List<String> setPaths(FileType type) {
        Module mod = project.getModules().get(0);
        List<File> files = mod.getFilesByType(type);
        FileTree tree = new FileTree();
        for (File f : files) {
            tree.addPath(f.getName().replace(mod.getFullPath(), ""));
        }
        Set<String> rootNamespaces = Sets.newHashSet();
        for (Namespace ns : mod.getNamespaces())
            rootNamespaces.add(ns.getName());
        List<String> srcPaths = Lists.newArrayList(tree.findPaths(rootNamespaces));

        for (File f : files) {
            String relPath = f.getName().replace(mod.getFullPath(), "");
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
                if (file.toString().contains("test"))
                    type = FileType.TEST;
            } else if (file.toString().endsWith(".class")) {
                type = FileType.BINARY;
            } else if (buildFileTypes.contains(file.getFileName().toString())) {
                type = FileType.BUILD;
                project.getModules().get(0).addBuildFile(file.getFileName().toString());
            } else if (file.endsWith(".jar") || file.endsWith(".war") || file.endsWith(".ear")) {
                type = FileType.MODULE;
            }
            if (type != null) {
                File f = File.builder()
                        .fileKey(file.toString())
                        .name(file.toString())
                        /*.language("Java")*/
                        .type(type)
                        .create();
                files.add(f);
            }


            return FileVisitResult.CONTINUE;
        }
    }
}
