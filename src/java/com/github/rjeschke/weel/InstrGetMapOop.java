/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrGetMapOop implements Instr
{
    Value key = null;
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.GETMAPOOP;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        if(this.key != null)
            return "GETMAPOOP " + this.key;
        return "GETMAPOOP";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        if(this.key != null)
        {
            mw.ldc(this.key.getString());
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "getMapOop", "(Ljava/lang/String;)V");
        }
        else
        {
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "getMapOop", "()V");
        }
    }
}
