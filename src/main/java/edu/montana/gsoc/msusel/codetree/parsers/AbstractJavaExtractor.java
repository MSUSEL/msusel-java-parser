package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.BaseCodeTreeBuilder;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8BaseListener;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public abstract class AbstractJavaExtractor extends Java8BaseListener {

    BaseCodeTreeBuilder treeBuilder;

    public AbstractJavaExtractor(BaseCodeTreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterPackageDeclaration(final Java8Parser.PackageDeclarationContext ctx) {
        final List<TerminalNode> idx = ctx.Identifier();

        final StringBuilder pkg = new StringBuilder();
        boolean first = true;
        for (final TerminalNode n : idx) {
            if (!first) {
                pkg.append(".");
            }
            pkg.append(n.getText());
            first = false;
        }

        treeBuilder.constructNamespaceNode(pkg.toString());

        super.enterPackageDeclaration(ctx);
    }

    @Override
    public void enterUnannClassType_lf_unannClassOrInterfaceType(Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {
        treeBuilder.constructTypeRef(ctx.Identifier().getText());

        super.enterUnannClassType_lf_unannClassOrInterfaceType(ctx);
    }

    @Override
    public void enterUnannClassType_lfno_unannClassOrInterfaceType(Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        treeBuilder.constructTypeRef(ctx.Identifier().getText());

        super.enterUnannClassType_lfno_unannClassOrInterfaceType(ctx);
    }

    @Override
    public void enterWildcard(Java8Parser.WildcardContext ctx) {
        treeBuilder.createWildcardTypeRef();

        super.enterWildcard(ctx);
    }

    public void exitWildcard(Java8Parser.WildcardContext ctx) {
        super.exitWildcard(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterUnannTypeVariable(Java8Parser.UnannTypeVariableContext ctx) {
        treeBuilder.createTypeVariable(ctx.Identifier().getText());

        super.enterUnannTypeVariable(ctx);
    }

    @Override
    public void enterArrayType(Java8Parser.ArrayTypeContext ctx) {
        treeBuilder.createArrayTypeRef(ctx.dims().getText(), true);

        super.enterArrayType(ctx);
    }

    public void exitArrayType(Java8Parser.ArrayTypeContext ctx) {
        super.exitArrayType(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterTypeVariable(Java8Parser.TypeVariableContext ctx) {
        treeBuilder.createTypeVarTypeRef(ctx.Identifier().getText());

        super.enterTypeVariable(ctx);
    }

    @Override
    public void enterClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        treeBuilder.createTypeRef(ctx.Identifier().getText());

        super.enterClassType_lf_classOrInterfaceType(ctx);
    }

    public void exitClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        super.exitClassType_lf_classOrInterfaceType(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        treeBuilder.createTypeRef(ctx.Identifier().getText());

        super.enterClassType_lfno_classOrInterfaceType(ctx);
    }

    public void exitClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        treeBuilder.finalizeTypeRef();

        super.exitClassType_lfno_classOrInterfaceType(ctx);
    }

    @Override
    public void enterUnannArrayType(Java8Parser.UnannArrayTypeContext ctx) {
        treeBuilder.createArrayTypeRef(ctx.dims().getText());

        super.enterUnannArrayType(ctx);
    }

    @Override
    public void exitUnannArrayType(Java8Parser.UnannArrayTypeContext ctx) {
        super.exitUnannArrayType(ctx);

        treeBuilder.finalizeTypeRef();
    }

    @Override
    public void enterUnannPrimitiveType(Java8Parser.UnannPrimitiveTypeContext ctx) {
        treeBuilder.createPrimitiveTypeRef(ctx.getText());

        super.enterUnannPrimitiveType(ctx);
    }
}
