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
