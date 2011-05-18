/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrVarLoad implements Instr
{
    VarInstrType type;
    int index;
    
    public InstrVarLoad(final VarInstrType type, final int index)
    {
        this.type = type;
        this.index = index;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.VARLOAD;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        switch(this.type)
        {
        case LOCAL:
            return "LLOC #" + this.index;
        case PRIVATE:
            return "LPRIV #" + this.index;
        case GLOBAL:
            return "LGLOB #" + this.index;
        case CVAR:
            return "LINENV #" + this.index;
        }
        return "";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.index);
        switch(this.type)
        {
        case LOCAL:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "lloc", "(I)V");
            break;
        case PRIVATE:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "lpriv", "(I)V");
            break;
        case GLOBAL:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "lglob", "(I)V");
            break;
        case CVAR:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "linenv", "(I)V");
            break;
        }
    }
}
