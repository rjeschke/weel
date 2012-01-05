/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
