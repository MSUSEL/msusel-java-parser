package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CFGTest {

    CodeTree tree;

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

    }

    @Test
    public void testBasic() {

    }

    @Test
    public void testBlock() {

    }

    @Test
    public void testBreak() {

    }

    @Test
    public void testContinue() {

    }

    @Test
    public void testDoWhile() {

    }

    @Test
    public void testEmpty() {

    }

    @Test
    public void testExpression() {

    }

    @Test
    public void testFor() {

    }

    @Test
    public void testIfs() {

    }

    @Test
    public void testLabeled() {

    }

    @Test
    public void testReturn() {

    }

    @Test
    public void testSwitch() {

    }

    @Test
    public void testSynchronized() {

    }

    @Test
    public void testThrow() {

    }

    @Test
    public void testTryCatch() {

    }

    @Test
    public void testVarDecl() {

    }

    @Test
    public void testWhile() {

    }

    @Test
    public void testNested() {

    }
}
