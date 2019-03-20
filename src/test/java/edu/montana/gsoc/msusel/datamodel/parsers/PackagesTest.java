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

import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.structural.File;
import edu.montana.gsoc.msusel.codetree.node.structural.Namespace;
import edu.montana.gsoc.msusel.codetree.node.type.Type;
import edu.montana.gsoc.msusel.datamodel.DataModelMediator;
import edu.montana.gsoc.msusel.datamodel.structural.File;
import edu.montana.gsoc.msusel.datamodel.structural.Namespace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PackagesTest {

    DataModelMediator tree;

    @Before
    public void setUp() throws Exception {
        JavaModelBuilder builder = new JavaModelBuilder();
        tree = builder.build("Test", "./data/java-test-project/Packages");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPackages() {
        List<Namespace> namespaces = tree.getNamespaces();

        assertEquals(2, namespaces.size());
        for (Namespace n : namespaces) {
            assertEquals(2, ((List<Type>) n.types()).size());
            assertEquals(2, ((List<File>) n.files()).size());
        }
    }

    @Test
    public void testPackageOuter() {
        Namespace outer = (Namespace) tree.findNamespace("outer");
        assertNotNull(outer);
        assertEquals(1, ((List<Namespace>) outer.namespaces()).size());
    }

    @Test
    public void testPackageInner() {

    }
}
