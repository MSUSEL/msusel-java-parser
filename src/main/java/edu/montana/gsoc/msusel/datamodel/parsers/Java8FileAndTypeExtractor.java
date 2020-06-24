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

import com.google.common.collect.Lists;
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaParser;
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaParserBaseListener;

import java.util.List;

public class Java8FileAndTypeExtractor extends JavaParserBaseListener {

    BaseModelBuilder treeBuilder;
    List<String> modifiers = Lists.newArrayList();
    boolean inPackage;
    boolean inImport;
    boolean inTypeDecl;
    boolean inTypeParams;

    public Java8FileAndTypeExtractor(BaseModelBuilder builder) {
        treeBuilder = builder;
        inPackage = false;
        inImport = false;
        inTypeDecl = false;
        inTypeParams = false;
    }

    ////////////////////////////
    //
    // Handle package declarations
    //
    ////////////////////////////
    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        inPackage = true;

        super.enterPackageDeclaration(ctx);
    }

    @Override
    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        inPackage = false;

        super.exitPackageDeclaration(ctx);
    }

    ////////////////////////////
    //
    // Handle import declarations
    //
    ////////////////////////////
    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        inImport = true;

        super.enterImportDeclaration(ctx);
    }

    @Override
    public void exitImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
        inImport = false;

        super.exitImportDeclaration(ctx);
    }

    @Override
    public void enterQualifiedName(JavaParser.QualifiedNameContext ctx) {
        if (inPackage) {
            treeBuilder.createNamespace(ctx.getText());
        } else if (inImport) {
            treeBuilder.createImport(ctx.getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
        }

        super.enterQualifiedName(ctx);
    }

    ////////////////////////////
    //
    // Handle type declarations
    //
    ////////////////////////////
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        modifiers.clear();
        inTypeDecl = true;

        super.enterTypeDeclaration(ctx);
    }

    public void exitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        treeBuilder.endType();

        super.exitTypeDeclaration(ctx);
    }

    public void enterLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext ctx) {
        modifiers.clear();
        inTypeDecl = true;

        super.enterLocalTypeDeclaration(ctx);
    }

    public void exitLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext ctx) {
        treeBuilder.endType();

        super.exitLocalTypeDeclaration(ctx);
    }

    ////////////////////////////
    //
    // Specific types
    //
    ////////////////////////////
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        treeBuilder.createClass(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);

        super.enterClassDeclaration(ctx);
    }

    public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
        treeBuilder.createEnum(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);

        super.enterEnumDeclaration(ctx);
    }

    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        treeBuilder.createInterface(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);

        super.enterInterfaceDeclaration(ctx);
    }

    public void enterAnnotationTypeDeclaration(JavaParser.AnnotationTypeDeclarationContext ctx) {
        treeBuilder.createAnnotation(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);

        super.enterAnnotationTypeDeclaration(ctx);
    }

    ////////////////////////////
    //
    // Modifiers
    //
    ////////////////////////////
    public void enterClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
        if (ctx.annotation() == null)
            modifiers.add(ctx.getText());

        super.enterClassOrInterfaceModifier(ctx);
    }

    ////////////////////////////
    //
    // Type Parameters
    //
    ////////////////////////////
    public void enterTypeParameters(JavaParser.TypeParametersContext ctx) {
        inTypeParams = true;

        super.enterTypeParameters(ctx);
    }

    public void exitTypeParameters(JavaParser.TypeParametersContext ctx) {
        inTypeParams = false;

        super.exitTypeParameters(ctx);
    }

    public void enterTypeParameter(JavaParser.TypeParameterContext ctx) {
        treeBuilder.createTypeTypeParameter(ctx.IDENTIFIER().getText());

        super.enterTypeParameter(ctx);
    }

    ////////////////////////////
    //
    // Handle types
    //
    ////////////////////////////
    public void enterClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        if (inTypeParams && inTypeDecl) {
            //treeBuilder.addTypeParamBoundType(ctx.getText());
        }

        super.enterClassOrInterfaceType(ctx);
    }

    ////////////////////////////
    //
    // Bodies
    //
    ////////////////////////////
    public void enterClassBody(JavaParser.ClassBodyContext ctx) {
        inTypeDecl = false;

        super.enterClassBody(ctx);
    }

    public void enterInterfaceBody(JavaParser.InterfaceBodyContext ctx) {
        inTypeDecl = false;

        super.enterInterfaceBody(ctx);
    }

    public void enterEnumConstants(JavaParser.EnumConstantsContext ctx) {
        inTypeDecl = false;

        super.enterEnumConstants(ctx);
    }

    public void enterEnumBodyDeclarations(JavaParser.EnumBodyDeclarationsContext ctx) {
        inTypeDecl = false;

        super.enterEnumBodyDeclarations(ctx);
    }

    public void enterAnnotationTypeBody(JavaParser.AnnotationTypeBodyContext ctx) {
        inTypeDecl = false;

        super.enterAnnotationTypeBody(ctx);
    }
}
