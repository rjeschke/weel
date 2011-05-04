/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
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

    LOCAL, LOCK,

    NULL,

    OUTER,
    
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
