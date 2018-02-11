package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import codetree.node.Accessibility;
import codetree.node.Modifiers;
import codetree.node.type.ClassNode;
import codetree.node.type.InterfaceNode;
import codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TypesTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Types");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassModifiers_1() {
        TypeNode type = tree.getUtils().findType("ClassModifiers");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());
        assertTrue(type.hasModifier(Modifiers.STRICTFP));
    }

    @Test
    public void testClassModifiers_2() {
        TypeNode type = tree.getUtils().findType("ClassModifiers2");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertEquals(Accessibility.PROTECTED, type.getAccessibility());
    }

    @Test
    public void testClassModifiers_3() {
        TypeNode type = tree.getUtils().findType("ClassModifiers3");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertEquals(Accessibility.DEFAULT, type.getAccessibility());
    }

    @Test
    public void testClassModifiers_4() {
        TypeNode type = tree.getUtils().findType("ClassModifiers4");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());
        assertTrue(type.hasModifier(Modifiers.FINAL));
    }

    @Test
    public void testClassModifiers_5() {
        TypeNode type = tree.getUtils().findType("ClassModifiers5");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertEquals(Accessibility.PRIVATE, type.getAccessibility());
        assertTrue(type.hasModifier(Modifiers.STATIC));
    }

    @Test
    public void testClassModifiers_6() {
        TypeNode type = tree.getUtils().findType("ClassModifiers6");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());
        assertTrue(type.hasModifier(Modifiers.ABSTRACT));
    }

    @Test
    public void testInterfaceModifiers_1() {
        TypeNode type = tree.getUtils().findType("InterfaceModifiers");
        assertNotNull(type);
        assertTrue(type instanceof InterfaceNode);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());
    }

    @Test
    public void testInterfaceModifiers_2() {
        TypeNode type = tree.getUtils().findType("InterfaceModifiers2");
        assertNotNull(type);
        assertTrue(type instanceof InterfaceNode);
        assertEquals(Accessibility.DEFAULT, type.getAccessibility());
    }
}
