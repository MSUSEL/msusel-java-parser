/**
 * The MIT License (MIT)
 *
 * MSUSEL Java Parser
 * Copyright (c) 2015-2017 Montana State University, Gianforte School of Computing,
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

public class FieldsTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Fields");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassA() {
        retrieveType("A", Accessibility.PUBLIC, ClassNode.class);
    }

    @Test
    public void testClassB() {
        retrieveType("B", Accessibility.PUBLIC, ClassNode.class);
    }

    @Test
    public void testFieldModifiers() {
        TypeNode type = retrieveType("FieldModifiers", Accessibility.PUBLIC, ClassNode.class);

        retrieveField(type, "XXX", "String", Accessibility.PUBLIC, Modifiers.STATIC, Modifiers.FINAL);
        retrieveField(type, "yyy", "String", Accessibility.PROTECTED, Modifiers.TRANSIENT);
        retrieveField(type, "zzz", "String", Accessibility.PRIVATE, Modifiers.VOLATILE);
        retrieveField(type, "aaa", "String", Accessibility.DEFAULT);
    }

    @Test
    public void testMultipleFieldArrayType() {
        TypeNode type = retrieveType("MultipleFieldArrayType", Accessibility.PUBLIC, ClassNode.class);

        retrieveField(type, "field1", "String[]", Accessibility.PRIVATE);
        retrieveField(type, "field2", "String[][]", Accessibility.PRIVATE);
    }

    @Test
    public void testMultipleFields() {
        TypeNode type = retrieveType("MultipleFields", Accessibility.PUBLIC, ClassNode.class);

        retrieveField(type, "field1", "String", Accessibility.DEFAULT);
        retrieveField(type, "field2", "A", Accessibility.DEFAULT);
        retrieveField(type, "field3", "B", Accessibility.DEFAULT);
        retrieveField(type, "field4", "String", Accessibility.DEFAULT);
    }

    @Test
    public void testSingleFieldArrayType() {
        TypeNode type = retrieveType("SingleFieldArrayType", Accessibility.PUBLIC, ClassNode.class);

        retrieveField(type, "field", "A[]", Accessibility.PRIVATE);
    }

    @Test
    public void testSingleFieldKnownType() {
        TypeNode type = retrieveType("SingleFieldKnownType", Accessibility.PUBLIC, ClassNode.class);

        retrieveField(type, "field", "A", Accessibility.PRIVATE);
    }

    @Test
    public void testSingleFieldUnknownType() {
        TypeNode type = retrieveType("SingleFieldUnknownType", Accessibility.PUBLIC, ClassNode.class);

        retrieveField(type, "field", "String", Accessibility.PRIVATE);
    }

    @Test
    public void testSingleFieldPrimitiveType() {
        TypeNode type = retrieveType("SingleFieldPrimitiveType", Accessibility.PUBLIC, ClassNode.class);

        retrieveField(type, "x", "int", Accessibility.PRIVATE);
    }
}
