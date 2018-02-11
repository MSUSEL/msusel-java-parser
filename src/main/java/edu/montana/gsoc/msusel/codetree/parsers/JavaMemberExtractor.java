package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.BaseCodeTreeBuilder;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;

public abstract class JavaMemberExtractor extends AbstractJavaExtractor {

    public JavaMemberExtractor(BaseCodeTreeBuilder builder) {
        super(builder);
    }

    @Override
    public void enterNormalClassDeclaration(final Java8Parser.NormalClassDeclarationContext ctx) {
        treeBuilder.findTypeNode(ctx.Identifier().getText());

        super.enterNormalClassDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx) {
        treeBuilder.findTypeNode(ctx.Identifier().getText());

        super.enterEnumDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterNormalInterfaceDeclaration(final Java8Parser.NormalInterfaceDeclarationContext ctx) {
        treeBuilder.findTypeNode(ctx.Identifier().getText());

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
}
