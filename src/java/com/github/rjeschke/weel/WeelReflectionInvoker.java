/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.lang.reflect.Method;

/**
 * Weel reflection function invoker.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class WeelReflectionInvoker implements WeelInvoker
{
    /** Method for invocation. */
    private Method method;
    /** Are we initialized? */
    private boolean initialized = false;

    /** @see com.github.rjeschke.weel.WeelInvoker#initialize(WeelFunction) */
    @Override
    public void initialize(Weel weel, WeelFunction function)
    {
        if (this.initialized)
            return;

        try
        {
            final Class<?> clazz = function.loader == null ? weel.classLoader
                    .loadClass(function.clazz) : function.loader
                    .loadClass(function.clazz);
            this.method = clazz.getDeclaredMethod(function.javaName,
                    WeelRuntime.class);
        }
        catch (Exception e)
        {
            if (e instanceof WeelException)
                throw (WeelException) e;
            if (e.getCause() != null && e.getCause() instanceof WeelException)
                throw (WeelException) e.getCause();
            throw new WeelException(e);
        }
        this.initialized = true;
    }

    /** @see com.github.rjeschke.weel.WeelInvoker#invoke(WeelRuntime) */
    @Override
    public void invoke(WeelRuntime runtime)
    {
        try
        {
            this.method.invoke(null, runtime);
        }
        catch (Exception e)
        {
            if (e instanceof WeelException)
                throw (WeelException) e;
            if (e.getCause() != null && e.getCause() instanceof WeelException)
                throw (WeelException) e.getCause();
            throw new WeelException(e);
        }
    }

    /**
     * @see com.github.rjeschke.weel.WeelInvoker#invoke(WeelRuntime,
     *      WeelFunction)
     */
    @Override
    public void invoke(WeelRuntime runtime, WeelFunction function)
    {
        try
        {
            runtime.initVirtual(function);
            this.method.invoke(null, runtime);
            runtime.exitVirtual();
        }
        catch (Exception e)
        {
            if (e instanceof WeelException)
                throw (WeelException) e;
            if (e.getCause() != null && e.getCause() instanceof WeelException)
                throw (WeelException) e.getCause();
            throw new WeelException(e);
        }
    }
}
