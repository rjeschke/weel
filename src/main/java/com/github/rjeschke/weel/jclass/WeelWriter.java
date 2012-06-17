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
package com.github.rjeschke.weel.jclass;

import java.io.BufferedWriter;
import java.io.IOException;

import com.github.rjeschke.weel.Value;
import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel buffered writer implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "writer", isPrivate = true, usesOop = true)
public final class WeelWriter
{
    public static ValueMap ME;
    
    private WeelWriter()
    {
        // empty
    }
    
    /** @see java.io.BufferedWriter#write(int) */
    @WeelMethod
    public final static boolean write(final ValueMap thiz, final Value val)
    {
        final BufferedWriter w = WeelOop
                .getInstance(thiz, BufferedWriter.class);
        try
        {
            if(val.isNumber())
            {
                w.write((int) val.getNumber());
            }
            else
            {
                w.write(val.getString());
            }
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    /** @see java.io.BufferedWriter#flush() */
    @WeelMethod
    public final static boolean flush(final ValueMap thiz)
    {
        final BufferedWriter w = WeelOop
                .getInstance(thiz, BufferedWriter.class);
        try
        {
            w.flush();
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    /** @see java.io.BufferedWriter#newLine() */
    @WeelMethod
    public final static boolean newLine(final ValueMap thiz)
    {
        final BufferedWriter w = WeelOop
                .getInstance(thiz, BufferedWriter.class);
        try
        {
            w.newLine();
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    /** @see java.io.BufferedWriter#close() */
    @WeelMethod
    public final static void close(final ValueMap thiz)
    {
        final BufferedWriter w = WeelOop
                .getInstance(thiz, BufferedWriter.class);
        try
        {
            w.close();
        }
        catch(IOException e)
        {
            // ignore
        }
    }
}
