package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import codetree.node.Accessibility;
import codetree.node.Modifiers;
import codetree.node.type.ClassNode;
import codetree.node.type.EnumNode;
import codetree.node.type.InterfaceNode;
import codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BasicTypesTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/BasicTypes");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAbstractClass() {
        TypeNode type = tree.getUtils().findType("AbstractClass");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.hasModifier(Modifiers.ABSTRACT));
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }

    @Test
    public void testBasicClass() {
        TypeNode type = tree.getUtils().findType("BasicClass");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }

    @Test
    public void testBasicEnum() {
        TypeNode type = tree.getUtils().findType("BasicEnum");
        assertNotNull(type);
        assertTrue(type instanceof EnumNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }

    @Test
    public void testBasicInterface() {
        TypeNode type = tree.getUtils().findType("BasicInterface");
        assertNotNull(type);
        assertTrue(type instanceof InterfaceNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }

    @Test
    public void testFinalClass() {
        TypeNode type = tree.getUtils().findType("FinalClass");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.hasModifier(Modifiers.FINAL));
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }

    @Test
    public void testFinalStaticClass() {
        TypeNode type = tree.getUtils().findType("FinalStaticClass");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.hasModifier(Modifiers.FINAL));
        assertTrue(type.hasModifier(Modifiers.STATIC));
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }
}
