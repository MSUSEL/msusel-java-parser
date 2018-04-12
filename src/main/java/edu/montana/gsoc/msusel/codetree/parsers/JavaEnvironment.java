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

import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.DefaultCodeTree;
import edu.montana.gsoc.msusel.codetree.environment.EnvironmentLoader;
import edu.montana.gsoc.msusel.codetree.node.structural.NamespaceNode;
import edu.montana.gsoc.msusel.codetree.node.structural.ProjectNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;

import java.io.InputStream;

/**
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class JavaEnvironment extends EnvironmentLoader {

    public JavaEnvironment(CodeTree tree) {
        super(tree);
    }

    public void find(String ns, String name) {
        InputStream is = JavaEnvironment.class.getResourceAsStream("/environment/" + ns + ".types");
        find(ns, is, name);
    }

    public static void main(String args[]) {
        CodeTree tree = new DefaultCodeTree();
        tree.setProject(ProjectNode.builder().key("Test").create());
        JavaEnvironment je = new JavaEnvironment(tree);
        je.find("java.lang", "System");

        NamespaceNode ns = tree.languageNamespace("java.lang");
        assert ns != null;
        System.out.println("NS: " + ns);
        TypeNode type = ns.findTypeByName("System");
        assert (type != null);
    }
}
