/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.util.Arrays;
import java.util.HashSet;

class InstrAlu2 implements Instr
{
    Alu2InstrType type;
    Value value = null;
    
    final static HashSet<Alu2InstrType> OVERLOADED = new HashSet<Alu2InstrType>(Arrays.asList(
            Alu2InstrType.add,
            Alu2InstrType.sub,
            Alu2InstrType.mul,
            Alu2InstrType.div,
            Alu2InstrType.mod,
            Alu2InstrType.pow,
            Alu2InstrType.cmpEq,
            Alu2InstrType.cmpNe,
            Alu2InstrType.cmpGt,
            Alu2InstrType.cmpGe,
            Alu2InstrType.cmpLt,
            Alu2InstrType.cmpLe
            ));
    
    public InstrAlu2(final Alu2InstrType type)
    {
        this.type = type;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.ALU2;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        if(this.value != null)
            return this.type.toString().toUpperCase() + " " + this.value;
        return this.type.toString().toUpperCase();
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
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", this.type.toString(), "(D)V");
        }
        else
        {
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", this.type.toString(), "()V");
        }
    }
}
