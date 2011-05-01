/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public class Scope
{
    /** The scope type. */
    final ScopeType type;
    /** The parent. */
    Scope parent;
    /** The CodeBlock. */
    CodeBlock block;
    /** Local name to index mapping. */
    final HashMap<String, Integer> locals = new HashMap<String, Integer>();
    /** List of continue jumps. */
    final LinkedList<Integer> continues = new LinkedList<Integer>();
    /** List of break jumps. */
    final LinkedList<Integer> breaks = new LinkedList<Integer>();
    /** Various flags. */
    boolean hasElse, hasDefault, hasCase, hasReturn;
    
    public Scope(final ScopeType type)
    {
        this.type = type;
    }

    Scope findOuterCodeScope()
    {
        Scope current = this;
        while(current != null)
        {
            if(current.block != this.block)
                return current;
            current = current.parent;
        }
        return null;
    }

    Scope getBorderScope()
    {
        Scope current = this;
        while(current != null)
        {
            if(current.type == ScopeType.BORDER)
                return current;
            current = current.parent;
        }
        return null;
    }
    
    void addBreak(final int pc)
    {
        this.breaks.add(pc);
    }
    
    int removeBreak()
    {
        return this.breaks.removeLast();
    }
    
    void addContinue(final int pc)
    {
        this.continues.add(pc);
    }

    int removeContinue()
    {
        return this.continues.removeLast();
    }

    int findLocal(String name)
    {
        if(this.locals.containsKey(name))
        {
            return this.locals.get(name);
        }
        if(this.parent != null)
        {
            return this.parent.findLocal(name);
        }
        return -1;
    }
    
    int addLocal(String name)
    {
        int ret = this.block.registerLocal();
        this.locals.put(name, ret);
        return ret;
    }
    
    void unregisterLocals()
    {
        for(final int idx : this.locals.values())
        {
            this.block.unregisterLocal(idx);
        }
    }
}
