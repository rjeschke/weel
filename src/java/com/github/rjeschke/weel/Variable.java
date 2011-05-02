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
    Type type;
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
     * Variable type enumeration.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    enum Type
    {
        GLOBAL,
        LOCAL,
        CVAR,
        NONE
    }
}
