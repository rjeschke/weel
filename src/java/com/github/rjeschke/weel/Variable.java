/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
class Variable
{
    public String name;
    public int index;
    public Type type;

    /**
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    public enum Type
    {
        FUNCTION,
        GLOBAL,
        LOCAL,
        CVAR,
        NONE
    }
}
