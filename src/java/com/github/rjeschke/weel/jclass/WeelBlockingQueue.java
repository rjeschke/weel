/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.jclass;

import java.util.concurrent.LinkedBlockingQueue;

import com.github.rjeschke.weel.Value;
import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel blocking queue implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "java.BlockingQueue", usesOop = true)
public final class WeelBlockingQueue
{
    private WeelBlockingQueue()
    {
        // empty
    }
    
    @WeelMethod
    public final static void ctor(final ValueMap thiz)
    {
        WeelOop.setInstance(thiz, new LinkedBlockingQueue<Value>());
    }

    /**
     * Gets a value, blocks until a value is available.
     * 
     * @param thiz This.
     * @return The value.
     */
    @SuppressWarnings("unchecked")
    @WeelMethod
    public final static Value take(final ValueMap thiz)
    {
        try
        {
            return ((LinkedBlockingQueue<Value>)thiz.get("#INSTANCE#").getObject()).take();
        }
        catch (InterruptedException e)
        {
            return new Value();
        }
    }

    /**
     * Sets a value.
     * 
     * @param thiz This.
     * @param value The value.
     */
    @SuppressWarnings("unchecked")
    @WeelMethod
    public final static void put(final ValueMap thiz, final Value value)
    {
        try
        {
            ((LinkedBlockingQueue<Value>)thiz.get("#INSTANCE#").getObject()).put(value);
        }
        catch (InterruptedException e)
        {
            //
        }
    }
}
