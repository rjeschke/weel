/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrGetMap implements Instr
{
    Value key = null;
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.GETMAP;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        if(this.key != null)
            return "GETMAP " + this.key;
        return "GETMAP";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        if(this.key != null)
        {
            if(this.key.type == ValueType.NUMBER)
            {
                mw.ldc((int)this.key.number);
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "getMap", "(I)V");
            }
            else
            {
                mw.ldc(this.key.string);
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "getMap", "(Ljava/lang/String;)V");
            }
        }
        else
        {
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "getMap", "()V");
        }
    }
}
