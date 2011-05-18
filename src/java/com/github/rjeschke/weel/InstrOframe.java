/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrOframe implements Instr
{
    int args;
    int locals;
    
    public InstrOframe(final int args, final int locals)
    {
        this.args = args;
        this.locals = locals;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.OFRAME;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "OFRAME(" + this.args + ", " + this.locals + ")";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.args);
        mw.ldc(this.locals);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "openFrame", "(II)V");
    }
}
