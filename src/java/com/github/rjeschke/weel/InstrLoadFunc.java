/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrLoadFunc implements Instr
{
    int index;
    
    public InstrLoadFunc(final int index)
    {
        this.index = index;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.LOADFUNC;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "LOADFUNC #" + this.index;
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.index);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "loadFunc", "(I)V");
    }
}
