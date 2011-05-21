/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.jclass;

import java.util.concurrent.locks.ReentrantLock;

import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel ReentrantLock implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "java.lock", usesOop = true)
public final class WeelLock
{
    private WeelLock()
    {
        // empty
    }
    /**
     * Constructor.
     * @param thiz This.
     * @see java.util.concurrent.locks.ReentrantLock#ReentrantLock()
     */
    @WeelMethod
    public final static void ctor(final ValueMap thiz)
    {
        WeelOop.setInstance(thiz, new ReentrantLock(true));
    }

    /**
     * Lock.
     * 
     * @param thiz This.
     * @see java.util.concurrent.locks.ReentrantLock#lock()
     */
    @WeelMethod
    public final static void lock(final ValueMap thiz)
    {
        final ReentrantLock rl = WeelOop.getInstance(thiz, ReentrantLock.class);
        rl.lock();
    }

    /**
     * Unlock.
     * 
     * @param thiz This.
     * @see java.util.concurrent.locks.ReentrantLock#unlock()
     */
    @WeelMethod
    public final static void unlock(final ValueMap thiz)
    {
        final ReentrantLock rl = WeelOop.getInstance(thiz, ReentrantLock.class);
        rl.unlock();
    }
}
