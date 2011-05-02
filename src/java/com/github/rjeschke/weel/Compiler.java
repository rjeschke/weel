/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
    ArrayList<Scope> scopes = new ArrayList<Scope>();
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
        this.initialize();

        this.tokenizer.next();

        while (this.tokenizer.token != Token.EOF)
            this.compileToken();

        this.scope.block.closeBlock();

        Weel.classLoader.addClass(this.classWriter);
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
        this.initialize();

        this.tokenizer.next();

        while (this.tokenizer.token != Token.EOF)
            this.compileToken();

        this.scope.block.closeBlock();

        Weel.classLoader.addClass(this.classWriter);
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
        s.parent = null;

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
            break;
        }
        // this.tokenizer.next();
    }

    /**
     * Parses an assign or a function call
     */
    private void parseVarsAndFuncs(final boolean getContext)
    {
        Variable var = this.scope.findVariable(new Variable(),
                this.tokenizer.string);
        boolean first = true, stackCall = false, end = false, isThis = false, oop = false;
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
                    if (var.type != Variable.Type.NONE)
                    {
                        this.writeGetVariable(var);
                        stackCall = true;
                    }
                }
                else
                {
                    if (expr == ExpressionType.ARRAY)
                    {
                        this.block.callRuntime("getMap");
                        stackCall = true;
                        if (oop)
                        {
                            this.block.callRuntime("sdupx1");
                            paramc++;
                        }
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
                        break;
                    }
                }
                else
                {
                    boolean wantsValue = false;
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
            case DOT:
                if(first)
                {
                    if(var.type == Type.NONE)
                    {
                        throw new WeelException(this.tokenizer.error("Unknown variable '%s'", var.name));
                    }
                    this.writeGetVariable(var);
                }
                this.tokenizer.next();
                this.checkToken(Token.NAME);
                this.block.load(this.tokenizer.string);
                this.tokenizer.next();
                this.block.callRuntime("getMap");
                oop = first = false;
                expr = ExpressionType.ARRAY;
                break;
            case BRACKET_OPEN:
                if(first)
                {
                    if(var.type == Type.NONE)
                    {
                        throw new WeelException(this.tokenizer.error("Unknown variable '%s'", var.name));
                    }
                    this.writeGetVariable(var);
                }
                this.tokenizer.next();
                this.parseExpression();
                this.checkToken(Token.BRACKET_CLOSE);
                this.tokenizer.next();
                this.block.callRuntime("getMap");
                oop = first = false;
                expr = ExpressionType.ARRAY;
                break;
            case ASSIGN:
                if (first)
                {
                    if (var.type == Type.NONE)
                    {
                        this.scope.findAddVariable(var, var.name);
                    }
                    expr = ExpressionType.VARIABLE;
                }
                end = true;
                break;
            default:
                if (!getContext && first && var.type == Type.NONE)
                {
                    this.syntaxError();
                }
                end = true;
                break;
            }
        }

        switch (this.tokenizer.token)
        {
        case ASSIGN:
            this.tokenizer.next();
            this.parseExpression();
            if(getContext)
                this.block.callRuntime("sdup");
            this.writeSetVariable(var);
            break;
        default:
            if (first && getContext)
            {
                this.writeGetVariable(var);
            }
            else if (expr != ExpressionType.FUNCTION)
            {
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
                // case THIS:
                // this.parseGetVarOrFuncCall();
                // return;
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
        this.parseExpression(-1);
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
            this.block.callRuntime("loadFunc", var.index);
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
