package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.AbstractTypeRef;
import codetree.CodeTree;
import codetree.node.Accessibility;
import codetree.node.Modifiers;
import codetree.node.member.InitializerNode;
import codetree.node.member.MethodNode;
import codetree.node.member.ParameterNode;
import codetree.node.type.TypeNode;
import codetree.typeref.TypeVarTypeRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MethodsTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Methods");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAbstractClassMethods() {
        TypeNode type = tree.getUtils().findType("AbstractClassMethods");
        assertNotNull(type);
        assertTrue(type.hasModifier(Modifiers.ABSTRACT));
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("testMethod1")) {
                assertEquals("void", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());
                assertTrue(node.hasModifier(Modifiers.ABSTRACT));
            } else if (node.name().equals("testMethod2")) {
                assertEquals("void", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());
                assertTrue(node.hasModifier(Modifiers.ABSTRACT));
            } else {
                fail("unknown method name");
            }
        }
    }

    @Test
    public void testInterfaceMethods() {
        TypeNode type = tree.getUtils().findType("InterfaceMethods");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());
        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("testMethod1")) {
                assertEquals("void", node.getType().name());
            } else if (node.name().equals("testMethod2")) {
                assertEquals("void", node.getType().name());
            } else {
                fail("unknown method name");
            }
        }
    }

    @Test
    public void testMethodExceptions() {
        TypeNode type = tree.getUtils().findType("MethodExceptions");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("exceptions")) {
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());
                assertEquals("void", node.getType().name());
                List<AbstractTypeRef> refs = (List<AbstractTypeRef>) node.getExceptions();
//                if (!refs.get(0).name().equals("RuntimeException"))
//                    fail("Incorrect exception type");
            } else if (node.name().equals("exceptions2")) {
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());
                assertEquals("void", node.getType().name());
                List<AbstractTypeRef> refs = (List<AbstractTypeRef>) node.getExceptions();
                for (AbstractTypeRef ref : refs) {
                    if (!refs.get(0).name().equals("RuntimeException") && !refs.get(1).name().equals("NullPointerException"))
                        fail("Incorrect exception type");
                }
            } else {
                fail("unknown method name");
            }
        }
    }

    @Test
    public void testMethodMultipleParams() {
        TypeNode type = tree.getUtils().findType("MethodMultipleParams");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("method")) {
                assertEquals("void", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());
                List<ParameterNode> params = (List<ParameterNode>) node.getParams();
                for (ParameterNode p : params) {
                    if (p.name().equals("param1"))
                        assertEquals("String", p.getType().name());
                    else if (p.name().equals("param2"))
                        assertEquals("String", p.getType().name());
                    else
                        fail("Unknown parameter");
                }
            } else if (node.name().equals("vargs")) {
                List<ParameterNode> params = (List<ParameterNode>) node.getParams();
                for (ParameterNode p : params) {
                    if (p.name().equals("param1"))
                        assertEquals("String", p.getType().name());
                    else if (p.name().equals("param2"))
                        assertEquals("String", p.getType().name());
                    else
                        fail("Unknown parameter");
                }
            } else {
                fail("unknown method name: " + node.name());
            }
        }
    }

    @Test
    public void testMethodParamMods() {
        TypeNode type = tree.getUtils().findType("MethodParamMods");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("method")) {
                assertEquals("void", node.getType().name());
                ParameterNode param0 = ((List<ParameterNode>) node.getParams()).get(0);
//                assertEquals("param", param0.name());
//                assertEquals("String", param0.getType().name());
                assertTrue(param0.hasModifier(Modifiers.FINAL));
            } else {
                fail("unknown method name");
            }
        }
    }

    @Test
    public void testMethodParams() {
        TypeNode type = tree.getUtils().findType("MethodParams");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("knownType")) {
                assertEquals("void", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());

                List<ParameterNode> params = (List<ParameterNode>) node.getParams();
                assertEquals("param", params.get(0).name());
                assertEquals("B", params.get(0).getType().name());
            } else if (node.name().equals("unknownType")) {
                assertEquals("void", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());

                List<ParameterNode> params = (List<ParameterNode>) node.getParams();
                assertEquals("param", params.get(0).name());
                assertEquals("String", params.get(0).getType().name());
            } else {
                fail("unknown method name: " + node.name());
            }
        }
    }

    @Test
    public void testMethodsReturnType() {
        TypeNode type = tree.getUtils().findType("MethodsReturnType");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("unknownType")) {

            } else if (node.name().equals("knownType")) {

            } else {
                fail("unknown method name");
            }
        }
    }

    @Test
    public void testMethodsTypeParam() {
        TypeNode type = tree.getUtils().findType("MethodsTypeParam");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("testMethod")) {
                assertEquals("T", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());

                List<TypeVarTypeRef> params = (List<TypeVarTypeRef>) node.getTypeParams();
//                assertEquals("T", params.get(0).name());
            } else {
                fail("unknown method name");
            }
        }
    }

    @Test
    public void testVoidMethods() {
        TypeNode type = tree.getUtils().findType("VoidMethods");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        for (MethodNode node : methods) {
            if (node.name().equals("testMethod1")) {
                assertEquals("void", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());
            } else if (node.name().equals("testMethod2")) {
                assertEquals("void", node.getType().name());
                assertEquals(Accessibility.PUBLIC, node.getAccessibility());
            } else {
                fail("unknown method name");
            }
        }
    }

    @Test
    public void testConstructors() {
        TypeNode type = tree.getUtils().findType("Constructors");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        assertEquals("Constructors", methods.get(0).name());
        assertEquals(Accessibility.PUBLIC, methods.get(0).getAccessibility());

        assertEquals("Constructors", methods.get(1).name());
        assertEquals(Accessibility.PRIVATE, methods.get(1).getAccessibility());

        ParameterNode param = (ParameterNode) methods.get(1).getParameterByName("x");
        assertNotNull(param);
        assertEquals("int", param.getType().name());
    }

    @Test
    public void testStaticInit() {
        TypeNode type = tree.getUtils().findType("StaticInit");
        assertNotNull(type);
        assertEquals(Accessibility.PUBLIC, type.getAccessibility());

        List<MethodNode> methods = (List<MethodNode>) type.methods();
        assertTrue(methods.get(0) instanceof InitializerNode);
        InitializerNode init = (InitializerNode) methods.get(0);
        assertFalse(init.isInstance());
        assertEquals("<static_init$1>", init.name());
    }
}
