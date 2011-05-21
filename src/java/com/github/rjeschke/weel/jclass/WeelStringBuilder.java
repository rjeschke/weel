/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.jclass;

import com.github.rjeschke.weel.Value;
import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel StringBuilder implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "java.StringBuilder", usesOop = true)
public final class WeelStringBuilder
{
    private WeelStringBuilder()
    {
        //
    }
    
    /**
     * Constructor.
     * 
     * @param thiz This.
     * @see java.lang.StringBuilder#StringBuilder()
     */
    @WeelMethod
    public final static void ctor(final ValueMap thiz)
    {
        WeelOop.setInstance(thiz, new StringBuilder());
    }
    
    /**
     * Constructor.
     * 
     * @param thiz This.
     * @param size The size.
     * @see java.lang.StringBuilder#StringBuilder(int)
     */
    @WeelMethod
    public final static void ctor(final ValueMap thiz, final int size)
    {
        WeelOop.setInstance(thiz, new StringBuilder(size));
    }
 
    /**
     * Appends 'value' to this StringBuilder.
     * 
     * @param thiz This.
     * @param value The value.
     * @see java.lang.StringBuilder#append(String)
     */
    @WeelMethod
    public final static void append(final ValueMap thiz, final Value value)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        sb.append(value.toString());
    }

    /**
     * Appends a char (given as a number) to this StringBuilder.
     * 
     * @param thiz This.
     * @param value The char.
     * @see java.lang.StringBuilder#append(String)
     */
    @WeelMethod
    public final static void appendChar(final ValueMap thiz, final Value value)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        sb.append((char)value.getNumber());
    }
    
    /**
     * Sets the length of this StringBuilder to 0.
     * 
     * @param thiz This.
     * @see java.lang.StringBuilder#setLength(int)
     */
    @WeelMethod
    public final static void clear(final ValueMap thiz)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        sb.setLength(0);
    }
    
    /**
     * Sets the length of this StringBuilder.
     * 
     * @param thiz This.
     * @param length The length.
     * @see java.lang.StringBuilder#setLength(int)
     */
    @WeelMethod
    public final static void clear(final ValueMap thiz, final int length)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        sb.setLength(length);
    }
 
    /**
     * Gets the length of this StringBuilder.
     * 
     * @param thiz This.
     * @return The length.
     * @see java.lang.StringBuilder#setLength(int)
     */
    @WeelMethod
    public final static double length(final ValueMap thiz)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        return sb.length();
    }
    
    /**
     * Returns a string representation of this StringBuilder.
     * 
     * @param thiz This.
     * @return The string representation.
     * @see java.lang.StringBuilder#toString()
     */
    @WeelMethod(name = "toString")
    public final static String sbToString(final ValueMap thiz)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        return sb.toString();
    }
}
