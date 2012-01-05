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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Container for type bound functions.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
class TypeFunctions
{
    /** Function list. */
    ArrayList<WeelFunction> functions = new ArrayList<WeelFunction>();
    /** Name to function index mapping. */
    HashMap<String, Integer> mapFunctions = new HashMap<String, Integer>();

    /**
     * Constructor.
     */
    TypeFunctions()
    {
        // empty
    }
    /**
     * Finds the given function.
     * 
     * @param name
     *            The function's internal name.
     * @return The WeelFunction or <code>null</code> if none was found.
     */
    public WeelFunction findFunction(final String name)
    {
        final Integer index = this.mapFunctions.get(name.toLowerCase());
        return index != null ? this.functions.get(index) : null;
    }

    /**
     * Adds the given function to this function list.
     * 
     * @param iname
     *            The internal name.
     * @param func
     *            The function.
     */
    void addFunction(final String iname, final WeelFunction func)
    {
        this.addFunction(iname, func, true);
    }

    /**
     * Adds the given function to this function list.
     * 
     * @param iname
     *            The internal name.
     * @param func
     *            The function.
     * @param addToHash
     *            Add it to the function name hash map?
     */
    void addFunction(final String iname, final WeelFunction func,
            final boolean addToHash)
    {
        final int index = this.functions.size();
        func.index = index;
        this.functions.add(func);
        if (addToHash)
        {
            this.mapFunctions.put(iname, index);
        }
    }
}
