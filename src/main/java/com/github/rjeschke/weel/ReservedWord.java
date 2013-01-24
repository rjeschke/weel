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
 * Reserved words enumeration.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
enum ReservedWord
{
    BREAK,

    CASE, CONTINUE,

    DEFAULT, DO,

    ELSE, ELSEIF, END, EXIT,

    FALSE, FOR, FOREACH, FUNC,

    GLOBAL,

    IF, IN,

    LOCAL,

    NULL,

    OUTER,
    
    PRIVATE,
    
    RETURN,

    SUB, SWITCH,

    THEN, THIS, TRUE,

    UNTIL,

    WHILE;

    /** String to enum mapping. */
    private final static HashMap<String, ReservedWord> map = new HashMap<String, ReservedWord>();

    static
    {
        for (final ReservedWord e : ReservedWord.values())
        {
            map.put(e.toString().toLowerCase(), e);
        }
    }

    /**
     * Maps a String to a ReservedWord.
     * 
     * @param value
     *            The String to map.
     * @return The ReservedWord or <code>null</code> if the value doesn't map to
     *         a ReservedWord.
     */
    public static ReservedWord fromString(final String value)
    {
        return map.get(value);
    }
}
