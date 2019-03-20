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
public class JavaMethodExtractor extends JavaMemberExtractor {

    private int staticInitCount = 0;
    private int instanceInitCount = 0;

    public JavaMethodExtractor(BaseModelBuilder builder) {
        super(builder);
    }

    @Override
    public void enterConstructorDeclaration(final Java8Parser.ConstructorDeclarationContext ctx) {
        treeBuilder.createMethod(true);

        super.enterConstructorDeclaration(ctx);
    }

    @Override
    public void exitConstructorDeclaration(final Java8Parser.ConstructorDeclarationContext ctx) {
        treeBuilder.finalizeMethod();

        super.exitConstructorDeclaration(ctx);
    }

    @Override
    public void enterConstructorDeclarator(final Java8Parser.ConstructorDeclaratorContext ctx) {
        treeBuilder.handleMethodDeclarator(ctx.simpleTypeName().Identifier().getText());

        super.enterConstructorDeclarator(ctx);
    }

    @Override
    public void enterFormalParameter(final Java8Parser.FormalParameterContext ctx) {
        treeBuilder.createFormalParameter();

        super.enterFormalParameter(ctx);
    }

    @Override
    public void enterLastFormalParameter(final Java8Parser.LastFormalParameterContext ctx) {
        if (ctx.formalParameter() == null) {
            treeBuilder.createFormalParameter();
        }

        super.enterLastFormalParameter(ctx);
    }

    @Override
    public void exitLastFormalParameter(final Java8Parser.LastFormalParameterContext ctx) {
        if (ctx.formalParameter() == null) {
            treeBuilder.finalizeFormalParameter();
        }

        super.exitLastFormalParameter(ctx);
    }

    @Override
    public void enterReceiverParameter(final Java8Parser.ReceiverParameterContext ctx) {
        treeBuilder.createFormalParameter(ctx.Identifier().getText(), true);

        super.enterReceiverParameter(ctx);
    }

    @Override
    public void exitFormalParameter(final Java8Parser.FormalParameterContext ctx) {
        treeBuilder.finalizeFormalParameter();

        super.exitFormalParameter(ctx);
    }

    @Override
    public void exitReceiverParameter(final Java8Parser.ReceiverParameterContext ctx) {
        treeBuilder.finalizeFormalParameter();

        super.exitReceiverParameter(ctx);
    }

    @Override
    public void enterVariableDeclaratorId(final Java8Parser.VariableDeclaratorIdContext ctx) {
        treeBuilder.handleVarDeclId(ctx.Identifier().getText());

        super.enterVariableDeclaratorId(ctx);
    }

    @Override
    public void enterVariableModifier(final Java8Parser.VariableModifierContext ctx) {
        treeBuilder.startVariableModifier(ctx.getText());

        super.enterVariableModifier(ctx);
    }

    @Override
    public void enterInterfaceMethodDeclaration(final Java8Parser.InterfaceMethodDeclarationContext ctx) {
        treeBuilder.createMethod();

        super.enterInterfaceMethodDeclaration(ctx);
    }

    @Override
    public void enterInterfaceMethodModifier(final Java8Parser.InterfaceMethodModifierContext ctx) {
        treeBuilder.startMethodModifier(ctx.getText());

        super.enterInterfaceMethodModifier(ctx);
    }

    @Override
    public void exitInterfaceMethodDeclaration(final Java8Parser.InterfaceMethodDeclarationContext ctx) {
        treeBuilder.finalizeMethod(true);

        super.exitInterfaceMethodDeclaration(ctx);
    }

    @Override
    public void exitInterfaceMethodModifier(final Java8Parser.InterfaceMethodModifierContext ctx) {
        treeBuilder.finalizeMethodModifier();

        super.exitInterfaceMethodModifier(ctx);
    }

    @Override
    public void exitVariableModifier(final Java8Parser.VariableModifierContext ctx) {
        treeBuilder.finalizeVariableModifier();

        super.exitVariableModifier(ctx);
    }

    @Override
    public void enterTypeParameter(final Java8Parser.TypeParameterContext ctx) {
        treeBuilder.createTypeParameter(ctx.Identifier().getText().trim());

        super.enterTypeParameter(ctx);
    }

    @Override
    public void enterTypeParameterModifier(final Java8Parser.TypeParameterModifierContext ctx) {
        treeBuilder.handleModifiers(ctx.getText(), false, false, true);

        super.enterTypeParameterModifier(ctx);
    }

    @Override
    public void exitTypeParameter(final Java8Parser.TypeParameterContext ctx) {
        treeBuilder.finalizeTypeParameter();

        super.exitTypeParameter(ctx);
    }

    @Override
    public void enterConstructorModifier(final Java8Parser.ConstructorModifierContext ctx) {
        treeBuilder.startMethodModifier(ctx.getText());

        super.enterConstructorModifier(ctx);
    }

    @Override
    public void exitConstructorModifier(final Java8Parser.ConstructorModifierContext ctx) {
        treeBuilder.finalizeMethodModifier();

        super.exitConstructorModifier(ctx);
    }

    @Override
    public void enterMethodDeclaration(final Java8Parser.MethodDeclarationContext ctx) {
        treeBuilder.createMethod();

        super.enterMethodDeclaration(ctx);
    }

    @Override
    public void exitMethodDeclaration(final Java8Parser.MethodDeclarationContext ctx) {
        super.exitMethodDeclaration(ctx);

        treeBuilder.finalizeMethod();
    }

    @Override
    public void enterMethodDeclarator(final Java8Parser.MethodDeclaratorContext ctx) {
        String methodId = ctx.Identifier().getText();
        String dimensions = "";
        if (ctx.dims() != null)
            dimensions = ctx.dims().getText(); // TODO handle dimensions

        treeBuilder.setMethodIdentifier(methodId, dimensions);

        super.enterMethodDeclarator(ctx);
    }

    @Override
    public void enterTypeParameterList(final Java8Parser.TypeParameterListContext ctx) {
        treeBuilder.startTypeParamList();

        super.enterTypeParameterList(ctx);
    }

    @Override
    public void exitTypeParameterList(final Java8Parser.TypeParameterListContext ctx) {
        treeBuilder.finalizeTypeParamList();

        super.exitTypeParameterList(ctx);
    }

    @Override
    public void enterResult(final Java8Parser.ResultContext ctx) {
        treeBuilder.startMethodReturnType(ctx.unannType() == null);

        super.enterResult(ctx);
    }

    @Override
    public void exitResult(final Java8Parser.ResultContext ctx) {
        treeBuilder.finalizeMethodReturnType();

        super.exitResult(ctx);
    }

    @Override
    public void enterThrows_(final Java8Parser.Throws_Context ctx) {
        treeBuilder.startMethodExceptionList();

        super.enterThrows_(ctx);
    }

    @Override
    public void enterClassType(final Java8Parser.ClassTypeContext ctx) {
        if (ctx.Identifier() != null) {
            treeBuilder.constructOuterTypeRef(ctx.Identifier().getText());
        }

        super.enterClassType(ctx);
    }

    @Override
    public void exitClassType(final Java8Parser.ClassTypeContext ctx) {
        if (ctx.Identifier() != null) {
            treeBuilder.finalizeTypeRef();
        }

        super.exitClassType(ctx);
    }

    @Override
    public void exitThrows_(final Java8Parser.Throws_Context ctx) {
        treeBuilder.finalizeMethodExceptionList();

        super.exitThrows_(ctx);
    }

    @Override
    public void enterMethodModifier(final Java8Parser.MethodModifierContext ctx) {
        treeBuilder.startMethodModifier(ctx.getText());

        super.enterMethodModifier(ctx);
    }

    @Override
    public void exitMethodModifier(final Java8Parser.MethodModifierContext ctx) {
        treeBuilder.finalizeMethodModifier();

        super.exitMethodModifier(ctx);
    }

    @Override
    public void enterInstanceInitializer(final Java8Parser.InstanceInitializerContext ctx) {
        treeBuilder.createInitializer("<init$" + (++instanceInitCount) + ">");

        super.enterInstanceInitializer(ctx);

    }

    @Override
    public void enterStaticInitializer(final Java8Parser.StaticInitializerContext ctx) {
        treeBuilder.createInitializer("<clinit$" + (++staticInitCount) + ">", false);

        super.enterStaticInitializer(ctx);
    }

    @Override
    public void exitInstanceInitializer(final Java8Parser.InstanceInitializerContext ctx) {
        treeBuilder.finalizeMethod();

        super.exitInstanceInitializer(ctx);

    }

    @Override
    public void exitStaticInitializer(final Java8Parser.StaticInitializerContext ctx) {
        treeBuilder.finalizeMethod();

        super.exitStaticInitializer(ctx);
    }
}