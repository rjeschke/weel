/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrGoto implements Instr
{
    int index;
    
    public InstrGoto(int index)
    {
        this.index = index;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.GOTO;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "GOTO L" + (this.index + 1);
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.writeJmp(JvmOp.GOTO, this.index);
    }
}
