/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main building block for the Weel compiler.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class CodeBlock
{
    /** List of used locals. */
    ArrayList<Boolean> locals = new ArrayList<Boolean>();
    /** Closure variables get registered at block level. */ 
    ArrayList<Integer> cvarIndex = new ArrayList<Integer>();
    /** Closure variable name to index mapping. */
    HashMap<String, Integer> cvars = new HashMap<String, Integer>();
    
    /** The method writer. */
    final JvmMethodWriter methodWriter;
    
    public CodeBlock(final JvmMethodWriter methodWriter)
    {
        this.methodWriter = methodWriter;
    }

    int registerLocal()
    {
        for(int i = 0; i < this.locals.size(); i++)
        {
            if(!this.locals.get(i).booleanValue())
            {
                this.locals.set(i, true);
                return i;
            }
        }
        final int ret = this.locals.size();
        this.locals.add(true);
        return ret;
    }
    
    void unregisterLocal(final int index)
    {
        this.locals.set(index, false);
    }
}
