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
    String name;
    /** The index. */
    int index;
    /** The type. */
    Type type = Type.NONE;
    /** The function. */
    WeelFunction function;

    /**
     * Constructor.
     */
    Variable()
    {
        //
    }
    
    /**
     * Check if this Variable contains a function.
     * 
     * @return <code>true</code> if so.
     */
    public boolean isFunction()
    {
        return this.function != null;
    }
    
    /**
     * Variable type enumeration.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    enum Type
    {
        GLOBAL,
        PRIVATE,
        LOCAL,
        CVAR,
        NONE
    }
}
