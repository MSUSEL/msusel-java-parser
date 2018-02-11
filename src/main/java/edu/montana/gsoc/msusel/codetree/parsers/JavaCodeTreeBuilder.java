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

import codetree.BaseCodeTreeBuilder;
import codetree.node.structural.FileNode;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Lexer;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Using the parser, this class incrementally builds a CodeTree one file at a
 * time.
 *
 * @author Isaac Griffith
 * @version 1.2.0
 */
@Slf4j
public class JavaCodeTreeBuilder extends BaseCodeTreeBuilder {

    public JavaCodeTreeBuilder() {
        super(new JavaArtifactIdentifier());
    }

    @Override
    public void gatherTypes(String file) {
        setFile(FileNode.builder().key(file).create());
        tree.getProject().addChild(getFile());

        utilizeParser(new JavaTypeExtractor(this));
    }

    public void gatherRelationships() {
        utilizeParser(new JavaRelationshipExtractor(this));
    }

    public void addComponentsToTypes() {
        utilizeParser(new JavaFieldExtractor(this));
        utilizeParser(new JavaMethodExtractor(this));
    }

    public void utilizeParser(ParseTreeListener listener) {
        try {
            final JavaCodeTreeBuilder.JavaParserConstructor pt = new JavaCodeTreeBuilder.JavaParserConstructor();
            final Java8Parser parser = pt.loadFile(getFile().getKey());
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
     * @version 1.2.0
     */
    private class JavaParserConstructor {

        /**
         * @param file
         * @return
         * @throws IOException
         */
        @org.jetbrains.annotations.NotNull
        private synchronized Java8Parser loadFile(final String file) throws IOException {
            final Java8Lexer lexer = new Java8Lexer(new ANTLRFileStream(file));
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            return new Java8Parser(tokens);
        }
    }

}