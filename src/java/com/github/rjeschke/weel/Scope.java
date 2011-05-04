/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.HashMap;
import java.util.LinkedList;

import com.github.rjeschke.weel.Variable.Type;

/**
 * A Weel scope.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
class Scope
{
    /** The weel. */
    final Weel weel;
    /** The scope type. */
    final ScopeType type;
    /** The parent. */
    final Scope parent;
    /** The CodeBlock. */
    CodeBlock block;
    /** Local name to index mapping. */
    final HashMap<String, Integer> locals = new HashMap<String, Integer>();
    /** List of continue jumps. */
    final LinkedList<Integer> continues = new LinkedList<Integer>();
    /** List of break jumps. */
    final LinkedList<Integer> breaks = new LinkedList<Integer>();
    /** Various flags. */
    boolean hasElse, hasDefault, hasCase;
    /** Starting pc for loops. */
    int startPc;
    /** Local variable index for FOR loops. */
    int localIndex;
    /** OOP variable for functions. */
    Variable oopVariable;
    /** OOP array index. */
    String oopIndex;
    /** Is this a 'real-oop' function. */
    boolean isOop;
    
    /**
     * Constructor.
     * 
     * @param weel
     *            The Weel.
     * @param type
     *            The type.
     */
    public Scope(final Weel weel, final ScopeType type)
    {
        this.type = type;
        this.weel = weel;
        this.parent = null;
    }

    /**
     * COnstructor.
     * 
     * @param parent
     *            The scope parent.
     * @param type
     *            The type.
     */
    public Scope(final Scope parent, final ScopeType type)
    {
        this.type = type;
        this.parent = parent;
        this.block = parent.block;
        this.weel = parent.weel;
    }

    /**
     * Finds a surrounding code block.
     * 
     * @return The code block scope or <code>null</code>
     */
    Scope findOuterCodeScope()
    {
        Scope current = this;
        while (current != null)
        {
            if (current.block != this.block)
                return current;
            current = current.parent;
        }
        return null;
    }

    /**
     * Finds a surrounding function scope.
     * 
     * @return The function scope or <code>null</code>
     */
    Scope findFunctionScope()
    {
        Scope current = this;
        while (current != null)
        {
            if (current.block.function != null)
                return current;
            current = current.parent;
        }
        return null;
    }

    /**
     * Finds a border scope.
     * 
     * @return The border scope or <code>null</code>
     */
    Scope getBorderScope()
    {
        Scope current = this;
        while (current != null)
        {
            if (current.type == ScopeType.BORDER)
                return current;
            current = current.parent;
        }
        return null;
    }

    /**
     * Gets a scope which accepts a 'break'.
     * 
     * @return The scope or <code>null</code>
     */
    Scope getBreakScope()
    {
        Scope current = this;
        while (current != null)
        {
            switch (current.type)
            {
            case FUNC:
            case SUB:
                return null;
            case FOR:
            case FOREACH:
            case SWITCH:
            case DO:
            case WHILE:
                return current;
            default:
                break;
            }
            current = current.parent;
        }
        return null;
    }

    /**
     * Gets a scope which accepts a 'continue'.
     * 
     * @return The scope or <code>null</code>
     */
    Scope getContinueScope()
    {
        Scope current = this;
        while (current != null)
        {
            switch (current.type)
            {
            case FUNC:
            case SUB:
                return null;
            case FOR:
            case FOREACH:
            case DO:
            case WHILE:
                return current;
            default:
                break;
            }
            current = current.parent;
        }
        return null;
    }

    /**
     * Adds a break.
     * 
     * @param pc
     *            Program counter.
     */
    void addBreak(final int pc)
    {
        this.breaks.add(pc);
    }

    /**
     * Removes a break.
     * 
     * @return The break's PC.
     */
    int removeBreak()
    {
        return this.breaks.removeLast();
    }

    /**
     * Adds a continue.
     * 
     * @param pc
     *            Program counter.
     */
    void addContinue(final int pc)
    {
        this.continues.add(pc);
    }

    /**
     * Removes a continue.
     * 
     * @return The continue's PC.
     */
    int removeContinue()
    {
        return this.continues.removeLast();
    }

    /**
     * Finds a local variable.
     * 
     * @param name
     *            The name.
     * @return The index or <code>-1</code>
     */
    int findLocal(final String name)
    {
        if (this.locals.containsKey(name))
        {
            return this.locals.get(name);
        }
        if (this.parent != null
                && (this.type != ScopeType.SUB && this.type != ScopeType.FUNC))
        {
            return this.parent.findLocal(name);
        }
        return -1;
    }

    /**
     * Finds a local variable, searches up to static scope.
     * 
     * @param name
     *            The name.
     * @return The index or <code>-1</code>
     */
    int findLocalFull(final String name)
    {
        if (this.locals.containsKey(name))
        {
            return this.locals.get(name);
        }
        if (this.parent != null)
        {
            return this.parent.findLocalFull(name);
        }
        return -1;
    }

    /**
     * Finds a closure variable.
     * 
     * @param name
     *            The name.
     * @return The index or <code>-1</code>
     */
    int findCvar(final String name)
    {
        if (this.block.cvars.containsKey(name))
        {
            return this.block.cvars.get(name);
        }
        if (this.parent != null
                && (this.type != ScopeType.SUB && this.type != ScopeType.FUNC))
        {
            return this.parent.findCvar(name);
        }
        return -1;
    }

    /**
     * Finds a closure variable, searches up to top nested anonymous function.
     * 
     * @param name
     *            The name.
     * @return The index or <code>-1</code>
     */
    int findCvarFull(final String name)
    {
        if (this.block.cvars.containsKey(name))
        {
            return this.block.cvars.get(name);
        }
        if (this.parent != null && this.parent.block.isAnonymousFunction)
        {
            return this.parent.findCvarFull(name);
        }
        return -1;
    }

    /**
     * Adds a local variable to this scope.
     * 
     * @param name
     *            The name.
     * @return The index.
     */
    int addLocal(String name)
    {
        int ret = this.block.registerLocal();
        this.locals.put(name, ret);
        return ret;
    }

    /**
     * Unregisters all locals assigned with this scope.
     */
    void unregisterLocals()
    {
        for (final int idx : this.locals.values())
        {
            this.block.unregisterLocal(idx);
        }
    }

    /**
     * Initialize Variable.
     * 
     * @param var
     *            The Variable (out).
     * @param name
     *            The name.
     * @return The supplied Variable.
     */
    Variable findVariable(final Variable var, final String name)
    {
        final Integer glob = this.weel.mapGlobals.get(name);
        var.name = name;
        var.type = Type.NONE;
        final int loc = this.findLocal(name);
        if (loc != -1)
        {
            var.index = loc;
            var.type = Type.LOCAL;
        }
        else if (this.block.isAnonymousFunction)
        {
            final int cv = this.findCvar(name);
            if (cv != -1)
            {
                var.index = cv;
                var.type = Type.CVAR;
            }
        }
        if (var.type == Type.NONE && glob != null)
        {
            var.index = glob;
            var.type = Type.GLOBAL;
        }
        var.function = this.weel.findFunction(name);
        return var;
    }

    /**
     * Finds a variable, performs a full search. Used in closures.
     * 
     * @param var
     *            The Variable (out).
     * @param name
     *            The name.
     * @return The supplied Variable.
     */
    private Variable findVariableFull(final Variable var, final String name)
    {
        final Integer glob = this.weel.mapGlobals.get(name);
        var.name = name;
        var.type = Type.NONE;
        if (this.block.isAnonymousFunction)
        {
            final int cv = this.findCvarFull(name);
            if (cv != -1)
            {
                var.index = cv;
                var.type = Type.CVAR;
            }
        }
        if(var.type == Type.NONE)
        {
            final int loc = this.findLocalFull(name);
            if (loc != -1)
            {
                var.index = loc;
                var.type = Type.LOCAL;
            }
        }
        if(var.type == Type.NONE && glob != null)
        {
            var.index = glob;
            var.type = Type.GLOBAL;
        }
        var.function = this.weel.findFunction(name);
        return var;
    }

    /**
     * Checks if we can create a CVAR and if so, creates it.
     * 
     * @param var
     *            The variable (out).
     */
    void maybeCreateCvar(final Variable var)
    {
        final Scope s = this.getBorderScope();
        if (s == null)
            return;
        final Variable v = s.findVariableFull(new Variable(), var.name);
        if (v.type == Type.LOCAL || v.type == Type.CVAR)
        {
            var.index = this.block.getCvar(v);
            if(var.index != -1)
            {
                this.block.cvars.put(var.name, var.index);
                var.type = Type.CVAR;
            }
            else
            {
                var.type = Type.NONE;
            }
            
        }
    }
}
