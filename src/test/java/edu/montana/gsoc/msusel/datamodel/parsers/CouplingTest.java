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
package edu.montana.gsoc.msusel.datamodel.parsers;

import edu.montana.gsoc.msusel.codetree.node.Accessibility;
import edu.montana.gsoc.msusel.codetree.node.Modifier;
import edu.montana.gsoc.msusel.codetree.node.type.Class;
import edu.montana.gsoc.msusel.codetree.node.type.Type;
import edu.montana.gsoc.msusel.datamodel.Accessibility;
import edu.montana.gsoc.msusel.datamodel.Modifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CouplingTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaModelBuilder builder = new JavaModelBuilder();
        tree = builder.build("Test", "./data/java-test-project/Coupling");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBiDirectionalAssociation() {
        Type type = retrieveType("BiDirectionalAssociation", Accessibility.PUBLIC, Class.class);

        assertTrue(tree.hasBiDirectionalAssociation(type, tree.findType("B")));
    }

    @Test
    public void testTypeB() {
        Type type = retrieveType("B", Accessibility.PUBLIC, Class.class);

        assertTrue(tree.hasBiDirectionalAssociation(type, tree.findType("BiDirectionalAssociation")));
    }

    @Test
    public void testContainedClasses() {
        Type type = retrieveType("ContainedClasses", Accessibility.PUBLIC, Class.class);
        Type cont = retrieveType("ContainedClasses.ContainedClass", Accessibility.PRIVATE, Class.class, Modifier.STATIC);

        assertTrue(tree.hasContainmentRelation(cont, type));
    }

    @Test
    public void testContainedClass_ContainedClass() {
        Type type = retrieveType("ContainedClasses", Accessibility.PUBLIC, Class.class);
        Type cont = retrieveType("ContainedClasses.ContainedClass", Accessibility.PRIVATE, Class.class, Modifier.STATIC);

        assertTrue(tree.hasContainmentRelation(cont, type));
    }

    @Test
    public void testUniDirectionalAssociation() {
        Type type = retrieveType("UniDirectionalAssociation", Accessibility.PUBLIC, Class.class);

        assertTrue(tree.hasUniDirectionalAssociation(type, tree.findType("C")));
    }

    @Test
    public void testTypeC() {
        Type type = retrieveType("C", Accessibility.PUBLIC, Class.class);

        assertTrue(tree.hasUniDirectionalAssociation(tree.findType("UniDirectionalAssociation"), type));
    }

    @Test
    public void testUseDependency() {
        Type type = retrieveType("UseDependency", Accessibility.PUBLIC, Class.class);

        //Project.out.println("Table: " + model.getTable().get(type, model.findType("D")));

        assertTrue(tree.hasUseDependency(type, tree.findType("D")));
    }

    @Test
    public void testTypeD() {
        Type type = retrieveType("D", Accessibility.PUBLIC, Class.class);

        assertTrue(tree.hasUseDependency(tree.findType("UseDependency"), type));
    }
}
