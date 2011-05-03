/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import com.github.rjeschke.weel.Variable.Type;

/**
 * Weel compiler.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class Compiler
{
    /** The current tokenizer. */
    Tokenizer tokenizer;
    /** The Weel. */
    final Weel weel;
    /** Nested scopes. */
    LinkedList<Scope> scopes = new LinkedList<Scope>();
    /** The current CodeBlock. */
    CodeBlock block;
    /** The current Scope. */
    Scope scope;

    /** Counter for compiled script classes. */
    private static int classCounter = 0;
    /** Counter for anonymous functions. */
    private static int anonCounter = 0;
    /** The class writer. */
    JvmClassWriter classWriter;

    public Compiler(final Weel weel)
    {
        this.weel = weel;
    }

    /**
     * Compiles the given input String.
     * 
     * @param input
     *            The input String.
     */
    public void compile(final String input)
    {
        this.tokenizer = new Tokenizer(new StringReader(input));
        this.compile();
    }

    /**
     * Compiles the given input stream.
     * 
     * @param input
     *            The input stream.
     */
    public void compile(final InputStream input)
    {
        try
        {
            this.tokenizer = new Tokenizer(new BufferedReader(
                    new InputStreamReader(input, "UTF-8")));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new WeelException(e);
        }
        this.compile();
    }

    /**
     * Main compile method.
     */
    private void compile()
    {
        this.initialize();

        this.tokenizer.next();

        while (this.tokenizer.token != Token.EOF)
            this.compileToken();

        this.scope.block.closeBlock();

        Weel.classLoader.addClass(this.classWriter);

        this.weel.initAllInvokers();
    }

    /**
     * Initializes this compiler.
     */
    private void initialize()
    {
        this.classWriter = new JvmClassWriter(
                "com.github.rjeschke.weel.scripts.Script" + classCounter++);

        this.weel.scriptClasses.add(this.classWriter.className);

        final Scope s = new Scope(this.weel, ScopeType.STATIC);
        s.block = new CodeBlock(this.classWriter.createMethod("STATIC",
                "(Lcom/github/rjeschke/weel/Runtime;)V"));

        this.addScope(s);
    }

    /**
     * Adds a scope.
     * 
     * @param s
     *            The scope.
     */
    private void addScope(final Scope s)
    {
        this.scopes.add(s);
        this.scope = s;
        this.block = s.block;
    }

    /**
     * Removes a scope.
     */
    private void removeScope()
    {
        this.scopes.removeLast().unregisterLocals();
        final Scope s = this.scopes.isEmpty() ? null : this.scopes.getLast();
        this.scope = s;
        this.block = s != null ? s.block : null;
    }

    /**
     * Compiles a token.
     */
    private void compileToken()
    {
        switch (this.tokenizer.token)
        {
        case NAME:
            this.parseVarsAndFuncs(false);
            this.skipSemi();
            break;
        case RESERVED:
            switch (this.tokenizer.reserved)
            {
            case THIS:
                this.parseVarsAndFuncs(false);
                this.skipSemi();
                break;
            case LOCAL:
                this.parseLocal();
                this.skipSemi();
                break;
            case GLOBAL:
                this.parseGlobal();
                this.skipSemi();
                break;
            case IF:
                this.openIf();
                break;
            case ELSEIF:
                this.addElseIf();
                break;
            case ELSE:
                this.addElse();
                break;
            case FOR:
                this.openFor();
                break;
            case BREAK:
                this.addBreak();
                this.skipSemi();
                break;
            case CONTINUE:
                this.addContinue();
                this.skipSemi();
                break;
            case DO:
                this.openDo();
                break;
            case UNTIL:
                this.closeDoUntil();
                this.skipSemi();
                break;
            case WHILE:
                this.openWhile();
                this.skipSemi();
                break;
            case FUNC:
            case SUB:
                if (this.scope.findFunctionScope() != null)
                {
                    throw new WeelException(
                            this.tokenizer
                                    .error("Can't declare a sub/func inside another sub/func"));
                }
                this.openFunction(false);
                break;
            case EXIT:
                this.addExit();
                this.skipSemi();
                break;
            case RETURN:
                this.addReturn();
                this.skipSemi();
                break;
            case END:
                this.closeScope();
                break;
            default:
                this.syntaxError();
                break;
            }
            break;
        case EOF:
            break;
        default:
            this.syntaxError();
            break;
        }
    }

    /**
     * Parses an assign or a function call
     * 
     * @param getContext
     *            Flag to indicate that we need a return value.
     */
    private void parseVarsAndFuncs(final boolean getContext)
    {
        boolean first = true, stackCall = false, end = false, oop = false;
        Variable var;
        if (this.tokenizer.token == Token.RESERVED
                && this.tokenizer.reserved == ReservedWord.THIS)
        {
            final Scope s = this.scope.findFunctionScope();
            if (s == null || !s.isOop)
            {
                throw new WeelException(this.tokenizer
                        .error("Illegal use of 'this'"));
            }
            var = new Variable();
            var.type = Type.LOCAL;
            var.index = 0;
        }
        else
        {
            var = this.scope
                    .findVariable(new Variable(), this.tokenizer.string);
        }

        ExpressionType expr = var.type != Type.NONE ? ExpressionType.VARIABLE
                : ExpressionType.NONE;

        this.tokenizer.next();

        while (!end)
        {
            switch (this.tokenizer.token)
            {
            case BRACE_OPEN:
            {
                int paramc = 0;
                this.tokenizer.next();

                if (first)
                {
                    if (!var.isFunction())
                    {
                        this.needGetVariable(var);
                        this.writeGetVariable(var);
                        stackCall = true;
                    }
                }
                else
                {
                    if (expr == ExpressionType.ARRAY)
                    {
                        if (oop)
                        {
                            this.block.callRuntime("getMapOop");
                            paramc++;
                        }
                        else
                        {
                            this.block.callRuntime("getMap");
                        }
                        stackCall = true;
                    }
                }

                while (this.tokenizer.token != Token.BRACE_CLOSE)
                {
                    paramc++;
                    this.parseExpression();
                    if (this.tokenizer.token == Token.BRACE_CLOSE)
                    {
                        break;
                    }
                    if (this.tokenizer.token == Token.COMMA)
                    {
                        this.tokenizer.next();
                        continue;
                    }
                    this.syntaxError();
                }
                this.checkToken(Token.BRACE_CLOSE);
                this.tokenizer.next();
                if (first && !stackCall)
                {
                    WeelFunction func = this.weel
                            .findFunction(var.name, paramc);
                    if (func == null)
                    {
                        throw new WeelException(this.tokenizer
                                .error("Unknown function/sub " + var.name + "("
                                        + paramc + ")"));
                    }

                    this.writeCallFunction(func);

                    switch (this.tokenizer.token)
                    {
                    case BRACE_OPEN:
                    case BRACKET_OPEN:
                    case DOT:
                    case ARROW:
                        if (!func.returnsValue)
                        {
                            throw new WeelException(this.tokenizer.error("Sub "
                                    + func.name + "(" + func.arguments
                                    + ") doesn't return a value"));
                        }
                        break;
                    default:
                        if (func.returnsValue && !getContext)
                        {
                            this.block.callRuntime("pop1");
                        }
                        else if (!func.returnsValue && getContext)
                        {
                            throw new WeelException(this.tokenizer.error("Sub "
                                    + func.name + "(" + func.arguments
                                    + ") doesn't return a value"));
                        }
                        break;
                    }
                }
                else
                {
                    boolean wantsValue = getContext;
                    switch (this.tokenizer.token)
                    {
                    case BRACE_OPEN:
                    case BRACKET_OPEN:
                    case DOT:
                        wantsValue = true;
                        break;
                    default:
                        break;
                    }
                    this.block.doStackcall(paramc, wantsValue);
                }
                oop = first = false;
                expr = ExpressionType.FUNCTION;
                break;
            }
            case ARROW:
                if (first)
                {
                    this.needGetVariable(var);
                    this.writeGetVariable(var);
                }
                else
                {
                    if (expr == ExpressionType.ARRAY)
                        this.block.callRuntime("getMap");
                }
                this.tokenizer.next();
                this.checkToken(Token.NAME);
                this.block.load(this.tokenizer.string);
                this.tokenizer.next();
                oop = true;
                first = false;
                expr = ExpressionType.ARRAY;
                break;
            case DOT:
                if (first)
                {
                    this.needGetVariable(var);
                    this.writeGetVariable(var);
                }
                else
                {
                    if (expr == ExpressionType.ARRAY)
                        this.block.callRuntime("getMap");
                }
                this.tokenizer.next();
                this.checkToken(Token.NAME);
                this.block.load(this.tokenizer.string);
                this.tokenizer.next();
                oop = first = false;
                expr = ExpressionType.ARRAY;
                break;
            case BRACKET_OPEN:
                if (first)
                {
                    this.needGetVariable(var);
                    this.writeGetVariable(var);
                }
                else
                {
                    if (expr == ExpressionType.ARRAY)
                        this.block.callRuntime("getMap");
                }
                this.tokenizer.next();
                this.parseExpression();
                this.checkToken(Token.BRACKET_CLOSE);
                this.tokenizer.next();
                oop = first = false;
                expr = ExpressionType.ARRAY;
                break;
            case ASSIGN_ADD:
            case ASSIGN_DIV:
            case ASSIGN_AND:
            case ASSIGN_MODULO:
            case ASSIGN_MUL:
            case ASSIGN_OR:
            case ASSIGN_SUB:
            case ASSIGN_XOR:
            case ASSIGN_STRCAT:
                if (first)
                {
                    this.needGetVariable(var);
                    expr = ExpressionType.VARIABLE;
                }
                end = true;
                break;
            case ASSIGN:
                if (first)
                {
                    this.needSetVariable(var);
                    expr = ExpressionType.VARIABLE;
                }
                end = true;
                break;
            default:
                if (!getContext && first && var.type == Type.NONE)
                {
                    this.syntaxError();
                }
                else if (getContext && first)
                {
                    if (!var.isFunction())
                        this.needGetVariable(var);
                    expr = ExpressionType.VARIABLE;
                }
                end = true;
                break;
            }
        }

        switch (this.tokenizer.token)
        {
        case ASSIGN_ADD:
        case ASSIGN_DIV:
        case ASSIGN_AND:
        case ASSIGN_MODULO:
        case ASSIGN_MUL:
        case ASSIGN_OR:
        case ASSIGN_SUB:
        case ASSIGN_XOR:
        case ASSIGN_STRCAT:
        {
            final Token op = this.tokenizer.token;
            if (first && expr != ExpressionType.ARRAY && var.type == Type.NONE)
                this.syntaxError();
            if (expr == ExpressionType.ARRAY)
            {
                this.block.callRuntime("sdup2");
                this.block.callRuntime("getMap");
            }
            else
            {
                this.writeGetVariable(var);
            }
            this.tokenizer.next();
            this.parseExpression();
            switch (op)
            {
            case ASSIGN_ADD:
                this.block.callRuntime("add");
                break;
            case ASSIGN_DIV:
                this.block.callRuntime("div");
                break;
            case ASSIGN_AND:
                this.block.callRuntime("and");
                break;
            case ASSIGN_MODULO:
                this.block.callRuntime("mod");
                break;
            case ASSIGN_MUL:
                this.block.callRuntime("mul");
                break;
            case ASSIGN_OR:
                this.block.callRuntime("or");
                break;
            case ASSIGN_SUB:
                this.block.callRuntime("sub");
                break;
            case ASSIGN_XOR:
                this.block.callRuntime("xor");
                break;
            case ASSIGN_STRCAT:
                this.block.callRuntime("strcat");
                break;
            default:
                break;
            }
            if (expr == ExpressionType.ARRAY)
            {
                if (getContext)
                    this.block.callRuntime("sdups");
                this.block.callRuntime("setMap");
            }
            else
            {
                if (getContext)
                    this.block.callRuntime("sdup");
                this.writeSetVariable(var);
            }
            break;
        }
        case ASSIGN:
            this.tokenizer.next();
            this.parseExpression();
            if (expr == ExpressionType.ARRAY)
            {
                if (getContext)
                    this.block.callRuntime("sdups");
                this.block.callRuntime("setMap");
            }
            else if (expr == ExpressionType.VARIABLE)
            {
                if (getContext)
                    this.block.callRuntime("sdup");
                this.writeSetVariable(var);
            }
            else
            {
                this.syntaxError();
            }
            break;
        default:
            if (first && getContext
                    && (var.type != Type.NONE || var.function != null))
            {
                this.writeGetVariable(var);
            }
            else if (getContext && expr == ExpressionType.ARRAY)
            {
                this.block.callRuntime("getMap");
            }
            else if (expr != ExpressionType.FUNCTION)
            {
                if (getContext && first)
                {
                    throw new WeelException(this.tokenizer.error(
                            "Unknown variable '%s'", var.name));
                }
                this.syntaxError();
            }
            break;
        }
    }

    /**
     * Parses an operand.
     */
    private void parseOperand()
    {
        switch (this.tokenizer.token)
        {
        case NUMBER:
            this.block.load(this.tokenizer.number);
            this.tokenizer.next();
            return;
        case STRING:
            this.block.load(this.tokenizer.string);
            this.tokenizer.next();
            return;
        case RESERVED:
            switch (this.tokenizer.reserved)
            {
            case TRUE:
                this.block.load(-1);
                this.tokenizer.next();
                return;
            case FALSE:
                this.block.load(0);
                this.tokenizer.next();
                return;
            case NULL:
                this.block.callRuntime("load");
                this.tokenizer.next();
                return;
            case THIS:
                this.parseVarsAndFuncs(true);
                return;
            default:
                this.syntaxError();
                return;
            }
        case NAME:
            this.parseVarsAndFuncs(true);
            return;
        case CURLY_BRACE_OPEN:
        {
            int index = 0;
            this.block.callRuntime("createMap");
            this.tokenizer.next();
            while (this.tokenizer.token != Token.CURLY_BRACE_CLOSE)
            {
                this.block.callRuntime("sdup");
                switch (this.tokenizer.token)
                {
                case DOT:
                    this.tokenizer.next();
                    this.checkToken(Token.NAME);
                    this.block.load(this.tokenizer.string);
                    this.tokenizer.next();
                    this.checkToken(Token.ASSIGN);
                    this.tokenizer.next();
                    this.parseExpression();
                    this.block.callRuntime("setMap");
                    break;
                case BRACKET_OPEN:
                    this.tokenizer.next();
                    this.parseExpression();
                    this.checkToken(Token.BRACKET_CLOSE);
                    this.tokenizer.next();
                    this.checkToken(Token.ASSIGN);
                    this.tokenizer.next();
                    this.parseExpression();
                    this.block.callRuntime("setMap");
                    break;
                case NAME:
                case STRING:
                {
                    final Token prev = this.tokenizer.token;
                    final String name = this.tokenizer.string;
                    if (this.tokenizer.next() == Token.ASSIGN)
                    {
                        this.block.load(name);
                        this.tokenizer.next();
                        this.parseExpression();
                        this.block.callRuntime("setMap");
                    }
                    else
                    {
                        switch (this.tokenizer.token)
                        {
                        case NAME:
                        case RESERVED:
                        case NUMBER:
                        case STRING:
                            this.syntaxError();
                            break;
                        default:
                            break;
                        }
                        this.tokenizer.ungetToken(prev);
                        this.block.load(index++);
                        this.parseExpression();
                        this.block.callRuntime("setMap");
                    }
                    break;
                }
                default:
                    this.block.load(index++);
                    this.parseExpression();
                    this.block.callRuntime("setMap");
                    break;
                }
                if (this.tokenizer.token == Token.CURLY_BRACE_CLOSE)
                    break;
                if (this.tokenizer.token == Token.COMMA)
                {
                    this.tokenizer.next();
                    continue;
                }
                this.syntaxError();
            }
            this.checkToken(Token.CURLY_BRACE_CLOSE);
            this.tokenizer.next();
            return;
        }
        default:
            this.syntaxError();
        }
    }

    /**
     * Parses an expression.
     */
    private void parseExpression()
    {
        if (this.tokenizer.token == Token.RESERVED
                && (this.tokenizer.reserved == ReservedWord.FUNC || this.tokenizer.reserved == ReservedWord.SUB))
        {
            // Parse anonymous function
            final Scope start = this.scope;

            this.openFunction(true);

            while (this.scope != start && this.tokenizer.token != Token.EOF)
            {
                this.compileToken();
            }
        }
        else
        {
            this.parseExpression(-1);
        }
    }

    /**
     * Parses an expression.
     * 
     * @param prio
     *            The priority.
     * @return The last operator token.
     */
    // TODO static expression evaluation
    private Token parseExpression(final int prio)
    {
        this.checkExpr();
        if (this.tokenizer.isUnary(this.tokenizer.token))
        {
            switch (this.tokenizer.token)
            {
            case BRACE_OPEN:
                this.tokenizer.next();
                this.parseExpression(-1);
                this.checkToken(Token.BRACE_CLOSE);
                this.tokenizer.next();
                break;
            case SUB:
                this.tokenizer.next();
                if (this.tokenizer.token == Token.NUMBER)
                {
                    this.block.load(-this.tokenizer.number);
                    this.tokenizer.next();
                }
                else
                {
                    this.parseExpression(Tokenizer.UOPR_PRIORITY);
                    this.block.callRuntime("neg");
                }
                break;
            default:
            {
                Token tok = this.tokenizer.token;
                this.tokenizer.next();
                this.parseExpression(Tokenizer.UOPR_PRIORITY);
                switch (tok)
                {
                case LOGICAL_NOT:
                    this.block.callRuntime("lnot");
                    break;
                case BINARY_NOT:
                    this.block.callRuntime("not");
                    break;
                default:
                    break;
                }
            }
                break;
            }
        }
        else
        {
            this.parseOperand();
        }

        Token bop = this.tokenizer.token;

        while (this.tokenizer.isBinary(bop)
                && this.tokenizer.getBinaryPriority(bop) > prio)
        {
            this.tokenizer.next();
            int taddr = 0;
            // Short circuit logical and/or
            if (bop == Token.LOGICAL_AND)
            {
                this.block.callRuntime("testPopFalse", "()Z");
                taddr = this.block.writeJmp(JvmOp.IFNE, 0);
            }
            else if (bop == Token.LOGICAL_OR)
            {
                this.block.callRuntime("testPopTrue", "()Z");
                taddr = this.block.writeJmp(JvmOp.IFNE, 0);
            }

            Token nbop = this.parseExpression(this.tokenizer
                    .getBinaryPriority(bop));

            switch (bop)
            {
            case ADD:
                this.block.callRuntime("add");
                break;
            case SUB:
                this.block.callRuntime("sub");
                break;
            case MUL:
                this.block.callRuntime("mul");
                break;
            case DIV:
                this.block.callRuntime("div");
                break;
            case LOGICAL_AND:
            case LOGICAL_OR:
                this.block.writeShortAt(taddr, this.block.getPc() - taddr + 1);
                break;
            case EQUAL:
                this.block.callRuntime("cmpEq");
                break;
            case NOT_EQUAL:
                this.block.callRuntime("cmpNe");
                break;
            case GREATER:
                this.block.callRuntime("cmpGt");
                break;
            case GREATER_EQUAL:
                this.block.callRuntime("cmpGe");
                break;
            case LESS:
                this.block.callRuntime("cmpLt");
                break;
            case LESS_EQUAL:
                this.block.callRuntime("cmpLe");
                break;
            case STRING_CONCAT:
                this.block.callRuntime("strcat");
                break;
            case MODULO:
                this.block.callRuntime("mod");
                break;
            case BINARY_AND:
                this.block.callRuntime("and");
                break;
            case BINARY_OR:
                this.block.callRuntime("or");
                break;
            case BINARY_XOR:
                this.block.callRuntime("xor");
                break;
            default:
                break;
            }

            bop = nbop;
        }

        return bop;
    }

    /**
     * Closes a scope.
     */
    private void closeScope()
    {
        switch (this.scope.type)
        {
        case IF:
            this.closeIf();
            break;
        case FOR:
            this.closeFor();
            break;
        case DO:
            this.closeDo();
            break;
        case WHILE:
            this.closeWhile();
            break;
        case SUB:
        case FUNC:
            this.closeFunction();
            break;
        }
    }

    /**
     * Adds a 'break' to a scope.
     */
    private void addBreak()
    {
        final Scope s = this.scope.getBreakScope();
        if (s == null)
        {
            throw new WeelException(this.tokenizer
                    .error("BREAK without suitable scope"));
        }
        s.addBreak(s.block.writeJmp(JvmOp.GOTO, 0));
        this.tokenizer.next();
    }

    /**
     * Adds a 'continue' to a scope.
     */
    private void addContinue()
    {
        final Scope s = this.scope.getBreakScope();
        if (s == null)
        {
            throw new WeelException(this.tokenizer
                    .error("CONTINUE without suitable scope"));
        }
        s.addContinue(s.block.writeJmp(JvmOp.GOTO, 0));
        this.tokenizer.next();
    }

    /**
     * Opens a FOR scope.
     */
    private void openFor()
    {
        this.addScope(new Scope(this.scope, ScopeType.FOR));
        this.tokenizer.next();
        this.checkToken(Token.NAME);
        final Variable var = this.scope.findVariable(new Variable(),
                this.tokenizer.string);
        if (var.type != Type.LOCAL)
        {
            var.index = this.scope.addLocal(var.name);
            var.type = Type.LOCAL;
        }
        this.tokenizer.next();
        this.checkToken(Token.ASSIGN);
        this.tokenizer.next();
        this.parseExpression();
        this.block.callRuntime("sloc", var.index);
        this.checkToken(Token.COMMA);
        this.tokenizer.next();
        this.parseExpression();
        if (this.tokenizer.token == Token.COMMA)
        {
            this.tokenizer.next();
            this.parseExpression();
        }
        else
        {
            this.block.load(1);
        }
        this.checkReserved(ReservedWord.DO);
        this.tokenizer.next();
        this.block.callRuntime("beginForLoop", "(I)Z", var.index);
        this.scope.addBreak(this.block.writeJmp(JvmOp.IFEQ, 0));
        this.scope.startPc = this.block.getPc();
        this.scope.localIndex = var.index;
    }

    /**
     * Closes a FOR scope.
     */
    private void closeFor()
    {
        for (final int pc : this.scope.continues)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        this.block.callRuntime("endForLoop", "(I)Z", this.scope.localIndex);
        this.block
                .writeJmp(JvmOp.IFNE, this.scope.startPc - this.block.getPc());
        for (final int pc : this.scope.breaks)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        this.block.callRuntime("pop", 2);
        this.removeScope();
        this.tokenizer.next();
    }

    /**
     * Opens a DO scope.
     */
    private void openDo()
    {
        this.addScope(new Scope(this.scope, ScopeType.DO));
        this.tokenizer.next();
        this.scope.startPc = this.block.getPc();
    }

    /**
     * Closes a DO scope.
     */
    private void closeDo()
    {
        for (final int pc : this.scope.breaks)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        if (!this.scope.continues.isEmpty())
            throw new WeelException(this.tokenizer.error("Misplaced CONTINUE"));
        this.removeScope();
        this.tokenizer.next();
    }

    /**
     * Closes a DO scope.
     */
    private void closeDoUntil()
    {
        for (final int pc : this.scope.continues)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        this.tokenizer.next();
        this.parseExpression();
        this.block.callRuntime("popBoolean", "()Z");
        this.block
                .writeJmp(JvmOp.IFEQ, this.scope.startPc - this.block.getPc());

        for (final int pc : this.scope.breaks)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        this.removeScope();
    }

    /**
     * Opens a WHILE scope.
     */
    private void openWhile()
    {
        this.addScope(new Scope(this.scope, ScopeType.WHILE));
        this.tokenizer.next();
        this.scope.startPc = this.block.getPc();
        this.parseExpression();
        this.checkReserved(ReservedWord.DO);
        this.tokenizer.next();
        this.block.callRuntime("popBoolean", "()Z");
        this.scope.addBreak(this.block.writeJmp(JvmOp.IFEQ, 0));
    }

    /**
     * Closes a WHILE scope.
     */
    private void closeWhile()
    {
        this.scope.addContinue(this.block.writeJmp(JvmOp.GOTO, 0));
        for (final int pc : this.scope.continues)
        {
            this.block.writeShortAt(pc, this.scope.startPc - pc + 1);
        }
        for (final int pc : this.scope.breaks)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        this.removeScope();
        this.tokenizer.next();
    }

    /**
     * Opens an IF scope.
     */
    private void openIf()
    {
        this.tokenizer.next();
        this.parseExpression();
        this.checkReserved(ReservedWord.THEN);
        this.tokenizer.next();
        this.addScope(new Scope(this.scope, ScopeType.IF));
        this.block.callRuntime("popBoolean", "()Z");
        this.scope.addContinue(this.block.writeJmp(JvmOp.IFEQ, 0));
    }

    /**
     * Adds an 'ELSEIF' to an IF scope.
     */
    private void addElseIf()
    {
        if (this.scope == null || this.scope.type != ScopeType.IF)
            throw new WeelException(this.tokenizer.error("ELSE without IF"));
        if (this.scope.hasElse)
            throw new WeelException(this.tokenizer.error("ELSEIF after ELSE"));
        this.scope.addBreak(this.block.writeJmp(JvmOp.GOTO, 0));
        final int cont = this.scope.removeContinue();
        this.block.writeShortAt(cont, this.block.getPc() - cont + 1);

        this.tokenizer.next();
        this.parseExpression();
        this.checkReserved(ReservedWord.THEN);
        this.tokenizer.next();
        this.block.callRuntime("popBoolean", "()Z");
        this.scope.addContinue(this.block.writeJmp(JvmOp.IFEQ, 0));
    }

    /**
     * Adds an 'ELSE' to an IF scope.
     */
    private void addElse()
    {
        if (this.scope == null || this.scope.type != ScopeType.IF)
            throw new WeelException(this.tokenizer.error("ELSE without IF"));
        if (this.scope.hasElse)
            throw new WeelException(this.tokenizer.error("Duplicate ELSE"));

        this.tokenizer.next();
        this.scope.hasElse = true;
        this.scope.addBreak(this.block.writeJmp(JvmOp.GOTO, 0));
        final int cont = this.scope.removeContinue();
        this.block.writeShortAt(cont, this.block.getPc() - cont + 1);
    }

    /**
     * Closes an IF scope.
     */
    private void closeIf()
    {
        for (final int pc : this.scope.continues)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        for (final int pc : this.scope.breaks)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        this.tokenizer.next();
        this.removeScope();
    }

    /**
     * Opens a function.
     * 
     * @param anonymous
     *            Are we anonymous?
     */
    private void openFunction(final boolean anonymous)
    {
        if (anonymous)
            this.addScope(new Scope(this.scope, ScopeType.BORDER));
        this.addScope(new Scope(this.scope,
                this.tokenizer.reserved == ReservedWord.SUB ? ScopeType.SUB
                        : ScopeType.FUNC));
        this.block = this.scope.block = new CodeBlock();
        this.block.isAnonymousFunction = anonymous;

        final WeelFunction func = this.block.function = new WeelFunction();
        func.returnsValue = this.tokenizer.reserved == ReservedWord.FUNC;
        this.tokenizer.next();

        if (!anonymous)
        {
            this.checkToken(Token.NAME);
            func.name = this.tokenizer.string;
            this.tokenizer.next();

            if (this.tokenizer.token == Token.DOT
                    || this.tokenizer.token == Token.DOUBLE_COLON)
            {
                this.scope.isOop = this.tokenizer.token == Token.DOUBLE_COLON;
                // We use the scope parent here ... why?
                // Because we want to support 'local classes' ... and the parent
                // scope should always be valid here (static scope)
                this.scope.oopVariable = this.scope.parent.findVariable(
                        new Variable(), func.name);
                this.needGetVariable(this.scope.oopVariable);
                this.tokenizer.next();
                this.checkToken(Token.NAME);
                func.name += (this.scope.isOop ? "$$" : "$")
                        + (this.scope.oopIndex = this.tokenizer.string);
                this.tokenizer.next();
            }
        }
        else
        {
            func.name = "ANON";
        }

        int paramc = 0;

        if (this.scope.isOop)
        {
            this.scope.addLocal("THIS");
            paramc++;
        }

        this.checkToken(Token.BRACE_OPEN);
        this.tokenizer.next();
        while (this.tokenizer.token != Token.BRACE_CLOSE)
        {
            paramc++;
            this.checkToken(Token.NAME);
            this.scope.addLocal(this.tokenizer.string);
            this.tokenizer.next();
            if (this.tokenizer.token == Token.COMMA)
            {
                this.tokenizer.next();
                continue;
            }
            if (this.tokenizer.token != Token.BRACE_CLOSE)
            {
                this.syntaxError();
            }
        }
        this.tokenizer.next();
        func.arguments = paramc;

        if (this.weel.findFunction(func.name, func.arguments) != null)
        {
            throw new WeelException(this.tokenizer.error("Duplicate function: "
                    + func));
        }

        func.clazz = this.classWriter.className;
        func.javaName = anonymous ? "Anon_" + anonCounter++ : "_" + func.name
                + "_" + func.arguments;
        this.block.setMethodWriter(this.classWriter.createMethod(func.javaName,
                "(Lcom/github/rjeschke/weel/Runtime;)V"));

        this.weel.addFunction(func.name + "#" + func.arguments, func,
                !anonymous);
    }

    /**
     * Closes a function.
     */
    private void closeFunction()
    {
        final WeelFunction func = this.block.function;
        final boolean anonymous = this.block.isAnonymousFunction;
        final Variable oopVar = this.scope.oopVariable;
        final String oopIndex = this.scope.oopIndex;

        for (final int pc : this.scope.breaks)
        {
            this.block.writeShortAt(pc, this.block.getPc() - pc + 1);
        }
        this.block.closeBlock();

        if (!this.block.cvarIndex.isEmpty())
        {
            func.environment = new Value[this.block.cvarIndex.size()];
            func.envLocals = new int[this.block.cvarIndex.size()];
            for (int i = 0; i < func.environment.length; i++)
            {
                func.envLocals[i] = this.block.cvarIndex.get(i);
            }
        }

        this.removeScope();
        if (anonymous)
        {
            this.removeScope();
            if (func.envLocals != null)
                this.block.callRuntime("createVirtual", func.index);
            else
                this.block.callRuntime("loadFunc", func.index);
        }
        this.tokenizer.next();

        if (oopVar != null)
        {
            this.writeGetVariable(oopVar);
            this.block.load(oopIndex);
            this.block.callRuntime("loadFunc", func.index);
            this.block.callRuntime("setMap");
        }
    }

    /**
     * Adds 'exit' to a sub.
     */
    private void addExit()
    {
        final Scope s = this.scope.findFunctionScope();
        if (s == null || s.type == ScopeType.FUNC)
        {
            throw new WeelException(this.tokenizer.error("EXIT without SUB"));
        }
        // TODO exit pops
        this.scope.addBreak(this.block.writeJmp(JvmOp.GOTO, 0));
        this.tokenizer.next();
    }

    /**
     * Adds 'return' to a sub.
     */
    private void addReturn()
    {
        final Scope s = this.scope.findFunctionScope();
        if (s == null || s.type == ScopeType.SUB)
        {
            throw new WeelException(this.tokenizer.error("RETURN without FUNC"));
        }
        // TODO exit pops
        this.tokenizer.next();
        this.parseExpression();
        this.scope.addBreak(this.block.writeJmp(JvmOp.GOTO, 0));
    }

    /**
     * Parses the 'local' keyword
     */
    private void parseLocal()
    {
        this.tokenizer.next();

        while (this.tokenizer.token != Token.EOF)
        {
            this.checkToken(Token.NAME);
            final String name = this.tokenizer.string;
            if (this.scope.locals.containsKey(name)
                    || this.block.cvars.containsKey(name))
            {
                throw new WeelException(this.tokenizer.error(
                        "Duplicate explicit local variable '%s'", name));
            }
            final int index = this.scope.addLocal(name);
            this.tokenizer.next();
            if (this.tokenizer.token == Token.ASSIGN)
            {
                this.tokenizer.next();
                this.parseExpression();
                this.block.callRuntime("sloc", index);
            }
            if (this.tokenizer.token == Token.COMMA)
            {
                this.tokenizer.next();
                continue;
            }
            break;
        }
    }

    /**
     * Parses the 'outer' keyword
     */
    private void parseOuter()
    {
        //
    }

    /**
     * Parses the 'global' keyword
     */
    private void parseGlobal()
    {
        this.tokenizer.next();

        while (this.tokenizer.token != Token.EOF)
        {
            this.checkToken(Token.NAME);
            final String name = this.tokenizer.string;
            if (this.weel.mapGlobals.containsKey(name))
            {
                throw new WeelException(this.tokenizer.error(
                        "Duplicate global variable '%s'", name));
            }
            final int index = this.weel.addGlobal(name);
            this.tokenizer.next();
            if (this.tokenizer.token == Token.ASSIGN)
            {
                this.tokenizer.next();
                this.parseExpression();
                this.block.callRuntime("sglob", index);
            }
            if (this.tokenizer.token == Token.COMMA)
            {
                this.tokenizer.next();
                continue;
            }
            break;
        }
    }

    /**
     * Checks if there's an expression.
     * 
     * @throws WeelException
     *             if there's no expression.
     */
    private void checkExpr()
    {
        if (!this.tokenizer.isExpression())
            throw new WeelException(this.tokenizer.error("Expression expected"));
    }

    /**
     * Checks if there's the specified ReservedWord.
     * 
     * @param rw
     *            The ReservedWord.
     * @throws WeelException
     *             if there's no ReservedWord.
     */
    private void checkReserved(final ReservedWord rw)
    {
        if (this.tokenizer.token != Token.RESERVED
                || this.tokenizer.reserved != rw)
            throw new WeelException(this.tokenizer.error("'%s' expected", rw
                    .toString().toLowerCase()));
    }

    /**
     * Check if the specified token is there.
     * 
     * @param tok
     *            The token.
     * @throws WeelException
     *             if there's no token.
     */
    private void checkToken(final Token tok)
    {
        if (this.tokenizer.token != tok)
            this.errorExp(tok);
    }

    /**
     * Skips a semicolon if present.
     */
    private void skipSemi()
    {
        if (this.tokenizer.token == Token.SEMICOLON)
            this.tokenizer.next();
    }

    /**
     * Throws a syntax error.
     */
    private void syntaxError()
    {
        throw new WeelException(this.tokenizer.error("Syntax error"));
    }

    /**
     * Token error message.
     * 
     * @param tok
     *            The token.
     */
    private void errorExp(final Token tok)
    {
        String msg = "";
        switch (tok)
        {
        case BRACE_OPEN:
            msg += "'('";
            break;
        case BRACE_CLOSE:
            msg += "')'";
            break;
        case BRACKET_CLOSE:
            msg += "']'";
            break;
        case CURLY_BRACE_CLOSE:
            msg += "'}'";
            break;
        case COMMA:
            msg += "','";
            break;
        case COLON:
            msg += "':'";
            break;
        case ASSIGN:
            msg += "'='";
            break;
        case NAME:
            msg += "Name";
            break;
        case NUMBER:
            msg += "Number";
            break;
        case STRING:
            msg += "String";
            break;
        default:
            break;
        }
        throw new WeelException(this.tokenizer.error(msg + " expected"));
    }

    /**
     * Write a load variable instruction.
     * 
     * @param var
     *            The variable.
     */
    private void writeGetVariable(final Variable var)
    {
        if (var.type == Type.LOCAL)
            this.block.callRuntime("lloc", var.index);
        else if (var.type == Type.GLOBAL)
            this.block.callRuntime("lglob", var.index);
        else if (var.type == Type.CVAR)
            this.block.callRuntime("linenv", var.index);
        else if (var.function != null)
            this.block.callRuntime("loadFunc", var.function.index);
    }

    /**
     * Makes sure we get a variable to read from.
     * 
     * @param var
     *            The Variable.
     */
    private void needGetVariable(final Variable var)
    {
        if (var.type != Type.NONE)
            return;
        if (this.block.isAnonymousFunction)
        {
            this.scope.maybeCreateCvar(var);
        }
        if (var.type == Type.NONE)
        {
            throw new WeelException(this.tokenizer.error(
                    "Unknown variable '%s'", var.name));
        }
    }

    /**
     * Makes sure we get a variable to write to.
     * 
     * @param var
     *            The Variable.
     */
    private void needSetVariable(final Variable var)
    {
        if (var.type != Type.NONE)
            return;
        if (this.block.isAnonymousFunction)
        {
            this.scope.maybeCreateCvar(var);
        }
        if (var.type == Type.NONE)
        {
            var.type = Type.LOCAL;
            var.index = this.scope.addLocal(var.name);
        }
    }

    /**
     * Write a store variable instruction.
     * 
     * @param var
     *            The variable.
     */
    private void writeSetVariable(final Variable var)
    {
        if (var.type == Type.LOCAL)
            this.block.callRuntime("sloc", var.index);
        else if (var.type == Type.GLOBAL)
            this.block.callRuntime("sglob", var.index);
        else if (var.type == Type.CVAR)
            this.block.callRuntime("sinenv", var.index);
        else
            this.syntaxError();
    }

    /**
     * Write a function call.
     * 
     * @param func
     *            The WeelFunction.
     */
    // TODO nice functions (static wrapper may hide object instance)
    private void writeCallFunction(final WeelFunction func)
    {
        if (func.instance != null)
        {
            this.block.callRuntime("getFunctionInstance", func.index);
        }

        this.block.code.add(JvmOp.ALOAD_0);

        if (func.instance == null)
        {
            this.block.code.add(JvmOp.INVOKESTATIC);
        }
        else
        {
            this.block.code.add(JvmOp.INVOKEVIRTUAL);
        }
        this.block.code.addShort(this.classWriter.addMethodRefConstant(
                func.clazz, func.javaName,
                "(Lcom/github/rjeschke/weel/Runtime;)V"));
    }
}
