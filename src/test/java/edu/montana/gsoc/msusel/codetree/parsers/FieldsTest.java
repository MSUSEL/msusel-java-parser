package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import codetree.node.Accessibility;
import codetree.node.Modifiers;
import codetree.node.member.FieldNode;
import codetree.node.type.ClassNode;
import codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FieldsTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Fields");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassA() {
        TypeNode type = tree.getUtils().findType("A");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }

    @Test
    public void testClassB() {
        TypeNode type = tree.getUtils().findType("B");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);
    }

    @Test
    public void testFieldModifiers() {
        TypeNode type = tree.getUtils().findType("FieldModifiers");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        List<FieldNode> fields = (List<FieldNode>) type.fields();
        assertFalse(fields.isEmpty());
        for (FieldNode f : fields) {
            if (f.name().equals("XXX")) {
                assertTrue(f.hasModifier(Modifiers.STATIC));
                assertTrue(f.hasModifier(Modifiers.FINAL));
                assertEquals("String", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PUBLIC);
            } else if (f.name().equals("yyy")) {
                assertTrue(f.hasModifier(Modifiers.TRANSIENT));
                assertEquals("String", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PROTECTED);
            } else if (f.name().equals("zzz")) {
                assertTrue(f.hasModifier(Modifiers.VOLATILE));
                assertEquals("String", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PRIVATE);
            } else if (f.name().equals("aaa")) {
                assertEquals("String", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.DEFAULT);
            } else {
                fail("Unknown field name found: " + f.name());
            }
        }

    }

    @Test
    public void testMultipleFieldArrayType() {
        TypeNode type = tree.getUtils().findType("MultipleFieldArrayType");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        List<FieldNode> fields = (List<FieldNode>) type.fields();
        assertFalse(fields.isEmpty());
        for (FieldNode f : fields) {
            if (f.name().equals("field1")) {
                assertEquals("String[]", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PRIVATE);
            } else if (f.name().equals("field2")) {
                assertEquals("String[][]", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PRIVATE);
            } else {
                fail("Unknown field name found: " + f.name());
            }
        }
    }

    @Test
    public void testMultipleFields() {
        TypeNode type = tree.getUtils().findType("MultipleFields");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        List<FieldNode> fields = (List<FieldNode>) type.fields();
        assertFalse(fields.isEmpty());
        for (FieldNode f : fields) {
            if (f.name().equals("field1")) {
                assertEquals("String", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.DEFAULT);
            } else if (f.name().equals("field2")) {
                assertEquals("A", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.DEFAULT);
            } else if (f.name().equals("field3")) {
                assertEquals("B", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.DEFAULT);
            } else if (f.name().equals("field4")) {
                assertEquals("String", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.DEFAULT);
            } else {
                fail("Unknown field name found: " + f.name());
            }
        }
    }

    @Test
    public void testSingleFieldArrayType() {
        TypeNode type = tree.getUtils().findType("SingleFieldArrayType");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        List<FieldNode> fields = (List<FieldNode>) type.fields();
        assertFalse(fields.isEmpty());
        for (FieldNode f : fields) {
            if (f.name().equals("field")) {
                assertEquals("A[]", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PRIVATE);
            } else {
                fail("Unknown field name found: " + f.name());
            }
        }
    }

    @Test
    public void testSingleFieldKnownType() {
        TypeNode type = tree.getUtils().findType("SingleFieldKnownType");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        List<FieldNode> fields = (List<FieldNode>) type.fields();
        assertFalse(fields.isEmpty());
        for (FieldNode f : fields) {
            if (f.name().equals("field")) {
                assertEquals("A", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PRIVATE);
            } else {
                fail("Unknown field name found: " + f.name());
            }
        }
    }

    @Test
    public void testSingleFieldUnknownType() {
        TypeNode type = tree.getUtils().findType("SingleFieldUnknownType");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        List<FieldNode> fields = (List<FieldNode>) type.fields();
        assertFalse(fields.isEmpty());
        for (FieldNode f : fields) {
            if (f.name().equals("field")) {
                assertEquals("String", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PRIVATE);
            } else {
                fail("Unknown field name found: " + f.name());
            }
        }
    }

    @Test
    public void testSingleFieldPrimitiveType() {
        TypeNode type = tree.getUtils().findType("SingleFieldPrimitiveType");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);
        assertTrue(type.getAccessibility() == Accessibility.PUBLIC);

        List<FieldNode> fields = (List<FieldNode>) type.fields();
        assertFalse(fields.isEmpty());
        for (FieldNode f : fields) {
            if (f.name().equals("x")) {
                assertEquals("int", f.getType().name());
                assertTrue(f.getAccessibility() == Accessibility.PRIVATE);
            } else {
                fail("Unknown field name found: " + f.name());
            }
        }
    }
}
