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
                mw.ldc((String)this.key.object);
                mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "setMap", "(Ljava/lang/String;)V");
            }
        }
        else
        {
            mw.invokeVirtual("com.github.rjeschke.weel.WeelRuntime", "setMap", "()V");
        }
    }
}
