/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrSpecialCall implements Instr
{
    String name;
    int paramc;
    boolean needsReturn;
    
    public InstrSpecialCall(final String name, final int paramc, final boolean needsReturn)
    {
        this.name = name;
        this.paramc = paramc;
        this.needsReturn = needsReturn;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.SPECIALCALL;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "SPECIALCALL " + (this.needsReturn ? "func " : "sub ") + this.name + "(" + this.paramc + ")";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.name + "#" + (this.paramc + 1));
        mw.ldc(this.paramc);
        mw.ldc(this.needsReturn);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "specialCall", "(Ljava/lang/String;IZ)V");
    }
}
