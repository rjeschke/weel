/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel.jclass;

import java.util.concurrent.Semaphore;

import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel semaphore implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "java.Semaphore", usesOop = true)
public class WeelSemaphore
{
    private WeelSemaphore()
    {
        //
    }
    
    /**
     * Constructor.
     * 
     * @param thiz This.
     * @param permits Number of permits.
     * @see java.util.concurrent.Semaphore#Semaphore(int)
     */
    @WeelMethod
    public final static void ctor(final ValueMap thiz, final int permits)
    {
        WeelOop.setInstance(thiz, new Semaphore(permits));
    }
    
    /**
     * Acquire.
     * 
     * @param thiz This.
     * @param permits Number of permits.
     * @return false if the operation was interrupted.
     * @see java.util.concurrent.Semaphore#acquire(int)
     */
    @WeelMethod
    public final static boolean acquire(final ValueMap thiz, final int permits)
    {
        final Semaphore sem = WeelOop.getInstance(thiz, Semaphore.class);
        try
        {
            sem.acquire(permits);
            return true;
        }
        catch (InterruptedException e)
        {
            return false;
        }
    }
    
    /**
     * Acquire.
     * 
     * @param thiz This.
     * @return false if the operation was interrupted.
     * @see java.util.concurrent.Semaphore#acquire()
     */
    @WeelMethod
    public final static boolean acquire(final ValueMap thiz)
    {
        final Semaphore sem = WeelOop.getInstance(thiz, Semaphore.class);
        try
        {
            sem.acquire();
            return true;
        }
        catch (InterruptedException e)
        {
            return false;
        }
    }

    /**
     * Release.
     * 
     * @param thiz This.
     * @param permits Number of permits.
     * @see java.util.concurrent.Semaphore#release(int)
     */
    @WeelMethod
    public final static void release(final ValueMap thiz, final int permits)
    {
        final Semaphore sem = WeelOop.getInstance(thiz, Semaphore.class);
        sem.release(permits);
    }

    /**
     * Release.
     * 
     * @param thiz This.
     * @see java.util.concurrent.Semaphore#release(int)
     */
    @WeelMethod
    public final static void release(final ValueMap thiz)
    {
        final Semaphore sem = WeelOop.getInstance(thiz, Semaphore.class);
        sem.release();
    }

    /**
     * Available permits.
     * 
     * @param thiz This.
     * @return The number of available permits.
     * @see java.util.concurrent.Semaphore#availablePermits()
     */
    @WeelMethod
    public final static double available(final ValueMap thiz)
    {
        final Semaphore sem = WeelOop.getInstance(thiz, Semaphore.class);
        return sem.availablePermits();
    }
}
