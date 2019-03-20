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

import edu.montana.gsoc.msusel.codetree.AbstractTypeRef;
import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.member.Field;
import edu.montana.gsoc.msusel.codetree.node.member.Method;
import edu.montana.gsoc.msusel.codetree.node.member.Parameter;
import edu.montana.gsoc.msusel.codetree.node.type.Class;
import edu.montana.gsoc.msusel.codetree.node.type.Type;
import edu.montana.gsoc.msusel.codetree.typeref.TypeRef;
import edu.montana.gsoc.msusel.codetree.typeref.TypeVarTypeRef;
import edu.montana.gsoc.msusel.codetree.typeref.WildCardTypeRef;
import edu.montana.gsoc.msusel.datamodel.DataModelMediator;
import edu.montana.gsoc.msusel.datamodel.TypeReference;
import edu.montana.gsoc.msusel.datamodel.member.Field;
import edu.montana.gsoc.msusel.datamodel.member.Method;
import edu.montana.gsoc.msusel.datamodel.member.Parameter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GenericsTest {

    DataModelMediator tree;

    @Before
    public void setUp() throws Exception {
        JavaModelBuilder builder = new JavaModelBuilder();
        tree = builder.build("Test", "./data/java-test-project/Generics");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClassMultiTypeParam() {
        Type type = tree.findType("ClassMultiTypeParam");
        assertNotNull(type);
        assertTrue(type instanceof Class);

        System.out.println("Number of Template Params: " + type.getTemplateParams().size());
        for (TypeVarTypeRef node : type.getTemplateParams())
            System.out.println("\tName: " + node.name());

        TypeVarTypeRef typeParam = type.getTypeParamByName("X");
        assertNotNull(typeParam);

        typeParam = type.getTypeParamByName("Y");
        assertNotNull(typeParam);

        Field field = (Field) type.getField("field1");
        assertNotNull(field);
        assertEquals("X", field.getType().name());

        field = (Field) type.getField("field2");
        assertNotNull(field);
        assertEquals("Y", field.getType().name());
    }

    @Test
    public void testClassSingleTypeParam() {
        Type type = tree.findType("ClassSingleTypeParam");
        assertNotNull(type);
        assertTrue(type instanceof Class);

        Field field = (Field) type.getField("field");
        assertNotNull(field);
        assertEquals("T", field.getType().name());

        Method method = (Method) type.getMethod("method1");
        assertNotNull(method);
        assertEquals("T", method.getType().name());

        method = (Method) type.getMethod("method2");
        assertNotNull(method);
        assertEquals("K", method.getType().name());
        assertEquals("K", ((List<TypeVarTypeRef>) method.getTypeParams()).get(0).name());

        method = (Method) type.getMethod("method3");
        assertNotNull(method);
        assertEquals("X", method.getType().name());

        TypeVarTypeRef typeParam = method.getTypeParamByName("X");
        assertNotNull(typeParam);

        typeParam = method.getTypeParamByName("Y");
        assertNotNull(typeParam);

        Parameter param = (Parameter) method.getParameterByName("param");
        assertNotNull(param);
        assertEquals("Y", param.getType().name());

        method = (Method) type.getMethod("method4");
        assertNotNull(method);
        assertEquals("X", method.getType().name());

        typeParam = method.getTypeParamByName("X");
        assertNotNull(typeParam);
    }

    @Test
    public void testClassSingleTypeParamMultiBound() {
        Type type = tree.findType("ClassSingleTypeParamMultiBound");
        assertNotNull(type);
        assertTrue(type instanceof Class);

        TypeVarTypeRef typeParam = type.getTypeParamByName("X");
        assertNotNull(typeParam);
    }

    @Test
    public void testClassWithCollections() {
        Type type = tree.findType("ClassWithCollections");
        assertNotNull(type);
        assertTrue(type instanceof Class);

        Field field = (Field) type.getField("field1");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        Assert.assertEquals("String", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0).name());

        field = (Field) type.getField("field2");
        assertNotNull(field);
        assertEquals("Map", field.getType().name());
        assertEquals("String", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0).name());
        assertEquals("Integer", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(1).name());

        field = (Field) type.getField("field3");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        TypeReference typeRef = ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0);
        assertEquals("?", typeRef.name());
        assertTrue(typeRef instanceof WildCardTypeRef);
        WildCardTypeRef wildRef = (WildCardTypeRef) typeRef;
        List<TypeReference> bounds = wildRef.getBounds();
        assertEquals("Number", bounds.get(0).name());

        field = (Field) type.getField("field4");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        typeRef = ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0);
        assertEquals("List", typeRef.name());
        assertTrue(typeRef instanceof TypeRef);
        TypeRef typed = (TypeRef) typeRef;
        assertNotNull(typed.getTypeArgs());
        System.out.println("Typed Size: " + ((List<TypeReference>) typed.getTypeArgs()).size());
        assertEquals("String", ((List<TypeReference>)((TypeRef)((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0)).getTypeArgs()).get(0).name());

        field = (Field) type.getField("field5");
        assertNotNull(field);
        assertEquals("List", field.getType().name());
        assertEquals("?", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0).name());
    }
}
