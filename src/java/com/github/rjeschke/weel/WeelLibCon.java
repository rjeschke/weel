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

import java.util.Locale;

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

    /**
     * <code>printf(v)</code>
     * <p>
     * Prints the given value.
     * </p>
     * 
     * @param runtime
     *            The Weel Runtime.
     */
    @WeelRawMethod(name = "printf", args = 1)
    public final static void printf1(final WeelRuntime runtime)
    {
        System.out.print(runtime.pop());
    }
    
    /**
     * <code>printf(fmt, list)</code>
     * <p>
     * Prints the given formatted string.
     * </p>
     * 
     * @param runtime
     *            The Weel Runtime.
     */
    @WeelRawMethod(name = "printf", args = 2)
    public final static void printf2(final WeelRuntime runtime)
    {
        final ValueMap l = runtime.popMap();
        final String fmt = runtime.popString();
        System.out.print(WeelLibString.format(Locale.getDefault(), fmt, l));
    }
    
    /**
     * <code>printf(locale, fmt, list)</code>
     * <p>
     * Prints the given formatted string using the given language.
     * </p>
     * 
     * @param runtime
     *            The Weel Runtime.
     */
    @WeelRawMethod(name = "printf", args = 3)
    public final static void printf3(final WeelRuntime runtime)
    {
        final ValueMap l = runtime.popMap();
        final String fmt = runtime.popString();
        final String locale = runtime.popString();
        System.out.print(WeelLibString.format(new Locale(locale), fmt, l));
    }
}
