/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.HashMap;

/**
 * Weel Value type enumeration.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public enum ValueType
{
    /** NULL. */
    NULL,
    /** A double. */
    NUMBER,
    /** A String. */
    STRING,
    /** A ValueMap. */
    MAP,
    /** A function. */
    FUNCTION,
    /** An object. */
    OBJECT;

    /** Name to enum mapping. */
    private final static HashMap<String, ValueType> map = new HashMap<String, ValueType>();

    static
    {
        for (final ValueType v : ValueType.values())
        {
            map.put(v.toString(), v);
        }
    }

    /**
     * Returns an enum from a String.
     * 
     * @param value
     *            The name.
     * @return The enum or <code>null</code>.
     */
    public final static ValueType fromString(final String value)
    {
        return map.get(value);
    }
}
