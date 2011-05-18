/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrLabel implements Instr
{
    int index;
    int line;
    
    public InstrLabel(int index)
    {
        this.index = index;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.LABEL;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "L" + (this.index + 1);
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.addLabel(this.index);
    }
}
