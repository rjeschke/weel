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
     * <code>mapToList(map)</code>
     * <p>
     * Transforms a map into a list.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapToList(WeelRuntime runtime)
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
     * <code>mapIsList(map)</code>
     * <p>
     * Returns true if this map is a list.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapIsList(WeelRuntime runtime)
    {
        final ValueMap in = runtime.popMap();
        runtime.load(in.ordered);
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
    public final static void mapClone(WeelRuntime runtime)
    {
        runtime.load(runtime.popMap().clone());
    }

    /**
     * <code>mapReverse(m)</code>
     * <p>
     * Reverses the order of the given map.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapReverse(WeelRuntime runtime)
    {
        runtime.load(runtime.popMap().reverse());
    }

    /**
     * <code>mapHasKey(m, k)</code>
     * <p>
     * Checks if the map 'm' contains key 'k'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 2, returnsValue = true)
    public final static void mapHasKey(WeelRuntime runtime)
    {
        final Value key = runtime.pop();
        runtime.load(runtime.popMap().hasKey(key));
    }
}
