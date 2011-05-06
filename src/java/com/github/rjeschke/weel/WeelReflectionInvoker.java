/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Weel reflection function invoker.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class WeelReflectionInvoker implements WeelInvoker
{
    /** The Weel function. */
    private WeelFunction function;
    /** Method for invocation. */
    private Method method;
    /** Are we initialized? */
    private boolean initialized = false;

    /** @see com.github.rjeschke.weel.WeelInvoker#initialize(WeelFunction) */
    @Override
    public void initialize(Weel weel, WeelFunction function)
    {
        if(this.initialized)
            return;
        
        this.function = function;

        try
        {
            final Class<?> clazz = weel.classLoader.loadClass(function.clazz);
            this.method = clazz.getDeclaredMethod(function.javaName,
                    Runtime.class);
        }
        catch (ClassNotFoundException e)
        {
            throw new WeelException(e);
        }
        catch (SecurityException e)
        {
            throw new WeelException(e);
        }
        catch (NoSuchMethodException e)
        {
            throw new WeelException(e);
        }
        this.initialized = true;
    }

    /** @see com.github.rjeschke.weel.WeelInvoker#invoke(Runtime) */
    @Override
    public void invoke(Runtime runtime)
    {
        try
        {
            this.method.invoke(this.function.instance, runtime);
        }
        catch (IllegalArgumentException e)
        {
            throw new WeelException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new WeelException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new WeelException(e);
        }
    }

    /** @see com.github.rjeschke.weel.WeelInvoker#invoke(Runtime, WeelFunction) */
    @Override
    public void invoke(Runtime runtime, WeelFunction function)
    {
        try
        {
            runtime.initVirtual(function);
            this.method.invoke(this.function.instance, runtime);
            runtime.exitVirtual();
        }
        catch (IllegalArgumentException e)
        {
            throw new WeelException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new WeelException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new WeelException(e);
        }
    }
}
