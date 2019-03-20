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
import edu.montana.gsoc.msusel.codetree.node.type.Type;
import edu.montana.gsoc.msusel.datamodel.Accessibility;
import edu.montana.gsoc.msusel.datamodel.Modifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FieldsTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaModelBuilder builder = new JavaModelBuilder();
        tree = builder.build("Test", "./data/java-test-project/Fields");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassA() {
        retrieveType("A", Accessibility.PUBLIC, Class.class);
    }

    @Test
    public void testClassB() {
        retrieveType("B", Accessibility.PUBLIC, Class.class);
    }

    @Test
    public void testFieldModifiers() {
        Type type = retrieveType("FieldModifiers", Accessibility.PUBLIC, Class.class);

        retrieveField(type, "XXX", "String", Accessibility.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        retrieveField(type, "yyy", "String", Accessibility.PROTECTED, Modifier.TRANSIENT);
        retrieveField(type, "zzz", "String", Accessibility.PRIVATE, Modifier.VOLATILE);
        retrieveField(type, "aaa", "String", Accessibility.DEFAULT);
    }

    @Test
    public void testMultipleFieldArrayType() {
        Type type = retrieveType("MultipleFieldArrayType", Accessibility.PUBLIC, Class.class);

        retrieveField(type, "field1", "String[]", Accessibility.PRIVATE);
        retrieveField(type, "field2", "String[][]", Accessibility.PRIVATE);
    }

    @Test
    public void testMultipleFields() {
        Type type = retrieveType("MultipleFields", Accessibility.PUBLIC, Class.class);

        retrieveField(type, "field1", "String", Accessibility.DEFAULT);
        retrieveField(type, "field2", "A", Accessibility.DEFAULT);
        retrieveField(type, "field3", "B", Accessibility.DEFAULT);
        retrieveField(type, "field4", "String", Accessibility.DEFAULT);
    }

    @Test
    public void testSingleFieldArrayType() {
        Type type = retrieveType("SingleFieldArrayType", Accessibility.PUBLIC, Class.class);

        retrieveField(type, "field", "A[]", Accessibility.PRIVATE);
    }

    @Test
    public void testSingleFieldKnownType() {
        Type type = retrieveType("SingleFieldKnownType", Accessibility.PUBLIC, Class.class);

        retrieveField(type, "field", "A", Accessibility.PRIVATE);
    }

    @Test
    public void testSingleFieldUnknownType() {
        Type type = retrieveType("SingleFieldUnknownType", Accessibility.PUBLIC, Class.class);

        retrieveField(type, "field", "String", Accessibility.PRIVATE);
    }

    @Test
    public void testSingleFieldPrimitiveType() {
        Type type = retrieveType("SingleFieldPrimitiveType", Accessibility.PUBLIC, Class.class);

        retrieveField(type, "x", "int", Accessibility.PRIVATE);
    }
}
