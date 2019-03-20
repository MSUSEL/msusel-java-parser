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
import edu.montana.gsoc.msusel.codetree.node.Accessibility;
import edu.montana.gsoc.msusel.codetree.node.Modifier;
import edu.montana.gsoc.msusel.codetree.node.member.Field;
import edu.montana.gsoc.msusel.codetree.node.member.Initializer;
import edu.montana.gsoc.msusel.codetree.node.member.Method;
import edu.montana.gsoc.msusel.codetree.node.member.Parameter;
import edu.montana.gsoc.msusel.codetree.node.type.Class;
import edu.montana.gsoc.msusel.codetree.node.type.Type;
import edu.montana.gsoc.msusel.datamodel.DataModelMediator;
import edu.montana.gsoc.msusel.datamodel.TypeReference;
import edu.montana.gsoc.msusel.datamodel.Accessibility;
import edu.montana.gsoc.msusel.datamodel.Modifier;
import edu.montana.gsoc.msusel.datamodel.member.Field;
import edu.montana.gsoc.msusel.datamodel.member.Initializer;
import edu.montana.gsoc.msusel.datamodel.member.Method;
import edu.montana.gsoc.msusel.datamodel.member.Parameter;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BaseTestClass {

    DataModelMediator tree;

    public Type retrieveType(String typeName, Accessibility access, java.lang.Class clazz, Modifier... mods) {
        Type type = tree.findType(typeName);
        assertNotNull(type);
        assertTrue(clazz.isInstance(type));
        assertEquals(access, type.getAccessibility());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(type.hasModifier(mod)));

        return type;
    }

    public Field retrieveField(Type type, String fieldName, String typeName, Accessibility access, Modifier... mods) {
        Field field = (Field) type.getField(fieldName);
        assertNotNull(field);
        assertEquals(typeName, field.getType().name());
        assertEquals(access, field.getAccessibility());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(field.hasModifier(mod)));

        return field;
    }

    public Method retrieveMethod(Type type, String methodSig, String returnType, Accessibility access, Modifier... mods) {
        Method method = type.findMethodBySignature(methodSig);
        if (!returnType.isEmpty())
            assertEquals(returnType, method.getType().name());
        assertEquals(access, method.getAccessibility());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(method.hasModifier(mod)));

        return method;
    }

    public Parameter retrieveMethodParameter(Method method, String paramName, String paramType, Modifier... mods) {
        Parameter param = (Parameter) method.getParameterByName(paramName);
        assertEquals(paramName, param.name());
        assertEquals(paramType, param.getType().name());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(param.hasModifier(mod)));

        return param;
    }

    public TypeReference retrieveMethodException(Method method, String exception) {
        TypeReference ref = method.getExceptionByName(exception);
        assertNotNull(ref);
        assertEquals(exception, ref.name());

        return ref;
    }

    protected Initializer retrieveStaticInitializer(Class type, final int i) {
        Initializer init = type.getStaticInitializer(i);
        assertFalse(init.isInstance());
        assertEquals("<static_init$" + i + ">", init.name());

        return init;
    }

    protected Initializer retrieveInstanceInitializer(Class type, final int i) {
        Initializer init = type.getInstanceInitializer(i);
        assertTrue(init.isInstance());
        assertEquals("<init$" + i + ">", init.name());

        return init;
    }
}
