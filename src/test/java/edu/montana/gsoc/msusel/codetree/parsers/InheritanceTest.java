package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import codetree.node.type.ClassNode;
import codetree.node.type.EnumNode;
import codetree.node.type.InterfaceNode;
import codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class InheritanceTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Inheritance");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testTypeA() {
        TypeNode type = tree.getUtils().findType("A");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
    }

    @Test
    public void testTypeB() {
        TypeNode type = tree.getUtils().findType("B");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
    }

    @Test
    public void testTypeC() {
        TypeNode type = tree.getUtils().findType("C");
        assertNotNull(type);
        assertTrue(type instanceof InterfaceNode);
    }

    @Test
    public void testTypeD() {
        TypeNode type = tree.getUtils().findType("D");
        assertNotNull(type);
        assertTrue(type instanceof InterfaceNode);
    }

    @Test
    public void testClassGenMultiReal() {
        TypeNode type = tree.getUtils().findType("ClassGenMultiReal");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        List<TypeNode> gens = (List<TypeNode>) tree.getGeneralizedFrom(type);
        assertFalse(gens.isEmpty());
        for (TypeNode t : gens) {
            if (!t.name().equals("A"))
                fail("Incorrect type name for generalization");
        }

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertFalse(reals.isEmpty());
        for (TypeNode t : reals) {
            if (!t.name().equals("C") && !t.name().equals("D"))
                fail("Incorrect type for realization");
        }
    }

    @Test
    public void testClassGenReal() {
        TypeNode type = tree.getUtils().findType("ClassGenReal");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        List<TypeNode> gens = (List<TypeNode>) tree.getGeneralizedFrom(type);
        assertFalse(gens.isEmpty());
        if (!gens.get(0).name().equals("B"))
            fail("Incorrect class for generaliation");

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertFalse(reals.isEmpty());
        if (!reals.get(0).name().equals("C"))
            fail("Incorrect class for realization");
    }

    @Test
    public void testClassInheritance() {
        TypeNode type = tree.getUtils().findType("ClassInheritance");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        List<TypeNode> gens = (List<TypeNode>) tree.getGeneralizedFrom(type);
        assertFalse(gens.isEmpty());
        if (!gens.get(0).name().equals("A"))
            fail("Incorrect class for generalization");

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertTrue(reals.isEmpty());
    }

    @Test
    public void testClassRealization() {
        TypeNode type = tree.getUtils().findType("ClassRealization");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        List<TypeNode> gens = (List<TypeNode>) tree.getGeneralizedFrom(type);
        assertTrue(gens.isEmpty());

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertFalse(reals.isEmpty());
        if (!reals.get(0).name().equals("C"))
            fail("Incorrect class for realization");
    }

    @Test
    public void testEnumMultipleInterface() {
        TypeNode type = tree.getUtils().findType("EnumMultipleInterface");
        assertNotNull(type);
        assertTrue(type instanceof EnumNode);

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertFalse(reals.isEmpty());
        for (TypeNode t : reals) {
            if (!t.name().equals("Serializable") && !t.name().equals("Comparable"))
                fail("Incorrect type for realization: " + t.name());
        }
    }

    @Test
    public void testEnumSingleInterface() {
        TypeNode type = tree.getUtils().findType("EnumSingleInterface");
        assertNotNull(type);
        assertTrue(type instanceof EnumNode);

        List<TypeNode> gens = (List<TypeNode>) tree.getGeneralizedFrom(type);
        assertTrue(gens.isEmpty());

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertFalse(reals.isEmpty());
        if (!reals.get(0).name().equals("Comparable"))
            fail("Incorrect type for realization");
    }

    @Test
    public void testInteraceGen() {
        TypeNode type = tree.getUtils().findType("InterfaceGen");
        assertNotNull(type);
        assertTrue(type instanceof InterfaceNode);

        List<TypeNode> gens = (List<TypeNode>) tree.getGeneralizedFrom(type);
        assertFalse(gens.isEmpty());
        if (!gens.get(0).name().equals("C"))
            fail("Incorrect type for realization");

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertTrue(reals.isEmpty());

    }
}
