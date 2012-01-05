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

/**
 * Helper class for variable/function compilation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
class Variable
{
    /** The name. */
    String name;
    /** The index. */
    int index;
    /** The type. */
    Type type = Type.NONE;
    /** The function. */
    WeelFunction function;

    /**
     * Constructor.
     */
    Variable()
    {
        //
    }
    
    /**
     * Check if this Variable contains a function.
     * 
     * @return <code>true</code> if so.
     */
    public boolean isFunction()
    {
        return this.function != null;
    }
    
    /**
     * Variable type enumeration.
     * 
     * @author René Jeschke <rene_jeschke@yahoo.de>
     */
    enum Type
    {
        GLOBAL,
        PRIVATE,
        LOCAL,
        CVAR,
        NONE
    }
}
