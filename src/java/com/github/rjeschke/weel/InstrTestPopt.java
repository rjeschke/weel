/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrTestPopt implements Instr
{
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.TESTPOPT;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "TESTPOPT";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "testPopTrue", "()Z");
    }
}
