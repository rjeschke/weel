/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel string library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelLibString
{
    private WeelLibString()
    {
        // empty
    }

    /**
     * <code>strUpper(s)</code>
     * <p>
     * Returns the string converted to upper case.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#toUpperCase()
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void strUpper(final Runtime runtime)
    {
        runtime.load(runtime.popString().toUpperCase());
    }

    /**
     * <code>strLower(s)</code>
     * <p>
     * Returns the string converted to lower case.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#toLowerCase()
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void strLower(final Runtime runtime)
    {
        runtime.load(runtime.popString().toLowerCase());
    }

    /**
     * <code>strIndex(str, val)</code>
     * <p>
     * Returns the index of the first occurence of 'val' in 'str', -1 if none
     * was found.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#indexOf(String)
     */
    @WeelRawMethod(name = "strindex", args = 2, returnsValue = true)
    public final static void strIndex2(final Runtime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        runtime.load(a.indexOf(b));
    }

    /**
     * <code>strIndex(str, val, i)</code>
     * <p>
     * Returns the index of the first occurence of 'val' in 'str' starting from
     * 'i', -1 if none was found.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#indexOf(String, int)
     */
    @WeelRawMethod(name = "strindex", args = 3, returnsValue = true)
    public final static void strIndex3(final Runtime runtime)
    {
        final int i = (int) runtime.popNumber();
        final String b = runtime.popString();
        final String a = runtime.popString();
        runtime.load(a.indexOf(b, i));
    }

    /**
     * <code>strSplit(str, val)</code>
     * <p>
     * Splits this string around matches of the given regular expression.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     * @see java.lang.String#split(String)
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void strSplit(final Runtime runtime)
    {
        final String b = runtime.popString();
        final String a = runtime.popString();
        final String[] t = a.split(b);
        final ValueMap m  = new ValueMap();
        for(int i = 0; i < t.length; i++)
        {
            m.append(new Value(t[i]));
        }
        runtime.load(m);
    }
}
