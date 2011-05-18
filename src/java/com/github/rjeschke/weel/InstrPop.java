/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrPop implements Instr
{
    int pops;
    
    public InstrPop(final int pops)
    {
        this.pops = pops;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.POP;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "POP(" + this.pops + ")";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        if(this.pops == 1)
        {
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "pop1", "()V");
        }
        else
        {
            mw.ldc(this.pops);
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "pop", "(I)V");
        }
    }
}
