/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Weel compiler.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class Compiler
{
    /*
     * Setup:
     * 
     * - We've got Scopes and CodeBlocks Each file has a static scope with a
     * code block Every function/sub opens a new scope and code block
     * 
     * - Local variables get registered in the code block, names are stored in
     * scope.
     * 
     * - Closure variables are registered and stored in scope.
     */
    /** The current tokenizer. */
    Tokenizer tokenizer;
    /** The Weel. */
    Weel weel;
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
    private JvmClassWriter classWriter;

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
    }

    /**
     * Initializes this compiler.
     */
    private void initialize()
    {
        this.classWriter = new JvmClassWriter(
                "com.github.rjeschke.weel.scripts.Script" + classCounter++);

        final Scope s = new Scope(ScopeType.STATIC);
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
    }

    /**
     * Compiles a token.
     */
    private void compileToken()
    {
        switch (this.tokenizer.token)
        {
        case NAME:
            break;
        case RESERVED:
            break;
        }
    }

    private void findVarOrFunc(final String name, final int[] var)
    {

    }
}
