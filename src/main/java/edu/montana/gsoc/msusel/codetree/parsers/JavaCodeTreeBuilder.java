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

import java.util.List;
import java.util.Stack;

import edu.montana.gsoc.msusel.codetree.node.FileNode;
import edu.montana.gsoc.msusel.codetree.node.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.TypeNode;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8BaseListener;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Using the parser, this class incrementally builds a CodeTree one file at a
 * time.
 *
 * @author Isaac Griffith
 * @version 1.1.1
 */
public class JavaCodeTreeBuilder extends Java8BaseListener {

    /**
     * Logger to log the process of the builder
     */
    private static final Logger   LOG    = LoggerFactory.getLogger(JavaCodeTreeBuilder.class);
    /**
     * Method parameter list
     */
    private List<String>          params = Lists.newArrayList();
    /**
     * Stack used for creating types
     */
    private final Stack<TypeNode> classes;
    /**
     * Package name defined at top of file
     */
    private String                packageName;
    /**
     * FileNode whose contents will be added as the file is parsed.
     */
    private final FileNode file;

    /**
     * Construct a new JavaCodeTreeBuilder for the provided FileNode
     * 
     * @param file
     */
    public JavaCodeTreeBuilder(final @NonNull FileNode file)
    {
        if (file == null)
            throw new IllegalArgumentException("FileNode cannot be null");
        this.file = file;
        packageName = null;
        classes = new Stack<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterClassDeclaration(final Java8Parser.ClassDeclarationContext ctx)
    {
        final int start = ctx.getStart().getLine();
        final int end = ctx.getStop().getLine();

        String name = null;
        if (ctx.normalClassDeclaration() != null)
            name = ctx.normalClassDeclaration().Identifier().getText();
        else
            name = ctx.enumDeclaration().Identifier().getText();

        final String fullName = packageName == null ? name : packageName + "." + name;
        final TypeNode node = TypeNode.builder(fullName, name).range(start, end).create();
        classes.add(node);
        file.addType(node);

        super.enterClassDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterConstructorDeclaration(final Java8Parser.ConstructorDeclarationContext ctx)
    {
        params.clear();

        super.enterConstructorDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx)
    {
        final int start = ctx.getStart().getLine();
        final int end = ctx.getStop().getLine();

        final String name = ctx.Identifier().getText();
        final String fullName = packageName == null ? name : packageName + "." + name;
        final TypeNode node = TypeNode.builder(fullName, name).range(start, end).create();
        classes.add(node);
        file.addType(node);

        super.enterEnumDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterInterfaceDeclaration(final Java8Parser.InterfaceDeclarationContext ctx)
    {
        final int start = ctx.getStart().getLine();
        final int end = ctx.getStop().getLine();

        final Java8Parser.NormalInterfaceDeclarationContext nidx = ctx.normalInterfaceDeclaration();
        final String name = nidx.Identifier().getText();
        final String fullName = packageName == null ? name : packageName + "." + name;

        final TypeNode node = TypeNode.builder(fullName, name).range(start, end).create();
        classes.push(node);
        file.addType(node);

        super.enterInterfaceDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodDeclarator(final Java8Parser.MethodDeclaratorContext ctx)
    {
        params.clear();

        super.enterMethodDeclarator(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterPackageDeclaration(final Java8Parser.PackageDeclarationContext ctx)
    {
        final List<TerminalNode> idx = ctx.Identifier();

        final StringBuilder pkg = new StringBuilder();
        boolean first = true;
        for (final TerminalNode n : idx)
        {
            if (!first)
            {
                pkg.append(".");
            }
            pkg.append(n.getText());
            first = false;
        }

        packageName = pkg.toString();

        super.enterPackageDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterUnannType(final Java8Parser.UnannTypeContext ctx)
    {
        params.add(ctx.getText());
        super.enterUnannType(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitClassDeclaration(final Java8Parser.ClassDeclarationContext ctx)
    {
        classes.pop();

        super.exitClassDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitConstructorDeclaration(final Java8Parser.ConstructorDeclarationContext ctx)
    {
        final int start = ctx.getStart().getLine();
        final int end = ctx.getStart().getLine();

        final StringBuilder methodName = new StringBuilder();
        methodName.append(classes.peek().getName());
        String name = classes.peek().getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        methodName.append(name + "(");

        boolean first = true;
        for (final String px : params)
        {
            if (!first)
            {
                methodName.append(" ,");
            }
            methodName.append(px);
            first = false;
        }

        methodName.append(")");
        name = methodName.toString();
        final String fullName = classes.peek().getQIdentifier() + "#" + name;
        final MethodNode node = MethodNode.builder(fullName, name).constructor().range(start, end).create();
        classes.peek().addMethod(node);

        super.exitConstructorDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx)
    {
        classes.pop();

        super.exitEnumDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitInterfaceDeclaration(final Java8Parser.InterfaceDeclarationContext ctx)
    {
        classes.pop();

        super.exitInterfaceDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodDeclarator(final Java8Parser.MethodDeclaratorContext ctx)
    {
        final int start = ctx.getStart().getLine();
        final int end = ctx.getStop().getLine();

        final StringBuilder methodName = new StringBuilder();
        methodName.append(classes.peek().getName());
        String name = ctx.Identifier().getText();
        methodName.append(name + "(");

        boolean first = true;
        for (final String px : params)
        {
            if (!first)
            {
                methodName.append(" ,");
            }
            methodName.append(px);
            first = false;
        }

        methodName.append(")");
        name = methodName.toString();
        final String fullName = classes.peek().getQIdentifier() + "#" + name;
        final MethodNode node = MethodNode.builder(fullName, name).range(start, end).create();
        classes.peek().addMethod(node);

        super.exitMethodDeclarator(ctx);
    }
}