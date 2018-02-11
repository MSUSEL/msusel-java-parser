package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.BaseCodeTreeBuilder;
import codetree.node.type.TypeNode;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;

import java.util.List;

// TODO Need to handle the containment relationship between types when types are defined within each other and for anonymous types
public class JavaRelationshipExtractor extends AbstractJavaExtractor {

    public JavaRelationshipExtractor(BaseCodeTreeBuilder builder) {
        super(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterNormalClassDeclaration(final Java8Parser.NormalClassDeclarationContext ctx) {
        TypeNode type = treeBuilder.findTypeNode(ctx.Identifier().getText());

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx) {
        TypeNode type = treeBuilder.findTypeNode(ctx.Identifier().getText());

        if (ctx.superinterfaces() != null) {
            List<Java8Parser.InterfaceTypeContext> types = ctx.superinterfaces().interfaceTypeList().interfaceType();
            handleRealizations(type, types);
        }

        treeBuilder.createContainment(type);

        super.enterEnumDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterNormalInterfaceDeclaration(final Java8Parser.NormalInterfaceDeclarationContext ctx) {
        TypeNode type = treeBuilder.findTypeNode(ctx.Identifier().getText());

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitNormalClassDeclaration(final Java8Parser.NormalClassDeclarationContext ctx) {
        treeBuilder.dropTypeNode();

        super.exitNormalClassDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx) {
        treeBuilder.dropTypeNode();

        super.exitEnumDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitNormalInterfaceDeclaration(final Java8Parser.NormalInterfaceDeclarationContext ctx) {
        treeBuilder.dropTypeNode();

        super.exitNormalInterfaceDeclaration(ctx);
    }

    private void handleRealizations(TypeNode type, List<Java8Parser.InterfaceTypeContext> types) {
        for (Java8Parser.InterfaceTypeContext t : types) {
            treeBuilder.createRealization(type, t.classType().Identifier().getText());
        }
    }
}
