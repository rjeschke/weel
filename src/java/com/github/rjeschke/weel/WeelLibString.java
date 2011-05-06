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
}
