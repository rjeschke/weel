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

class InstrSpecialCall implements Instr
{
    String name;
    int paramc;
    boolean needsReturn;
    
    public InstrSpecialCall(final String name, final int paramc, final boolean needsReturn)
    {
        this.name = name;
        this.paramc = paramc;
        this.needsReturn = needsReturn;
    }
    
    /** @see Instr#getType() */
    @Override
    public Op getType()
    {
        return Op.SPECIALCALL;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString()
    {
        return "SPECIALCALL " + (this.needsReturn ? "func " : "sub ") + this.name + "(" + this.paramc + ")";
    }

    /** @see Instr#write(JvmMethodWriter) */
    @Override
    public void write(JvmMethodWriter mw)
    {
        mw.aload(0);
        mw.ldc(this.name + "#" + (this.paramc + 1));
        mw.ldc(this.paramc);
        mw.ldc(this.needsReturn);
        mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "specialCall", "(Ljava/lang/String;IZ)V");
    }
}
