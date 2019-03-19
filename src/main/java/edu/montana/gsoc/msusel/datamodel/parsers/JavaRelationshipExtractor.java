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
import edu.isu.isuese.datamodel.Type;
import edu.montana.gsoc.msusel.datamodel.parsers.java8.Java8Parser;

import java.util.List;

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
// TODO Need to handle the containment relationship between types when types are defined within each other and for anonymous types
public class JavaRelationshipExtractor extends AbstractJavaExtractor {

    public JavaRelationshipExtractor(BaseModelBuilder builder) {
        super(builder);
    }

    @Override
    public void enterNormalClassDeclaration(final Java8Parser.NormalClassDeclarationContext ctx) {
        Type type = treeBuilder.findTypeNode(ctx.Identifier().getText());

        if (ctx.superclass() != null) {
            String extendsType = ctx.superclass().classType().Identifier().getText();
            treeBuilder.createGeneralization(type, extendsType);
        }

        if (ctx.superinterfaces() != null) {
            List<Java8Parser.InterfaceTypeContext> types = ctx.superinterfaces().interfaceTypeList().interfaceType();
            handleRealizations(type, types);
        }

        treeBuilder.createContainment(type);

        super.enterNormalClassDeclaration(ctx);
    }

    @Override
    public void enterEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx) {
        Type type = treeBuilder.findTypeNode(ctx.Identifier().getText());

        if (ctx.superinterfaces() != null) {
            List<Java8Parser.InterfaceTypeContext> types = ctx.superinterfaces().interfaceTypeList().interfaceType();
            handleRealizations(type, types);
        }

        treeBuilder.createContainment(type);

        super.enterEnumDeclaration(ctx);
    }

    @Override
    public void enterNormalInterfaceDeclaration(final Java8Parser.NormalInterfaceDeclarationContext ctx) {
        Type type = treeBuilder.findTypeNode(ctx.Identifier().getText());

        if (ctx.extendsInterfaces() != null) {
            List<Java8Parser.InterfaceTypeContext> types = ctx.extendsInterfaces().interfaceTypeList().interfaceType();
            for (Java8Parser.InterfaceTypeContext t : types) {
                String genType = t.classType().Identifier().getText();
                treeBuilder.createGeneralization(type, genType);
            }
        }

        treeBuilder.createContainment(type);

        super.enterNormalInterfaceDeclaration(ctx);
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

    private void handleRealizations(Type type, List<Java8Parser.InterfaceTypeContext> types) {
        for (Java8Parser.InterfaceTypeContext t : types) {
            treeBuilder.createRealization(type, t.classType().Identifier().getText());
        }
    }
}
