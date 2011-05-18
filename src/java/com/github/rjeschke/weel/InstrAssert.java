/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrAssert implements Instr
{
    String error;
    
    public InstrAssert(String string)
    {
        this.error = string;
    }

    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.ASSERT;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "ASSERT " + this.error;
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.error);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "weelAssert", "(Ljava/lang/String;)V");
    }
}
