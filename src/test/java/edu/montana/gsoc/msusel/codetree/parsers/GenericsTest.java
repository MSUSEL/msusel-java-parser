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

import edu.montana.gsoc.msusel.codetree.AbstractTypeRef;
import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.member.FieldNode;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.member.ParameterNode;
import edu.montana.gsoc.msusel.codetree.node.type.ClassNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import edu.montana.gsoc.msusel.codetree.typeref.TypeRef;
import edu.montana.gsoc.msusel.codetree.typeref.TypeVarTypeRef;
import edu.montana.gsoc.msusel.codetree.typeref.WildCardTypeRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GenericsTest {

    CodeTree tree;

    @Before
    public void setUp() throws Exception {
        JavaCodeTreeBuilder builder = new JavaCodeTreeBuilder();
        tree = builder.build("Test", "./data/java-test-project/Generics");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassMultiTypeParam() {
        TypeNode type = tree.getUtils().findType("ClassMultiTypeParam");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        System.out.println("Number of Template Params: " + type.getTemplateParams().size());
        for (TypeVarTypeRef node : type.getTemplateParams())
            System.out.println("\tName: " + node.name());

        TypeVarTypeRef typeParam = type.getTypeParamByName("X");
        assertNotNull(typeParam);

        typeParam = type.getTypeParamByName("Y");
        assertNotNull(typeParam);

        FieldNode field = (FieldNode) type.getField("field1");
        assertNotNull(field);
        assertEquals("X", field.getType().name());

        field = (FieldNode) type.getField("field2");
        assertNotNull(field);
        assertEquals("Y", field.getType().name());
    }

    @Test
    public void testClassSingleTypeParam() {
        TypeNode type = tree.getUtils().findType("ClassSingleTypeParam");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        FieldNode field = (FieldNode) type.getField("field");
        assertNotNull(field);
        assertEquals("T", field.getType().name());

        MethodNode method = (MethodNode) type.getMethod("method1");
        assertNotNull(method);
        assertEquals("T", method.getType().name());

        method = (MethodNode) type.getMethod("method2");
        assertNotNull(method);
        assertEquals("K", method.getType().name());
        assertEquals("K", ((List<TypeVarTypeRef>) method.getTypeParams()).get(0).name());

        method = (MethodNode) type.getMethod("method3");
        assertNotNull(method);
        assertEquals("X", method.getType().name());

        TypeVarTypeRef typeParam = method.getTypeParamByName("X");
        assertNotNull(typeParam);

        typeParam = method.getTypeParamByName("Y");
        assertNotNull(typeParam);

        ParameterNode param = (ParameterNode) method.getParameterByName("param");
        assertNotNull(param);
        assertEquals("Y", param.getType().name());

        method = (MethodNode) type.getMethod("method4");
        assertNotNull(method);
        assertEquals("X", method.getType().name());

        typeParam = method.getTypeParamByName("X");
        assertNotNull(typeParam);
    }

    @Test
    public void testClassSingleTypeParamMultiBound() {
        TypeNode type = tree.getUtils().findType("ClassSingleTypeParamMultiBound");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        TypeVarTypeRef typeParam = type.getTypeParamByName("X");
        assertNotNull(typeParam);
    }

    @Test
    public void testClassWithCollections() {
        TypeNode type = tree.getUtils().findType("ClassWithCollections");
        assertNotNull(type);
        assertTrue(type instanceof ClassNode);

        FieldNode field = (FieldNode) type.getField("field1");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        assertEquals("String", ((List<AbstractTypeRef>)((TypeRef) field.getType()).getTypeArgs()).get(0).name());

        field = (FieldNode) type.getField("field2");
        assertNotNull(field);
        assertEquals("Map", field.getType().name());
        assertEquals("String", ((List<AbstractTypeRef>)((TypeRef) field.getType()).getTypeArgs()).get(0).name());
        assertEquals("Integer", ((List<AbstractTypeRef>)((TypeRef) field.getType()).getTypeArgs()).get(1).name());

        field = (FieldNode) type.getField("field3");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        AbstractTypeRef typeRef = ((List<AbstractTypeRef>)((TypeRef) field.getType()).getTypeArgs()).get(0);
        assertEquals("?", typeRef.name());
        assertTrue(typeRef instanceof WildCardTypeRef);
        WildCardTypeRef wildRef = (WildCardTypeRef) typeRef;
        List<AbstractTypeRef> bounds = wildRef.getBounds();
        assertEquals("Number", bounds.get(0).name());

        field = (FieldNode) type.getField("field4");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        typeRef = ((List<AbstractTypeRef>)((TypeRef) field.getType()).getTypeArgs()).get(0);
        assertEquals("List", typeRef.name());
        assertTrue(typeRef instanceof TypeRef);
        TypeRef typed = (TypeRef) typeRef;
        assertNotNull(typed.getTypeArgs());
        System.out.println("Typed Size: " + ((List<AbstractTypeRef>) typed.getTypeArgs()).size());
        assertEquals("String", ((List<AbstractTypeRef>)((TypeRef)((List<AbstractTypeRef>)((TypeRef) field.getType()).getTypeArgs()).get(0)).getTypeArgs()).get(0).name());

        field = (FieldNode) type.getField("field5");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        assertEquals("?", ((List<AbstractTypeRef>)((TypeRef) field.getType()).getTypeArgs()).get(0).name());
    }
}
