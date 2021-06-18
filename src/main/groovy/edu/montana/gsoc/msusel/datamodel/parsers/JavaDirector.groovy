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
import edu.isu.isuese.datamodel.util.DBCredentials
import edu.isu.isuese.datamodel.util.DBManager
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaLexer
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaParser
import groovy.util.logging.Log4j2
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
@Log4j2
class JavaDirector extends BaseDirector {

    JavaDirector(Project proj, DBCredentials credentials, boolean statements = false, boolean useSinglePass = true, boolean createCFG = false, boolean useExpressions = false) {
        super(proj, new JavaArtifactIdentifier(credentials), credentials, statements, useSinglePass, createCFG, useExpressions)
    }

    void gatherAllInfoAtOnce(File file, int current, int total) {
        builder = new JavaModelBuilder(proj, file, credentials)
        utilizeParser(file, new Java8SinglePassExtractor(builder, createCFG, useExpressions), current, total)
    }

    @Override
    void gatherFileAndTypeInfo(File file) {
        DBManager.instance.open(credentials)
        int stage = file.getParseStage()
        DBManager.instance.close()

        if (stage >= 1)
            return

        builder = new JavaModelBuilder(proj, file, credentials)
        utilizeParser(file, new Java8FileAndTypeExtractor(builder))

        DBManager.instance.open(credentials)
        file.setParseStage(1)
        DBManager.instance.close()
    }

    @Override
    void gatherMembersAndBasicRelationInfo(File file) {
        DBManager.instance.open(credentials)
        int stage = file.getParseStage()
        DBManager.instance.close()

        if (stage >= 2)
            return

        BaseModelBuilder builder = new JavaModelBuilder(proj, file, credentials)
        utilizeParser(file, new Java8MemberAndGenRealUseRelsExtractor(builder))

        DBManager.instance.open(credentials)
        file.setParseStage(2)
        DBManager.instance.close()
    }

    @Override
    void gatherMemberUsageInfo(File file) {
        DBManager.instance.open(credentials)
        int stage = file.getParseStage()
        DBManager.instance.close()

        if (stage >= 3)
            return

        BaseModelBuilder builder = new JavaModelBuilder(proj, file, credentials)
        utilizeParser(file, new Java8MemberUseExtractor(builder))

        DBManager.instance.open(credentials)
        file.setParseStage(3)
        DBManager.instance.close()
    }

    @Override
    void gatherStatementInfo(File file) {
        DBManager.instance.open(credentials)
        int stage = file.getParseStage()
        DBManager.instance.close()

        if (stage >= 4)
            return

        BaseModelBuilder builder = new JavaModelBuilder(proj, file, credentials)
        utilizeParser(file, new Java8StatementExtractor(builder))

        DBManager.instance.open(credentials)
        file.setParseStage(4)
        DBManager.instance.close()
    }

    @Override
    boolean includeFile(File file) {
        file.getName().endsWith(".java")
    }

    void utilizeParser(File file, ParseTreeListener listener, int current = 0, int total = 0) {
        DBManager.instance.open(credentials)
//        String path = file.getFullPath()
        String path = file.getName()
        DBManager.instance.close()

        log.info "Parsing ($current / $total)... $path"
        try {
            final JavaParserConstructor pt = new JavaParserConstructor()
            final JavaParser parser = pt.loadFile(path)
            final JavaParser.CompilationUnitContext cuContext = parser.compilationUnit()
            final ParseTreeWalker walker = new ParseTreeWalker()
            walker.walk(listener, cuContext)
        } catch (final IOException e) {
            log.atError().withThrowable(e).log(e.getMessage())
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
