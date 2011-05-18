/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrLoad implements Instr
{
    Value value;
    
    public InstrLoad()
    {
        this.value = new Value();
    }
    
    public InstrLoad(final double v)
    {
        this.value = new Value(v);
    }
    
    public InstrLoad(final String v)
    {
        this.value = new Value(v);
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.LOAD;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "LOAD " + this.value;
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        switch(this.value.getType())
        {
        case NULL:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "load", "()V");
            break;
        case STRING:
            mw.ldc(this.value.getString());
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "load", "(Ljava/lang/String;)V");
            break;
        case NUMBER:
        {
            final double val = this.value.getNumber(); 
            final int iVal = (int) val;
            if (iVal == val)
            {
                mw.ldc(iVal);
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "load", "(I)V");
            }
            else
            {
                final float check = (float) val;
                if (Double.compare(check, val) == 0)
                {
                    mw.ldc(check);
                    mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "load", "(F)V");
                }
                else
                {
                    mw.ldc(val);
                    mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "load", "(D)V");
                }
            }
            break;
        }
        default:
            throw new WeelException("Illegal value: " + this.value);
        }
    }
}
