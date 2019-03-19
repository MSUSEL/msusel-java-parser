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

import edu.isu.isuese.BaseModelBuilder;
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8Parser;

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
public class JavaUseRelationExtractor extends JavaMemberExtractor {

    private boolean methodCall = false;

    public JavaUseRelationExtractor(BaseModelBuilder builder) {
        super(builder);
    }

    @Override
    public void enterTypeName(final Java8Parser.TypeNameContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.getText());
        }

        super.enterTypeName(ctx);
    }

    @Override
    public void enterExpressionName(final Java8Parser.ExpressionNameContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.getText());
        }

        super.enterExpressionName(ctx);
    }

    @Override
    public void enterClassType(final Java8Parser.ClassTypeContext ctx) {
        if (methodCall) {
            if (ctx.Identifier() != null) {
                treeBuilder.addUseDependency(ctx.Identifier().getText());
            }
        }

        super.enterClassType(ctx);
    }

    @Override
    public void enterClassType_lf_classOrInterfaceType(final Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.Identifier().getText());
        }

        super.enterClassType_lf_classOrInterfaceType(ctx);
    }

    @Override
    public void enterClassType_lfno_classOrInterfaceType(final Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.Identifier().getText());
        }

        super.enterClassType_lfno_classOrInterfaceType(ctx);
    }

    // Method Reference
    @Override
    public void enterMethodReference(final Java8Parser.MethodReferenceContext ctx) {
        methodCall = true;

        super.enterMethodReference(ctx);
    }

    @Override
    public void exitMethodReference(final Java8Parser.MethodReferenceContext ctx) {
        methodCall = false;

        super.exitMethodReference(ctx);
    }

    @Override
    public void enterMethodReference_lf_primary(final Java8Parser.MethodReference_lf_primaryContext ctx) {
        methodCall = true;

        super.enterMethodReference_lf_primary(ctx);
    }

    @Override
    public void exitMethodReference_lf_primary(final Java8Parser.MethodReference_lf_primaryContext ctx) {
        methodCall = false;

        super.exitMethodReference_lf_primary(ctx);
    }

    @Override
    public void enterMethodReference_lfno_primary(final Java8Parser.MethodReference_lfno_primaryContext ctx) {
        methodCall = true;

        super.enterMethodReference_lfno_primary(ctx);
    }

    @Override
    public void exitMethodReference_lfno_primary(final Java8Parser.MethodReference_lfno_primaryContext ctx) {
        methodCall = false;

        super.exitMethodReference_lfno_primary(ctx);
    }

    @Override
    public void enterMethodInvocation(final Java8Parser.MethodInvocationContext ctx) {
        methodCall = true;

        super.enterMethodInvocation(ctx);
    }

    @Override
    public void exitMethodInvocation(final Java8Parser.MethodInvocationContext ctx) {
        methodCall = false;

        super.exitMethodInvocation(ctx);
    }

    @Override
    public void enterMethodInvocation_lf_primary(final Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        methodCall = true;

        super.enterMethodInvocation_lf_primary(ctx);
    }

    @Override
    public void exitMethodInvocation_lf_primary(final Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        methodCall = false;

        super.exitMethodInvocation_lf_primary(ctx);
    }

    @Override
    public void enterMethodInvocation_lfno_primary(final Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        methodCall = true;

        super.enterMethodInvocation_lfno_primary(ctx);
    }

    @Override
    public void exitMethodInvocation_lfno_primary(final Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        methodCall = false;

        super.exitMethodInvocation_lfno_primary(ctx);
    }
}
