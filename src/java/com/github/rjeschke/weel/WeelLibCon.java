/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel console library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelLibCon
{
    private WeelLibCon()
    {
        // empty
    }
    
    /**
     * <code>println(v)</code>
     * <p>
     * Prints the given value with a trailing new line.
     * </p>
     * 
     * @param runtime
     *            The Weel Runtime.
     * @see java.io.PrintStream#println(String)
     */
    @WeelRawMethod(name = "println", args = 1)
    public final static void println1(final WeelRuntime runtime)
    {
        System.out.println(runtime.pop());
    }

    /**
     * <code>print(v)</code>
     * <p>
     * Prints the given value.
     * </p>
     * 
     * @param runtime
     *            The Weel Runtime.
     * @see java.io.PrintStream#print(String)
     */
    @WeelRawMethod(args = 1)
    public final static void print(final WeelRuntime runtime)
    {
        System.out.print(runtime.pop());
    }

    /**
     * <code>printb(v)</code>
     * <p>
     * Prints the given raw byte.
     * </p>
     * 
     * @param runtime
     *            The Weel Runtime.
     */
    @WeelRawMethod(args = 1)
    public final static void printb(final WeelRuntime runtime)
    {
        System.out.write((int)runtime.popNumber());
    }

    /**
     * <code>println()</code>
     * <p>
     * Prints a new line.
     * </p>
     * 
     * @param runtime
     *            The Weel Runtime.
     * @see java.io.PrintStream#println()
     */
    @WeelRawMethod()
    public final static void println(final WeelRuntime runtime)
    {
        System.out.println();
    }
}
