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

import edu.isu.isuese.datamodel.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GenericsTest extends BaseTestClass {

    public String getBasePath() {
        return "data/java-example-project/Methods";
    }

    @Test
    public void testClassMultiTypeParam() {
        Type type = proj.findTypeByQualifiedName("ClassMultiTypeParam");
        assertNotNull(type);
        assertEquals(Type.CLASS, type.getType());

        java.lang.System.out.println("Number of Template Params: " + type.getTemplateParams().size());
        for (TemplateParam node : type.getTemplateParams())
            java.lang.System.out.println("\tName: " + node.getTypeRefs().get(0).getTypeName());

        TemplateParam typeParam = type.getTemplateParam("X");
        assertNotNull(typeParam);

        typeParam = type.getTemplateParam("Y");
        assertNotNull(typeParam);

        Field field = type.getFieldWithName("field1");
        assertNotNull(field);
        assertEquals("X", field.getType().getTypeName());

        field = type.getFieldWithName("field2");
        assertNotNull(field);
        assertEquals("Y", field.getType().getTypeName());
    }

    @Test
    public void testClassSingleTypeParam() {
        Type type = tree.findType("ClassSingleTypeParam");
        assertNotNull(type);
        assertEquals(Type.CLASS, type.getType());

        Field field = type.getFieldWithName("field");
        assertNotNull(field);
        assertEquals("T", field.getType().getTypeName());

        Method method = type.getMethodWithName("method1");
        assertNotNull(method);
        assertEquals("T", method.getType().getTypeName());

        method = type.getMethodWithName("method2");
        assertNotNull(method);
        assertEquals("K", method.getType().getTypeName());
        assertEquals("K", method.getTemplateParams().get(0).getTypeRefs().get(0).getTypeName());

        method = type.getMethodWithName("method3");
        assertNotNull(method);
        assertEquals("X", method.getType().getTypeName());

        TemplateParam typeParam = method.getTemplateParam("X");
        assertNotNull(typeParam);

        typeParam = method.getTemplateParam("Y");
        assertNotNull(typeParam);

        Parameter param = method.getParameterByName("param");
        assertNotNull(param);
        assertEquals("Y", param.getType().getTypeName());

        method = type.getMethodWithName("method4");
        assertNotNull(method);
        assertEquals("X", method.getType().getTypeName());

        typeParam = method.getTemplateParam("X");
        assertNotNull(typeParam);
    }

    @Test
    public void testClassSingleTypeParamMultiBound() {
        Type type = tree.findType("ClassSingleTypeParamMultiBound");
        assertNotNull(type);
        assertEquals(Type.CLASS, type.getType());

        TemplateParam typeParam = type.getTemplateParam("X");
        assertNotNull(typeParam);
    }

    @Test
    public void testClassWithCollections() {
        Type type = tree.findType("ClassWithCollections");
        assertNotNull(type);
        assertEquals(Type.CLASS, type.getType());

        Field field = (Field) type.getFieldWithName("field1");
        assertNotNull(field);
        assertEquals("List", field.getType().getTypeName());
        Assert.assertEquals("String", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0).getName());

        field = (Field) type.getFieldWithName("field2");
        assertNotNull(field);
        assertEquals("Map", field.getType().getTypeName());
        assertEquals("String", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0).getName());
        assertEquals("Integer", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(1).getName());

        field = (Field) type.getFieldWithName("field3");
        assertNotNull(field);
        assertEquals("List", field.getType().getTypeName());
        TypeReference typeRef = ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0);
        assertEquals("?", typeRef.getName());
        assertTrue(typeRef instanceof WildCardTypeRef);
        WildCardTypeRef wildRef = (WildCardTypeRef) typeRef;
        List<TypeReference> bounds = wildRef.getBounds();
        assertEquals("Number", bounds.get(0).getName());

        field = (Field) type.getFieldWithName("field4");
        assertNotNull(field);
        assertEquals("List", field.getType().getTypeName());
        typeRef = ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0);
        assertEquals("List", typeRef.getName());
        assertTrue(typeRef instanceof TypeRef);
        TypeRef typed = (TypeRef) typeRef;
        assertNotNull(typed.getTypeArgs());
        System.out.println("Typed Size: " + ((List<TypeReference>) typed.getTypeArgs()).size());
        assertEquals("String", ((List<TypeReference>)((TypeRef)((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0)).getTypeArgs()).get(0).getName());

        field = (Field) type.getFieldWithName("field5");
        assertNotNull(field);
        assertEquals("List", field.getType().getTypeName());
        assertEquals("?", ((List<TypeReference>)((TypeRef) field.getType()).getTypeArgs()).get(0).getName());
    }
}
