/**
 * The MIT License (MIT)
 * <p>
 * MSUSEL Java Parser
 * Copyright (c) 2015-2017 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import edu.montana.gsoc.msusel.codetree.node.member.EnumLiteralNode;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.type.EnumNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class EnumsTest extends BaseTestClass {

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Enums");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEnums() {
        TypeNode type = retrieveType("Enums", Accessibility.PUBLIC, EnumNode.class);

        List<EnumLiteralNode> literals = (List<EnumLiteralNode>) ((EnumNode) type).getLiterals();
    }

    @Test
    public void testEnums2() {
        TypeNode type = retrieveType("Enums2", Accessibility.PUBLIC, EnumNode.class);

        List<EnumLiteralNode> literals = (List<EnumLiteralNode>) ((EnumNode) type).getLiterals();
        List<MethodNode> methods = (List<MethodNode>) type.methods();
    }
}
