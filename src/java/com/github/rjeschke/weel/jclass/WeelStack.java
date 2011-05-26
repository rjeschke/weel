/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.jclass;

import java.util.Stack;

import com.github.rjeschke.weel.Value;
import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel simple stack implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "java.Stack", usesOop = true)
public final class WeelStack
{
    /**
     * Constructor.
     * 
     * @param thiz
     *            This.
     */
    @WeelMethod
    public final static void ctor(final ValueMap thiz)
    {
        WeelOop.setInstance(thiz, new Stack<Value>());
    }

    /**
     * Push a avlue onto the stack.
     * 
     * @param thiz
     *            This.
     * @param value
     *            The value.
     */
    @SuppressWarnings("unchecked")
    public final static void push(final ValueMap thiz, final Value value)
    {
        final Stack<Value> stack = (Stack<Value>) thiz.get("#INSTANCE#")
                .getObject();
        stack.push(value);
    }

    /**
     * Peeks a value.
     * 
     * @param thiz
     *            This.
     * @return The value.
     */
    @SuppressWarnings("unchecked")
    public final static Value peek(final ValueMap thiz)
    {
        final Stack<Value> stack = (Stack<Value>) thiz.get("#INSTANCE#")
                .getObject();
        return stack.peek();
    }

    /**
     * Pops a value from this stack.
     * 
     * @param thiz
     *            This.
     * @return The value.
     */
    @SuppressWarnings("unchecked")
    public final static Value pop(final ValueMap thiz)
    {
        final Stack<Value> stack = (Stack<Value>) thiz.get("#INSTANCE#")
                .getObject();
        return stack.pop();
    }

    /**
     * Gets the size of this stack.
     * 
     * @param thiz
     *            This.
     * @return The size.
     */
    @SuppressWarnings("unchecked")
    public final static int size(final ValueMap thiz)
    {
        final Stack<Value> stack = (Stack<Value>) thiz.get("#INSTANCE#")
                .getObject();
        return stack.size();
    }

    /**
     * Clears this stack.
     * 
     * @param thiz
     *            This.
     */
    @SuppressWarnings("unchecked")
    public final static void clear(final ValueMap thiz)
    {
        final Stack<Value> stack = (Stack<Value>) thiz.get("#INSTANCE#")
                .getObject();
        stack.clear();
    }
}
