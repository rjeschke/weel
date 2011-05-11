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
     * <code>cbrt(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#cbrt(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void cbrt(final Runtime runtime)
    {
        runtime.load(Math.cbrt(runtime.popNumber()));
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
     * <code>sinh(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#sinh(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void sinh(final Runtime runtime)
    {
        runtime.load(Math.sinh(runtime.popNumber()));
    }

    /**
     * <code>cosh(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#cosh(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void cosh(final Runtime runtime)
    {
        runtime.load(Math.cosh(runtime.popNumber()));
    }

    /**
     * <code>tanh(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#tanh(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void tanh(final Runtime runtime)
    {
        runtime.load(Math.tanh(runtime.popNumber()));
    }

    /**
     * <code>asin(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#asin(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void asin(final Runtime runtime)
    {
        runtime.load(Math.asin(runtime.popNumber()));
    }

    /**
     * <code>acos(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#acos(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void acos(final Runtime runtime)
    {
        runtime.load(Math.acos(runtime.popNumber()));
    }

    /**
     * <code>atan(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#atan(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void atan(final Runtime runtime)
    {
        runtime.load(Math.atan(runtime.popNumber()));
    }

    /**
     * <code>atan2(y, x)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#atan2(double, double)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void atan2(final Runtime runtime)
    {
        final double x = runtime.popNumber();
        final double y = runtime.popNumber();
        runtime.load(Math.atan2(y, x));
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
     * <code>exp10(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void exp10(final Runtime runtime)
    {
        runtime.load(Math.pow(10.0, runtime.popNumber()));
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

    /**
     * <code>clamp(x, min, max)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void clamp(final Runtime runtime)
    {
        final double max = runtime.popNumber();
        final double min = runtime.popNumber();
        final double x = runtime.popNumber();
        runtime.load(x < min ? min : x > max ? max : x);
    }

    /**
     * <code>lerp(a, b, f)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 3, returnsValue = true)
    public final static void lerp(final Runtime runtime)
    {
        final double f = runtime.popNumber();
        final double b = runtime.popNumber();
        final double a = runtime.popNumber();
        runtime.load(a + (b - a) * f);
    }

    /**
     * <code>min(a, b)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#min(double, double)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void min(final Runtime runtime)
    {
        final double b = runtime.popNumber();
        final double a = runtime.popNumber();
        runtime.load(Math.min(a, b));
    }

    /**
     * <code>max(a, b)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#max(double, double)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void max(final Runtime runtime)
    {
        final double b = runtime.popNumber();
        final double a = runtime.popNumber();
        runtime.load(Math.max(a, b));
    }

    /**
     * <code>sign(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#signum(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void sign(final Runtime runtime)
    {
        runtime.load(Math.signum(runtime.popNumber()));
    }

    /**
     * <code>toDeg(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#toDegrees(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void toDeg(final Runtime runtime)
    {
        runtime.load(Math.toDegrees(runtime.popNumber()));
    }

    /**
     * <code>toRad(a)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#toRadians(double)
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void toRad(final Runtime runtime)
    {
        runtime.load(Math.toRadians(runtime.popNumber()));
    }

    /**
     * <code>fract(a)</code>
     * <p>
     * Returns <code>a - floor(a)</code>
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void fract(final Runtime runtime)
    {
        final double a = runtime.popNumber();
        runtime.load(a - Math.floor(a));
    }

    /**
     * <code>rand()</code>
     * 
     * @param runtime
     *            The Weel runtime.
     * @see java.lang.Math#random()
     */
    @WeelRawMethod(returnsValue = true)
    public final static void rand(final Runtime runtime)
    {
        runtime.load(Math.random());
    }

    /**
     * <code>rand(max)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(name = "rand", args = 1, returnsValue = true)
    public final static void rand1(final Runtime runtime)
    {
        final double max = runtime.popNumber();
        runtime.load(Math.random() * max);
    }

    /**
     * <code>rand(min, max)</code>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(name = "rand", args = 2, returnsValue = true)
    public final static void rand2(final Runtime runtime)
    {
        final double max = runtime.popNumber();
        final double min = runtime.popNumber();
        runtime.load(Math.random() * (max - min) + min);
    }
}
