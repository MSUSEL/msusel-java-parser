package edu.montana.gsoc.msusel.codetree.parsers;

import edu.montana.gsoc.msusel.codetree.node.Accessibility;
import edu.montana.gsoc.msusel.codetree.node.type.ClassNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class UseDepsTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/UseDeps");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testTypes() {
        retrieveType("Types", Accessibility.PUBLIC, ClassNode.class);
        retrieveType("A", Accessibility.PUBLIC, ClassNode.class);
        retrieveType("B", Accessibility.PUBLIC, ClassNode.class);
        retrieveType("C", Accessibility.PUBLIC, ClassNode.class);
    }

    @Test
    public void testMethodInvocation_Known() {
        TypeNode type = retrieveType("MethodInvocation", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodKnown(A)", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testMethodInvocation_Unknown() {
        TypeNode type = retrieveType("MethodInvocation", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodUnknown(String)", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testMethodReference_Known() {
        TypeNode type = retrieveType("MethodReference", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);

        // TODO Finish this. fail("Not finished");
    }

    @Test
    public void testMethodReference_Unknown() {
        TypeNode type = retrieveType("MethodReference", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);

        // TODO Finish this. fail("Not finished");
    }

    @Test
    public void testMethodParam_Known() {
        TypeNode type = retrieveType("MethodParameter", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodKnown(A)", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testMethodParam_Unknown() {
        TypeNode type = retrieveType("MethodParameter", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodUnknown(String)", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testMethodReturn_Known() {
        TypeNode type = retrieveType("MethodReturnType", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "A testMethodKnown()", "A", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testMethodReturn_Unknown() {
        TypeNode type = retrieveType("MethodReturnType", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "String testMethodUnknown()", "String", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testInstanceCreation_Known() {
        TypeNode type = retrieveType("InstanceCreation", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testInstanceCreation_Unknown() {
        TypeNode type = retrieveType("InstanceCreation", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testLocalVariable_Known() {
        TypeNode type = retrieveType("LocalVariable", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testLocalVariable_Unknown() {
        TypeNode type = retrieveType("LocalVariable", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testFieldAccess_Known() {
        TypeNode type = retrieveType("FieldAccess", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    @Test
    public void testFieldAccess_Unknown() {
        TypeNode type = retrieveType("FieldAccess", Accessibility.PUBLIC, ClassNode.class);
        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);

        checkUseDeps(type, "A", "String");
    }

    private void checkUseDeps(TypeNode type, String... depNames) {
        List<String> deps = Arrays.asList(depNames);

        Map<TypeNode, ?> map = (Map<TypeNode, ?>)tree.getUseFrom(type);
        map.keySet().forEach(key -> { assertTrue(deps.contains(key.name())); });
    }
}
