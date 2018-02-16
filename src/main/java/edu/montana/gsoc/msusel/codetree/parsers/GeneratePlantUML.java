/**
 * The MIT License (MIT)
 *
 * MSUSEL Java Parser
 * Copyright (c) 2015-2017 Montana State University, Gianforte School of Computing,
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

import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.structural.ProjectNode;
import edu.montana.gsoc.msusel.codetree.node.structural.FileNode;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8BaseListener;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Lexer;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class GeneratePlantUML {

    public static void main(String args[]) {
        GeneratePlantUML generator = new GeneratePlantUML();

        generator.execute("Test");
    }

    public void execute(String projectKey) {
        JavaArtifactIdentifier jai = new JavaArtifactIdentifier();
        ProjectNode root = ProjectNode.builder().key(projectKey).create();
        CodeTree tree = new CodeTree();
        tree.setProject(root);

        jai.identify("/home/git/msusel/msusel-java-parser/data/java-test-project/CFG/");
        final List<Path> files = jai.getSourceFiles();
        parseStructure(files, tree);

        System.out.println(tree.generatePlantUML());
    }

    /**
     * Parses the code structure from the provided set of input files, adding
     * this structure to the root project provided.
     *
     * @param files Set of input files to be parsed
     * @param tree  the code tree
     */
    private void parseStructure(final List<Path> files, CodeTree tree) {
        System.out.println("Number of files to process: " + files.size());
        files.forEach((file) -> {
            try {
                FileNode node = gatherTypes(file.toAbsolutePath().toString(), file.toAbsolutePath().toString(), tree);
            } catch (final RecognitionException e) {
                log.warn(e.getMessage(), e);
            }
        });

        files.forEach((file) -> {
            gatherRelationships(file.toAbsolutePath().toString(), tree);
            addComponentsToTypes(file.toAbsolutePath().toString(), tree);
        });
    }

    public FileNode gatherTypes(String key, final String file, CodeTree tree) {
        // TODO Make this code specific to subclasses
        final FileNode node = FileNode.builder().key(key).create();
        int length = 0;
        try {
            length = Files.readAllLines(Paths.get(file)).size();
        } catch (IOException e) {
            // TODO Log this
        }
//            node.setEnd(length);
//            node.setStart(1);
        tree.getProject().addChild(node);

        //utilizeParser(file, new JavaTypeExtractor(this));

        return node;
    }

    public void gatherRelationships(String file, CodeTree tree) {
        FileNode node = tree.getUtils().getFile(file);
        //utilizeParser(file, new JavaRelationshipExtractor(this));
    }

    public void addComponentsToTypes(String file, CodeTree tree) {
        FileNode node = tree.getUtils().getFile(file);
//        utilizeParser(file, new JavaFieldExtractor(tree, node));
//        utilizeParser(file, new JavaMethodExtractor(tree, node));
//        utilizeParser(file, new JavaStatementExtractor(tree, node));
    }

    public void utilizeParser(String file, Java8BaseListener listener) {
        try {
            final JavaParserConstructor pt = new JavaParserConstructor();
            final Java8Parser parser = pt.loadFile(file);
            final Java8Parser.CompilationUnitContext cuContext = parser.compilationUnit();
            final ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, cuContext);
        } catch (final IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Constructs the Java parser needed to extract code tree information
     *
     * @author Isaac Griffith
     * @version 1.1.1
     */
    private class JavaParserConstructor {

        /**
         * @param file
         * @return
         * @throws IOException
         */
        private synchronized Java8Parser loadFile(final String file) throws IOException {
            final Java8Lexer lexer = new Java8Lexer(new ANTLRFileStream(file));
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            return new Java8Parser(tokens);
        }
    }
}
