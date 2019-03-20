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

import edu.isu.isuese.BaseModelBuilder;
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8Parser;

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
public class JavaFieldExtractor extends JavaMemberExtractor {

    public JavaFieldExtractor(BaseModelBuilder builder) {
        super(builder);
    }

    // TODO handle interface constants
    // TODO handle enum literals

    @Override
    public void enterFieldDeclaration(final Java8Parser.FieldDeclarationContext ctx) {
        if (ctx.variableDeclaratorList() != null) {
            for (final Java8Parser.VariableDeclaratorContext decl : ctx.variableDeclaratorList().variableDeclarator()) {
                treeBuilder.createFieldNode(decl.variableDeclaratorId().Identifier().getText(), decl.getStart().getLine(), decl.getStop().getLine());
            }
        }

        super.enterFieldDeclaration(ctx);
    }

    @Override
    public void enterFloatingPointType(final Java8Parser.FloatingPointTypeContext ctx) {
        treeBuilder.createFloatingPointTypeRef(ctx.getText());

        super.enterFloatingPointType(ctx);
    }

    @Override
    public void enterIntegralType(final Java8Parser.IntegralTypeContext ctx) {
        treeBuilder.createIntegralTypeRef(ctx.getText());


        super.enterIntegralType(ctx);
    }

    @Override
    public void enterFieldModifier(final Java8Parser.FieldModifierContext ctx) {
        treeBuilder.handleModifiers(ctx.getText(), false, true, false);

        super.enterFieldModifier(ctx);
    }

    @Override
    public void exitFieldDeclaration(final Java8Parser.FieldDeclarationContext ctx) {
        super.exitFieldDeclaration(ctx);

        treeBuilder.finalizeFieldNodes();
    }
}
