/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrStackCall implements Instr
{
    int paramc;
    boolean needsReturn;
    
    public InstrStackCall(final int paramc, final boolean needsReturn)
    {
        this.paramc = paramc;
        this.needsReturn = needsReturn;
    }

    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.STACKCALL;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "STACKCALL " + (this.needsReturn ? "func" : "sub") + "(" + this.paramc + ")";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.paramc);
        mw.ldc(this.needsReturn);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "stackCall", "(IZ)V");
    }
}
