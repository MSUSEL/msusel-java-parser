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

import edu.montana.gsoc.msusel.codetree.node.Accessibility;
import edu.montana.gsoc.msusel.codetree.node.Modifier;
import edu.montana.gsoc.msusel.codetree.node.member.Method;
import edu.montana.gsoc.msusel.codetree.node.type.Class;
import edu.montana.gsoc.msusel.codetree.node.type.Interface;
import edu.montana.gsoc.msusel.codetree.node.type.Type;
import edu.montana.gsoc.msusel.datamodel.Accessibility;
import edu.montana.gsoc.msusel.datamodel.Modifier;
import edu.montana.gsoc.msusel.datamodel.member.Method;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MethodsTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaModelBuilder builder = new JavaModelBuilder();
        tree = builder.build("Test", "./data/java-test-project/Methods");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAbstractClassMethods() {
        Type type = retrieveType("AbstractClassMethods", Accessibility.PUBLIC, Class.class, Modifier.ABSTRACT);

        retrieveMethod(type, "void testMethod1()", "void", Accessibility.PUBLIC, Modifier.ABSTRACT);
        retrieveMethod(type, "void testMethod2()", "void", Accessibility.PUBLIC, Modifier.ABSTRACT);
    }

    @Test
    public void testInterfaceMethods() {
        Type type = retrieveType("InterfaceMethods", Accessibility.PUBLIC, Interface.class);

        retrieveMethod(type, "void testMethod1()", "void", Accessibility.PUBLIC, Modifier.ABSTRACT);
        retrieveMethod(type, "void testMethod2()", "void", Accessibility.PUBLIC, Modifier.ABSTRACT);
    }

    @Test
    public void testMethodExceptions() {
        Type type = retrieveType("MethodExceptions", Accessibility.PUBLIC, Class.class);

        Method method = retrieveMethod(type, "void exceptions()", "void", Accessibility.PUBLIC);
//        ((List<?>)method.getExceptions()).forEach(Project.out::println);
//        retrieveMethodException(method, "RuntimeException");

        method = retrieveMethod(type, "void exceptions2()", "void", Accessibility.PUBLIC);
        System.out.println(method.getExceptions());
        retrieveMethodException(method, "RuntimeException");
        retrieveMethodException(method, "NullPointerException");
    }

    @Test
    public void testMethodMultipleParams() {
        Type type = retrieveType("MethodMultipleParams", Accessibility.PUBLIC, Class.class);

        Method method = retrieveMethod(type, "void method(String, String)", "void", Accessibility.PUBLIC);
        retrieveMethodParameter(method, "param1", "String");
        retrieveMethodParameter(method, "param2", "String");

        method = retrieveMethod(type, "void vargs(String, String)", "void", Accessibility.PUBLIC);
        retrieveMethodParameter(method, "param1", "String");
        retrieveMethodParameter(method, "param2", "String");
    }

    @Test
    public void testMethodParamMods() {
        Type type = retrieveType("MethodParamMods", Accessibility.PUBLIC, Class.class);

        Method method = retrieveMethod(type, "void method(String)", "void", Accessibility.PUBLIC);
        retrieveMethodParameter(method, "param", "String", Modifier.FINAL);
    }

    @Test
    public void testMethodParams() {
        Type type = retrieveType("MethodParams", Accessibility.PUBLIC, Class.class);

        Method method = retrieveMethod(type, "void knownType(B)", "void", Accessibility.PUBLIC);
        retrieveMethodParameter(method, "param", "B");
        method = retrieveMethod(type, "void unknownType(String)", "void", Accessibility.PUBLIC);
        retrieveMethodParameter(method, "param", "String");
    }

    @Test
    public void testMethodsReturnType() {
        Type type = retrieveType("MethodsReturnType", Accessibility.PUBLIC, Class.class);

        retrieveMethod(type, "String unknownType()", "String", Accessibility.PUBLIC);
        retrieveMethod(type, "A knownType()", "A", Accessibility.PUBLIC);
    }

    @Test
    public void testMethodsTypeParam() {
        Type type = retrieveType("MethodsTypeParam", Accessibility.PUBLIC, Class.class);

        retrieveMethod(type, "T testMethod()", "T", Accessibility.PUBLIC);
    }

    @Test
    public void testVoidMethods() {
        Type type = retrieveType("VoidMethods", Accessibility.PUBLIC, Class.class);

        retrieveMethod(type, "void testMethod1()", "void", Accessibility.PUBLIC);
        retrieveMethod(type, "void testMethod2()", "void", Accessibility.PUBLIC);
    }

    @Test
    public void testConstructors() {
        Type type = retrieveType("Constructors", Accessibility.PUBLIC, Class.class);

        retrieveMethod(type, "Constructors()", "", Accessibility.PUBLIC);
        Method method = retrieveMethod(type, "Constructors(int)", "", Accessibility.PRIVATE);
        retrieveMethodParameter(method, "x", "int");
    }

    @Test
    public void testStaticInit() {
        Class type = (Class) retrieveType("StaticInit", Accessibility.PUBLIC, Class.class);
        retrieveStaticInitializer(type, 1);
    }
}
