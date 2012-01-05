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

import java.util.HashMap;

/**
 * Weel Value type enumeration.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public enum ValueType
{
    /** NULL. */
    NULL,
    /** A double. */
    NUMBER,
    /** A String. */
    STRING,
    /** A ValueMap. */
    MAP,
    /** A function. */
    FUNCTION,
    /** An object. */
    OBJECT;

    /** Name to enum mapping. */
    private final static HashMap<String, ValueType> map = new HashMap<String, ValueType>();

    static
    {
        for (final ValueType v : ValueType.values())
        {
            map.put(v.toString(), v);
        }
    }

    /**
     * Returns an enum from a String.
     * 
     * @param value
     *            The name.
     * @return The enum or <code>null</code>.
     */
    public final static ValueType fromString(final String value)
    {
        return map.get(value);
    }
}
