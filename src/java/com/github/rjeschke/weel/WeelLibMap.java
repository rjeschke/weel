/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.Map.Entry;

import com.github.rjeschke.weel.annotations.WeelRawMethod;

/**
 * Weel map/list library.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public final class WeelLibMap
{
    /**
     * <code>mapFlat(map)</code>
     * <p>
     * Transforms a map into a list.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapFlat(Runtime runtime)
    {
        final ValueMap in = runtime.popMap();
        final ValueMap out = new ValueMap();
        for (final Entry<Value, Value> e : in)
        {
            out.append(e.getValue());
        }
        runtime.load(out);
    }

    /**
     * <code>mapClone(m)</code>
     * <p>
     * Creates a clone of the given map.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapClone(Runtime runtime)
    {
        runtime.load(runtime.popMap().clone());
    }
}
