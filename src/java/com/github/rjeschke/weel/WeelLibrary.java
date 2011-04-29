/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import com.github.rjeschke.weel.annotations.WeelMethod;
import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel standard library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelLibrary
{
    /*
     * *** Console functions ***
     */

    @WeelMethod
    public final static double test(double a, double b)
    {
        return a + b;
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
    public final static void println1(final Runtime runtime)
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
    public final static void print(final Runtime runtime)
    {
        System.out.print(runtime.pop());
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
    public final static void println(final Runtime runtime)
    {
        System.out.println();
    }

    /*
     * *** Math functions ***
     */

    /**
     * <code>pow(a, b)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#pow(double, double)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void pow(final Runtime runtime)
    {
        final double b = runtime.pop().getNumber();
        final double a = runtime.pop().getNumber();
        runtime.load(Math.pow(a, b));
    }

    /**
     * <code>abs(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#abs(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void abs(final Runtime runtime)
    {
        runtime.load(Math.abs(runtime.pop().getNumber()));
    }

    /**
     * <code>sqrt(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#sqrt(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void sqrt(final Runtime runtime)
    {
        runtime.load(Math.sqrt(runtime.pop().getNumber()));
    }

    /**
     * <code>sin(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#sin(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void sin(final Runtime runtime)
    {
        runtime.load(Math.sin(runtime.pop().getNumber()));
    }

    /**
     * <code>cos(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#cos(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void cos(final Runtime runtime)
    {
        runtime.load(Math.cos(runtime.pop().getNumber()));
    }

    /**
     * <code>exp(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#exp(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void exp(final Runtime runtime)
    {
        runtime.load(Math.exp(runtime.pop().getNumber()));
    }

    /**
     * <code>log(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#log(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void log(final Runtime runtime)
    {
        runtime.load(Math.log(runtime.pop().getNumber()));
    }

    /**
     * <code>log10(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#log10(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void log10(final Runtime runtime)
    {
        runtime.load(Math.log10(runtime.pop().getNumber()));
    }
}
