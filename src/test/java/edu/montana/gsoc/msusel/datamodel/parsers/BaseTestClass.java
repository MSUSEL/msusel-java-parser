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

import java.util.Arrays;
import java.util.List;

import edu.isu.isuese.datamodel.*;

import static org.junit.Assert.*;

public class BaseTestClass {

    BaseModelBuilder tree;

    public Type retrieveType(String typeName, Accessibility access, int typeType, Modifier... mods) {
        Type type = tree.findType(typeName);
        assertNotNull(type);
        assertEquals(typeType, type.getType());
        assertEquals(access, type.getAccessibility());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(type.hasModifier(mod)));

        return type;
    }

    public Field retrieveField(Type type, String fieldName, String typeName, Accessibility access, Modifier... mods) {
        Field field = type.getFieldWithName(fieldName);
        assertNotNull(field);
        assertEquals(typeName, field.getType().getTypeName());
        assertEquals(access, field.getAccessibility());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(field.hasModifier(mod)));

        return field;
    }

    public Method retrieveMethod(Type type, String methodSig, String returnType, Accessibility access, Modifier... mods) {
        Method method = type.findMethodBySignature(methodSig);
        if (!returnType.isEmpty())
            assertEquals(returnType, method.getType().getTypeName());
        assertEquals(access, method.getAccessibility());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(method.hasModifier(mod)));

        return method;
    }

    public Parameter retrieveMethodParameter(Method method, String paramName, String paramType, Modifier... mods) {
        Parameter param = method.getParameterByName(paramName);
        assertEquals(paramName, param.getName());
        assertEquals(paramType, param.getType().getTypeName());
        List<Modifier> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(param.hasModifier(mod)));

        return param;
    }

    public MethodException retrieveMethodException(Method method, String exception) {
        MethodException ref = method.getExceptionByName(exception);
        assertNotNull(ref);
        assertEquals(exception, ref.getTypeRef().getTypeName());

        return ref;
    }

    protected Initializer retrieveStaticInitializer(Type type, final int i) {
        Initializer init = type.getStaticInitializer(i);
        assertFalse(init.isInstance());
        assertEquals("<static_init$" + i + ">", init.getName());

        return init;
    }

    protected Initializer retrieveInstanceInitializer(Type type, final int i) {
        Initializer init = type.getInstanceInitializer(i);
        assertTrue(init.isInstance());
        assertEquals("<init$" + i + ">", init.getName());

        return init;
    }
}
