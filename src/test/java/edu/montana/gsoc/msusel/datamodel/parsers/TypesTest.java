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
import edu.montana.gsoc.msusel.codetree.node.type.Class;
import edu.montana.gsoc.msusel.codetree.node.type.Interface;
import edu.montana.gsoc.msusel.datamodel.Accessibility;
import edu.montana.gsoc.msusel.datamodel.Modifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypesTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaModelBuilder builder = new JavaModelBuilder();
        tree = builder.build("Test", "./data/java-test-project/Types");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassModifiers_1() {
        retrieveType("ClassModifiers", Accessibility.PUBLIC, Class.class, Modifier.STRICTFP);
    }

    @Test
    public void testClassModifiers_2() {
        retrieveType("ClassModifiers2", Accessibility.PROTECTED, Class.class);
    }

    @Test
    public void testClassModifiers_3() {
        retrieveType("ClassModifiers3", Accessibility.DEFAULT, Class.class);
    }

    @Test
    public void testClassModifiers_4() {
        retrieveType("ClassModifiers4", Accessibility.PUBLIC, Class.class, Modifier.FINAL);
    }

    @Test
    public void testClassModifiers_5() {
        retrieveType("ClassModifiers5", Accessibility.PRIVATE, Class.class, Modifier.STATIC);
    }

    @Test
    public void testClassModifiers_6() {
        retrieveType("ClassModifiers6", Accessibility.PUBLIC, Class.class, Modifier.ABSTRACT);
    }

    @Test
    public void testInterfaceModifiers_1() {
        retrieveType("InterfaceModifiers", Accessibility.PUBLIC, Interface.class);
    }

    @Test
    public void testInterfaceModifiers_2() {
        retrieveType("InterfaceModifiers2", Accessibility.DEFAULT, Interface.class);
    }
}