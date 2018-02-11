package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import codetree.node.member.EnumLiteralNode;
import codetree.node.member.MethodNode;
import codetree.node.type.EnumNode;
import codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EnumsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        CodeTree tree = builder.build("Test", "./data/java-test-project/Enums");

        TypeNode type = tree.getUtils().findType("Enums");
        assertNotNull(type);
        assertTrue(type instanceof EnumNode);
        List<EnumLiteralNode> literals = (List<EnumLiteralNode>) ((EnumNode) type).getLiterals();

        type = tree.getUtils().findType("Enums2");
        assertNotNull(type);
        assertNotNull(type);
        assertTrue(type instanceof EnumNode);
        literals = (List<EnumLiteralNode>) ((EnumNode) type).getLiterals();
        List<MethodNode> methods = (List<MethodNode>) type.methods();
    }
}
