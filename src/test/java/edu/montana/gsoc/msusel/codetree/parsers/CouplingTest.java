package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import codetree.node.Accessibility;
import codetree.node.Modifiers;
import codetree.node.type.ClassNode;
import codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CouplingTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Coupling");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBiDirectionalAssociation() {
        TypeNode type = tree.getUtils().findType("BiDirectionalAssociation");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        assertTrue(tree.hasBiDirectionalAssociation(type, tree.getUtils().findType("B")));
    }

    @Test
    public void testTypeB() {
        TypeNode type = tree.getUtils().findType("B");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        assertTrue(tree.hasBiDirectionalAssociation(type, tree.getUtils().findType("B")));
    }

    @Test
    public void testContainedClasses() {
        TypeNode type = tree.getUtils().findType("ContainedClasses");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        assertTrue(tree.hasContainmentRelation(type, tree.getUtils().findType("ContainedClasses.ContainedClass")));
    }

    @Test
    public void testContainedClass_ContainedClass() {
        TypeNode type = tree.getUtils().findType("ContainedClasses.ContainedClass");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.hasModifier(Modifiers.STATIC));
        assertTrue(type.getAccessibility() == Accessibility.PRIVATE);

        assertTrue(tree.hasContainmentRelation(tree.getUtils().findType("ContainedClasses.ContainedClass"), type));
    }

    @Test
    public void testUniDirectionalAssociation() {
        TypeNode type = tree.getUtils().findType("UniDirectionalAssociation");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        assertTrue(tree.hasUniDirectionalAssociation(type, tree.getUtils().findType("C")));
    }

    @Test
    public void testTypeC() {
        TypeNode type = tree.getUtils().findType("C");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        assertTrue(tree.hasUniDirectionalAssociation(tree.getUtils().findType("UniDirectionalAssociation"), type));
    }

    @Test
    public void testUseDependency() {
        TypeNode type = tree.getUtils().findType("UseDependency");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        assertTrue(tree.hasUseDependency(type, tree.getUtils().findType("D")));
    }

    @Test
    public void testTypeD() {
        TypeNode type = tree.getUtils().findType("D");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        assertTrue(tree.hasUseDependency(tree.getUtils().findType("UseDependency"), type));
    }
}
