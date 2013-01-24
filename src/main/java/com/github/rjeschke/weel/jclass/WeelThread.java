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

import com.github.rjeschke.weel.WeelException;
import com.github.rjeschke.weel.WeelRuntime;
import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.Weel;
import com.github.rjeschke.weel.WeelFunction;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel thread implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "java.Thread", usesOop = true)
public final class WeelThread
{
    private WeelThread()
    {
        //
    }
    
    /**
     * Constructor.
     * 
     * @param runtime
     *            The runtime.
     * @param thiz
     *            This.
     * @param func
     *            The thread function.
     */
    @WeelMethod
    public final static void ctor(final WeelRuntime runtime,
            final ValueMap thiz, final WeelFunction func)
    {
        if(func.getNumArguments() != 0)
        {
            throw new WeelException("Illegal thread function: " + func);
        }
        WeelOop.setInstance(thiz, new ThreadImpl(runtime.getMother(), func,
                null));
    }

    /**
     * Constructor.
     * 
     * @param runtime
     *            The runtime.
     * @param thiz
     *            This.
     * @param func
     *            The thread function.
     * @param finFunc
     *            The function to call when the thread is finished.
     */
    @WeelMethod
    public final static void ctor(final WeelRuntime runtime,
            final ValueMap thiz, final WeelFunction func,
            final WeelFunction finFunc)
    {
        if(func.getNumArguments() != 0)
        {
            throw new WeelException("Illegal thread function: " + func);
        }
        if(finFunc.getNumArguments() > 1)
        {
            throw new WeelException("Illegal thread finish function: " + finFunc);
        }
        WeelOop.setInstance(thiz, new ThreadImpl(runtime.getMother(), func,
                finFunc));
    }

    /**
     * Starts this thread.
     * 
     * @param runtime
     *            The runtime.
     * @param thiz
     *            This.
     */
    @WeelMethod
    public final static void start(final WeelRuntime runtime,
            final ValueMap thiz)
    {
        final ThreadImpl t = WeelOop.getInstance(thiz, ThreadImpl.class);
        t.start();
    }

    /**
     * Weel thread impl.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    private final static class ThreadImpl extends Thread
    {
        /** The weel. */
        private final Weel weel;
        /** The functions. */
        private final WeelFunction func, finFunc;

        /**
         * Constructor.
         * 
         * @param weel
         *            The weel.
         * @param func
         *            The thread function.
         * @param finFunc
         *            The thread finish function.
         */
        public ThreadImpl(final Weel weel, final WeelFunction func,
                final WeelFunction finFunc)
        {
            this.func = func;
            this.finFunc = finFunc;
            this.weel = weel;
            this.setDaemon(true);
        }

        /** @see java.lang.Thread#run() */
        @Override
        public void run()
        {
            final WeelRuntime runtime = this.weel.getRuntime();

            this.func.invoke(runtime);

            if (this.finFunc != null)
            {
                if (this.finFunc.getNumArguments() == 1
                        && !this.func.returnsValue())
                    runtime.load();
                else if (this.finFunc.getNumArguments() == 0
                        && this.func.returnsValue())
                    runtime.pop1();
                this.finFunc.invoke(runtime);
            }
            else
            {
                if (this.func.returnsValue())
                    runtime.pop1();
            }
        }
    }
}
