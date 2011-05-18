/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrEndAssert implements Instr
{
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.ENDASSERT;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "ENDASSERT";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        // empty
    }
}
