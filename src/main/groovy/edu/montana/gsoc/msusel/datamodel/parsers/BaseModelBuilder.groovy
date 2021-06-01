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
package edu.montana.gsoc.msusel.datamodel.parsers

import com.google.common.collect.Sets
import edu.isu.isuese.datamodel.*
import edu.isu.isuese.datamodel.util.DBCredentials
import edu.isu.isuese.datamodel.util.DBManager
import groovy.util.logging.Log4j2

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Log4j2
abstract class BaseModelBuilder {

    System system
    Project proj
    Namespace namespace
    File file
    Stack<Type> types = []
    Stack<Member> methods = []
    Stack<Set<String>> scopes = []
    TemplateParam currentTypeParam
    Parameter currentParam
    DBCredentials credentials

    void withDb(Closure cl) {
        DBManager.instance.open(credentials)
        cl.call()
        DBManager.instance.close()
    }

    BaseModelBuilder(Project proj, File file, DBCredentials credentials) {
        this.credentials = credentials

        if (!proj || !file) {
            throw new IllegalArgumentException("Project and File must not be null")
        }

        DBManager.instance.open(credentials)
        this.system = proj.getParentSystem()
        DBManager.instance.close()

        if (this.system == null) {
            throw new IllegalArgumentException("Project must be a part of a system")
        }

        this.proj = proj
        this.file = file

        DBManager.instance.open(credentials)
        if (file.getParentNamespace() != null)
            namespace = file.getParentNamespace()
        else
            namespace = proj.getDefaultNamespace()
        DBManager.instance.close()
    }

    //////////////////////
    // Setup Methods
    //////////////////////
    void setFile(File file) {
        DBManager.instance.open(credentials)
        this.file = file
        if (file.getParentNamespace() != null) {
            namespace = file.getParentNamespace()
        }
        DBManager.instance.close()

    }

    //////////////////////
    // Structures
    //////////////////////
    void createNamespace(String name) {
        log.atInfo().log("Finding/Creating Namespace: " + name)
        String[] packageNames
        if (name.contains("."))
            packageNames = name.split(/\./)
        else
            packageNames = [name]

        Namespace current = null;
        for (String seg : packageNames) {
            DBManager.instance.open(credentials)
            String n = current == null ? seg : String.join(".", current.getName(), seg)
            String relPath = seg

            boolean hasNamespace = proj.hasNamespace(n)
            DBManager.instance.close()

            if (!hasNamespace) {
                log.atInfo().log("Creating Namespace: " + n)
                Namespace parent = current

                DBManager.instance.open(credentials)
                current = Namespace.builder()
                        .name(n)
                        .nsKey(n)
                        .relPath(relPath)
                        .create()
                if (parent != null)
                    parent.addNamespace(current)
                proj.addNamespace(current)
                current.updateKey()
                DBManager.instance.close()
            } else {
                DBManager.instance.open(credentials)
                Namespace parent = current
                setParentNamespace(parent, current)
                current = proj.findNamespace(n)
                DBManager.instance.close()
            }

            DBManager.instance.open(credentials)
            current.addFile(file)
            namespace = current

            file.updateKey()
            file.refresh()
            DBManager.instance.close()
        }
    }

    void createImport(String name, int start, int end) {
        DBManager.instance.open(credentials)
        Import imp = Import.findFirst("name = ?", name)
        if (!imp) {
            log.atInfo().log("Creating import with name: " + name)
            imp = Import.builder().name(name).start(start).end(end).create()
        }

        file.addImport(imp)
        DBManager.instance.close()
    }

    //////////////////////
    // Types
    //////////////////////
    void endType() {
        if (!types) {
            Type type = types.pop()
            if (types.isEmpty()) {
                DBManager.instance.open(credentials)
                if (file.getTypeByName(type.getName()) == null)
                    file.addType(type)
                DBManager.instance.close()
            } else {
                DBManager.instance.open(credentials)
                if (types.peek().getTypeByName(type.getName()) == null)
                    types.peek().addType(type)
                DBManager.instance.close()
            }

            DBManager.instance.open(credentials)
            type.updateKey()
            DBManager.instance.close()
        }

    }

    void findClass(String name) {
        Type type
        if (types) {
            DBManager.instance.open(credentials)
            type = types.peek().getTypeByName(name)
            DBManager.instance.close()
        } else {
            DBManager.instance.open(credentials)
            type = namespace.getTypeByName(name)
            DBManager.instance.close()
        }

        if (type != null)
            types.push(type)
        else
            createClass(name, 1, 1)
    }

    void createClass(String name, int start, int stop) {
        DBManager.instance.open(credentials)
        if (types && types.peek().getTypeByName(name) != null)
            types.push(types.peek().getTypeByName(name))
        else if (namespace.getTypeByName(name) != null) {
            Type t = namespace.getTypeByName(name)
            t.setEnd(stop)
            t.setStart(start)
            t.save()
            types.push(t)
        } else {
            Class cls = Class.builder()
                    .name(name)
                    .compKey(name)
                    .accessibility(Accessibility.PUBLIC)
                    .start(start)
                    .end(stop)
                    .create()
            if (types) {
                types.peek().addType(cls)
            }

            namespace.addType(cls)
            file.addType(cls)

            cls.updateKey()
            types.push(cls)
        }
        DBManager.instance.close()
    }

    void findEnum(String name) {
        Type type
        if (types) {
            DBManager.instance.open(credentials)
            type = types.peek().getTypeByName(name)
            DBManager.instance.close()
        } else {
            DBManager.instance.open(credentials)
            type = namespace.getTypeByName(name)
            DBManager.instance.close()
        }

        if (type != null)
            types.push(type)
        else createEnum(name, 1, 1)
    }

    void createEnum(String name, int start, int stop) {
        DBManager.instance.open(credentials)
        if (types && types.peek().getTypeByName(name) != null)
            types.push(types.peek().getTypeByName(name))
        else if (namespace.getTypeByName(name) != null) {
            Type t = namespace.getTypeByName(name)
            t.setEnd(stop)
            t.setStart(start)
            t.save()
            types.push(t)
        } else {
            Enum enm = Enum.builder()
                    .name(name)
                    .compKey(name)
                    .accessibility(Accessibility.PUBLIC)
                    .start(start)
                    .end(stop)
                    .create()
            if (types) {
                types.peek().addType(enm)
            }

            namespace.addType(enm)
            file.addType(enm)

            enm.updateKey()
            types.push(enm)
        }
        DBManager.instance.close()

    }

    void findInterface(String name) {
        Type type = null
        if (types) {
            DBManager.instance.open(credentials)
            type = types.peek().getTypeByName(name)
            DBManager.instance.close()
        } else {
            DBManager.instance.open(credentials)
            type = namespace.getTypeByName(name)
            DBManager.instance.close()
        }

        if (!types.isEmpty() && type != null)
            types.push(type)
        else
            createInterface(name, 1, 1)
    }

    void createInterface(String name, int start, int stop) {
        DBManager.instance.open(credentials)
        if (types && types.peek().getTypeByName(name) != null)
            types.push(types.peek().getTypeByName(name))
        else if (namespace.getTypeByName(name) != null) {
            Type t = namespace.getTypeByName(name)
            t.setEnd(stop)
            t.setStart(start)
            t.save()
            types.push(t)
        } else {
            Interface ifc = Interface.builder()
                    .name(name)
                    .compKey(name)
                    .accessibility(Accessibility.PUBLIC)
                    .start(start)
                    .end(stop)
                    .create()
            if (types) {
                types.peek().addType(ifc)
            }

            namespace.addType(ifc)
            file.addType(ifc)

            ifc.updateKey()
            types.push(ifc)
        }
        DBManager.instance.close()
    }

    void findAnnotation(String name) {
        findInterface(name)
    }

    void createAnnotation(String name, int start, int stop) {
        createInterface(name, start, stop)
    }

    void setTypeModifiers(List<String> modifiers) {
        if (types) {
            modifiers.each { mod ->
                DBManager.instance.open(credentials)
                Accessibility access = handleAccessibility(mod)
                Modifier modifier = handleNamedModifiers(mod)
                if (access) types.peek().setAccessibility(access)
                if (modifier) types.peek().addModifier(modifier)
                DBManager.instance.close()
            }
        }
    }

    void createTypeTypeParameter(String name) {
        if (types) {
            DBManager.instance.open(credentials)
            if (types.peek().hasTemplateParam(name))
                currentTypeParam = types.peek().getTemplateParam(name)
            else {
                currentTypeParam = TemplateParam.builder().name(name).create()

                if (types) {
                    types.peek().addTemplateParam(currentTypeParam)
                }
            }
            DBManager.instance.close()
        }

    }

    ///////////////////
    // Methods
    ///////////////////

    void findInitializer(String name, boolean instance) {
        DBManager.instance.open(credentials)
        Initializer init = !types.isEmpty() ? types.peek().getInitializerWithName(name) : null
        DBManager.instance.close()

        if (init) methods.push(init)
        scopes.push(Sets.newHashSet())
    }

    void createInitializer(String name, boolean instance, int start, int end) {
        if (types) {
            DBManager.instance.open(credentials)
            if (types.peek().hasInitializerWithName(name)) {
                Initializer i = types.peek().getInitializerWithName(name)
                i.setEnd(end)
                i.setStart(start)
                i.save()
                methods.push(i)
            } else {
                Initializer init = Initializer.builder()
                        .name(name)
                        .compKey(name)
                        .accessibility(Accessibility.PUBLIC)
                        .start(start)
                        .end(end)
                        .instance(instance)
                        .create()
                types.peek().addMember(init)
                init.updateKey()
                methods.push(init)
            }
            DBManager.instance.close()
        }

        scopes.push(Sets.newHashSet())
    }

    void findMethod(String signature) {
        DBManager.instance.open(credentials)
        Method meth = types ? Method.findFirst("compKey = ?", "${types.peek().getCompKey()}#$signature") : null
        DBManager.instance.close()

        scopes.push(Sets.newHashSet())
        if (meth) methods.push(meth)
    }

    void createMethod(String name, int start, int end) {
        if (types) {
            DBManager.instance.open(credentials)
            if (types.peek().hasMethodWithName(name)) {
                Method m = types.peek().getMethodWithName(name)
                m.setEnd(end)
                m.setStart(start)
                m.save()
                methods.push(m)
            } else {
                Method meth = Method.builder()
                        .name(name)
                        .compKey(name)
                        .accessibility(Accessibility.PUBLIC)
                        .start(start)
                        .end(end)
                        .create()
                types.peek().addMember(meth)
                meth.updateKey()
                methods.push(meth)
            }
            DBManager.instance.close()
        }

        scopes.push(Sets.newHashSet())
    }

    void findConstructor(String signature) {
        DBManager.instance.open(credentials)
        Constructor cons = types ? Constructor.findFirst("compKey = ?", "${types.peek().getCompKey()}#$signature") : null as Constructor
        DBManager.instance.close()

        scopes.push(Sets.newHashSet())
        if (cons) methods.push(cons)
    }

    void createConstructor(String name, int start, int end) {
        if (types) {
            DBManager.instance.open(credentials)

            Constructor cons = Constructor.creator()
                    .name(name)
                    .compKey(name)
                    .accessibility(Accessibility.PUBLIC)
                    .start(start)
                    .end(end)
                    .create()
            types.peek().addMember(cons)
            cons.updateKey()
            methods.push(cons)

            DBManager.instance.close()
        }
        scopes.push(Sets.newHashSet())
    }

    void createMethodParameter() {
        DBManager.instance.open(credentials)
        currentParam = Parameter.builder().varg(false).create()
        if (methods && methods.peek() instanceof Method)
            ((Method) methods.peek()).addParameter(currentParam)
        DBManager.instance.close()
    }

    void setVariableParameter(boolean varg) {
        DBManager.instance.open(credentials)
        currentParam.setVarg(varg)
        DBManager.instance.close()
    }

    void addParameterModifier(String mod) {
        DBManager.instance.open(credentials)
        currentParam?.addModifier(mod)
        DBManager.instance.close()
    }

    void setMethodModifiers(List<String> mods) {
        if (methods) {
            mods.each { mod ->
                DBManager.instance.open(credentials)
                Accessibility access = handleAccessibility(mod)
                Modifier modifier = handleNamedModifiers(mod)
                if (access) methods.peek().setAccessibility(access)
                if (modifier) methods.peek().addModifier(modifier)
                DBManager.instance.close()
            }
        }
    }

    void createMethodTypeParameter(String name) {
        DBManager.instance.open(credentials)
        currentTypeParam = TemplateParam.builder().name(name).create()
        if (methods && methods.peek() instanceof Method) {
            ((Method) methods.peek()).addTemplateParam(currentTypeParam)
        }
        DBManager.instance.close()
    }

    void setParameterType(String type) {
        Type t = findType(type)

        DBManager.instance.open(credentials)
        currentParam.setType(t.createTypeRef())
        DBManager.instance.close()
    }

    void setParameterPrimitiveType(String type) {
        DBManager.instance.open(credentials)
        currentParam.setType(TypeRef.createPrimitiveTypeRef(type))
        DBManager.instance.close()
    }

    void setMethodReturnType(String type) {
        Type t = findType(type)

        if (t && methods && methods.peek() instanceof Method) {
            DBManager.instance.open(credentials)
            ((Method) methods.peek()).setReturnType(t.createTypeRef())
            DBManager.instance.close()
        }

    }

    void setMethodReturnTypeVoid() {
        if (methods && methods.peek() instanceof Method) {
            DBManager.instance.open(credentials)
            ((Method) methods.peek()).setReturnType(TypeRef.createPrimitiveTypeRef("void"))
            DBManager.instance.close()
        }

    }

    void setMethodReturnPrimitiveType(String type) {
        if (methods && methods.peek() instanceof Method) {
            DBManager.instance.open(credentials)
            ((Method) methods.peek()).setReturnType(TypeRef.createPrimitiveTypeRef(type))
            DBManager.instance.close()
        }

    }

    void addMethodException(String type) {
        Type t = findType(type)

        if (t && methods && methods.peek() instanceof Method) {
            DBManager.instance.open(credentials)
            ((Method) methods.peek()).addException(t.createTypeRef())
            DBManager.instance.close()
        }

    }

    void finishMethod() {
        if (!methods.isEmpty()) {
            Member member = methods.pop()
            if (member instanceof Method) {
                Set<String> localVars = scopes.pop()

                DBManager.instance.open(credentials)
                ((Method) member).setLocalVarCount(localVars.size())
                DBManager.instance.close()
            } else if (member instanceof Initializer) {
                Set<String> localVars = scopes.pop()

                DBManager.instance.open(credentials)
                ((Initializer) member).setLocalVarCount(localVars.size())
                DBManager.instance.close()
            }

        }

    }

    ///////////////////
    // Fields
    ///////////////////
    void createField(String name, String fieldType, boolean primitive, int start, int end, List<String> modifiers) {
        if (types) {
            DBManager.instance.open(credentials)
            boolean hasField = types.peek().hasFieldWithName(name)
            DBManager.instance.close()

            if (hasField) {
                DBManager.instance.open(credentials)
                Field f = types.peek().getFieldWithName(name)
                f.setEnd(end)
                f.setStart(start)
                f.save()
                DBManager.instance.close()
            } else {
                DBManager.instance.open(credentials)
                Field field = Field.builder()
                        .name(name)
                        .compKey(name)
                        .accessibility(Accessibility.PUBLIC)
                        .start(start)
                        .end(end)
                        .create()
                types.peek().addMember(field)
                field.updateKey()
                DBManager.instance.close()

                if (primitive) {
                    DBManager.instance.open(credentials)
                    field.setType(TypeRef.createPrimitiveTypeRef(fieldType))
                    DBManager.instance.close()
                } else {
                    Type t = findType(fieldType)

                    if (t) {
                        DBManager.instance.open(credentials)
                        log.atInfo().log("Creating type ref to ${t.getName()} for field ${field.getName()}")
                        field.setType(t.createTypeRef())
                        DBManager.instance.close()
                    }
                }
                setFieldModifiers(field, modifiers)
            }
        }
    }

    void createEnumLiteral(String name, int start, int end) {
        if (types && types.peek() instanceof Enum) {
            Enum enm = (Enum) types.peek()

            DBManager.instance.open(credentials)
            if (enm.hasLiteralWithName(name)) {
                Literal l = enm.getLiteralWithName(name)
                l.setEnd(end)
                l.setStart(start)
                l.save()
            }
            else {
                Literal lit = Literal.builder()
                        .name(name)
                        .compKey(name)
                        .start(start)
                        .end(end)
                        .create()
                enm.addMember(lit)
                lit.updateKey()
            }
            DBManager.instance.close()
        }
    }

    void setFieldModifiers(Field field, List<String> modifiers) {
        modifiers.each { mod ->
            DBManager.instance.open(credentials)
            Accessibility access = handleAccessibility(mod)
            Modifier modifier = handleNamedModifiers(mod)
            if (access) field.setAccessibility(access)
            if (modifier) field.addModifier(modifier)
            DBManager.instance.close()
        }
    }

    ///////////////////
    // Relationships
    ///////////////////
    void addRealization(String typeName) {
        if (types) {
            Type t = findType(typeName)

            if (t) {
                DBManager.instance.open(credentials)
                types.peek().realizes(t)
                DBManager.instance.close()
            }
        }
    }

    void addGeneralization(String typeName) {
        if (types) {
            Type t = findType(typeName)

            if (t) {
                DBManager.instance.open(credentials)
                types.peek().generalizedBy(t)
                DBManager.instance.close()
            }
        }
    }

    void addAssociation(Type type) {
        if (types) {
            DBManager.instance.open(credentials)
            types.peek().associatedTo(type)
            DBManager.instance.close()
        }
    }

    void addUseDependency(Type type) {
        if (types) {
            DBManager.instance.open(credentials)
            types.peek().useTo(type)
            DBManager.instance.close()
        }
    }

    void addUseDependency(String type) {
        if (types) {
            Type t = findType(type)

            if (t) {
                DBManager.instance.open(credentials)
                types.peek().useTo(t)
                DBManager.instance.close()
            }
        }
    }

    void addLocalVarToScope(String varName) {
        if (scopes) {
            scopes.peek().add(varName)
        }
    }

    void processExpression(String expr) {
        expr = cleanExpression(expr)
        def list = expr.split(/\s+/)

        Type type
        list.each { str ->
//            println "\n$str"
            String[] components = str.split(/\./)
            components.eachWithIndex { comp, index ->
                if (comp.find(/\(.*\)/)) {
                    String args = comp.find(/\(.*\)/)
                    args = args.substring(args.indexOf("(") + 1, args.lastIndexOf(")"))
                    int numParams = args.split(",").size()
                    type = handleMethodCall(type, comp, numParams)
                } else
                    type = handleNonMethodCall(type, comp, components, index)
            }
        }
    }

    abstract String cleanExpression(String expr)

    Type handleNonMethodCall(Type type, String comp, String[] components, int index) {
        switch (comp) {
            case "this":
                return handleThis(type)
            case "super":
                String name = components[index + 1]
                return handleSuper(type, name)
            default:
                return handleIdentifier(type, comp)
        }
    }

    Type handleMethodCall(Type type, String comp, int numParams) {
        if (comp.startsWith("super")) {
            if (type) {
                findSuperTypeWithNParamConstructor(type, numParams)
            } else {
                findSuperTypeWithNParamConstructor(types.peek(), numParams)
            }
            return createConstructorCall(type, numParams)
        } else if (comp.startsWith("this")) {
            if (!type)
                type = types.peek()
            return createConstructorCall(type, numParams)
        } else {
            if (!type)
                type = types.peek()
            return createMethodCall(type, comp, numParams)
        }
    }

    Type handleThis(Type type) {
        if (!type)
            return ((Stack<Type>) types).peek()
        return type
    }

    Type handleSuper(Type type, String name) {
        if (type) {
            return findSuperTypeWithTypeFieldOrMethodNamed(type, name)
        } else {
            return findSuperTypeWithTypeFieldOrMethodNamed(types.peek(), name)
        }
    }

    Type handleIdentifier(Type type, String comp) {
        Type t = null
        DBManager.instance.open(credentials)
        if (type) {
            if (type.hasFieldWithName(comp)) {
                t = createFieldUse(type, comp)
            } else {
                t = type.getTypeByName(comp)
            }
        } else {
            if (checkIsLocalVar(comp)) {
                // TODO Finish me
                // set type
            } else if (types.peek().hasFieldWithName(comp)) {
                t = createFieldUse(types.peek(), comp)
            } else if (types.peek().hasTypeWithName(comp)) {
                t = types.peek().getTypeByName(comp)
            }
        }
        DBManager.instance.close()

        return t
    }

    private boolean checkIsLocalVar(String name) {
        !scopes.isEmpty() && scopes.peek().contains(name)
    }

    Type createMethodCall(Type current, String comp, int numParams) {
        Type type = current

        DBManager.instance.open(credentials)
        Method called = findMethodCalled(type, comp, numParams)

        if (type && called) {
            def m
            if (((Stack<Member>) methods).peek() instanceof Method) {
                m = (Method) ((Stack<Member>) methods).peek()
                if (((Method) m).getType().getType() == TypeRefType.Type)
                    type = m.getType().getType(proj.getProjectKey())
            } else if (((Stack<Member>) methods).peek() instanceof Initializer) {
                m = (Initializer) ((Stack<Member>) methods).peek()
            }

            if (m)
                m.callsMethod(called)
        }
        DBManager.instance.close()

        return type
    }

    Method findMethodCalled(Type type, String comp, int numParams) {
        if (type) {
            Method called = type.getMethodWithNameAndNumParams(comp, numParams)

            if (!called) {
                for (Type anc : type.getAncestorTypes()) {
                    if (anc.hasMethodWithNameAndNumParams(comp, numParams)) {
                        type = anc
                        break
                    }
                }
                called = type.getMethodWithNameAndNumParams(comp, numParams)
            }
            return called
        }
        return null
    }

    void createConstructorCall(Type type, int numParams) {
        if (type && !methods.isEmpty()) {
            def m

            if (((Stack<Member>) methods).peek() instanceof Method) {
                m = (Method) ((Stack<Member>) methods).peek()
            } else if (((Stack<Member>) methods).peek() instanceof Initializer) {
                m = (Initializer) ((Stack<Member>) methods).peek()
            }

            DBManager.instance.open(credentials)
            Constructor cons = type.getConstructorWithNParams(numParams)
            if (m && cons)
                m.callsMethod(cons)
            DBManager.instance.close()
        }
    }

    Type findSuperTypeWithNParamConstructor(Type type, int numParams) {
        if (type) {
            Type ancestor = null
            DBManager.instance.open(credentials)
            for (Type anc : type.getAncestorTypes()) {
                if (anc.hasConstructorWithNParams(numParams)) {
                    ancestor = anc
                    break
                }
            }
            DBManager.instance.close()

            if (ancestor)
                return ancestor
        }

        return type
    }

    Type findSuperTypeWithTypeFieldOrMethodNamed(Type child, String name) {
        Type type = child

        if (type) {
            Type ancestor = null
            DBManager.instance.open(credentials)
            for (Type anc : type.getAncestorTypes()) {
                if (anc.hasTypeWithName(name) || anc.hasFieldWithName(name) || anc.hasMethodWithName(name)) {
                    ancestor = anc
                    break
                }
            }
            DBManager.instance.close()

            if (ancestor)
                return ancestor
        }

        return type
    }

    Type createFieldUse(Type current, String comp) {
        Type type = current

        if (type) {
            Method m

            if (!methods.isEmpty()) {
                if (((Stack<Member>) methods).peek() instanceof Method) {
                    m = (Method) ((Stack<Member>) methods).peek()
                } else if (((Stack<Member>) methods).peek() instanceof Initializer) {
                    m = (Initializer) ((Stack<Member>) methods).peek()
                }
            }

//            DBManager.instance.open(credentials)
            Field f = type.getFieldWithName(comp)
            if (f && m) {
                m.usesField(f)
                if (f.getType().getType() == TypeRefType.Type) {
                    log.atInfo().log("Field Name: ${f.getName()}")
                    type = (Type) f.getType().getType(proj.getProjectKey())
                }
            }
//            DBManager.instance.close()
        }

        return type
    }

    ///////////////////
    // Utilities
    ///////////////////
    def handleNamedModifiers(String mod) {
        try {
            return Modifier.forName(mod)
        } catch (IllegalArgumentException e) {

        }

        null
    }

    def handleAccessibility(String mod) {
        try {
            return Accessibility.valueOf(mod.toUpperCase())
        } catch (IllegalArgumentException e) {

        }

        null
    }

    /**
     * Adds the child namespace to the provided parent namespace
     * @param parent Parent of the child namespace
     * @param child Child namespace
     */
    void setParentNamespace(Namespace parent, Namespace child) {
        if (parent != null && !parent.containsNamespace(child))
            parent.addNamespace(child)
    }

    /**
     * Finds a type with the given name, first searching the current namespace, then searching fully specified imported types,
     * then checking the types defined in known language specific wildcard imports, then checking automatically included language
     * types, and finally checking unknown wildcard imports. If the type is not defined within the context of the system under analysis
     * it will be constructed as an UnknownType.
     *
     * @param name The name of the type
     * @return The Type corresponding to the provided type name.
     */
    Type findType(String name) { // FIXME
        log.atInfo().log("Finding type: $name")

        Type candidate

        DBManager.instance.open(credentials)
        if (notFullySpecified(name)) {
            candidate = findTypeInNamespace(name)
            if (candidate == null) candidate = findTypeUsingSpecificImports(name)
            if (candidate == null) candidate = findTypeUsingGeneralImports(name)
            if (candidate == null) candidate = findTypeInDefaultNamespace(name)
        } else {
            candidate = findTypeByQualifiedName(name)
        }

        // In the event that no valid candidate was found, then it is an unknown type
        if (candidate == null) candidate = createUnknownType(name)
        DBManager.instance.close()

        candidate
    }

    private Type findTypeByQualifiedName(String name) {
        return proj.findType("qualified_name", name)
    }

    private Type createUnknownType(String name) {
        String typeName = determineTypeName(name)
        findOrCreateUnknownType(typeName)
    }

    private Type findOrCreateUnknownType(String typeName) {
        Type candidate = UnknownType.findFirst("name = ?", typeName)
        if (!candidate) {
            candidate = UnknownType.builder()
                    .name(typeName)
                    .compKey(typeName)
                    .create()
            proj.addUnknownType((UnknownType) candidate)
            candidate.updateKey()
        }
        candidate
    }

    private String determineTypeName(String name) {
        String specific = file.getImports()*.getName().find { !it.endsWith(name) }
        String general = file.getImports()*.getName().find { it.endsWith("*") }

        String typeName
        if (specific) {
            typeName = specific
        } else if (general) {
            typeName = general.replace("*", name)
        } else {
            if (name.count(".") < 1)
                typeName = "java.lang." + name
            else
                typeName = name
        }
        typeName
    }

    private Type findTypeInDefaultNamespace(String name) {
        return findTypeByQualifiedName("java.lang." + name)
    }

    private Type findTypeUsingGeneralImports(String name) {
        List<String> general = file.getImports()*.getName().findAll { it.endsWith("*") }
        Type candidate = null
        for (String gen : general) {
            String imp = gen.replace("*", name)

            candidate = findTypeByQualifiedName(imp)

            if (candidate != null)
                break
        }
        candidate
    }

    private Type findTypeUsingSpecificImports(String name) {
        String specific = file.getImports()*.getName().find { it.endsWith(name) }
        Type candidate = null

        if (specific) {
            candidate = findTypeByQualifiedName(specific)
        }

        candidate
    }

    private Type findTypeInNamespace(String name) {
        Type candidate = null

        if (namespace != null) {
            candidate = findTypeByQualifiedName(namespace.getName() + "." + name)
        }

        candidate
    }

    /**
     * A heuristic to determine if the provided type name is fully defined. That is whether the type name has more than one "."
     * @param name Name to evaluate
     * @return true if the name has more than one ".", false otherwise.
     */
    boolean notFullySpecified(String name) {
        return name.count(".") < 1
    }

    /**
     * Retrieves the full name of the the type with the short name provided. This name is constructed by appending the current package name
     * to the provided name, if it is not already present.
     * @param name The name to evaluate.
     * @return The fully qualified name of the type
     */
    String getFullName(String name) {
        if (!types.empty()) {
            "${typeKey()}.${name}"
        } else {
            namespace == null ? name : "${namespace.getName()}\$${name}"
        }
    }

    String typeKey() {
        types.peek().getCompKey()
    }

}
