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
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8BaseListener;
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8Parser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
public abstract class AbstractJavaExtractor extends Java8BaseListener {

    BaseModelBuilder treeBuilder;

    /**
     * @param treeBuilder
     */
    public AbstractJavaExtractor(BaseModelBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    @Override
    public void enterPackageDeclaration(final Java8Parser.PackageDeclarationContext ctx) {
        final List<TerminalNode> idx = ctx.Identifier();

        final StringBuilder pkg = new StringBuilder();
        boolean first = true;
        for (final TerminalNode n : idx) {
            if (!first) {
                pkg.append(".");
            }
            pkg.append(n.getText());
            first = false;
        }

        treeBuilder.constructNamespaceNode(pkg.toString());

        super.enterPackageDeclaration(ctx);
    }

    @Override
    public void enterUnannClassType_lf_unannClassOrInterfaceType(final Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {
        treeBuilder.constructOuterTypeRef(ctx.Identifier().getText());

        super.enterUnannClassType_lf_unannClassOrInterfaceType(ctx);
    }

    @Override
    public void enterUnannClassType_lfno_unannClassOrInterfaceType(final Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        treeBuilder.constructOuterTypeRef(ctx.Identifier().getText());

        super.enterUnannClassType_lfno_unannClassOrInterfaceType(ctx);
    }

    @Override
    public void exitUnannClassType_lf_unannClassOrInterfaceType(final Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {
        treeBuilder.finalizeTypeRef();

        super.exitUnannClassType_lf_unannClassOrInterfaceType(ctx);
    }

    @Override
    public void exitUnannClassType_lfno_unannClassOrInterfaceType(final Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        treeBuilder.finalizeTypeRef();

        super.exitUnannClassType_lfno_unannClassOrInterfaceType(ctx);
    }

    @Override
    public void enterWildcard(final Java8Parser.WildcardContext ctx) {
        treeBuilder.createWildcardTypeRef();

        super.enterWildcard(ctx);
    }

    @Override
    public void exitWildcard(final Java8Parser.WildcardContext ctx) {
        super.exitWildcard(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterUnannTypeVariable(final Java8Parser.UnannTypeVariableContext ctx) {
        treeBuilder.createTypeVariable(ctx.Identifier().getText());

        super.enterUnannTypeVariable(ctx);
    }

    @Override
    public void enterArrayType(final Java8Parser.ArrayTypeContext ctx) {
        treeBuilder.createArrayTypeRef(ctx.dims().getText(), true);

        super.enterArrayType(ctx);
    }

    @Override
    public void exitArrayType(final Java8Parser.ArrayTypeContext ctx) {
        super.exitArrayType(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterTypeVariable(final Java8Parser.TypeVariableContext ctx) {
        treeBuilder.createTypeVarTypeRef(ctx.Identifier().getText());

        super.enterTypeVariable(ctx);
    }

    @Override
    public void enterClassType_lf_classOrInterfaceType(final Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        treeBuilder.createInnerTypeRef(ctx.Identifier().getText());

        super.enterClassType_lf_classOrInterfaceType(ctx);
    }

    @Override
    public void exitClassType_lf_classOrInterfaceType(final Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        super.exitClassType_lf_classOrInterfaceType(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterClassType_lfno_classOrInterfaceType(final Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        treeBuilder.createInnerTypeRef(ctx.Identifier().getText());

        super.enterClassType_lfno_classOrInterfaceType(ctx);
    }

    @Override
    public void exitClassType_lfno_classOrInterfaceType(final Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        treeBuilder.finalizeTypeRef();

        super.exitClassType_lfno_classOrInterfaceType(ctx);
    }

    @Override
    public void enterUnannArrayType(final Java8Parser.UnannArrayTypeContext ctx) {
        treeBuilder.createArrayTypeRef(ctx.dims().getText());

        super.enterUnannArrayType(ctx);
    }

    @Override
    public void exitUnannArrayType(final Java8Parser.UnannArrayTypeContext ctx) {
        super.exitUnannArrayType(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterUnannPrimitiveType(final Java8Parser.UnannPrimitiveTypeContext ctx) {
        treeBuilder.createPrimitiveTypeRef(ctx.getText());

        super.enterUnannPrimitiveType(ctx);
    }
}
