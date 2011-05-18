/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

class InstrVarStore implements Instr
{
    VarInstrType type;
    int index;
    
    public InstrVarStore(final VarInstrType type, final int index)
    {
        this.type = type;
        this.index = index;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.VARSTORE;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        switch(this.type)
        {
        case LOCAL:
            return "SLOC #" + this.index;
        case PRIVATE:
            return "SPRIV #" + this.index;
        case GLOBAL:
            return "SGLOB #" + this.index;
        case CVAR:
            return "SINENV #" + this.index;
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
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "sloc", "(I)V");
            break;
        case PRIVATE:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "spriv", "(I)V");
            break;
        case GLOBAL:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "sglob", "(I)V");
            break;
        case CVAR:
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "sinenv", "(I)V");
            break;
        }
    }
}
