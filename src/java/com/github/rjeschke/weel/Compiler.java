/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rjeschke.weel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;

import com.github.rjeschke.weel.Value;
import com.github.rjeschke.weel.Weel;
import com.github.rjeschke.weel.WeelException;
import com.github.rjeschke.weel.WeelFunction;
import com.github.rjeschke.weel.Variable.Type;

/**
 * Weel compiler.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class Compiler
{
    /** The current tokenizer. */
    private Tokenizer tokenizer;
    /** The Weel. */
    private final Weel weel;
    /** Nested scopes. */
    private final LinkedList<Scope> scopes = new LinkedList<Scope>();
    /** The current CodeBlock. */
    private WeelCode block;
    /** The current Scope. */
    private Scope scope;
    /** The class writer. */
    private JvmClassWriter classWriter;
    /** Flag indicating that we're used. */
    private boolean used = false;
    /** Flag indicating that we're doing runtime compilation. */
    private boolean runtimeCompile = false;
    /** Last compiled function (for runtime compilation). */
    private WeelFunction lastFunction = null;
    /** Anonymous function counter. */
    private int anonCounter = 0;
    /** Name to exact (private) function index mapping. */
    private final HashMap<String, Integer> mapFunctionsExact = new HashMap<String, Integer>();
    /** Name to (private) function index mapping. */
    private final HashMap<String, Integer> mapFunctions = new HashMap<String, Integer>();

    /**
     * Constructor.
     * 
     * @param weel
     *            The Weel.
     */
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
    public void compile(final String input, final String filename)
    {
        this.tokenizer = new Tokenizer(new StringReader(input), filename);
        this.compile();
    }

    /**
     * Compiles the given input stream.
     * 
     * @param input
     *            The input stream.
     */
    public void compile(final InputStream input, final String filename)
    {
        try
        {
            this.tokenizer = new Tokenizer(new BufferedReader(
                    new InputStreamReader(input, "UTF-8")), filename);
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
        if (this.used)
        {
            throw new WeelException("Trying to reuse a compiler instance.");
        }
        this.initialize();

        this.tokenizer.next();

        while (this.tokenizer.token != Token.EOF)
            this.compileToken();

        if (this.scope.type != ScopeType.STATIC)
        {
            throw new WeelException("Open block: " + this.scope.type.toString());
        }

        this.scope.block.closeBlock(this.weel.debugMode, this.weel.dumpCode);

        this.blockToBytecode(this.block);

        this.weel.classLoader.addClass(this.classWriter);

        this.weel.initAllInvokers();

        this.used = true;
    }

    /**
     * Compile method for runtime compilation.
     */
    WeelFunction compileFunction(final String input)
    {
        this.tokenizer = new Tokenizer(new StringReader(input), null);
        if (this.used)
        {
            throw new WeelException("Trying to reuse a compiler instance.");
        }
        this.runtimeCompile = true;
        this.initialize();

        this.tokenizer.next();

        switch (this.tokenizer.token)
        {
        case ANON_OPEN:
            this.openFunction(true, true, false);
            break;
        case RESERVED:
            switch (this.tokenizer.reserved)
            {
            case FUNC:
            case SUB:
                this.openFunction(true, false, false);
                break;
            default:
                this.syntaxError();
                break;
            }
            break;
        default:
            this.syntaxError();
            break;
        }

        while (this.tokenizer.token != Token.EOF)
            this.compileToken();

        if (this.scope.type != ScopeType.STATIC)
        {
            throw new WeelException("Open block: " + this.scope.type.toString());
        }

        if (this.lastFunction == null)
        {
            this.syntaxError();
        }

        this.scope.block.closeBlock(this.weel.debugMode, this.weel.dumpCode);

        this.blockToBytecode(this.block);

        final WeelLoader loader = this.lastFunction.loader = new WeelLoader(
                this.weel.classLoader);

        loader.addClass(this.classWriter);

        this.lastFunction.invoker = WeelInvokerFactory.create();
        this.lastFunction.invoker.initialize(this.weel, this.lastFunction);

        this.used = true;

        return this.lastFunction;
    }

    /**
     * Initializes this compiler.
     */
    private void initialize()
    {
        this.classWriter = new JvmClassWriter(
                "com.github.rjeschke.weel.scripts.Script"
                        + Weel.scriptCounter.getAndIncrement());

        this.weel.scriptClasses.add(this.classWriter.className);

        final Scope s = new Scope(this.weel, ScopeType.STATIC, this);
        s.block = new WeelCode(this.weel);
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
     * Opens a functions with checks.
     * 
     * @param anonymous
     *            Is it anonymous?
     * @param isAlternate
     *            Is it alternate syntax?
     * @param isPrivate
     *            Is it private?
     */
    private void doOpenFunction(final boolean anonymous,
            final boolean isAlternate, final boolean isPrivate)
    {
        if (this.scope.findFunctionScope() != null)
        {
            throw new WeelException(this.tokenizer
                    .error("Can't declare a sub/func inside another sub/func"));
        }
        if (this.runtimeCompile)
        {
            throw new WeelException(
                    this.tokenizer
                            .error("Can't declare a global or private sub/func during runtime compilation."));
        }
        this.openFunction(anonymous, isAlternate, isPrivate);
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
        case CURLY_BRACE_OPEN:
        case STRING:
        case BRACE_OPEN:
        case NUMBER:
            this.tryStaticSupport();
            this.skipSemi();
            break;
        case CURLY_BRACE_CLOSE:
        {
            if ((this.scope.type == ScopeType.FUNC || this.scope.type == ScopeType.SUB)
                    && this.block.isAlternateSyntax)
            {
                this.closeFunction();
            }
            else
                this.syntaxError();
            break;
        }
        case ANON_OPEN:
            this.doOpenFunction(false, true, false);
            break;
        case RESERVED:
            switch (this.tokenizer.reserved)
            {
            case NULL:
            case TRUE:
            case FALSE:
                this.tryStaticSupport();
                this.skipSemi();
                break;
            case THIS:
                this.parseVarsAndFuncs(false);
                this.skipSemi();
                break;
            case LOCAL:
                this.parseLocal();
                this.skipSemi();
                break;
            case PRIVATE:
                this.tokenizer.next();
                if (this.tokenizer.token == Token.ANON_OPEN)
                {
                    this.doOpenFunction(false, true, true);
                }
                else if (this.tokenizer.token == Token.RESERVED
                        && (this.tokenizer.reserved == ReservedWord.SUB || this.tokenizer.reserved == ReservedWord.FUNC))
                {
                    this.doOpenFunction(false, false, true);
                }
                else
                {
                    this.parsePrivate();
                    this.skipSemi();
                }
                break;
            case OUTER:
                this.parseOuter();
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
            case FOREACH:
                this.openForEach();
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
            case SWITCH:
                this.openSwitch();
                break;
            case CASE:
                this.addCase();
                break;
            case DEFAULT:
                this.addDefault();
                break;
            case FUNC:
            case SUB:
                this.doOpenFunction(false, false, false);
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
     * Checks if the expression is a static support function call, e.g.
     * <code>"Hello world!"::length()</code>
     */
    private void tryStaticSupport()
    {
        this.parseExpression(false);
        if (this.tokenizer.token == Token.DOUBLE_COLON)
        {
            this.parseVarsAndFuncs(false);
        }
        else
        {
            this.syntaxError();
        }
    }

    /**
     * Checks if the current expression continues with a token that indicates
     * that it needs a return value. Only used in variables and function parsing
     * for non-first expressions.
     * 
     * @return <code>true</code> if so.
     */
    private boolean wouldNeedReturnValue()
    {
        switch (this.tokenizer.token)
        {
        case BRACE_OPEN:
        case BRACKET_OPEN:
        case DOT:
        case ARROW:
        case DOUBLE_COLON:
            // as this is only used for non-first expressions
            // we will need a return value for all kinds of
            // assigns
        case ASSIGN:
        case ASSIGN_ADD:
        case ASSIGN_DIV:
        case ASSIGN_AND:
        case ASSIGN_MODULO:
        case ASSIGN_MUL:
        case ASSIGN_OR:
        case ASSIGN_SUB:
        case ASSIGN_XOR:
        case ASSIGN_STRCAT:
        case ASSIGN_MAPCAT:
        case ASSIGN_SHL:
        case ASSIGN_SHR:
        case ASSIGN_USHR:
            return true;
        default:
            return false;
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
        boolean first = true, stackCall = false, end = false, oop = false, append = false;
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
        else if (this.tokenizer.token == Token.DOUBLE_COLON)
        {
            var = new Variable();
        }
        else
        {
            var = this.scope
                    .findVariable(new Variable(), this.tokenizer.string);
        }

        ExpressionType expr = var.type != Type.NONE ? ExpressionType.VARIABLE
                : ExpressionType.NONE;

        if (this.tokenizer.token != Token.DOUBLE_COLON)
            this.tokenizer.next();

        while (!end)
        {
            switch (this.tokenizer.token)
            {
            case DOUBLE_COLON:
            {
                int paramc = 0;
                this.tokenizer.next();
                this.checkToken(Token.NAME);
                final String name = this.tokenizer.string;
                if (expr == ExpressionType.ARRAY)
                {
                    this.block.add(new InstrGetMap());
                }
                else if (first && expr == ExpressionType.VARIABLE)
                {
                    this.needGetVariable(var);
                    this.writeGetVariable(var);
                }
                else if (first && var.function != null)
                {
                    this.writeGetVariable(var);
                }
                // FIXME ... is there something missing here?
                this.tokenizer.next();
                this.checkToken(Token.BRACE_OPEN);
                this.tokenizer.next();
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

                this.block.add(new InstrSpecialCall(name, paramc, getContext
                        || this.wouldNeedReturnValue()));

                oop = first = false;
                expr = ExpressionType.FUNCTION;
                break;
            }
            case BRACE_OPEN:
            {
                int paramc = 0;
                final boolean isAssert;
                this.tokenizer.next();

                if (first)
                {
                    isAssert = var.name.equals("assert");
                    if (!isAssert && !var.isFunction())
                    {
                        this.needGetVariable(var, true);
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
                            this.block.add(new InstrGetMapOop());
                            paramc++;
                        }
                        else
                        {
                            this.block.add(new InstrGetMap());
                        }
                        stackCall = true;
                    }
                    isAssert = false;
                }

                if (isAssert)
                {
                    this.parseAssert();
                    return;
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
                    WeelFunction func = this.findFunction(var.name, paramc);
                    if (func == null)
                    {
                        throw new WeelException(this.tokenizer
                                .error("Unknown function/sub " + var.name + "("
                                        + paramc + ")"));
                    }

                    this.block.add(new InstrCall(func));

                    if (getContext || this.wouldNeedReturnValue())
                    {
                        if (!func.returnsValue)
                        {
                            throw new WeelException(this.tokenizer.error("Sub "
                                    + func.getName() + "("
                                    + func.getNumArguments()
                                    + ") doesn't return a value"));
                        }
                    }
                    else
                    {
                        if (func.returnsValue)
                        {
                            this.block.add(new InstrPop(1));
                        }
                    }
                }
                else
                {
                    this.block.add(new InstrStackCall(paramc, getContext
                            | this.wouldNeedReturnValue()));
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
                        this.block.add(new InstrGetMap());
                }
                this.tokenizer.next();
                if (this.tokenizer.token == Token.BRACKET_OPEN)
                {
                    this.tokenizer.next();
                    this.parseExpression();
                    this.checkToken(Token.BRACKET_CLOSE);
                }
                else
                {
                    this.checkToken(Token.NAME);
                    this.block.add(new InstrLoad(this.tokenizer.string));
                }
                this.block.add(new InstrKey());
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
                        this.block.add(new InstrGetMap());
                }
                this.tokenizer.next();
                this.checkToken(Token.NAME);
                this.block.add(new InstrLoad(this.tokenizer.string));
                this.block.add(new InstrKey());
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
                        this.block.add(new InstrGetMap());
                }
                this.tokenizer.next();
                if (!getContext && this.tokenizer.token == Token.BRACKET_CLOSE)
                {
                    this.tokenizer.next();
                    if (this.tokenizer.token != Token.ASSIGN)
                        this.syntaxError();
                    append = true;
                }
                else
                {
                    this.parseExpression();
                    this.block.add(new InstrKey());
                    this.checkToken(Token.BRACKET_CLOSE);
                    this.tokenizer.next();
                }
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
            case ASSIGN_MAPCAT:
            case ASSIGN_SHL:
            case ASSIGN_SHR:
            case ASSIGN_USHR:
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
        case ASSIGN_MAPCAT:
        case ASSIGN_SHL:
        case ASSIGN_SHR:
        case ASSIGN_USHR:
        {
            final Token op = this.tokenizer.token;
            if (first && expr != ExpressionType.ARRAY && var.type == Type.NONE)
                this.syntaxError();
            if (expr == ExpressionType.ARRAY)
            {
                this.block.add(new InstrSdup2());
                this.block.add(new InstrGetMap());
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
                this.block.add(new InstrAlu2(Alu2InstrType.add));
                break;
            case ASSIGN_DIV:
                this.block.add(new InstrAlu2(Alu2InstrType.div));
                break;
            case ASSIGN_AND:
                this.block.add(new InstrAlu2(Alu2InstrType.and));
                break;
            case ASSIGN_MODULO:
                this.block.add(new InstrAlu2(Alu2InstrType.mod));
                break;
            case ASSIGN_MUL:
                this.block.add(new InstrAlu2(Alu2InstrType.mul));
                break;
            case ASSIGN_OR:
                this.block.add(new InstrAlu2(Alu2InstrType.or));
                break;
            case ASSIGN_SUB:
                this.block.add(new InstrAlu2(Alu2InstrType.sub));
                break;
            case ASSIGN_XOR:
                this.block.add(new InstrAlu2(Alu2InstrType.xor));
                break;
            case ASSIGN_STRCAT:
                this.block.add(new InstrAlu2(Alu2InstrType.strcat));
                break;
            case ASSIGN_MAPCAT:
                this.block.add(new InstrAlu2(Alu2InstrType.mapcat2));
                break;
            case ASSIGN_SHL:
                this.block.add(new InstrAlu2(Alu2InstrType.shl));
                break;
            case ASSIGN_SHR:
                this.block.add(new InstrAlu2(Alu2InstrType.shr));
                break;
            case ASSIGN_USHR:
                this.block.add(new InstrAlu2(Alu2InstrType.ushr));
                break;
            default:
                break;
            }
            if (expr == ExpressionType.ARRAY)
            {
                if (getContext)
                    this.block.add(new InstrSdups());
                this.block.add(new InstrSetMap());
            }
            else
            {
                if (getContext)
                    this.block.add(new InstrSdup());
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
                    this.block.add(new InstrSdups());

                if (append)
                    this.block.add(new InstrAppendMap());
                else
                    this.block.add(new InstrSetMap());
            }
            else if (expr == ExpressionType.VARIABLE)
            {
                if (getContext)
                    this.block.add(new InstrSdup());
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
                this.block.add(new InstrGetMap());
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
            this.block.add(new InstrLoad(this.tokenizer.number));
            this.tokenizer.next();
            break;
        case STRING:
            this.block.add(new InstrLoad(this.tokenizer.string));
            this.tokenizer.next();
            break;
        case RESERVED:
            switch (this.tokenizer.reserved)
            {
            case TRUE:
                this.block.add(new InstrLoad(-1));
                this.tokenizer.next();
                break;
            case FALSE:
                this.block.add(new InstrLoad(0));
                this.tokenizer.next();
                break;
            case NULL:
                this.block.add(new InstrLoad());
                this.tokenizer.next();
                break;
            case THIS:
                this.parseVarsAndFuncs(true);
                break;
            default:
                this.syntaxError();
                break;
            }
            break;
        case NAME:
            this.parseVarsAndFuncs(true);
            break;
        case CURLY_BRACE_OPEN:
        {
            this.block.add(new InstrCreateMap());
            this.tokenizer.next();
            while (this.tokenizer.token != Token.CURLY_BRACE_CLOSE)
            {
                this.block.add(new InstrSdup());
                switch (this.tokenizer.token)
                {
                case DOT:
                    this.tokenizer.next();
                    this.checkToken(Token.NAME);
                    this.block.add(new InstrLoad(this.tokenizer.string));
                    this.block.add(new InstrKey());
                    this.tokenizer.next();
                    this.checkToken(Token.ASSIGN);
                    this.tokenizer.next();
                    this.parseExpression();
                    this.block.add(new InstrSetMap());
                    break;
                case BRACKET_OPEN:
                    this.tokenizer.next();
                    this.parseExpression();
                    this.block.add(new InstrKey());
                    this.checkToken(Token.BRACKET_CLOSE);
                    this.tokenizer.next();
                    this.checkToken(Token.ASSIGN);
                    this.tokenizer.next();
                    this.parseExpression();
                    this.block.add(new InstrSetMap());
                    break;
                case NAME:
                {
                    final Token prev = this.tokenizer.token;
                    final String name = this.tokenizer.string;
                    if (this.tokenizer.next() == Token.ASSIGN)
                    {
                        this.block.add(new InstrLoad(name));
                        this.block.add(new InstrKey());
                        this.tokenizer.next();
                        this.parseExpression();
                        this.block.add(new InstrSetMap());
                    }
                    else
                    {
                        // FIXME do I need this? is this correct?
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
                        this.parseExpression();
                        this.block.add(new InstrAppendMap());
                    }
                    break;
                }
                default:
                    this.parseExpression();
                    this.block.add(new InstrAppendMap());
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
            this.tokenizer.next();
            break;
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
        this.parseExpression(true);
    }

    /**
     * Parses an expression.
     * 
     * @param allowDoubleColon
     *            Flag indicating if we allow the parsing of <code>::</code>
     */
    private void parseExpression(final boolean allowDoubleColon)
    {
        if (this.tokenizer.token == Token.RESERVED
                && (this.tokenizer.reserved == ReservedWord.FUNC || this.tokenizer.reserved == ReservedWord.SUB)
                || this.tokenizer.token == Token.ANON_OPEN)
        {
            // Parse anonymous function
            final Scope start = this.scope;

            this.openFunction(true, this.tokenizer.token == Token.ANON_OPEN,
                    false);

            while (this.scope != start && this.tokenizer.token != Token.EOF)
            {
                this.compileToken();
            }
        }
        else
        {
            this.parseExpression(-1);

            if (this.tokenizer.token == Token.TERNARY)
            {
                final int first = this.block.registerLabel();
                final int second = this.block.registerLabel();
                this.block.add(new InstrPopBool());
                this.block.add(new InstrIfEq(first));
                this.tokenizer.next();
                this.parseExpression();
                this.checkToken(Token.COLON);
                this.tokenizer.next();
                this.block.add(new InstrGoto(second));
                this.block.add(new InstrLabel(first));
                this.parseExpression();
                this.block.add(new InstrLabel(second));
            }
            else if (this.tokenizer.token == Token.DOUBLE_COLON
                    && allowDoubleColon)
            {
                this.parseVarsAndFuncs(true);
            }
        }
    }

    /**
     * Parses an expression.
     * 
     * @param prio
     *            The priority.
     * @return The last operator token.
     */
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
                    this.block.add(new InstrLoad(-this.tokenizer.number));
                    this.tokenizer.next();
                }
                else
                {
                    this.parseExpression(Tokenizer.UOPR_PRIORITY);
                    this.block.add(new InstrNeg());
                }
                break;
            default:
            {
                final Token tok = this.tokenizer.token;
                this.tokenizer.next();
                this.parseExpression(Tokenizer.UOPR_PRIORITY);
                switch (tok)
                {
                case LOGICAL_NOT:
                    this.block.add(new InstrLnot());
                    break;
                case BINARY_NOT:
                    this.block.add(new InstrNot());
                    break;
                default:
                    break;
                }
                break;
            }
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
                this.block.add(new InstrTestPopf());
                taddr = this.block.registerLabel();
                this.block.add(new InstrIfNe(taddr));
            }
            else if (bop == Token.LOGICAL_OR)
            {
                this.block.add(new InstrTestPopt());
                taddr = this.block.registerLabel();
                this.block.add(new InstrIfNe(taddr));
            }

            final Token nbop = this.parseExpression(this.tokenizer
                    .getBinaryPriority(bop));

            switch (bop)
            {
            case ADD:
                this.block.add(new InstrAlu2(Alu2InstrType.add));
                break;
            case SUB:
                this.block.add(new InstrAlu2(Alu2InstrType.sub));
                break;
            case MUL:
                this.block.add(new InstrAlu2(Alu2InstrType.mul));
                break;
            case POW:
                this.block.add(new InstrAlu2(Alu2InstrType.pow));
                break;
            case DIV:
                this.block.add(new InstrAlu2(Alu2InstrType.div));
                break;
            case LOGICAL_AND:
            case LOGICAL_OR:
                this.block.add(new InstrLabel(taddr));
                break;
            case EQUAL:
                this.block.add(new InstrAlu2(Alu2InstrType.cmpEq));
                break;
            case NOT_EQUAL:
                this.block.add(new InstrAlu2(Alu2InstrType.cmpNe));
                break;
            case GREATER:
                this.block.add(new InstrAlu2(Alu2InstrType.cmpGt));
                break;
            case GREATER_EQUAL:
                this.block.add(new InstrAlu2(Alu2InstrType.cmpGe));
                break;
            case LESS:
                this.block.add(new InstrAlu2(Alu2InstrType.cmpLt));
                break;
            case LESS_EQUAL:
                this.block.add(new InstrAlu2(Alu2InstrType.cmpLe));
                break;
            case STRING_CONCAT:
                this.block.add(new InstrAlu2(Alu2InstrType.strcat));
                break;
            case MAP_CONCAT:
                this.block.add(new InstrAlu2(Alu2InstrType.mapcat));
                break;
            case MODULO:
                this.block.add(new InstrAlu2(Alu2InstrType.mod));
                break;
            case BINARY_AND:
                this.block.add(new InstrAlu2(Alu2InstrType.and));
                break;
            case BINARY_OR:
                this.block.add(new InstrAlu2(Alu2InstrType.or));
                break;
            case BINARY_XOR:
                this.block.add(new InstrAlu2(Alu2InstrType.xor));
                break;
            case SHL:
                this.block.add(new InstrAlu2(Alu2InstrType.shl));
                break;
            case SHR:
                this.block.add(new InstrAlu2(Alu2InstrType.shr));
                break;
            case USHR:
                this.block.add(new InstrAlu2(Alu2InstrType.ushr));
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
        case FOREACH:
            this.closeForEach();
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
        case SWITCH:
            this.closeSwitch();
            break;
        default:
            throw new WeelException(this.tokenizer.error("'end' without block"));
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
                    .error("'break' without suitable scope"));
        }
        this.block.add(new InstrGoto(s.addBreak()));
        this.tokenizer.next();
    }

    /**
     * Adds a 'continue' to a scope.
     */
    private void addContinue()
    {
        final Scope s = this.scope.getContinueScope();
        if (s == null)
        {
            throw new WeelException(this.tokenizer
                    .error("'continue' without suitable scope"));
        }
        this.block.add(new InstrGoto(s.addContinue()));
        this.tokenizer.next();
    }

    /**
     * Adds a case to a switch.
     */
    private void addCase()
    {
        int cont = 0;
        if (this.scope.type != ScopeType.SWITCH)
        {
            throw new WeelException(this.tokenizer
                    .error("'switch' without 'case'"));
        }
        if (this.scope.hasDefault)
        {
            throw new WeelException(this.tokenizer
                    .error("'switch' after 'default'"));
        }
        if (this.scope.hasCase)
        {
            cont = this.block.registerLabel();
            this.block.add(new InstrGoto(cont));
            this.block.add(new InstrLabel(this.scope.continueLabel));
            this.scope.continueLabel = -1;
        }
        this.block.add(new InstrSdup());
        this.tokenizer.next();
        this.parseExpression();
        this.checkToken(Token.COLON);
        this.tokenizer.next();
        this.block.add(new InstrCmpEqual());
        this.block.add(new InstrIfEq(this.scope.addContinue()));
        if (this.scope.hasCase)
        {
            this.block.add(new InstrLabel(cont));
        }
        this.scope.hasCase = true;
    }

    /**
     * Adds a default to a switch.
     */
    private void addDefault()
    {
        if (this.scope.type != ScopeType.SWITCH)
        {
            throw new WeelException(this.tokenizer
                    .error("'default' without 'case'"));
        }
        if (this.scope.hasDefault)
        {
            throw new WeelException(this.tokenizer.error("Duplicate 'default'"));
        }
        if (this.scope.hasCase)
        {
            this.block.add(new InstrLabel(this.scope.continueLabel));
            this.scope.continueLabel = -1;
        }

        this.tokenizer.next();
        this.checkToken(Token.COLON);
        this.tokenizer.next();
        this.scope.hasDefault = true;
    }

    /**
     * Opens a SWITCH scope.
     */
    private void openSwitch()
    {
        this.addScope(new Scope(this.scope, ScopeType.SWITCH));
        this.tokenizer.next();
        this.parseExpression();
        this.checkReserved(ReservedWord.DO);
        this.tokenizer.next();
    }

    /**
     * Closes a SWITCH scope.
     */
    private void closeSwitch()
    {
        if (this.scope.continueLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.continueLabel));
        }
        if (this.scope.breakLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.breakLabel));
        }
        this.block.add(new InstrPop(1));
        this.removeScope();
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
        this.block.add(new InstrVarStore(VarInstrType.LOCAL, var.index));
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
            this.block.add(new InstrLoad(1));
        }
        this.checkReserved(ReservedWord.DO);
        this.tokenizer.next();
        this.block.add(new InstrBeginFor(var.index));
        this.block.add(new InstrIfEq(this.scope.addBreak()));
        this.scope.start = this.block.registerLabel();
        this.block.add(new InstrLabel(this.scope.start));
        this.scope.localIndex = var.index;
    }

    /**
     * Closes a FOR scope.
     */
    private void closeFor()
    {
        if (this.scope.continueLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.continueLabel));
        }
        this.block.add(new InstrEndFor(this.scope.localIndex));
        this.block.add(new InstrIfNe(this.scope.start));
        if (this.scope.breakLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.breakLabel));
        }
        this.block.add(new InstrPop(2));
        this.removeScope();
        this.tokenizer.next();
    }

    /**
     * Opens a FOREACH scope.
     */
    private void openForEach()
    {
        this.addScope(new Scope(this.scope, ScopeType.FOREACH));
        this.tokenizer.next();
        this.checkToken(Token.NAME);
        Variable key = null, val = this.scope.findVariable(new Variable(),
                this.tokenizer.string);
        this.needSetVariable(val);
        this.tokenizer.next();
        if (this.tokenizer.token == Token.COMMA)
        {
            this.tokenizer.next();
            this.checkToken(Token.NAME);
            key = val;
            val = this.scope
                    .findVariable(new Variable(), this.tokenizer.string);
            this.needSetVariable(val);
            this.tokenizer.next();
        }
        this.checkReserved(ReservedWord.IN);
        this.tokenizer.next();
        if (this.tokenizer.token == Token.RESERVED
                && this.tokenizer.reserved == ReservedWord.THIS)
        {
            final Scope s = this.scope.findFunctionScope();
            if (s == null || !s.isOop)
            {
                throw new WeelException(this.tokenizer
                        .error("Illegal use of 'this'"));
            }
            this.block.add(new InstrVarLoad(VarInstrType.LOCAL, 0));
        }
        else
        {
            this.checkToken(Token.NAME);
            final Variable map = this.scope.findVariable(new Variable(),
                    this.tokenizer.string);
            this.needGetVariable(map);
            this.writeGetVariable(map);
        }
        this.tokenizer.next();
        this.checkReserved(ReservedWord.DO);
        this.tokenizer.next();

        this.block.add(new InstrPrepareForEach());
        this.block.add(new InstrLabel(this.scope.addContinue()));
        this.block.add(new InstrDoForEach());
        this.block.add(new InstrIfEq(this.scope.addBreak()));
        this.writeSetVariable(val);
        if (key != null)
            this.writeSetVariable(key);
        else
            this.block.add(new InstrPop(1));
    }

    /**
     * Closes a FOREACH scope.
     */
    private void closeForEach()
    {
        this.block.add(new InstrGoto(this.scope.addContinue()));
        if (this.scope.breakLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.breakLabel));
        }
        this.block.add(new InstrPop(1));
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
        this.scope.start = this.block.registerLabel();
        this.block.add(new InstrLabel(this.scope.start));
    }

    /**
     * Closes a DO scope.
     */
    private void closeDo()
    {
        if (this.scope.breakLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.breakLabel));
        }
        if (this.scope.continueLabel != -1)
            throw new WeelException(this.tokenizer
                    .error("Misplaced 'continue'"));
        this.removeScope();
        this.tokenizer.next();
    }

    /**
     * Closes a DO scope.
     */
    private void closeDoUntil()
    {
        if (this.scope.continueLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.continueLabel));
        }
        this.tokenizer.next();
        this.parseExpression();
        this.block.add(new InstrPopBool());
        this.block.add(new InstrIfEq(this.scope.start));
        if (this.scope.breakLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.breakLabel));
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
        this.block.add(new InstrLabel(this.scope.addContinue()));
        this.parseExpression();
        this.checkReserved(ReservedWord.DO);
        this.tokenizer.next();
        this.block.add(new InstrPopBool());
        this.block.add(new InstrIfEq(this.scope.addBreak()));
    }

    /**
     * Closes a WHILE scope.
     */
    private void closeWhile()
    {
        this.block.add(new InstrGoto(this.scope.continueLabel));
        this.block.add(new InstrLabel(this.scope.breakLabel));
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
        this.block.add(new InstrPopBool());
        this.block.add(new InstrIfEq(this.scope.addContinue()));
    }

    /**
     * Adds an 'ELSEIF' to an IF scope.
     */
    private void addElseIf()
    {
        if (this.scope == null || this.scope.type != ScopeType.IF)
            throw new WeelException(this.tokenizer
                    .error("'elseif' without 'if'"));
        if (this.scope.hasElse)
            throw new WeelException(this.tokenizer
                    .error("'elseif' after 'else'"));
        this.block.add(new InstrGoto(this.scope.addBreak()));
        this.block.add(new InstrLabel(this.scope.continueLabel));
        this.scope.continueLabel = -1;

        this.tokenizer.next();
        this.parseExpression();
        this.checkReserved(ReservedWord.THEN);
        this.tokenizer.next();
        this.block.add(new InstrPopBool());
        this.block.add(new InstrIfEq(this.scope.addContinue()));
    }

    /**
     * Adds an 'ELSE' to an IF scope.
     */
    private void addElse()
    {
        if (this.scope == null || this.scope.type != ScopeType.IF)
            throw new WeelException(this.tokenizer.error("'else' without 'if'"));
        if (this.scope.hasElse)
            throw new WeelException(this.tokenizer.error("Duplicate 'else'"));

        this.tokenizer.next();
        this.scope.hasElse = true;
        this.block.add(new InstrGoto(this.scope.addBreak()));
        this.block.add(new InstrLabel(this.scope.continueLabel));
        this.scope.continueLabel = -1;
    }

    /**
     * Closes an IF scope.
     */
    private void closeIf()
    {
        if (this.scope.continueLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.continueLabel));
        }
        if (this.scope.breakLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.breakLabel));
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
    private void openFunction(final boolean anonymous, final boolean alternate,
            final boolean isPrivate)
    {
        if (anonymous)
            this.addScope(new Scope(this.scope, ScopeType.BORDER));
        this.addScope(new Scope(this.scope,
                this.tokenizer.reserved == ReservedWord.SUB ? ScopeType.SUB
                        : ScopeType.FUNC));
        this.block = this.scope.block = new WeelCode(this.weel);
        this.block.source = this.tokenizer.error("");
        this.block.isAnonymousFunction = anonymous;

        final WeelFunction func = this.block.function = new WeelFunction();
        func.returnsValue = this.tokenizer.token == Token.RESERVED
                && this.tokenizer.reserved == ReservedWord.FUNC;

        this.tokenizer.next();

        if (!anonymous)
        {
            this.checkToken(Token.NAME);
            func.name = this.tokenizer.string;
            this.tokenizer.next();

            if (this.tokenizer.token == Token.DOT
                    || this.tokenizer.token == Token.COLON)
            {
                this.scope.isOop = this.tokenizer.token == Token.COLON;
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
            if (this.tokenizer.token == Token.COLON)
            {
                this.scope.isOop = true;
                this.tokenizer.next();
            }
            func.name = "ANON";
        }

        int paramc = 0;

        if (this.scope.isOop)
        {
            this.scope.addLocal("THIS");
            paramc++;
        }

        if ((alternate && this.tokenizer.token == Token.BRACE_OPEN)
                || !alternate)
        {
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
        }
        func.arguments = paramc;

        if (this.findFunction(func.name, func.arguments) != null)
        {
            throw new WeelException(this.tokenizer.error("Duplicate function: "
                    + func));
        }

        if (!this.runtimeCompile)
        {
            final int fi = this.weel.addFunction(func.name + "#"
                    + func.arguments, func, !(anonymous || isPrivate));
            if (isPrivate)
            {
                this.mapFunctions.put(func.name, fi);
                this.mapFunctionsExact
                        .put(func.name + "#" + func.arguments, fi);
            }
        }
        this.block.isAlternateSyntax = alternate;
    }

    /**
     * Closes a function.
     */
    private void closeFunction()
    {
        final WeelFunction func = this.lastFunction = this.block.function;
        final boolean anonymous = this.block.isAnonymousFunction;
        final Variable oopVar = this.scope.oopVariable;
        final String oopIndex = this.scope.oopIndex;

        if (this.block.isAlternateSyntax && this.block.hasReturn)
        {
            func.returnsValue = true;
        }

        if (this.scope.breakLabel != -1)
        {
            this.block.add(new InstrLabel(this.scope.breakLabel));
        }

        if (!this.block.cvarIndex.isEmpty())
        {
            func.environment = new Value[this.block.cvarIndex.size()];
            func.envLocals = new int[this.block.cvarIndex.size()];
            for (int i = 0; i < func.environment.length; i++)
            {
                func.envLocals[i] = this.block.cvarIndex.get(i);
            }
        }

        this.block.closeBlock(this.weel.debugMode, this.weel.dumpCode);

        this.blockToBytecode(this.block);

        this.removeScope();
        if (anonymous)
        {
            this.removeScope();
            if (!this.runtimeCompile)
            {
                if (func.envLocals != null)
                    this.block.add(new InstrCreateClosure(func.index));
                else
                    this.block.add(new InstrLoadFunc(func.index));
            }
        }
        this.tokenizer.next();

        if (oopVar != null)
        {
            this.writeGetVariable(oopVar);
            this.block.add(new InstrLoad(oopIndex));
            this.block.add(new InstrKey());
            this.block.add(new InstrLoadFunc(func.index));
            this.block.add(new InstrSetMap());
        }
    }

    /**
     * Adds 'exit' to a sub.
     */
    private void addExit()
    {
        final Scope s = this.scope.findFunctionScope();
        if (s == null || s.type == ScopeType.FUNC || this.block.hasReturn)
        {
            throw new WeelException(this.tokenizer
                    .error("'exit' without 'sub'"));
        }
        this.block.hasExit = true;
        this.writeExitPops();
        this.block.add(new InstrGoto(s.addBreak()));
        this.tokenizer.next();
    }

    /**
     * Adds 'return' to a sub.
     */
    private void addReturn()
    {
        final Scope s = this.scope.findFunctionScope();
        if (s == null || s.type == ScopeType.SUB || this.block.hasExit)
        {
            throw new WeelException(this.tokenizer
                    .error("'return' without 'func'"));
        }
        this.block.hasReturn = true;
        this.writeExitPops();
        this.tokenizer.next();
        this.parseExpression();
        this.block.add(new InstrGoto(s.addBreak()));
    }

    /**
     * Parses the 'local' keyword
     */
    private void parseLocal()
    {
        this.tokenizer.next();

        this.checkToken(Token.NAME);
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
                this.block.add(new InstrVarStore(VarInstrType.LOCAL, index));
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
        final Scope outer = this.scope.getBorderScope();
        if (outer == null)
        {
            throw new WeelException(this.tokenizer
                    .error("'outer' without anonymous function"));
        }
        this.tokenizer.next();

        this.checkToken(Token.NAME);
        while (this.tokenizer.token != Token.EOF)
        {
            this.checkToken(Token.NAME);
            final String name = this.tokenizer.string;
            int lidx = outer.findLocal(name);
            if (this.scope.findCvar(name) != -1 || lidx != -1)
            {
                throw new WeelException(this.tokenizer.error(
                        "Duplicate explicit cvar '%s'", name));
            }
            lidx = outer.addLocal(name);
            Variable var = new Variable();
            var.type = Type.NONE;
            var.name = name;
            this.scope.maybeCreateCvar(var);

            this.tokenizer.next();
            this.checkToken(Token.ASSIGN);
            this.tokenizer.next();

            final Scope old = this.scope;
            // We have to switch scopes to compile the
            // initialize expression into the border scope.
            this.scope = outer;
            this.block = outer.block;

            this.parseExpression();
            this.block.add(new InstrVarStore(VarInstrType.LOCAL, lidx));

            this.scope = old;
            this.block = old.block;

            if (this.tokenizer.token == Token.COMMA)
            {
                this.tokenizer.next();
                continue;
            }
            break;
        }
    }

    /**
     * Parses the 'global' keyword
     */
    private void parseGlobal()
    {
        this.tokenizer.next();

        if (this.runtimeCompile)
        {
            throw new WeelException(this.tokenizer
                    .error("Can't use 'global' in runtime compilation."));
        }

        this.checkToken(Token.NAME);
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
                this.block.add(new InstrVarStore(VarInstrType.GLOBAL, index));
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
     * Parses the 'private' keyword
     */
    private void parsePrivate()
    {
        this.checkToken(Token.NAME);
        while (this.tokenizer.token != Token.EOF)
        {
            this.checkToken(Token.NAME);
            final String name = this.tokenizer.string;
            if (this.scope.findPrivate(name) != -1)
            {
                throw new WeelException(this.tokenizer.error(
                        "Duplicate private variable '%s'", name));
            }
            final int index = this.scope.addPrivate(name);
            this.tokenizer.next();
            if (this.tokenizer.token == Token.ASSIGN)
            {
                this.tokenizer.next();
                this.parseExpression();
                this.block.add(new InstrVarStore(VarInstrType.PRIVATE, index));
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
        throw new WeelException(this.tokenizer.error("Syntax error ("
                + this.tokenizer.token.toString().toLowerCase() + ")"));
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
        switch (var.type)
        {
        case LOCAL:
            this.block.add(new InstrVarLoad(VarInstrType.LOCAL, var.index));
            break;
        case GLOBAL:
            this.block.add(new InstrVarLoad(VarInstrType.GLOBAL, var.index));
            break;
        case PRIVATE:
            this.block.add(new InstrVarLoad(VarInstrType.PRIVATE, var.index));
            break;
        case CVAR:
            this.block.add(new InstrVarLoad(VarInstrType.CVAR, var.index));
            break;
        default:
            if (var.function != null)
                this.block.add(new InstrLoadFunc(var.function.index));
            else
                this.syntaxError();
            break;
        }
    }

    /**
     * Makes sure we get a variable to read from.
     * 
     * @param var
     *            The Variable.
     */
    private void needGetVariable(final Variable var)
    {
        this.needGetVariable(var, false);
    }

    /**
     * Makes sure we get a variable to read from.
     * 
     * @param var
     *            The Variable.
     * @param maybeFunction
     *            Flag indicating that the may be a function. (Only affects
     *            error generation.)
     */
    private void needGetVariable(final Variable var, final boolean maybeFunction)
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
                    maybeFunction ? "Unknown function '%s'"
                            : "Unknown variable '%s'", var.name));
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
        switch (var.type)
        {
        case LOCAL:
            this.block.add(new InstrVarStore(VarInstrType.LOCAL, var.index));
            break;
        case GLOBAL:
            this.block.add(new InstrVarStore(VarInstrType.GLOBAL, var.index));
            break;
        case PRIVATE:
            this.block.add(new InstrVarStore(VarInstrType.PRIVATE, var.index));
            break;
        case CVAR:
            this.block.add(new InstrVarStore(VarInstrType.CVAR, var.index));
            break;
        default:
            this.syntaxError();
        }
    }

    /**
     * Calculates the number of pops needed to be placed before an 'exit' or a
     * 'return'. Writes a 'pop(n)' instruction if number of pops > 0.
     */
    private void writeExitPops()
    {
        int pops = 0;
        Scope s = this.scope;
        while (s != null
                && (s.type != ScopeType.SUB && s.type != ScopeType.FUNC))
        {
            switch (s.type)
            {
            case FOR:
                pops += 2;
                break;
            case FOREACH:
            case SWITCH:
                pops++;
                break;
            default:
                break;
            }
            s = s.parent;
        }
        if (pops > 0)
        {
            this.block.add(new InstrPop(pops));
        }
    }

    /**
     * Parses a Weel assert.
     */
    private void parseAssert()
    {
        this.block.add(new InstrBegAssert());
        this.parseExpression();
        String message = null;
        if (this.tokenizer.token == Token.COMMA)
        {
            this.tokenizer.next();
            this.checkToken(Token.STRING);
            message = this.tokenizer.string;
            this.tokenizer.next();
        }
        this.checkToken(Token.BRACE_CLOSE);
        this.tokenizer.next();
        this.block.add(new InstrAssert((message != null) ? this.tokenizer
                .error("Assert failed (%s)", message) : this.tokenizer
                .error("Assert failed")));
        this.block.add(new InstrEndAssert());
    }

    private void blockToBytecode(final WeelCode b)
    {
        JvmMethodWriter mw;
        if (b.function == null)
        {
            if (b.instrs.size() == 0)
            {
                return;
            }
            mw = this.classWriter.createMethod("STATIC",
                    "(Lcom/github/rjeschke/weel/WeelRuntime;)V");
        }
        else
        {
            b.function.clazz = this.classWriter.className;
            b.function.javaName = b.isAnonymousFunction ? "$anon$"
                    + this.anonCounter++ : b.function.name + "$"
                    + b.function.arguments;
            mw = this.classWriter.createMethod(b.function.javaName,
                    "(Lcom/github/rjeschke/weel/WeelRuntime;)V");
        }

        for (int i = 0; i < b.instrs.size(); i++)
        {
            b.instrs.get(i).write(mw);
        }

        mw.addOp(JvmOp.RETURN);

        mw.resolveLabels();
    }

    private WeelFunction findFunction(final String name, final int paramc)
    {
        final Integer idx = this.mapFunctionsExact.get(name.toLowerCase() + "#"
                + paramc);
        if (idx != null)
        {
            return this.weel.functions.get(idx);
        }
        return this.weel.findFunction(name, paramc);
    }

    WeelFunction findFunction(final String name)
    {
        final Integer idx = this.mapFunctions.get(name.toLowerCase());
        if (idx != null)
        {
            return this.weel.functions.get(idx);
        }
        return this.weel.findFunction(name);
    }
}
