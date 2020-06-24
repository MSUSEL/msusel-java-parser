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
//import edu.montana.gsoc.msusel.codetree.node.type.Enum;
//import edu.montana.gsoc.msusel.codetree.node.type.Interface;
//import edu.montana.gsoc.msusel.codetree.node.type.Type;
//import edu.montana.gsoc.msusel.datamodel.Accessibility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class InheritanceTest extends BaseTestClass {

//    @Before
//    public void setUp() throws Exception {
//        JavaModelBuilder builder = new JavaModelBuilder();
//        tree = builder.build("Test", "./data/java-test-project/Inheritance");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testTypeA() {
//        retrieveType("A", Accessibility.PUBLIC, Class.class);
//    }
//
//    @Test
//    public void testTypeB() {
//        retrieveType("B", Accessibility.PUBLIC, Class.class);
//    }
//
//    @Test
//    public void testTypeC() {
//        retrieveType("C", Accessibility.PUBLIC, Interface.class);
//    }
//
//    @Test
//    public void testTypeD() {
//        retrieveType("D", Accessibility.PUBLIC, Interface.class);
//    }
//
//    @Test
//    public void testClassGenMultiReal() {
//        Type type = retrieveType("ClassGenMultiReal", Accessibility.PUBLIC, Class.class);
//        Type gen = retrieveType("A", Accessibility.PUBLIC, Class.class);
//        Type real1 = retrieveType("D", Accessibility.PUBLIC, Interface.class);
//        Type real2 = retrieveType("C", Accessibility.PUBLIC, Interface.class);
//
//        assertTrue(tree.inheritsFrom(type, gen));
//        assertTrue(tree.realizes(type, real1));
//        assertTrue(tree.realizes(type, real2));
//    }
//
//    @Test
//    public void testClassGenReal() {
//        Type type = retrieveType("ClassGenReal", Accessibility.PUBLIC, Class.class);
//        Type gen = retrieveType("B", Accessibility.PUBLIC, Class.class);
//        Type real = retrieveType("C", Accessibility.PUBLIC, Interface.class);
//
//        assertTrue(tree.inheritsFrom(type, gen));
//        assertTrue(tree.realizes(type, real));
//    }
//
//    @Test
//    public void testClassInheritance() {
//        Type type = retrieveType("ClassInheritance", Accessibility.PUBLIC, Class.class);
//        Type gen = retrieveType("A", Accessibility.PUBLIC, Class.class);
//
//        assertTrue(tree.inheritsFrom(type, gen));
//    }
//
//    @Test
//    public void testClassRealization() {
//        Type type = retrieveType("ClassRealization", Accessibility.PUBLIC, Class.class);
//        Type real = retrieveType("C", Accessibility.PUBLIC, Interface.class);
//
//        assertTrue(tree.realizes(type, real));
//    }
//
//    @Test
//    public void testEnumMultipleInterface() {
//        Type type = retrieveType("EnumMultipleInterface", Accessibility.PUBLIC, Enum.class);
//
//        List<Type> reals = (List<Type>) tree.getRealizedFrom(type);
//        assertFalse(reals.isEmpty());
//        for (Type t : reals) {
//            if (!t.name().equals("Serializable") && !t.name().equals("Comparable"))
//                fail("Incorrect type for realization: " + t.name());
//        }
//    }
//
//    @Test
//    public void testEnumSingleInterface() {
//        Type type = retrieveType("EnumSingleInterface", Accessibility.PUBLIC, Enum.class);
//
//        List<Type> reals = (List<Type>) tree.getRealizedFrom(type);
//        assertFalse(reals.isEmpty());
//        if (!reals.get(0).name().equals("Comparable"))
//            fail("Incorrect type for realization");
//    }
//
//    @Test
//    public void testInteraceGen() {
//        Type type = retrieveType("InterfaceGen", Accessibility.PUBLIC, Interface.class);
//        Type real = retrieveType("C", Accessibility.PUBLIC, Interface.class);
//
//        assertTrue(tree.inheritsFrom(type, real));
//    }
}
