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

import edu.montana.gsoc.msusel.codetree.AbstractTypeRef;
import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.Accessibility;
import edu.montana.gsoc.msusel.codetree.node.Modifiers;
import edu.montana.gsoc.msusel.codetree.node.member.FieldNode;
import edu.montana.gsoc.msusel.codetree.node.member.InitializerNode;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.member.ParameterNode;
import edu.montana.gsoc.msusel.codetree.node.type.ClassNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BaseTestClass {

    CodeTree tree;

    public TypeNode retrieveType(String typeName, Accessibility access, Class clazz, Modifiers... mods) {
        TypeNode type = tree.getUtils().findType(typeName);
        assertNotNull(type);
        assertTrue(clazz.isInstance(type));
        assertEquals(access, type.getAccessibility());
        List<Modifiers> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(type.hasModifier(mod)));

        return type;
    }

    public FieldNode retrieveField(TypeNode type, String fieldName, String typeName, Accessibility access, Modifiers... mods) {
        FieldNode field = (FieldNode) type.getField(fieldName);
        assertNotNull(field);
        assertEquals(typeName, field.getType().name());
        assertEquals(access, field.getAccessibility());
        List<Modifiers> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(field.hasModifier(mod)));

        return field;
    }

    public MethodNode retrieveMethod(TypeNode type, String methodSig, String returnType, Accessibility access, Modifiers... mods) {
        MethodNode method = type.findMethodBySignature(methodSig);
        if (!returnType.isEmpty())
            assertEquals(returnType, method.getType().name());
        assertEquals(access, method.getAccessibility());
        List<Modifiers> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(method.hasModifier(mod)));

        return method;
    }

    public ParameterNode retrieveMethodParameter(MethodNode method, String paramName, String paramType, Modifiers... mods) {
        ParameterNode param = (ParameterNode) method.getParameterByName(paramName);
        assertEquals(paramName, param.name());
        assertEquals(paramType, param.getType().name());
        List<Modifiers> modList = Arrays.asList(mods);
        modList.forEach(mod -> assertTrue(param.hasModifier(mod)));

        return param;
    }

    public AbstractTypeRef retrieveMethodException(MethodNode method, String exception) {
        AbstractTypeRef ref = method.getExceptionByName(exception);
        assertNotNull(ref);
        assertEquals(exception, ref.name());

        return ref;
    }

    protected InitializerNode retrieveStaticInitializer(ClassNode type, final int i) {
        InitializerNode init = type.getStaticInitializer(i);
        assertFalse(init.isInstance());
        assertEquals("<static_init$" + i + ">", init.name());

        return init;
    }

    protected InitializerNode retrieveInstanceInitializer(ClassNode type, final int i) {
        InitializerNode init = type.getInstanceInitializer(i);
        assertTrue(init.isInstance());
        assertEquals("<init$" + i + ">", init.name());

        return init;
    }
}
