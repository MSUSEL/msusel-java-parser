package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.BaseCodeTreeBuilder;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;

/**
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class JavaFieldExtractor extends JavaMemberExtractor {

    public JavaFieldExtractor(BaseCodeTreeBuilder builder) {
        super(builder);
    }

    // TODO handle interface constants
    // TODO handle enum literals

    @Override
    public void enterFieldDeclaration(final Java8Parser.FieldDeclarationContext ctx) {
        if (ctx.variableDeclaratorList() != null) {
            for (final Java8Parser.VariableDeclaratorContext decl : ctx.variableDeclaratorList().variableDeclarator()) {
                treeBuilder.createFieldNode(decl.variableDeclaratorId().Identifier().getText(), decl.getStart().getLine(), decl.getStop().getLine());
//                decl.variableInitializer().arrayInitializer();
//                decl.variableInitializer().expression();
            }
        }

        super.enterFieldDeclaration(ctx);
    }

    @Override
    public void enterFloatingPointType(final Java8Parser.FloatingPointTypeContext ctx) {
        treeBuilder.createFloatingPointTypeRef(ctx.getText());

        super.enterFloatingPointType(ctx);
    }

    @Override
    public void enterIntegralType(final Java8Parser.IntegralTypeContext ctx) {
        treeBuilder.createIntegralTypeRef(ctx.getText());


        super.enterIntegralType(ctx);
    }

    @Override
    public void enterFieldModifier(final Java8Parser.FieldModifierContext ctx) {
        treeBuilder.handleFieldModifier(ctx.getText());

        super.enterFieldModifier(ctx);
    }

    @Override
    public void exitFieldDeclaration(final Java8Parser.FieldDeclarationContext ctx) {
        super.exitFieldDeclaration(ctx);

        treeBuilder.finalizeFieldNodes();
    }
}
