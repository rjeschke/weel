/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrBeginFor implements Instr
{
    int index;
    
    public InstrBeginFor(int index)
    {
        this.index = index;
    }

    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.BEGINFOR;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "BEGINFOR #" + this.index;
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.index);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "beginForLoop", "(I)Z");
    }
}
