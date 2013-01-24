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

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel Oop library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public class WeelLibOop
{
    /**
     * Calls 'new' using a variable amount of arguments.
     * 
     * @param runtime
     *            The runtime.
     * @param args
     *            Number of arguments.
     */
    private final static void callNew(final WeelRuntime runtime, final int args)
    {
        final Value[] a = new Value[args];
        for (int i = 0; i < args; i++)
        {
            a[args - 1 - i] = runtime.pop();
        }
        final ValueMap base = runtime.popMap();
        runtime.load(WeelOop.newClass(runtime, base, a));
    }

    /**
     * <code>new(class)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 1, returnsValue = true)
    public final static void weelNew(final WeelRuntime runtime)
    {
        callNew(runtime, 0);
    }

    /**
     * <code>new(class, arg0)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 2, returnsValue = true)
    public final static void weelNew2(final WeelRuntime runtime)
    {
        callNew(runtime, 1);
    }

    /**
     * <code>new(class, arg0, arg1)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 3, returnsValue = true)
    public final static void weelNew3(final WeelRuntime runtime)
    {
        callNew(runtime, 2);
    }

    /**
     * <code>new(class, arg0, arg1, arg2)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 4, returnsValue = true)
    public final static void weelNew4(final WeelRuntime runtime)
    {
        callNew(runtime, 3);
    }

    /**
     * <code>new(class, arg0, arg1, arg2, arg3)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 5, returnsValue = true)
    public final static void weelNew5(final WeelRuntime runtime)
    {
        callNew(runtime, 4);
    }
    
    /**
     * <code>new(class, arg0, arg1, arg2, arg3, arg4)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 6, returnsValue = true)
    public final static void weelNew6(final WeelRuntime runtime)
    {
        callNew(runtime, 5);
    }
}
