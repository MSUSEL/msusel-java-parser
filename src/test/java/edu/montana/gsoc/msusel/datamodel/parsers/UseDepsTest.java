/**
 * The MIT License (MIT)
 *
 * MSUSEL Java Parser
 * Copyright (c) 2015-2019 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory and Idaho State University, Informatics and
 * Computer Science, Empirical Software Engineering Laboratory
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
package edu.montana.gsoc.msusel.datamodel.parsers;

//import edu.montana.gsoc.msusel.codetree.node.Accessibility;
//import edu.montana.gsoc.msusel.codetree.node.type.Class;
//import edu.montana.gsoc.msusel.codetree.node.type.Type;
//import edu.montana.gsoc.msusel.datamodel.Accessibility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class UseDepsTest extends BaseTestClass {

//    @Before
//    public void setUp() throws Exception {
//        JavaModelBuilder builder = new JavaModelBuilder();
//        tree = builder.build("Test", "./data/java-test-project/UseDeps");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testTypes() {
//        retrieveType("Types", Accessibility.PUBLIC, Class.class);
//        retrieveType("A", Accessibility.PUBLIC, Class.class);
//        retrieveType("B", Accessibility.PUBLIC, Class.class);
//        retrieveType("C", Accessibility.PUBLIC, Class.class);
//    }
//
//    @Test
//    public void testMethodInvocation_Known() {
//        Type type = retrieveType("MethodInvocation", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodKnown(A)", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testMethodInvocation_Unknown() {
//        Type type = retrieveType("MethodInvocation", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodUnknown(String)", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testMethodReference_Known() {
//        Type type = retrieveType("MethodReference", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);
//
//        // TODO Finish this. fail("Not finished");
//    }
//
//    @Test
//    public void testMethodReference_Unknown() {
//        Type type = retrieveType("MethodReference", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);
//
//        // TODO Finish this. fail("Not finished");
//    }
//
//    @Test
//    public void testMethodParam_Known() {
//        Type type = retrieveType("MethodParameter", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodKnown(A)", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testMethodParam_Unknown() {
//        Type type = retrieveType("MethodParameter", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodUnknown(String)", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testMethodReturn_Known() {
//        Type type = retrieveType("MethodReturnType", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "A testMethodKnown()", "A", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testMethodReturn_Unknown() {
//        Type type = retrieveType("MethodReturnType", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "String testMethodUnknown()", "String", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testInstanceCreation_Known() {
//        Type type = retrieveType("InstanceCreation", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testInstanceCreation_Unknown() {
//        Type type = retrieveType("InstanceCreation", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testLocalVariable_Known() {
//        Type type = retrieveType("LocalVariable", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testLocalVariable_Unknown() {
//        Type type = retrieveType("LocalVariable", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testFieldAccess_Known() {
//        Type type = retrieveType("FieldAccess", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodKnown()", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    @Test
//    public void testFieldAccess_Unknown() {
//        Type type = retrieveType("FieldAccess", Accessibility.PUBLIC, Class.class);
//        retrieveMethod(type, "void testMethodUnknown()", "void", Accessibility.PUBLIC);
//
//        checkUseDeps(type, "A", "String");
//    }
//
//    private void checkUseDeps(Type type, String... depNames) {
//        List<String> deps = Arrays.asList(depNames);
//
//        Map<Type, ?> map = (Map<Type, ?>)tree.getUseFrom(type);
//        map.keySet().forEach(key -> { assertTrue(deps.contains(key.name())); });
//    }
}
