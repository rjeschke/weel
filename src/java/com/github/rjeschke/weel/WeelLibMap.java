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
        for(int i = 0; i < in.size; i++)
        {
            out.append(in.data.get(i));
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
    
    /**
     * <code>mapRem(m, k)</code>
     * <p>
     * Removes the key 'k' (and its value) from 'm'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 2)
    public final static void mapRem(WeelRuntime runtime)
    {
        final Value key = runtime.pop();
        runtime.popMap().remove(key);
    }
    
    /**
     * <code>mapReml(m)</code>
     * <p>
     * Removes the last entry from 'm' and returns it.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapReml(WeelRuntime runtime)
    {
        runtime.load(runtime.popMap().removeLast());
    }
    
    /**
     * <code>mapLastK(m)</code>
     * <p>
     * Returns the last key from 'm'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapLastk(WeelRuntime runtime)
    {
        final ValueMap map = runtime.popMap();
        if(map.size == 0)
            runtime.load();
        else if(map.ordered)
            runtime.load(map.size - 1);
        else
            runtime.load(map.keys.get(map.size - 1));
    }
    
    /**
     * <code>mapLastV(m)</code>
     * <p>
     * Returns the last value from 'm'.
     * </p>
     * 
     * @param runtime
     *            The runtime.
     */
    @WeelRawMethod(args = 1, returnsValue = true)
    public final static void mapLastv(WeelRuntime runtime)
    {
        final ValueMap map = runtime.popMap();
        if(map.size == 0)
            runtime.load();
        else 
            runtime.load(map.data.get(map.size - 1));
    }
}
