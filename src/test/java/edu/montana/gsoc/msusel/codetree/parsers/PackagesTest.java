package edu.montana.gsoc.msusel.codetree.parsers;

import codetree.CodeTree;
import codetree.node.structural.FileNode;
import codetree.node.structural.NamespaceNode;
import codetree.node.type.TypeNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PackagesTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Packages");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPackages() {
        List<NamespaceNode> namespaces = tree.getUtils().getNamespaces();

        assertEquals(2, namespaces.size());
        for (NamespaceNode n : namespaces) {
            assertEquals(2, ((List<TypeNode>) n.types()).size());
            assertEquals(2, ((List<FileNode>) n.files()).size());
        }
    }

    @Test
    public void testPackageOuter() {
        NamespaceNode outer = (NamespaceNode) tree.getUtils().findNamespace("outer");
        assertNotNull(outer);
        assertEquals(1, ((List<NamespaceNode>) outer.namespaces()).size());
    }

    @Test
    public void testPackageInner() {

    }
}
