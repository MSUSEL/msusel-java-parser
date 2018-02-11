package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.BaseCodeTreeBuilder;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;

// TODO Need to handle ananonymous types
// TODO Need to handle correct naming of types defined within types
public class JavaTypeExtractor extends AbstractJavaExtractor {

    /**
     * Construct a new JavaCodeTreeBuilder for the provided FileNode
     *
     * @param builder
     */
    public JavaTypeExtractor(BaseCodeTreeBuilder builder) {
        super(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterNormalClassDeclaration(final Java8Parser.NormalClassDeclarationContext ctx) {
        treeBuilder.createClassNode(ctx.Identifier().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());

        super.enterNormalClassDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterEnumDeclaration(final Java8Parser.EnumDeclarationContext ctx) {
        treeBuilder.createEnumNode(ctx.Identifier().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());

        super.enterEnumDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterNormalInterfaceDeclaration(final Java8Parser.NormalInterfaceDeclarationContext ctx) {
        treeBuilder.createInterfaceNode(ctx.Identifier().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());

        super.enterNormalInterfaceDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterInterfaceModifier(final Java8Parser.InterfaceModifierContext ctx) {
        treeBuilder.handleTypeModifier(ctx.getText());

        super.enterInterfaceModifier(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterClassModifier(final Java8Parser.ClassModifierContext ctx) {
        treeBuilder.handleTypeModifier(ctx.getText());

        super.enterClassModifier(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {
        treeBuilder.clearFlags();

        super.enterClassBodyDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterEnumBody(Java8Parser.EnumBodyContext ctx) {
        treeBuilder.clearFlags();

        super.enterEnumBody(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterTypeParameters(final Java8Parser.TypeParametersContext ctx) {
        treeBuilder.startTypeParamList();

        super.enterTypeParameters(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitTypeParameters(final Java8Parser.TypeParametersContext ctx) {
        treeBuilder.finalizeTypeParamList();

        super.exitTypeParameters(ctx);
    }

    // TODO pull up from here and MethodExtractor
    /**
     * {@inheritDoc}
     */
    @Override
    public void enterTypeParameter(final Java8Parser.TypeParameterContext ctx) {
        treeBuilder.createTypeVarTypeRef(ctx.Identifier().getText());

        super.enterTypeParameter(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterTypeBound(final Java8Parser.TypeBoundContext ctx) {
        // TODO handle the type bound

        super.enterTypeBound(ctx);
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
