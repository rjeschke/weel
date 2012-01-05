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

class InstrCframe implements Instr
{
    int maxStack;
    boolean returns;
    
    public InstrCframe(final int maxStack, final boolean returns)
    {
        this.maxStack = maxStack;
        this.returns = returns;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return this.returns ? Op.CFRAMERET : Op.CFRAME;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return (this.returns ? "CFRAMERET" : "CFRAME") + "(" + this.maxStack + ")";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.maxStack);
        if(this.returns)
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "closeFrameRet", "(I)V");
        else
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "closeFrame", "(I)V");
    }
}
