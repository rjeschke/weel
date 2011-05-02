/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * Helper class for variable/function compilation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
class Variable
{
    /** The name. */
    public String name;
    /** The index. */
    public int index;
    /** The type. */
    public Type type;
    /** The function. */
    public WeelFunction function;

    /**
     * Constructor.
     */
    public Variable()
    {
        //
    }
    
    /**
     * Variable type enumeration.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    public enum Type
    {
        GLOBAL,
        LOCAL,
        CVAR,
        NONE
    }
}
