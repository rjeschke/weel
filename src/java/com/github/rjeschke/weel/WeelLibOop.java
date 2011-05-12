/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
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
    private final static void callNew(final Runtime runtime, final int args)
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
    public final static void weelNew(final Runtime runtime)
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
    public final static void weelNew2(final Runtime runtime)
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
    public final static void weelNew3(final Runtime runtime)
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
    public final static void weelNew4(final Runtime runtime)
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
    public final static void weelNew5(final Runtime runtime)
    {
        callNew(runtime, 4);
    }
}
