/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrCall implements Instr
{
    WeelFunction func;
    
    public InstrCall(final WeelFunction func)
    {
        this.func = func;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.CALL;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "CALL " + this.func;
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.invokeStatic(this.func.clazz, this.func.javaName, "(Lcom/github/rjeschke/weel/WeelRuntime;)V");
    }
}
