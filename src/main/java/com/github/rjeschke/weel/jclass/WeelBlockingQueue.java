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
