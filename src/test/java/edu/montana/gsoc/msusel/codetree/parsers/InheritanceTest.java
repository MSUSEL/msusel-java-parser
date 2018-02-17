/**
 * The MIT License (MIT)
 *
 * MSUSEL Java Parser
 * Copyright (c) 2015-2018 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
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
package edu.montana.gsoc.msusel.codetree.parsers;

import edu.montana.gsoc.msusel.codetree.node.Accessibility;
import edu.montana.gsoc.msusel.codetree.node.type.ClassNode;
import edu.montana.gsoc.msusel.codetree.node.type.EnumNode;
import edu.montana.gsoc.msusel.codetree.node.type.InterfaceNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class InheritanceTest extends BaseTestClass {

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
        retrieveType("A", Accessibility.PUBLIC, ClassNode.class);
    }

    @Test
    public void testTypeB() {
        retrieveType("B", Accessibility.PUBLIC, ClassNode.class);
    }

    @Test
    public void testTypeC() {
        retrieveType("C", Accessibility.PUBLIC, InterfaceNode.class);
    }

    @Test
    public void testTypeD() {
        retrieveType("D", Accessibility.PUBLIC, InterfaceNode.class);
    }

    @Test
    public void testClassGenMultiReal() {
        TypeNode type = retrieveType("ClassGenMultiReal", Accessibility.PUBLIC, ClassNode.class);
        TypeNode gen = retrieveType("A", Accessibility.PUBLIC, ClassNode.class);
        TypeNode real1 = retrieveType("D", Accessibility.PUBLIC, InterfaceNode.class);
        TypeNode real2 = retrieveType("C", Accessibility.PUBLIC, InterfaceNode.class);

        assertTrue(tree.inheritsFrom(type, gen));
        assertTrue(tree.realizes(type, real1));
        assertTrue(tree.realizes(type, real2));
    }

    @Test
    public void testClassGenReal() {
        TypeNode type = retrieveType("ClassGenReal", Accessibility.PUBLIC, ClassNode.class);
        TypeNode gen = retrieveType("B", Accessibility.PUBLIC, ClassNode.class);
        TypeNode real = retrieveType("C", Accessibility.PUBLIC, InterfaceNode.class);

        assertTrue(tree.inheritsFrom(type, gen));
        assertTrue(tree.realizes(type, real));
    }

    @Test
    public void testClassInheritance() {
        TypeNode type = retrieveType("ClassInheritance", Accessibility.PUBLIC, ClassNode.class);
        TypeNode gen = retrieveType("A", Accessibility.PUBLIC, ClassNode.class);

        assertTrue(tree.inheritsFrom(type, gen));
    }

    @Test
    public void testClassRealization() {
        TypeNode type = retrieveType("ClassRealization", Accessibility.PUBLIC, ClassNode.class);
        TypeNode real = retrieveType("C", Accessibility.PUBLIC, InterfaceNode.class);

        assertTrue(tree.realizes(type, real));
    }

    @Test
    public void testEnumMultipleInterface() {
        TypeNode type = retrieveType("EnumMultipleInterface", Accessibility.PUBLIC, EnumNode.class);

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertFalse(reals.isEmpty());
        for (TypeNode t : reals) {
            if (!t.name().equals("Serializable") && !t.name().equals("Comparable"))
                fail("Incorrect type for realization: " + t.name());
        }
    }

    @Test
    public void testEnumSingleInterface() {
        TypeNode type = retrieveType("EnumSingleInterface", Accessibility.PUBLIC, EnumNode.class);

        List<TypeNode> reals = (List<TypeNode>) tree.getRealizedFrom(type);
        assertFalse(reals.isEmpty());
        if (!reals.get(0).name().equals("Comparable"))
            fail("Incorrect type for realization");
    }

    @Test
    public void testInteraceGen() {
        TypeNode type = retrieveType("InterfaceGen", Accessibility.PUBLIC, InterfaceNode.class);
        TypeNode real = retrieveType("C", Accessibility.PUBLIC, InterfaceNode.class);

        assertTrue(tree.inheritsFrom(type, real));
    }
}
