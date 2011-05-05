/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel math library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelLibMath
{
    private WeelLibMath()
    {
        // empty
    }

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
        final double b = runtime.popNumber();
        final double a = runtime.popNumber();
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
        runtime.load(Math.abs(runtime.popNumber()));
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
        runtime.load(Math.sqrt(runtime.popNumber()));
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
        runtime.load(Math.sin(runtime.popNumber()));
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
        runtime.load(Math.cos(runtime.popNumber()));
    }

    /**
     * <code>tan(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#tan(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void tan(final Runtime runtime)
    {
        runtime.load(Math.tan(runtime.popNumber()));
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
        runtime.load(Math.exp(runtime.popNumber()));
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
        runtime.load(Math.log(runtime.popNumber()));
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
        runtime.load(Math.log10(runtime.popNumber()));
    }

    /**
     * <code>floor(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#floor(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void floor(final Runtime runtime)
    {
        runtime.load(Math.floor(runtime.popNumber()));
    }

    /**
     * <code>ceil(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#ceil(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void ceil(final Runtime runtime)
    {
        runtime.load(Math.ceil(runtime.popNumber()));
    }

    /**
     * <code>round(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#round(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void round(final Runtime runtime)
    {
        runtime.load(Math.round(runtime.popNumber()));
    }
}
