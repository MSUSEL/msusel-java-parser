package edu.montana.gsoc.msusel.codetree.parsers;

import edu.montana.gsoc.msusel.codetree.CodeTree;
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
        CodeTree tree = new CodeTree();
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
