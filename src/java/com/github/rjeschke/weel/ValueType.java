/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

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
    OBJECT
}
