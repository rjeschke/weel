/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
