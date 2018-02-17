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

import com.google.common.collect.Lists;
import edu.montana.gsoc.msusel.codetree.cfg.ControlFlowGraph;
import edu.montana.gsoc.msusel.codetree.node.Accessibility;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.type.ClassNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CFGTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/CFG");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAssert() {
        TypeNode type = retrieveType("Assert", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodAssert1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();
        assertTrue(dot.contains("METHOD_START_0 -> VAR_DECL_1"));
        assertTrue(dot.contains("VAR_DECL_1 -> ASSERT_2"));
        assertTrue(dot.contains("ASSERT_2 -> METHOD_END_0"));
    }

    @Test
    public void testBasic() {
        TypeNode type = retrieveType("Basic", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void method()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();
        assertTrue(dot.contains("METHOD_START_0 -> VAR_DECL_1"));
        assertTrue(dot.contains("VAR_DECL_1 -> METHOD_END_0"));
    }

    @Test
    public void testBreak() {
        TypeNode type = retrieveType("Break", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodBreak1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> FOR_1",
                "  FOR_1 -> IF_4",
                "  FOR_1 -> END_3",
                "  END_3 -> METHOD_END_0",
                "  END_2 -> FOR_1",
                "  IF_4 -> END_5",
                "  IF_4 -> EMPTY_6",
                "  END_5 -> EXPRESSION_7",
                "  EMPTY_6 -> END_5",
                "  EXPRESSION_7 -> END_2"};

        compareDOT(contents, dot);
    }

    @Test
    public void testContinue() {
        TypeNode type = retrieveType("Continue", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodContinue1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> WHILE_2",
                "  WHILE_2 -> END_4",
                "  WHILE_2 -> IF_5",
                "  END_4 -> METHOD_END_0",
                "  END_3 -> WHILE_2",
                "  IF_5 -> EXPRESSION_9",
                "  IF_5 -> EXPRESSION_7",
                "  END_6 -> END_3",
                "  EXPRESSION_7 -> CONTINUE_8",
                "  CONTINUE_8 -> WHILE_2",
                "  EXPRESSION_9 -> END_6"};

        compareDOT(contents, dot);
    }

    @Test
    public void testDoWhile() {
        TypeNode type = retrieveType("DoWhile", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodDoWhile()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> DO_2",
                "  DO_2 -> EMPTY_5",
                "  END_3 -> DO_2",
                "  END_3 -> METHOD_END_0",
                "  EMPTY_5 -> END_3"};
        compareDOT(contents, dot);
    }

    @Test
    public void testEmpty() {
        TypeNode type = retrieveType("Empty", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodEmpty1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> EMPTY_1",
                "  EMPTY_1 -> METHOD_END_0"};
        compareDOT(contents, dot);
    }

    @Test
    public void testExpression() {
        TypeNode type = retrieveType("Expression", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodExpr1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> EXPRESSION_2",
                "  EXPRESSION_2 -> METHOD_END_0"};
        compareDOT(contents, dot);
    }

    @Test
    public void testFor() {
        TypeNode type = retrieveType("For", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodFor1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> FOR_1",
                "  FOR_1 -> END_3",
                "  FOR_1 -> EXPRESSION_4",
                "  END_3 -> METHOD_END_0",
                "  END_2 -> FOR_1",
                "  EXPRESSION_4 -> END_2"};

        compareDOT(contents, dot);
    }

    @Test
    public void testIfs() {
        TypeNode type = retrieveType("Ifs", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodIf1()", "void", Accessibility.DEFAULT);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> IF_2",
                "  IF_2 -> END_3",
                "  IF_2 -> EXPRESSION_4",
                "  END_3 -> EXPRESSION_5",
                "  EXPRESSION_4 -> END_3",
                "  EXPRESSION_5 -> METHOD_END_0"};

        compareDOT(contents, dot);
    }

    @Test
    public void testIfs_2() {
        TypeNode type = retrieveType("Ifs", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodIf2()", "void", Accessibility.DEFAULT);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();
        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> IF_2",
                "  IF_2 -> EXPRESSION_4",
                "  IF_2 -> EMPTY_5",
                "  END_3 -> METHOD_END_0",
                "  EXPRESSION_4 -> END_3",
                "  EMPTY_5 -> END_3"};

        compareDOT(contents, dot);
    }

    @Test
    public void testReturn() {
        TypeNode type = retrieveType("Return", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodReturn1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> IF_2",
                "  IF_2 -> RETURN_4",
                "  IF_2 -> END_3",
                "  END_3 -> METHOD_END_0",
                "  RETURN_4 -> METHOD_END_0"};
        compareDOT(contents, dot);
    }

    @Test
    public void testSwitch() {
        TypeNode type = retrieveType("Switch", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodSwitch1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> VAR_DECL_2",
                "  VAR_DECL_2 -> SWITCH_3",
                "  SWITCH_3 -> EXPRESSION_7",
                "  SWITCH_3 -> EXPRESSION_5",
                "  END_4 -> EXPRESSION_9",
                "  EXPRESSION_5 -> BREAK_6",
                "  BREAK_6 -> END_4",
                "  EXPRESSION_7 -> BREAK_8",
                "  BREAK_8 -> END_4",
                "  EXPRESSION_9 -> METHOD_END_0"};
        compareDOT(contents, dot);
    }

    @Test
    public void testSynchronized() {
        TypeNode type = retrieveType("Synchronized", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodSync1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> SYNCHRONIZED_2",
                "  SYNCHRONIZED_2 -> EXPRESSION_4",
                "  EXPRESSION_4 -> END_3",
                "  END_3 -> METHOD_END_0"};

        compareDOT(contents, dot);
    }

    @Test
    public void testThrow() {
        TypeNode type = retrieveType("Throw", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodThrow1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> THROW_1",
                "  THROW_1 -> METHOD_END_0"};
        compareDOT(contents, dot);
    }

    @Test
    public void testTryCatch_1() {
        TypeNode type = retrieveType("TryCatch", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodTryCatch1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> TRY_1",
                "  TRY_1 -> EMPTY_3",
                "  END_2 -> METHOD_END_0",
                "  EMPTY_3 -> CATCH_4",
                "  CATCH_4 -> END_5",
                "  END_5 -> END_2"};
        compareDOT(contents, dot);
    }

    @Test
    public void testTryCatch_2() {
        TypeNode type = retrieveType("TryCatch", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodTryCatch2()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> TRY_1",
                "  TRY_1 -> TRY_3",
                "  END_2 -> METHOD_END_0",
                "  TRY_3 -> EMPTY_5",
                "  END_4 -> END_2",
                "  EMPTY_5 -> CATCH_6",
                "  CATCH_6 -> EMPTY_8",
                "  END_7 -> END_4",
                "  EMPTY_8 -> END_7"};
        compareDOT(contents, dot);
    }

    @Test
    public void testTryCatch_3() {
        TypeNode type = retrieveType("TryCatch", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodTryCatch3()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> TRY_1",
                "  TRY_1 -> EMPTY_3",
                "  END_2 -> METHOD_END_0",
                "  EMPTY_3 -> CATCH_4",
                "  CATCH_4 -> EMPTY_6",
                "  END_5 -> FINALLY_7",
                "  EMPTY_6 -> END_5",
                "  FINALLY_7 -> EMPTY_9",
                "  END_8 -> END_2",
                "  EMPTY_9 -> END_8"};
        compareDOT(contents, dot);
    }

    @Test
    public void testVarDecl() {
        TypeNode type = retrieveType("VarDecl", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodVarDecl1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                    "  VAR_DECL_1 -> VAR_DECL_2",
                    "  VAR_DECL_2 -> METHOD_END_0"};
        compareDOT(contents, dot);
    }

    @Test
    public void testWhile() {
        TypeNode type = retrieveType("While", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodWhile1()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> WHILE_2",
                "  WHILE_2 -> EMPTY_5",
                "  WHILE_2 -> END_4",
                "  END_4 -> METHOD_END_0",
                "  END_3 -> WHILE_2",
                "  EMPTY_5 -> END_3"};
        compareDOT(contents, dot);
    }

    @Test
    public void testBlock() {
        TypeNode type = retrieveType("Block", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodBlock()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> EMPTY_1",
                "  EMPTY_1 -> METHOD_END_0"};
        compareDOT(contents, dot);
    }

    @Test
    public void testLabeled1() {
        TypeNode type = retrieveType("Labeled", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodLabeledBreak()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> VAR_DECL_1",
                "  VAR_DECL_1 -> LABELED_2",
                "  LABELED_2 -> FOR_3",
                "  FOR_3 -> END_5",
                "  FOR_3 -> FOR_6",
                "  END_5 -> METHOD_END_0",
                "  END_4 -> FOR_3",
                "  FOR_6 -> EXPRESSION_12",
                "  FOR_6 -> END_8",
                "  FOR_6 -> IF_9",
                "  END_8 -> END_4",
                "  END_7 -> FOR_6",
                "  IF_9 -> BREAK_11",
                "  IF_9 -> END_10",
                "  END_10 -> END_7",
                "  BREAK_11 -> LABELED_2",
                "  EXPRESSION_12 -> END_4"};
        compareDOT(contents, dot);
    }

    @Test
    public void testLabeled2() {
        TypeNode type = retrieveType("Labeled", Accessibility.PUBLIC, ClassNode.class);
        MethodNode method = retrieveMethod(type, "void methodLabeledContinue()", "void", Accessibility.PUBLIC);

        ControlFlowGraph cfg = method.getCfg();

        String dot = cfg.toDOT();

        String[] contents = {"  METHOD_START_0 -> LABELED_1",
                "  LABELED_1 -> FOR_2",
                "  FOR_2 -> END_4",
                "  FOR_2 -> EXPRESSION_5",
                "  END_4 -> METHOD_END_0",
                "  END_3 -> FOR_2",
                "  EXPRESSION_5 -> FOR_6",
                "  FOR_6 -> EXPRESSION_13",
                "  FOR_6 -> END_8",
                "  FOR_6 -> EXPRESSION_9",
                "  END_8 -> END_3",
                "  END_7 -> FOR_6",
                "  EXPRESSION_9 -> IF_10",
                "  IF_10 -> CONTINUE_12",
                "  IF_10 -> END_11",
                "  END_11 -> END_7",
                "  CONTINUE_12 -> FOR_6",
                "  EXPRESSION_13 -> END_3"};
        compareDOT(contents, dot);
    }

    @Test
    public void testNested() {
        // TODO finish this. fail("Not yet implemented");
    }

    private void compareDOT(String[] expected, String dot) {
        String[] split = dot.split("\n");
        List<String> expList = Lists.newArrayList(expected);
        List<String> dotList = Lists.newArrayList(split);
        dotList.remove(0);
        dotList.remove(dotList.size() - 1);

        dotList.removeAll(expList);
        assertTrue(dotList.isEmpty());
    }
}
