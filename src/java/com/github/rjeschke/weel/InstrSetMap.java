/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrSetMap implements Instr
{
    Value key = null;
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.SETMAP;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        if(this.key != null)
            return "SETMAP " + this.key;
        return "SETMAP";
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
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "setMap", "(I)V");
            }
            else
            {
                mw.ldc(this.key.string);
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "setMap", "(Ljava/lang/String;)V");
            }
        }
        else
        {
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "setMap", "()V");
        }
    }
}
