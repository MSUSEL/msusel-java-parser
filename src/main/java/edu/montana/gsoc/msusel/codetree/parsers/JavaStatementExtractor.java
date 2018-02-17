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
package edu.montana.gsoc.msusel.codetree.parsers;

import edu.montana.gsoc.msusel.codetree.BaseCodeTreeBuilder;
import edu.montana.gsoc.msusel.codetree.cfg.CFGBuilder;
import edu.montana.gsoc.msusel.codetree.cfg.JumpTo;
import edu.montana.gsoc.msusel.codetree.cfg.StatementType;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.parsers.java8.Java8Parser;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

/**
 * @author Isaac Griffith
 * @version 1.2.0
 */
@Slf4j
public class JavaStatementExtractor extends JavaMemberExtractor {

    private Stack<MethodNode> methods = new Stack<>();

    private StringBuilder sigBuilder;
    private int staticInitCount = 0;
    private int instanceInitCount = 0;
    private String currentSig;

    private Stack<CFGBuilder> builderStack = new Stack<>();
    CFGBuilder builder;

    public JavaStatementExtractor(BaseCodeTreeBuilder builder) {
        super(builder);
    }

    private void startMethod() {
        CFGBuilder newBuilder = new CFGBuilder();
        builderStack.push(newBuilder);
        builder = newBuilder;
    }

    private void endMethod() {
        builderStack.pop();
        if (builderStack.empty())
            builder = null;
        else
            builder = builderStack.peek();
    }

    // CFG Owners

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterStaticInitializer(final Java8Parser.StaticInitializerContext ctx) {
        MethodNode init = (MethodNode) treeBuilder.findStaticInitializer(++staticInitCount);
        if (init != null) {
            methods.push(init);

            startMethod();
            builder.startMethod();
        }

        super.enterStaticInitializer(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitStaticInitializer(final Java8Parser.StaticInitializerContext ctx) {
        if (!methods.empty()) {
            builder.endMethod(methods.peek());
            endMethod();
        }

        super.exitStaticInitializer(ctx);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void enterInstanceInitializer(final Java8Parser.InstanceInitializerContext ctx) {
        MethodNode init = (MethodNode) treeBuilder.findInstanceInitializer(++staticInitCount);
        if (init != null) {
            methods.push(init);

            startMethod();
            builder.startMethod();
        }

        super.enterInstanceInitializer(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitInstanceInitializer(final Java8Parser.InstanceInitializerContext ctx) {
        if (!methods.empty()) {
            builder.endMethod(methods.peek());
            endMethod();
        }

        super.exitInstanceInitializer(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterConstructorDeclarator(final Java8Parser.ConstructorDeclaratorContext ctx) {
        startMethod();
        sigBuilder = new StringBuilder();
        sigBuilder.append(ctx.simpleTypeName().Identifier().getText());
        sigBuilder.append("(");

        super.enterConstructorDeclarator(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitConstructorDeclarator(final Java8Parser.ConstructorDeclaratorContext ctx) {
        String sig = sigBuilder.toString();
        if (sig.endsWith(", "))
            sig = sig.substring(0, sig.lastIndexOf(", ")) + ")";
        else
            sig += ")";

        methods.push((MethodNode) treeBuilder.findMethodFromSignature(sig));

        super.exitConstructorDeclarator(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterFormalParameter(final Java8Parser.FormalParameterContext ctx) {
        sigBuilder.append(ctx.unannType().getText());
        sigBuilder.append(", ");

        super.enterFormalParameter(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterReceiverParameter(final Java8Parser.ReceiverParameterContext ctx) {
        sigBuilder.append(ctx.unannType().getText());
        sigBuilder.append(", ");

        super.enterReceiverParameter(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLastFormalParameter(final Java8Parser.LastFormalParameterContext ctx) {
        if (ctx.formalParameter() == null) {
            sigBuilder.append(ctx.unannType().getText());
            sigBuilder.append(", ");
        }

        super.enterLastFormalParameter(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterConstructorBody(final Java8Parser.ConstructorBodyContext ctx) {
        builder.startMethod();

        super.enterConstructorBody(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitConstructorBody(final Java8Parser.ConstructorBodyContext ctx) {
        builder.endMethod(methods.peek());
        endMethod();

        super.exitConstructorBody(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodHeader(final Java8Parser.MethodHeaderContext ctx) {
        sigBuilder = new StringBuilder();
        sigBuilder.append(ctx.result().getText());
        sigBuilder.append(" ");

        super.enterMethodHeader(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodDeclarator(final Java8Parser.MethodDeclaratorContext ctx) {
        startMethod();
        sigBuilder.append(ctx.Identifier().getText());
        sigBuilder.append("(");

        super.enterMethodDeclarator(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMethodBody(final Java8Parser.MethodBodyContext ctx) {
        String sig = sigBuilder.toString();
        if (sig.contains(", "))
            sig = sig.substring(0, sig.lastIndexOf(", ")) + ")";
        else
            sig += ")";

        currentSig = sig;

        methods.push((MethodNode) treeBuilder.findMethodFromSignature(currentSig));

        builder.startMethod();

        super.enterMethodBody(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitMethodBody(final Java8Parser.MethodBodyContext ctx) {
        MethodNode method = treeBuilder.getTypes().peek().findMethodBySignature(currentSig);
        builder.endMethod(method);
        endMethod();

        super.exitMethodBody(ctx);
    }

    // CFG Contents

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterExpressionStatement(final Java8Parser.ExpressionStatementContext ctx) {
        builder.createStatement(StatementType.EXPRESSION);

        super.enterExpressionStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLocalVariableDeclarationStatement(final Java8Parser.LocalVariableDeclarationStatementContext ctx) {
        builder.createStatement(StatementType.VAR_DECL);

        super.enterLocalVariableDeclarationStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLocalVariableDeclaration(final Java8Parser.LocalVariableDeclarationContext ctx) {
        // TODO handle creating use dependencies via local variable decls

        super.enterLocalVariableDeclaration(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterEmptyStatement(final Java8Parser.EmptyStatementContext ctx) {
        builder.createStatement(StatementType.EMPTY);

        super.enterEmptyStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterStatement(final Java8Parser.StatementContext ctx) {
        if (ctx.parent instanceof Java8Parser.IfThenElseStatementContext)
            builder.startDecisionBlock();
        super.enterStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterStatementNoShortIf(final Java8Parser.StatementNoShortIfContext ctx) {
        if (ctx.parent instanceof Java8Parser.IfThenElseStatementContext || ctx.parent instanceof Java8Parser.IfThenElseStatementNoShortIfContext)
            builder.startDecisionBlock();
        super.enterStatementNoShortIf(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLabeledStatement(final Java8Parser.LabeledStatementContext ctx) {
        builder.createStatement(null, ctx.Identifier().getText());

        super.enterLabeledStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLabeledStatementNoShortIf(final Java8Parser.LabeledStatementNoShortIfContext ctx) {
        builder.createStatement(null, ctx.Identifier().getText());

        super.enterLabeledStatementNoShortIf(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterIfThenStatement(final Java8Parser.IfThenStatementContext ctx) {
        builder.startDecision(StatementType.IF);

        super.enterIfThenStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitIfThenStatement(final Java8Parser.IfThenStatementContext ctx) {
        super.exitIfThenStatement(ctx);

        builder.endDecision();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterIfThenElseStatement(final Java8Parser.IfThenElseStatementContext ctx) {
        builder.startDecision(StatementType.IF);

        super.enterIfThenElseStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitIfThenElseStatement(final Java8Parser.IfThenElseStatementContext ctx) {
        super.exitIfThenElseStatement(ctx);

        builder.endDecision(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterIfThenElseStatementNoShortIf(final Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        builder.startDecision(StatementType.IF);

        super.enterIfThenElseStatementNoShortIf(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitIfThenElseStatementNoShortIf(final Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        super.exitIfThenElseStatementNoShortIf(ctx);

        builder.endDecision(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterAssertStatement(final Java8Parser.AssertStatementContext ctx) {
        builder.createStatement(StatementType.ASSERT);

        super.enterAssertStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterSwitchStatement(final Java8Parser.SwitchStatementContext ctx) {
        builder.startDecision(StatementType.SWITCH);

        super.enterSwitchStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitSwitchStatement(final Java8Parser.SwitchStatementContext ctx) {
        boolean def = true;
        for (final Java8Parser.SwitchBlockStatementGroupContext x : ctx.switchBlock().switchBlockStatementGroup()) {
            if (x.getText().startsWith("default")) {
                def = false;
                break;
            }
        }
        builder.endDecision(def);

        super.exitSwitchStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterSwitchBlockStatementGroup(final Java8Parser.SwitchBlockStatementGroupContext ctx) {
//        builder.startSwitchBlock();

        super.enterSwitchBlockStatementGroup(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitSwitchBlockStatementGroup(final Java8Parser.SwitchBlockStatementGroupContext ctx) {
        builder.exitSwitchBlock();

        super.exitSwitchBlockStatementGroup(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterWhileStatement(final Java8Parser.WhileStatementContext ctx) {
        builder.startLoop(StatementType.WHILE);

        super.enterWhileStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitWhileStatement(final Java8Parser.WhileStatementContext ctx) {
        builder.endLoop();

        super.exitWhileStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterWhileStatementNoShortIf(final Java8Parser.WhileStatementNoShortIfContext ctx) {
        builder.startLoop(StatementType.WHILE);

        super.enterWhileStatementNoShortIf(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitWhileStatementNoShortIf(final Java8Parser.WhileStatementNoShortIfContext ctx) {
        builder.endLoop();

        super.exitWhileStatementNoShortIf(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterDoStatement(final Java8Parser.DoStatementContext ctx) {
        builder.startLoop(StatementType.DO, true);

        super.enterDoStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitDoStatement(final Java8Parser.DoStatementContext ctx) {
        builder.endLoop(true);

        super.exitDoStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterForStatement(final Java8Parser.ForStatementContext ctx) {
        builder.startLoop(StatementType.FOR);

        super.enterForStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitForStatement(final Java8Parser.ForStatementContext ctx) {
        super.exitForStatement(ctx);

        builder.endLoop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterForStatementNoShortIf(final Java8Parser.ForStatementNoShortIfContext ctx) {
        builder.startLoop(StatementType.FOR);

        super.enterForStatementNoShortIf(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitForStatementNoShortIf(final Java8Parser.ForStatementNoShortIfContext ctx) {
        super.exitForStatementNoShortIf(ctx);

        builder.endLoop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterBreakStatement(final Java8Parser.BreakStatementContext ctx) {
        String identifier = ctx.Identifier() != null ? ctx.Identifier().getText() : null;
        if (identifier != null)
            builder.createStatement(StatementType.BREAK, identifier, true, JumpTo.LABEL);
        else
            builder.createStatement(StatementType.BREAK, null, true, JumpTo.LOOP_END);

        super.enterBreakStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterContinueStatement(final Java8Parser.ContinueStatementContext ctx) {
        String identifier = ctx.Identifier() != null ? ctx.Identifier().getText() : null;
        builder.createStatement(StatementType.CONTINUE, identifier, true, JumpTo.LOOP_START);

        super.enterContinueStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitContinueStatement(final Java8Parser.ContinueStatementContext ctx) {
        super.exitContinueStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterReturnStatement(final Java8Parser.ReturnStatementContext ctx) {
        builder.createReturnStatement();

        super.enterReturnStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterThrowStatement(final Java8Parser.ThrowStatementContext ctx) {
        builder.createStatement(StatementType.THROW, null, true, JumpTo.METHOD_END);

        super.enterThrowStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitThrowStatement(final Java8Parser.ThrowStatementContext ctx) {
        super.exitThrowStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterSynchronizedStatement(final Java8Parser.SynchronizedStatementContext ctx) {
        builder.startBlock(StatementType.SYNCHRONIZED);

        super.enterSynchronizedStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitSynchronizedStatement(final Java8Parser.SynchronizedStatementContext ctx) {
        builder.endBlock();

        super.exitSynchronizedStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterTryStatement(final Java8Parser.TryStatementContext ctx) {
        builder.startBlock(StatementType.TRY);

        super.enterTryStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitTryStatement(final Java8Parser.TryStatementContext ctx) {
        builder.endBlock();

        super.exitTryStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterCatchClause(final Java8Parser.CatchClauseContext ctx) {
        builder.startBlock(StatementType.CATCH);

        super.enterCatchClause(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitCatchClause(final Java8Parser.CatchClauseContext ctx) {
        builder.endBlock();

        super.exitCatchClause(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterFinally_(final Java8Parser.Finally_Context ctx) {
        builder.startBlock(StatementType.FINALLY);

        super.enterFinally_(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitFinally_(final Java8Parser.Finally_Context ctx) {
        builder.endBlock();

        super.exitFinally_(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterTryWithResourcesStatement(final Java8Parser.TryWithResourcesStatementContext ctx) {
        builder.startBlock(StatementType.TRY);

        super.enterTryWithResourcesStatement(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitTryWithResourcesStatement(final Java8Parser.TryWithResourcesStatementContext ctx) {
        builder.endBlock();

        super.exitTryWithResourcesStatement(ctx);
    }
}
