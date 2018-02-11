package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.BaseCodeTreeBuilder;
import codetree.cfg.CFGBuilder;
import codetree.cfg.JumpTo;
import codetree.cfg.StatementType;
import codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

@Slf4j
public class JavaStatementExtractor extends JavaMemberExtractor {

    private Stack<MethodNode> methods = new Stack<>();

    private StringBuilder sigBuilder;
    private int staticInitCount = 0;
    private int instanceInitCount = 0;

    CFGBuilder builder;

    public JavaStatementExtractor(BaseCodeTreeBuilder builder) {
        super(builder);
    }

    // CFG Owners
    public void enterStaticInitializer(final Java8Parser.StaticInitializerContext ctx) {
        sigBuilder = new StringBuilder();
        sigBuilder.append("<static_init$" + (++staticInitCount) + ">");
        builder.startMethod();
    }


    public void enterInstanceInitializer(final Java8Parser.InstanceInitializerContext ctx) {
        sigBuilder = new StringBuilder();
        sigBuilder.append("<init$" + (++instanceInitCount) + ">");

        treeBuilder.findMethodFromSignature(sigBuilder.toString());

        builder.startMethod();
    }

    @Override
    public void enterConstructorDeclarator(final Java8Parser.ConstructorDeclaratorContext ctx) {
        sigBuilder = new StringBuilder();
        sigBuilder.append(ctx.simpleTypeName().Identifier().getText());
        sigBuilder.append("(");

        super.enterConstructorDeclarator(ctx);
    }

    public void exitConstructorDeclarator(final Java8Parser.ConstructorDeclaratorContext ctx) {
        String sig = sigBuilder.toString();
        sig = sig.substring(0, sig.lastIndexOf(", ")) + ")";

        treeBuilder.findMethodFromSignature(sig);

        super.exitConstructorDeclarator(ctx);
    }

    @Override
    public void enterFormalParameter(final Java8Parser.FormalParameterContext ctx) {
        sigBuilder.append(ctx.unannType().getText());
        sigBuilder.append(", ");

        super.enterFormalParameter(ctx);
    }

    public void enterReceiverParameter(final Java8Parser.ReceiverParameterContext ctx) {
        sigBuilder.append(ctx.unannType().getText());
        sigBuilder.append(", ");

        super.enterReceiverParameter(ctx);
    }

    public void enterConstructorBody(final Java8Parser.ConstructorBodyContext ctx) {
        builder.startMethod();

        super.enterConstructorBody(ctx);
    }

    public void exitConstructorBody(final Java8Parser.ConstructorBodyContext ctx) {
        builder.endMethod();

        super.exitConstructorBody(ctx);
    }

    public void enterMethodHeader(final Java8Parser.MethodHeaderContext ctx) {
        sigBuilder = new StringBuilder();
        sigBuilder.append(ctx.result().getText());
        sigBuilder.append(" ");

        super.enterMethodHeader(ctx);
    }

    public void exitMethodHeader(final Java8Parser.MethodHeaderContext ctx) {
        super.exitMethodHeader(ctx);
    }

    public void enterMethodDeclarator(final Java8Parser.MethodDeclaratorContext ctx) {
        sigBuilder.append(ctx.Identifier().getText());
        sigBuilder.append("(");

        super.enterMethodDeclarator(ctx);
    }

    public void enterMethodBody(final Java8Parser.MethodBodyContext ctx) {
        String sig = sigBuilder.toString();
        if (sig.contains(", "))
            sig = sig.substring(0, sig.lastIndexOf(", ")) + ")";
        else
            sig += ")";

        treeBuilder.findMethodFromSignature(sig);

        builder.startMethod();

        super.enterMethodBody(ctx);
    }

    public void exitMethodBody(final Java8Parser.MethodBodyContext ctx) {
        builder.endMethod();

        super.exitMethodBody(ctx);
    }

    // CFG Contents
    @Override
    public void enterBlockStatement(final Java8Parser.BlockStatementContext ctx) {
        builder.startBlock(StatementType.BLOCK_START, StatementType.BLOCK_END);

        super.enterBlockStatement(ctx);
    }

    @Override
    public void exitBlockStatement(final Java8Parser.BlockStatementContext ctx) {
        builder.endBlock();

        super.exitBlockStatement(ctx);
    }

    @Override
    public void enterExpressionStatement(final Java8Parser.ExpressionStatementContext ctx) {
        builder.createStatement(StatementType.EXPRESSION);

        super.enterExpressionStatement(ctx);
    }

    @Override
    public void enterLocalVariableDeclarationStatement(final Java8Parser.LocalVariableDeclarationStatementContext ctx) {
        builder.createStatement(StatementType.VAR_DECL);

        super.enterLocalVariableDeclarationStatement(ctx);
    }

    public void enterLocalVariableDeclaration(final Java8Parser.LocalVariableDeclarationContext ctx) {
        // TODO handle creating use dependencies via local variable decls

        super.enterLocalVariableDeclaration(ctx);
    }

    @Override
    public void enterEmptyStatement(final Java8Parser.EmptyStatementContext ctx) {
        builder.createStatement(StatementType.EMPTY);

        super.enterEmptyStatement(ctx);
    }

    @Override
    public void exitEmptyStatement(final Java8Parser.EmptyStatementContext ctx) {
        super.exitEmptyStatement(ctx);
    }

    @Override
    public void enterLabeledStatement(final Java8Parser.LabeledStatementContext ctx) {
        builder.createStatement(null, ctx.Identifier().getText());

        super.enterLabeledStatement(ctx);
    }

    @Override
    public void enterLabeledStatementNoShortIf(final Java8Parser.LabeledStatementNoShortIfContext ctx) {
        builder.createStatement(null, ctx.Identifier().getText());

        super.enterLabeledStatementNoShortIf(ctx);
    }

    @Override
    public void enterIfThenStatement(final Java8Parser.IfThenStatementContext ctx) {
        builder.startDecision(StatementType.IF);

        super.enterIfThenStatement(ctx);
    }

    @Override
    public void exitIfThenStatement(final Java8Parser.IfThenStatementContext ctx) {
        super.exitIfThenStatement(ctx);

        builder.endDecision();
    }

    @Override
    public void enterIfThenElseStatement(final Java8Parser.IfThenElseStatementContext ctx) {
        builder.startDecision(StatementType.IF);

        super.enterIfThenElseStatement(ctx);
    }

    @Override
    public void exitIfThenElseStatement(final Java8Parser.IfThenElseStatementContext ctx) {
        super.exitIfThenElseStatement(ctx);

        builder.endDecision(false);
    }

    @Override
    public void enterIfThenElseStatementNoShortIf(final Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        builder.startDecision(StatementType.IF);

        super.enterIfThenElseStatementNoShortIf(ctx);
    }

    @Override
    public void exitIfThenElseStatementNoShortIf(final Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        super.exitIfThenElseStatementNoShortIf(ctx);

        builder.endDecision(false);
    }

    @Override
    public void enterAssertStatement(final Java8Parser.AssertStatementContext ctx) {
        builder.createStatement(StatementType.ASSERT);

        super.enterAssertStatement(ctx);
    }

    @Override
    public void enterSwitchStatement(final Java8Parser.SwitchStatementContext ctx) {
        ctx.expression();
        ctx.switchBlock().switchBlockStatementGroup(0).blockStatements();

        super.enterSwitchStatement(ctx);
    }

    @Override
    public void exitSwitchStatement(final Java8Parser.SwitchStatementContext ctx) {
        super.exitSwitchStatement(ctx);
    }

    @Override
    public void enterSwitchBlockStatementGroup(final Java8Parser.SwitchBlockStatementGroupContext ctx) {
        // TODO need to connect to previous block if previous did not have a break
        // TODO need to connect to start of switch as well

        super.enterSwitchBlockStatementGroup(ctx);
    }

    @Override
    public void exitSwitchBlockStatementGroup(final Java8Parser.SwitchBlockStatementGroupContext ctx) {
        super.exitSwitchBlockStatementGroup(ctx);
    }

    @Override
    public void enterWhileStatement(final Java8Parser.WhileStatementContext ctx) {
        builder.startLoop(StatementType.WHILE);

        super.enterWhileStatement(ctx);
    }

    @Override
    public void exitWhileStatement(final Java8Parser.WhileStatementContext ctx) {
        builder.endLoop();

        super.exitWhileStatement(ctx);
    }

    @Override
    public void enterWhileStatementNoShortIf(final Java8Parser.WhileStatementNoShortIfContext ctx) {
        builder.startLoop(StatementType.WHILE);

        super.enterWhileStatementNoShortIf(ctx);
    }

    @Override
    public void exitWhileStatementNoShortIf(final Java8Parser.WhileStatementNoShortIfContext ctx) {
        builder.endLoop();

        super.exitWhileStatementNoShortIf(ctx);
    }

    @Override
    public void enterDoStatement(final Java8Parser.DoStatementContext ctx) {
        builder.startLoop(StatementType.DO, true);

        super.enterDoStatement(ctx);
    }

    @Override
    public void exitDoStatement(final Java8Parser.DoStatementContext ctx) {
        builder.endLoop(true);

        super.exitDoStatement(ctx);
    }

    @Override
    public void enterForStatement(final Java8Parser.ForStatementContext ctx) {
        builder.startLoop(StatementType.FOR);

        super.enterForStatement(ctx);
    }

    @Override
    public void exitForStatement(final Java8Parser.ForStatementContext ctx) {
        super.exitForStatement(ctx);

        builder.endLoop();
    }

    @Override
    public void enterForStatementNoShortIf(final Java8Parser.ForStatementNoShortIfContext ctx) {
        builder.startLoop(StatementType.FOR);

        super.enterForStatementNoShortIf(ctx);
    }

    @Override
    public void exitForStatementNoShortIf(final Java8Parser.ForStatementNoShortIfContext ctx) {
        super.exitForStatementNoShortIf(ctx);

        builder.endLoop();
    }

    @Override
    public void enterBreakStatement(final Java8Parser.BreakStatementContext ctx) {
        String identifier = ctx.Identifier() != null ? ctx.Identifier().getText() : null;
        builder.createStatement(StatementType.BREAK, identifier, true, JumpTo.LOOP_END);

        super.enterBreakStatement(ctx);
    }

    @Override
    public void exitBreakStatement(final Java8Parser.BreakStatementContext ctx) {
        super.exitBreakStatement(ctx);
    }

    @Override
    public void enterContinueStatement(final Java8Parser.ContinueStatementContext ctx) {
        String identifier = ctx.Identifier() != null ? ctx.Identifier().getText() : null;
        builder.createStatement(StatementType.CONTINUE, identifier, true, JumpTo.LOOP_START);

        super.enterContinueStatement(ctx);
    }

    @Override
    public void exitContinueStatement(final Java8Parser.ContinueStatementContext ctx) {
        super.exitContinueStatement(ctx);
    }

    @Override
    public void enterReturnStatement(final Java8Parser.ReturnStatementContext ctx) {
        builder.createStatement(StatementType.RETURN, null, true, JumpTo.METHOD_END);

        super.enterReturnStatement(ctx);
    }

    @Override
    public void exitReturnStatement(final Java8Parser.ReturnStatementContext ctx) {
        super.exitReturnStatement(ctx);
    }

    @Override
    public void enterThrowStatement(final Java8Parser.ThrowStatementContext ctx) {
        builder.createStatement(StatementType.THROW, null, true, JumpTo.METHOD_END);

        super.enterThrowStatement(ctx);
    }

    @Override
    public void exitThrowStatement(final Java8Parser.ThrowStatementContext ctx) {
        super.exitThrowStatement(ctx);
    }

    @Override
    public void enterSynchronizedStatement(final Java8Parser.SynchronizedStatementContext ctx) {
        builder.startBlock(StatementType.SYNCHRONIZED);

        super.enterSynchronizedStatement(ctx);
    }

    @Override
    public void exitSynchronizedStatement(final Java8Parser.SynchronizedStatementContext ctx) {
        builder.endBlock();

        super.exitSynchronizedStatement(ctx);
    }

    @Override
    public void enterTryStatement(final Java8Parser.TryStatementContext ctx) {
        builder.startBlock(StatementType.TRY);

        super.enterTryStatement(ctx);
    }

    @Override
    public void exitTryStatement(final Java8Parser.TryStatementContext ctx) {
        builder.endBlock();

        super.exitTryStatement(ctx);
    }

    public void enterCatchClause(final Java8Parser.CatchClauseContext ctx) {
        builder.startBlock(StatementType.CATCH);
        
        super.enterCatchClause(ctx);
    }

    public void exitCatchClause(final Java8Parser.CatchClauseContext ctx) {
        builder.endBlock();

        super.exitCatchClause(ctx);
    }

    public void enterFinally_(final Java8Parser.Finally_Context ctx) {
        builder.startBlock(StatementType.FINALLY);
        
        super.enterFinally_(ctx);
    }

    public void exitFinally_(final Java8Parser.Finally_Context ctx) {
        builder.endBlock();

        super.exitFinally_(ctx);
    }

    @Override
    public void enterTryWithResourcesStatement(final Java8Parser.TryWithResourcesStatementContext ctx) {
        builder.startBlock(StatementType.TRY);

        super.enterTryWithResourcesStatement(ctx);
    }

    @Override
    public void exitTryWithResourcesStatement(final Java8Parser.TryWithResourcesStatementContext ctx) {
        builder.endBlock();

        super.exitTryWithResourcesStatement(ctx);
    }

    // Method Reference
    @Override
    public void enterMethodReference(final Java8Parser.MethodReferenceContext ctx) {
        super.enterMethodReference(ctx);
    }

    @Override
    public void exitMethodReference(final Java8Parser.MethodReferenceContext ctx) {
        super.exitMethodReference(ctx);
    }

    @Override
    public void enterMethodReference_lf_primary(final Java8Parser.MethodReference_lf_primaryContext ctx) {
        super.enterMethodReference_lf_primary(ctx);
    }

    @Override
    public void exitMethodReference_lf_primary(final Java8Parser.MethodReference_lf_primaryContext ctx) {
        super.exitMethodReference_lf_primary(ctx);
    }

    @Override
    public void enterMethodReference_lfno_primary(final Java8Parser.MethodReference_lfno_primaryContext ctx) {
        super.enterMethodReference_lfno_primary(ctx);
    }

    @Override
    public void exitMethodReference_lfno_primary(final Java8Parser.MethodReference_lfno_primaryContext ctx) {
        super.exitMethodReference_lfno_primary(ctx);
    }

    @Override
    public void enterMethodInvocation(final Java8Parser.MethodInvocationContext ctx) {
        super.enterMethodInvocation(ctx);
    }

    @Override
    public void exitMethodInvocation(final Java8Parser.MethodInvocationContext ctx) {
        super.exitMethodInvocation(ctx);
    }

    @Override
    public void enterMethodInvocation_lf_primary(final Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        super.enterMethodInvocation_lf_primary(ctx);
    }

    @Override
    public void exitMethodInvocation_lf_primary(final Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        super.exitMethodInvocation_lf_primary(ctx);
    }

    @Override
    public void enterMethodInvocation_lfno_primary(final Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        super.enterMethodInvocation_lfno_primary(ctx);
    }

    @Override
    public void exitMethodInvocation_lfno_primary(final Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        super.exitMethodInvocation_lfno_primary(ctx);
    }
}
