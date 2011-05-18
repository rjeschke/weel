/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrCframe implements Instr
{
    int maxStack;
    boolean returns;
    
    public InstrCframe(final int maxStack, final boolean returns)
    {
        this.maxStack = maxStack;
        this.returns = returns;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return this.returns ? Op.CFRAMERET : Op.CFRAME;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return (this.returns ? "CFRAMERET" : "CFRAME") + "(" + this.maxStack + ")";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.maxStack);
        if(this.returns)
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "closeFrameRet", "(I)V");
        else
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "closeFrame", "(I)V");
    }
}
