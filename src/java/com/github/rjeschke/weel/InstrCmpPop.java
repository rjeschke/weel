/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrCmpPop implements Instr
{
    Alu2InstrType type;
    
    public InstrCmpPop(final Alu2InstrType type)
    {
        this.type = type;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.CMPPOP;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return this.type.toString().toUpperCase() + "POP";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", this.type.toString() + "Pop", "()Z");
    }
}
