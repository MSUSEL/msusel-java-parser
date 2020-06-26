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


import edu.isu.isuese.datamodel.File
import edu.isu.isuese.datamodel.Project
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaLexer
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeListener
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.apache.logging.log4j.Logger
import org.jetbrains.annotations.NotNull

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class JavaDirector extends BaseDirector {

    JavaDirector(Project proj, Logger logger, boolean statements = false) {
        super(proj, new JavaArtifactIdentifier(logger), logger, statements)
    }

    @Override
    void gatherFileAndTypeInfo(File file) {
        BaseModelBuilder builder = new JavaModelBuilder(proj, file)
        utilizeParser(file, new Java8FileAndTypeExtractor(builder))
    }

    @Override
    void gatherMembersAndBasicRelationInfo(File file) {
        BaseModelBuilder builder = new JavaModelBuilder(proj, file)
        utilizeParser(file, new Java8MemberAndGenRealUseRelsExtractor(builder))
    }

    @Override
    void gatherMemberUsageInfo(File file) {
        BaseModelBuilder builder = new JavaModelBuilder(proj, file)
        utilizeParser(file, new Java8MemberUseExtractor(builder))
    }

    @Override
    void gatherStatementInfo(File file) {
        BaseModelBuilder builder = new JavaModelBuilder(proj, file)
        utilizeParser(file, new Java8StatementExtractor(builder))
    }

    @Override
    boolean includeFile(File file) {
        file.getName().endsWith(".java") && !file.getName().toLowerCase().contains("test")
    }

    void utilizeParser(File file, ParseTreeListener listener) {
        try {
            final JavaParserConstructor pt = new JavaParserConstructor()
            final JavaParser parser = pt.loadFile(file.getName())
            final JavaParser.CompilationUnitContext cuContext = parser.compilationUnit()
            final ParseTreeWalker walker = new ParseTreeWalker()
            walker.walk(listener, cuContext)
        } catch (final IOException e) {
            logger.atError().withThrowable(e).log(e.getMessage())
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
        private synchronized JavaParser loadFile(final String file) throws IOException {
            final JavaLexer lexer = new JavaLexer(CharStreams.fromFileName(file))
            final CommonTokenStream tokens = new CommonTokenStream(lexer)
            return new JavaParser(tokens)
        }
    }
}
