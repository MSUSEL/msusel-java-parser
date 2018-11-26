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
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8Parser;

// TODO Need to handle ananonymous types
// TODO Need to handle correct naming of types defined within types
/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
public class JavaTypeExtractor extends AbstractJavaExtractor {

    /**
     * Construct a new JavaModelBuilder for the provided File
     *
     * @param builder
     */
    public JavaTypeExtractor(BaseModelBuilder builder) {
        super(builder);
    }

    @Override
    public void enterNormalClassDeclaration(final Java8Parser.NormalClassDeclarationContext ctx) {
        treeBuilder.createClassNode(ctx.Identifier().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());

        super.enterNormalClassDeclaration(ctx);
    }

    @Override
    public void enterEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx) {
        treeBuilder.createEnumNode(ctx.Identifier().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());

        super.enterEnumDeclaration(ctx);
    }

    @Override
    public void enterNormalInterfaceDeclaration(final Java8Parser.NormalInterfaceDeclarationContext ctx) {
        treeBuilder.createInterfaceNode(ctx.Identifier().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());

        super.enterNormalInterfaceDeclaration(ctx);
    }

    @Override
    public void enterInterfaceModifier(final Java8Parser.InterfaceModifierContext ctx) {
        treeBuilder.handleModifiers(ctx.getText(), true, false, false);

        super.enterInterfaceModifier(ctx);
    }

    @Override
    public void enterClassModifier(final Java8Parser.ClassModifierContext ctx) {
        treeBuilder.handleModifiers(ctx.getText(), true, false, false);

        super.enterClassModifier(ctx);
    }

    @Override
    public void enterClassBodyDeclaration(final Java8Parser.ClassBodyDeclarationContext ctx) {
        treeBuilder.clearFlags();

        super.enterClassBodyDeclaration(ctx);
    }

    @Override
    public void enterEnumBody(final Java8Parser.EnumBodyContext ctx) {
        treeBuilder.clearFlags();

        super.enterEnumBody(ctx);
    }

    @Override
    public void enterTypeParameters(final Java8Parser.TypeParametersContext ctx) {
        treeBuilder.startTypeParamList();

        super.enterTypeParameters(ctx);
    }

    @Override
    public void exitTypeParameters(final Java8Parser.TypeParametersContext ctx) {
        treeBuilder.finalizeTypeParamList();

        super.exitTypeParameters(ctx);
    }

    // TODO pull up from here and MethodExtractor
    @Override
    public void enterTypeParameter(final Java8Parser.TypeParameterContext ctx) {
        treeBuilder.createTypeVarTypeRef(ctx.Identifier().getText());

        super.enterTypeParameter(ctx);
    }

    @Override
    public void enterTypeBound(final Java8Parser.TypeBoundContext ctx) {
        // TODO handle the type bound

        super.enterTypeBound(ctx);
    }

    @Override
    public void exitNormalClassDeclaration(final Java8Parser.NormalClassDeclarationContext ctx) {
        treeBuilder.dropTypeNode();

        super.exitNormalClassDeclaration(ctx);
    }

    @Override
    public void exitEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx) {
        treeBuilder.dropTypeNode();

        super.exitEnumDeclaration(ctx);
    }

    @Override
    public void exitNormalInterfaceDeclaration(final Java8Parser.NormalInterfaceDeclarationContext ctx) {
        treeBuilder.dropTypeNode();

        super.exitNormalInterfaceDeclaration(ctx);
    }
}
