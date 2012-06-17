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
    public final static void pow(final WeelRuntime runtime)
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
    public final static void abs(final WeelRuntime runtime)
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
    public final static void sqrt(final WeelRuntime runtime)
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
    public final static void cbrt(final WeelRuntime runtime)
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
    public final static void sin(final WeelRuntime runtime)
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
    public final static void cos(final WeelRuntime runtime)
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
    public final static void tan(final WeelRuntime runtime)
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
    public final static void sinh(final WeelRuntime runtime)
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
    public final static void cosh(final WeelRuntime runtime)
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
    public final static void tanh(final WeelRuntime runtime)
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
    public final static void asin(final WeelRuntime runtime)
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
    public final static void acos(final WeelRuntime runtime)
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
    public final static void atan(final WeelRuntime runtime)
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
    public final static void atan2(final WeelRuntime runtime)
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
    public final static void exp(final WeelRuntime runtime)
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
    public final static void log(final WeelRuntime runtime)
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
    public final static void exp10(final WeelRuntime runtime)
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
    public final static void log10(final WeelRuntime runtime)
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
    public final static void floor(final WeelRuntime runtime)
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
    public final static void ceil(final WeelRuntime runtime)
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
    public final static void round(final WeelRuntime runtime)
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
    public final static void clamp(final WeelRuntime runtime)
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
    public final static void lerp(final WeelRuntime runtime)
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
    public final static void min(final WeelRuntime runtime)
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
    public final static void max(final WeelRuntime runtime)
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
    public final static void sign(final WeelRuntime runtime)
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
    public final static void toDeg(final WeelRuntime runtime)
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
    public final static void toRad(final WeelRuntime runtime)
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
    public final static void fract(final WeelRuntime runtime)
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
    public final static void rand(final WeelRuntime runtime)
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
    public final static void rand1(final WeelRuntime runtime)
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
    public final static void rand2(final WeelRuntime runtime)
    {
        final double max = runtime.popNumber();
        final double min = runtime.popNumber();
        runtime.load(Math.random() * (max - min) + min);
    }

    /**
     * <code>imul(a, b)</code>
     * <p>
     * Integer multiply.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void imul(final WeelRuntime runtime)
    {
        final int b = (int) runtime.popNumber();
        final int a = (int) runtime.popNumber();
        runtime.load(a * b);
    }

    /**
     * <code>idiv(a, b)</code>
     * <p>
     * Integer division.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void idiv(final WeelRuntime runtime)
    {
        final int b = (int) runtime.popNumber();
        final int a = (int) runtime.popNumber();
        runtime.load(a / b);
    }

    /**
     * <code>iadd(a, b)</code>
     * <p>
     * Integer addition.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void iadd(final WeelRuntime runtime)
    {
        final int b = (int) runtime.popNumber();
        final int a = (int) runtime.popNumber();
        runtime.load(a + b);
    }

    /**
     * <code>isub(a, b)</code>
     * <p>
     * Integer subtraction.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void isub(final WeelRuntime runtime)
    {
        final int b = (int) runtime.popNumber();
        final int a = (int) runtime.popNumber();
        runtime.load(a - b);
    }

    /**
     * <code>imod(a, b)</code>
     * <p>
     * Integer modulo.
     * </p>
     * 
     * @param runtime
     *            The Weel runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void imod(final WeelRuntime runtime)
    {
        final int b = (int) runtime.popNumber();
        final int a = (int) runtime.popNumber();
        runtime.load(a % b);
    }
}
