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
     * <code>new(class)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 1, returnsValue = true)
    public final static void weelNew(final Runtime runtime)
    {
        final ValueMap base = runtime.popMap();
        runtime.load(WeelOop.newClass(runtime, base));
    }

    /**
     * <code>new(class, args)</code>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(name = "new", args = 2, returnsValue = true)
    public final static void weelNew2(final Runtime runtime)
    {
        final Value args = runtime.pop();
        final ValueMap base = runtime.popMap();
        runtime.load(WeelOop.newClass(runtime, base, args));
    }
}
