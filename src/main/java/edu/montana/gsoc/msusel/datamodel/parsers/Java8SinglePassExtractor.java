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

import com.google.common.collect.Lists;
import edu.isu.isuese.datamodel.Initializer;
import edu.isu.isuese.datamodel.Method;
import edu.isu.isuese.datamodel.Type;
import edu.isu.isuese.datamodel.cfg.CFGBuilder;
import edu.isu.isuese.datamodel.cfg.JumpTo;
import edu.isu.isuese.datamodel.cfg.StatementType;
import edu.isu.isuese.datamodel.util.DBCredentials;
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaParser;
import edu.montana.gsoc.msusel.datamodel.parsers.java2.JavaParserBaseListener;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Stack;

public class Java8SinglePassExtractor extends JavaParserBaseListener {

    BaseModelBuilder treeBuilder;
    List<String> modifiers = Lists.newArrayList();
    List<String> fieldNames = Lists.newArrayList();
    String primFieldType = null;
    String fieldType = null;
    boolean inPackage;
    boolean inImport;
    boolean onDemand;
    boolean inTypeDecl;
    boolean inTypeParams;
    boolean inMember = false;
    boolean inMethod = false;
    boolean inParameters = false;
    boolean inField = false;
    boolean inExceptions = false;
    boolean inConstant = false;
    boolean inConstructor = false;
    boolean inType = false;
    boolean inLocalVar = false;
    boolean inClass = false;
    boolean inEnum = false;
    boolean inInterface = false;
    boolean inTypeList = false;
    boolean inMethodBody = false;
    private Stack<CFGBuilder> builderStack = new Stack<>();
    CFGBuilder builder;
    DBCredentials credentials;
    Stack<Integer> initializerCount = new Stack<>();

    public Java8SinglePassExtractor(BaseModelBuilder builder) {
        treeBuilder = builder;
        inPackage = false;
        inImport = false;
        onDemand = false;
        inTypeDecl = false;
        inTypeParams = false;
        credentials = builder.getCredentials();
    }

    ////////////////////////////
    //
    // Handle package declarations
    //
    ////////////////////////////
    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Package");
        inPackage = true;

        super.enterPackageDeclaration(ctx);
    }

    @Override
    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Leaving Package");
        inPackage = false;

        super.exitPackageDeclaration(ctx);
    }

    ////////////////////////////
    //
    // Handle import declarations
    //
    ////////////////////////////
    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Import");
        inImport = true;

        if (ctx.MUL() != null)
            onDemand = true;

        super.enterImportDeclaration(ctx);
    }

    @Override
    public void exitImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Exiting Import");
        inImport = false;
        onDemand = false;

        super.exitImportDeclaration(ctx);
    }

    @Override
    public void enterQualifiedName(JavaParser.QualifiedNameContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Qualified Name");
        if (inPackage) {
            treeBuilder.createNamespace(ctx.getText());
        } else if (inImport) {
            String name = ctx.getText();
            if (onDemand)
                name += ".*";
            treeBuilder.createImport(name, ctx.getStart().getLine(), ctx.getStop().getLine());
        } else if (inExceptions) {
            treeBuilder.addMethodException(ctx.getText());
        }

        super.enterQualifiedName(ctx);
    }

    ////////////////////////////
    //
    // Handle type declarations
    //
    ////////////////////////////
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Type Declaration");
        modifiers.clear();
        inTypeDecl = true;
        initializerCount.push(0);

        super.enterTypeDeclaration(ctx);
    }

    public void exitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Exiting Type Declaration");
        treeBuilder.endType();
        initializerCount.pop();

        super.exitTypeDeclaration(ctx);
    }

    public void enterLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Local Type Declaration");
        modifiers.clear();
        inTypeDecl = true;
        initializerCount.push(0);

        super.enterLocalTypeDeclaration(ctx);
    }

    public void exitLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Exiting Local Type Declaration");
        treeBuilder.endType();
        initializerCount.pop();

        super.exitLocalTypeDeclaration(ctx);
    }

    ////////////////////////////
    //
    // Specific types
    //
    ////////////////////////////
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Class Declaration");
        treeBuilder.findOrCreateType(ctx.IDENTIFIER().getText(), Type.CLASS, ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);
        inType = true;
        inClass = true;

        super.enterClassDeclaration(ctx);
    }

    public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Enum Declaration");
        treeBuilder.findOrCreateType(ctx.IDENTIFIER().getText(), Type.ENUM, ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);
        inType = true;
        inEnum = true;

        super.enterEnumDeclaration(ctx);
    }

    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Interface Declaration");
        treeBuilder.findOrCreateType(ctx.IDENTIFIER().getText(), Type.INTERFACE, ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);
        inType = true;
        inInterface = true;

        super.enterInterfaceDeclaration(ctx);
    }

    public void enterAnnotationTypeDeclaration(JavaParser.AnnotationTypeDeclarationContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Annotation Type Declaration");
        treeBuilder.findOrCreateType(ctx.IDENTIFIER().getText(), Type.ANNOTATION, ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setTypeModifiers(modifiers);
        inType = true;
        inInterface = true;

        super.enterAnnotationTypeDeclaration(ctx);
    }

    ////////////////////////////
    //
    // Modifiers
    //
    ////////////////////////////
    public void enterClassOrInterfaceModifier(JavaParser.ClassOrInterfaceModifierContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Class or Interface Modifier");
//        if (inMember && ctx.annotation() == null) {
            modifiers.add(ctx.getText());
//        }

        super.enterClassOrInterfaceModifier(ctx);
    }

    ////////////////////////////
    //
    // Type Parameters
    //
    ////////////////////////////
    public void enterTypeParameters(JavaParser.TypeParametersContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Type Parameters");
        inTypeParams = true;

        super.enterTypeParameters(ctx);
    }

    public void exitTypeParameters(JavaParser.TypeParametersContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Exiting Type Parameters");
        inTypeParams = false;

        super.exitTypeParameters(ctx);
    }

    public void enterTypeParameter(JavaParser.TypeParameterContext ctx) {
        if (inTypeDecl) {
            treeBuilder.createTypeTypeParameter(ctx.IDENTIFIER().getText());
        } else {
            treeBuilder.createMethodTypeParameter(ctx.IDENTIFIER().getText());
        }

        super.enterTypeParameter(ctx);
    }

    ////////////////////////////
    //
    // Handle types
    //
    ////////////////////////////
    public void enterClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Class or Interface Type");
        if (inTypeList) {
            if (inClass || inEnum) {
                treeBuilder.addRealization(ctx.getText().replaceAll("<.*>", ""));
            } else if (inInterface) {
                treeBuilder.addGeneralization(ctx.getText().replaceAll("<.*>", ""));
            }
        } else if (!inTypeList && inClass) {
            treeBuilder.addGeneralization(ctx.getText().replaceAll("<.*>", ""));
        } else if (inMethod) {
            treeBuilder.setMethodReturnType(ctx.getText());
            handleMethodType(ctx);
        } else if (inConstructor) {
            handleMethodType(ctx);
        }
//        else if (inTypeParams) {
//            treeBuilder.addTypeParamBoundType(ctx.getText());
//        }
        else if (inField) {
            fieldType = ctx.getText();
            primFieldType = null;
        }

        super.enterClassOrInterfaceType(ctx);
    }

    ////////////////////////////
    //
    // Bodies
    //
    ////////////////////////////
    public void enterClassBody(JavaParser.ClassBodyContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Class Body");
        inTypeDecl = false;
        inType = false;
        inClass = false;

        super.enterClassBody(ctx);
    }

    public void enterInterfaceBody(JavaParser.InterfaceBodyContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Interface Body");
        inTypeDecl = false;
        inType = false;
        inInterface = false;

        super.enterInterfaceBody(ctx);
    }

    public void enterEnumConstants(JavaParser.EnumConstantsContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Enum Constants");
        inTypeDecl = false;
        inType = false;
        inEnum = false;

        super.enterEnumConstants(ctx);
    }

    public void enterEnumBodyDeclarations(JavaParser.EnumBodyDeclarationsContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Enum Body Declarations");
        inTypeDecl = false;
        inType = false;
        inEnum = false;

        super.enterEnumBodyDeclarations(ctx);
    }

    public void enterAnnotationTypeBody(JavaParser.AnnotationTypeBodyContext ctx) {
//        log.atInfo().log(treeBuilder.getFile().getName() + " Entering Annotation Type Body");
        inTypeDecl = false;

        super.enterAnnotationTypeBody(ctx);
    }

    ////////////////////
    //
    // Generalization and Realization
    //
    ////////////////////
    ////////////////////
    // Type Declarations
    ////////////////////
    ////////////////////////
    // Type bodies
    ////////////////////////
    @Override
    public void enterClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        modifiers.clear();
        if (ctx.block() != null) {
            inMethod = true;
            startMethod();
            handleInitializer(ctx);
            inMethod = false;
        }

        super.enterClassBodyDeclaration(ctx);
    }

    @Override
    public void exitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
        if (ctx.block() != null) {
            endMethod();
            treeBuilder.finishMethod();
        }

        super.exitClassBodyDeclaration(ctx);
    }

    @Override
    public void enterInterfaceBodyDeclaration(JavaParser.InterfaceBodyDeclarationContext ctx) {
        modifiers.clear();

        super.enterInterfaceBodyDeclaration(ctx);
    }

    @Override
    public void enterAnnotationTypeElementDeclaration(JavaParser.AnnotationTypeElementDeclarationContext ctx) {
        modifiers.clear();

        super.enterAnnotationTypeElementDeclaration(ctx);
    }

    //////////////////////
    //
    // Handle body setup
    //
    //////////////////////
    @Override
    public void enterBlock(JavaParser.BlockContext ctx) {
        inMember = false;
        inMethod = false;
        inField = false;
        inParameters = false;
        inTypeParams = false;
        inExceptions = false;

        super.enterBlock(ctx);
    }

    //////////////////////
    //
    // Handle Methods and Constructors
    //
    //////////////////////
    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        inMember = true;
        inMethod = true;
        treeBuilder.createMethod(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setMethodModifiers(modifiers);
        modifiers.clear();
        startMethod();

        super.enterMethodDeclaration(ctx);
    }

    @Override
    public void enterGenericMethodDeclaration(JavaParser.GenericMethodDeclarationContext ctx) {
        inMember = true;
        inMethod = true;

        super.enterGenericMethodDeclaration(ctx);
    }

    @Override
    public void enterInterfaceMethodDeclaration(JavaParser.InterfaceMethodDeclarationContext ctx) {
        inMember = true;
        inMethod = true;
        treeBuilder.createMethod(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
        treeBuilder.setMethodModifiers(modifiers);
        modifiers.clear();
        startMethod();

        super.enterInterfaceMethodDeclaration(ctx);
    }

    @Override
    public void enterGenericInterfaceMethodDeclaration(JavaParser.GenericInterfaceMethodDeclarationContext ctx) {
        inMember = true;
        inMethod = true;

        super.enterGenericInterfaceMethodDeclaration(ctx);
    }

    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        inMember = true;
        inMethod = true;
        inMethodBody = true;
        if (ctx.IDENTIFIER() != null) {
            treeBuilder.createConstructor(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());
            treeBuilder.setMethodModifiers(modifiers);
        }
        modifiers.clear();
        startMethod();

        super.enterConstructorDeclaration(ctx);
    }

    @Override
    public void enterGenericConstructorDeclaration(JavaParser.GenericConstructorDeclarationContext ctx) {
        inMember = true;
        inMethod = true;

        super.enterGenericConstructorDeclaration(ctx);
    }

    //////////////////////
    //
    // Handle Parameters
    //
    //////////////////////
    @Override
    public void enterFormalParameters(JavaParser.FormalParametersContext ctx) {
        inParameters = true;

        super.enterFormalParameters(ctx);
    }

    @Override
    public void exitFormalParameters(JavaParser.FormalParametersContext ctx) {
        inParameters = false;

        super.exitFormalParameters(ctx);
    }

    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        treeBuilder.createMethodParameter();

        super.enterFormalParameter(ctx);
    }

    @Override
    public void enterLastFormalParameter(JavaParser.LastFormalParameterContext ctx) {
        treeBuilder.createMethodParameter();
        treeBuilder.setVariableParameter(ctx.ELLIPSIS() != null);

        super.enterLastFormalParameter(ctx);
    }

    //////////////////////
    //
    // Handle Fields, Constants, and Enum Literals
    //
    //////////////////////
    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        inField = true;

        super.enterFieldDeclaration(ctx);
    }

    @Override
    public void exitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        createFields(ctx.getStart(), ctx.getStop());
        inField = false;

        super.exitFieldDeclaration(ctx);
    }

    @Override
    public void enterConstDeclaration(JavaParser.ConstDeclarationContext ctx) {
        inField = true;
        inConstant = true;
        fieldNames.clear();

        super.enterConstDeclaration(ctx);
    }

    @Override
    public void exitConstDeclaration(JavaParser.ConstDeclarationContext ctx) {
        createFields(ctx.getStart(), ctx.getStop());
        inConstant = false;

        super.exitConstDeclaration(ctx);
    }

    private void createFields(Token start, Token stop) {
        fieldNames.forEach(name -> {
            if (fieldType != null) {
                treeBuilder.createField(name, fieldType, false, start.getLine(), stop.getLine(), modifiers);
            } else if (primFieldType != null) {
                treeBuilder.createField(name, primFieldType, true, start.getLine(), stop.getLine(), modifiers);
            }
        });

        inField = false;
    }

    @Override
    public void enterConstantDeclarator(JavaParser.ConstantDeclaratorContext ctx) {
        fieldNames.add(ctx.IDENTIFIER().getText());

        super.enterConstantDeclarator(ctx);
    }

    @Override
    public void enterEnumConstant(JavaParser.EnumConstantContext ctx) {
        if (ctx.IDENTIFIER() != null)
            treeBuilder.createEnumLiteral(ctx.IDENTIFIER().getText(), ctx.getStart().getLine(), ctx.getStop().getLine());

        super.enterEnumConstant(ctx);
    }

    @Override
    public void enterVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {
        fieldNames.clear();

        super.enterVariableDeclarators(ctx);
    }

    @Override
    public void exitVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {
        super.exitVariableDeclarators(ctx);
    }

    @Override
    public void enterVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        if (inField)
            fieldNames.add(ctx.IDENTIFIER().getText());
        else if (inLocalVar && ctx.IDENTIFIER() != null) {
            treeBuilder.addLocalVarToScope(ctx.IDENTIFIER().getText());
        }
        // TODO add array knowledge here
//        if (ctx.RBRACK() != null) {
//            treeBuilder.setCurrentMethodParameterAsArray();
//        }

        super.enterVariableDeclaratorId(ctx);
    }

    ///////////////////
    //
    // Handle Modifiers
    //
    ///////////////////
    @Override
    public void enterModifier(JavaParser.ModifierContext ctx) {
//        if ((inField || inMethod || inMember) && ctx.classOrInterfaceModifier() == null) {
        modifiers.add(ctx.getText());
//        }

        super.enterModifier(ctx);
    }

    @Override
    public void enterInterfaceMethodModifier(JavaParser.InterfaceMethodModifierContext ctx) {
//        if (inMember && ctx.annotation() == null) {
            modifiers.add(ctx.getText());
//        }

        super.enterInterfaceMethodModifier(ctx);
    }

    @Override
    public void enterVariableModifier(JavaParser.VariableModifierContext ctx) {
        if (ctx.FINAL() != null)
            treeBuilder.addParameterModifier(ctx.getText());
    }

    ////////////////////
    //
    // Type Parameters
    //
    ////////////////////
    ////////////////////
    //
    // Types
    //
    ////////////////////
    @Override
    public void enterTypeList(JavaParser.TypeListContext ctx) {
        inTypeList = true;

        super.enterTypeList(ctx);
    }

    @Override
    public void exitTypeList(JavaParser.TypeListContext ctx) {
        inTypeList = false;

        super.exitTypeList(ctx);
    }

    private void handleMethodType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        if (inParameters) {
            treeBuilder.setParameterType(ctx.getText());
            treeBuilder.addUseDependency(ctx.getText().replaceAll("<.*>", ""));
        } else if (inLocalVar) {
            treeBuilder.addUseDependency(ctx.getText().replaceAll("<.*>", ""));
        } else {
            treeBuilder.addUseDependency(ctx.getText().replaceAll("<.*>", ""));
        }
    }

    @Override
    public void enterTypeTypeOrVoid(JavaParser.TypeTypeOrVoidContext ctx) {
        if (ctx.VOID() != null)
            treeBuilder.setMethodReturnTypeVoid();

        super.enterTypeTypeOrVoid(ctx);
    }

    @Override
    public void enterTypeType(JavaParser.TypeTypeContext ctx) {
        super.enterTypeType(ctx);
    }

    @Override
    public void enterPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
        if (inMethod && inParameters) {
            treeBuilder.setParameterPrimitiveType(ctx.getText());
        } else if (inMethod) {
            treeBuilder.setMethodReturnPrimitiveType(ctx.getText());
        } else if (inField) {
            primFieldType = ctx.getText();
            fieldType = null;
        }

        super.enterPrimitiveType(ctx);
    }

    /////////////////
    //
    // Exceptions
    //
    /////////////////
    @Override
    public void enterQualifiedNameList(JavaParser.QualifiedNameListContext ctx) {
        if (inMethod)
            inExceptions = true;

        super.enterQualifiedNameList(ctx);
    }

    @Override
    public void exitQualifiedNameList(JavaParser.QualifiedNameListContext ctx) {
        inExceptions = false;

        super.exitQualifiedNameList(ctx);
    }

    /////////////////////////
    //
    // Local Variables
    //
    /////////////////////////
    @Override
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        inLocalVar = true;
        builder.createStatement(StatementType.VARDECL);

        super.enterLocalVariableDeclaration(ctx);
    }

    @Override
    public void exitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        inLocalVar = false;

        super.exitLocalVariableDeclaration(ctx);
    }

    ////////////////////
    //
    // Generalization and Realization
    //
    ////////////////////
    ////////////////////
    //
    // Use Dependency - Method Return Type, Parameter Types, and Local Variable Types
    //
    ////////////////////
    @Override
    public void enterMethodBody(JavaParser.MethodBodyContext ctx) {
        inMethodBody = true;

        super.enterMethodBody(ctx);
    }

    @Override
    public void exitMethodBody(JavaParser.MethodBodyContext ctx) {
        inMethodBody = false;

        super.exitMethodBody(ctx);
    }

    @Override
    public void exitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        endMethod();
        inMethodBody = false;
        treeBuilder.finishMethod();

        super.exitConstructorDeclaration(ctx);
    }

    ///////////////////
    //
    // Method Calls and Field Uses
    //
    //////////////////
    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        if (inMethodBody && ctx.lambdaExpression() == null && !inField && ctx.getParent() instanceof JavaParser.StatementContext) {
            treeBuilder.processExpression(ctx.getText());
        }

        super.enterExpression(ctx);
    }

    private void startMethod() {
        CFGBuilder newBuilder = new CFGBuilder(credentials);
        builderStack.push(newBuilder);
        builder = newBuilder;
        builder.startMethod();
    }

    private void endMethod() {
        if (!treeBuilder.getMethods().isEmpty()) {
            if (treeBuilder.getMethods().peek() instanceof Method)
                builder.endMethod((Method) treeBuilder.getMethods().peek());
            else if (treeBuilder.getMethods().peek() instanceof Initializer) {
                builder.endMethod((Initializer) treeBuilder.getMethods().peek());
            }
        }

        builderStack.pop();
        if (builderStack.empty())
            builder = null;
        else
            builder = builderStack.peek();
    }

    ///////////////
    // Types
    ///////////////
    ///////////////
    // Methods
    ///////////////
    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        endMethod();
        treeBuilder.finishMethod();
        super.exitMethodDeclaration(ctx);
    }

    @Override
    public void exitInterfaceMethodDeclaration(JavaParser.InterfaceMethodDeclarationContext ctx) {
        endMethod();
        treeBuilder.finishMethod();
        super.exitInterfaceMethodDeclaration(ctx);
    }

    /////////////////
    // Statements
    /////////////////
    @Override
    public void enterStatement(JavaParser.StatementContext ctx) {
        if (ctx.ASSERT() != null) {
            builder.createStatement(StatementType.ASSERT);
        } else if (ctx.IF() != null) {
            builder.startDecision(StatementType.IF);
        } else if (ctx.FOR() != null) {
            builder.startLoop(StatementType.FOR);
        } else if (ctx.DO() != null) {
            builder.startLoop(StatementType.DO, true);
        } else if (ctx.WHILE() != null) {
            builder.startLoop(StatementType.WHILE);
        } else if (ctx.TRY() != null) {
            builder.startBlock(StatementType.TRY);
        } else if (ctx.SWITCH() != null) {
            builder.startDecision(StatementType.SWITCH);
        } else if (ctx.SYNCHRONIZED() != null) {
            builder.startBlock(StatementType.SYNCHRONIZED);
        } else if (ctx.RETURN() != null) {
            builder.createReturnStatement();
        } else if (ctx.BREAK() != null) {
            String identifier = ctx.IDENTIFIER() != null ? ctx.IDENTIFIER().getText() : null;
            if (identifier != null)
                builder.createStatement(StatementType.BREAK, identifier, true, JumpTo.LABEL);
            else
                builder.createStatement(StatementType.BREAK, null, true, JumpTo.LOOP_END);
        } else if (ctx.CONTINUE() != null) {
            String identifier = ctx.IDENTIFIER() != null ? ctx.IDENTIFIER().getText() : null;
            builder.createStatement(StatementType.CONTINUE, identifier, true, JumpTo.LOOP_START);
        } else if (ctx.THROW() != null) {
            builder.createStatement(StatementType.THROW, null, true, JumpTo.METHOD_END);
        } else if (ctx.SEMI() != null) {
            builder.createStatement(StatementType.EMPTY);
        } else if (ctx.identifierLabel != null) {
            builder.createStatement(null, ctx.IDENTIFIER().getText());
        } else if (ctx.expression() != null) {
            builder.createStatement(StatementType.EXPRESSION);
        }

        super.enterStatement(ctx);
    }

    @Override
    public void exitStatement(JavaParser.StatementContext ctx) {
        if (ctx.IF() != null) {
            builder.endDecision();
        } else if (ctx.FOR() != null) {
            builder.endLoop();
        } else if (ctx.DO() != null) {
            builder.endLoop(true);
        } else if (ctx.WHILE() != null) {
            builder.endLoop();
        } else if (ctx.TRY() != null) {
            builder.endBlock();
        } else if (ctx.SWITCH() != null) {
            boolean def = true;
            for (final JavaParser.SwitchBlockStatementGroupContext x : ctx.switchBlockStatementGroup()) {
                if (x.switchLabel(0).DEFAULT() == null) {
                    def = false;
                    break;
                }
            }
            builder.endDecision(def);
        } else if (ctx.SYNCHRONIZED() != null) {
            builder.endBlock();
        }

        super.exitStatement(ctx);
    }

    @Override
    public void enterCatchClause(JavaParser.CatchClauseContext ctx) {
        builder.startBlock(StatementType.CATCH);

        super.enterCatchClause(ctx);
    }

    @Override
    public void exitCatchClause(JavaParser.CatchClauseContext ctx) {
        builder.endBlock();

        super.exitCatchClause(ctx);
    }

    @Override
    public void enterFinallyBlock(JavaParser.FinallyBlockContext ctx) {
        builder.startBlock(StatementType.FINALLY);

        super.enterFinallyBlock(ctx);
    }

    @Override
    public void exitFinallyBlock(JavaParser.FinallyBlockContext ctx) {
        builder.endBlock();

        super.exitFinallyBlock(ctx);
    }

    @Override
    public void enterSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        builder.exitSwitchBlock();

        super.enterSwitchBlockStatementGroup(ctx);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////

    @NotNull
    protected String createSignature(TerminalNode identifier, JavaParser.FormalParametersContext formalParametersContext) {
        StringBuilder builder = new StringBuilder();
        builder.append(identifier.getText());
        builder.append("(");
        if (formalParametersContext != null) {
            if (formalParametersContext.formalParameterList() != null) {
                for (JavaParser.FormalParameterContext fpc : formalParametersContext.formalParameterList().formalParameter()) {
                    builder.append(fpc.typeType().getText());
                    builder.append(", ");
                }

                if (formalParametersContext.formalParameterList().lastFormalParameter() != null) {
                    JavaParser.LastFormalParameterContext lfpc = formalParametersContext.formalParameterList().lastFormalParameter();
                    builder.append(lfpc.typeType().getText());
                }
            }
        }
        builder.append(")");
        String signature = builder.toString();
        if (signature.endsWith(", )"))
            signature = signature.replace(", )", ")");
        return signature;
    }

    protected void handleInitializer(JavaParser.ClassBodyDeclarationContext ctx) {
        initializerCount.push(initializerCount.pop() + 1);
        treeBuilder.findInitializer(String.format("<init-%d>", initializerCount.peek()), ctx.STATIC() != null);
    }
}