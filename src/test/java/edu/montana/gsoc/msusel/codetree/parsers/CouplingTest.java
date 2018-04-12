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
import edu.montana.gsoc.msusel.codetree.node.Modifiers;
import edu.montana.gsoc.msusel.codetree.node.type.ClassNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CouplingTest extends BaseTestClass {

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
        TypeNode type = retrieveType("BiDirectionalAssociation", Accessibility.PUBLIC, ClassNode.class);

        assertTrue(tree.hasBiDirectionalAssociation(type, tree.getUtils().findType("B")));
    }

    @Test
    public void testTypeB() {
        TypeNode type = retrieveType("B", Accessibility.PUBLIC, ClassNode.class);

        assertTrue(tree.hasBiDirectionalAssociation(type, tree.getUtils().findType("BiDirectionalAssociation")));
    }

    @Test
    public void testContainedClasses() {
        TypeNode type = retrieveType("ContainedClasses", Accessibility.PUBLIC, ClassNode.class);
        TypeNode cont = retrieveType("ContainedClasses.ContainedClass", Accessibility.PRIVATE, ClassNode.class, Modifiers.STATIC);

        assertTrue(tree.hasContainmentRelation(cont, type));
    }

    @Test
    public void testContainedClass_ContainedClass() {
        TypeNode type = retrieveType("ContainedClasses", Accessibility.PUBLIC, ClassNode.class);
        TypeNode cont = retrieveType("ContainedClasses.ContainedClass", Accessibility.PRIVATE, ClassNode.class, Modifiers.STATIC);

        assertTrue(tree.hasContainmentRelation(cont, type));
    }

    @Test
    public void testUniDirectionalAssociation() {
        TypeNode type = retrieveType("UniDirectionalAssociation", Accessibility.PUBLIC, ClassNode.class);

        assertTrue(tree.hasUniDirectionalAssociation(type, tree.getUtils().findType("C")));
    }

    @Test
    public void testTypeC() {
        TypeNode type = retrieveType("C", Accessibility.PUBLIC, ClassNode.class);

        assertTrue(tree.hasUniDirectionalAssociation(tree.getUtils().findType("UniDirectionalAssociation"), type));
    }

    @Test
    public void testUseDependency() {
        TypeNode type = retrieveType("UseDependency", Accessibility.PUBLIC, ClassNode.class);

        //System.out.println("Table: " + tree.getTable().get(type, tree.getUtils().findType("D")));

        assertTrue(tree.hasUseDependency(type, tree.getUtils().findType("D")));
    }

    @Test
    public void testTypeD() {
        TypeNode type = retrieveType("D", Accessibility.PUBLIC, ClassNode.class);

        assertTrue(tree.hasUseDependency(tree.getUtils().findType("UseDependency"), type));
    }
}
