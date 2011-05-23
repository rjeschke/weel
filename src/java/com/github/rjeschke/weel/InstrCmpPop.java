/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrCmpPop implements Instr
{
    Alu2InstrType type;
    Value value = null;
    
    public InstrCmpPop(final Alu2InstrType type)
    {
        this.type = type;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.CMPPOP;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        if(this.value != null)
            return this.type.toString().toUpperCase() + "POP " + this.value;
        return this.type.toString().toUpperCase() + "POP";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        if(this.value != null)
        {
            final double val = this.value.getNumber(); 
            final int iVal = (int) val;
            if (iVal == val)
            {
                mw.ldc(iVal);
                mw.addOp(JvmOp.I2D);
                mw.add(1);
            }
            else
            {
                final float check = (float) val;
                if (Double.compare(check, val) == 0)
                {
                    mw.ldc(check);
                    mw.addOp(JvmOp.F2D);
                    mw.add(1);
                }
                else
                {
                    mw.ldc(val);
                }
            }
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", this.type.toString() + "Pop", "(D)Z");
        }
        else
        {
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", this.type.toString() + "Pop", "()Z");
        }
    }
}
