package edu.montana.gsoc.msusel.codetree.parsers;

import edu.montana.gsoc.msusel.codetree.BaseCodeTreeBuilder;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;

/**
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class JavaUseRelationExtractor extends JavaMemberExtractor {

    private boolean methodCall = false;

    public JavaUseRelationExtractor(BaseCodeTreeBuilder builder) {
        super(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterTypeName(final Java8Parser.TypeNameContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.getText());
        }

        super.enterTypeName(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterExpressionName(final Java8Parser.ExpressionNameContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.getText());
        }

        super.enterExpressionName(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterClassType(final Java8Parser.ClassTypeContext ctx) {
        if (methodCall) {
            if (ctx.Identifier() != null) {
                treeBuilder.addUseDependency(ctx.Identifier().getText());
            }
        }

        super.enterClassType(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterClassType_lf_classOrInterfaceType(final Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.Identifier().getText());
        }

        super.enterClassType_lf_classOrInterfaceType(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterClassType_lfno_classOrInterfaceType(final Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        if (methodCall) {
            treeBuilder.addUseDependency(ctx.Identifier().getText());
        }

        super.enterClassType_lfno_classOrInterfaceType(ctx);
    }

    // Method Reference
    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodReference(final Java8Parser.MethodReferenceContext ctx) {
        methodCall = true;

        super.enterMethodReference(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodReference(final Java8Parser.MethodReferenceContext ctx) {
        methodCall = false;

        super.exitMethodReference(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodReference_lf_primary(final Java8Parser.MethodReference_lf_primaryContext ctx) {
        methodCall = true;

        super.enterMethodReference_lf_primary(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodReference_lf_primary(final Java8Parser.MethodReference_lf_primaryContext ctx) {
        methodCall = false;

        super.exitMethodReference_lf_primary(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodReference_lfno_primary(final Java8Parser.MethodReference_lfno_primaryContext ctx) {
        methodCall = true;

        super.enterMethodReference_lfno_primary(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodReference_lfno_primary(final Java8Parser.MethodReference_lfno_primaryContext ctx) {
        methodCall = false;

        super.exitMethodReference_lfno_primary(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodInvocation(final Java8Parser.MethodInvocationContext ctx) {
        methodCall = true;

        super.enterMethodInvocation(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodInvocation(final Java8Parser.MethodInvocationContext ctx) {
        methodCall = false;

        super.exitMethodInvocation(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodInvocation_lf_primary(final Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        methodCall = true;

        super.enterMethodInvocation_lf_primary(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodInvocation_lf_primary(final Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        methodCall = false;

        super.exitMethodInvocation_lf_primary(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodInvocation_lfno_primary(final Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        methodCall = true;

        super.enterMethodInvocation_lfno_primary(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodInvocation_lfno_primary(final Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        methodCall = false;

        super.exitMethodInvocation_lfno_primary(ctx);
    }
}
