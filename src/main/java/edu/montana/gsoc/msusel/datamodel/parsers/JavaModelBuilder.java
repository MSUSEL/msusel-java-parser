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
package edu.montana.gsoc.msusel.datamodel.parsers;

import edu.montana.gsoc.msusel.datamodel.BaseModelBuilder;
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8Lexer;
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8Parser;
import edu.montana.gsoc.msusel.datamodel.structural.File;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Using the parser, this class incrementally builds a DataModelMediator one file at a
 * time.
 *
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Slf4j
public class JavaModelBuilder extends BaseModelBuilder {

    public JavaModelBuilder() {
        super(new JavaArtifactIdentifier());
    }

    @Override
    public void gatherTypes(String file) {
        setFile(File.builder().fileKey(file).name(file).create());

        utilizeParser(new JavaTypeExtractor(this));
    }

    @Override
    public void gatherRelationships() {
        utilizeParser(new JavaRelationshipExtractor(this));
    }

    @Override
    public void gatherTypeMembers() {
        utilizeParser(new JavaFieldExtractor(this));
        utilizeParser(new JavaMethodExtractor(this));
        utilizeParser(new JavaStatementExtractor(this));
        utilizeParser(new JavaUseRelationExtractor(this));
    }

    @Override
    public void utilizeParser(ParseTreeListener listener) {
        try {
            final JavaModelBuilder.JavaParserConstructor pt = new JavaModelBuilder.JavaParserConstructor();
            final Java8Parser parser = pt.loadFile(getFile().key());
            final Java8Parser.CompilationUnitContext cuContext = parser.compilationUnit();
            final ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, cuContext);
        } catch (final IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Constructs the Java parser needed to extract code model information
     *
     * @author Isaac Griffith
     * @version 1.3.0
     */
    private class JavaParserConstructor {

        /**
         * @param file
         * @return
         * @throws IOException
         */
        @NotNull
        private synchronized Java8Parser loadFile(final String file) throws IOException {
            final Java8Lexer lexer = new Java8Lexer(new ANTLRFileStream(file));
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            return new Java8Parser(tokens);
        }
    }

}