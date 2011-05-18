/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrBegAssert implements Instr
{
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.BEGASSERT;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "BEGASSERT";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        // empty
    }
}
