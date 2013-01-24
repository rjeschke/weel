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

import java.io.BufferedReader;
import java.io.IOException;

import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel buffered reader implementation.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
@WeelClass(name = "reader", isPrivate = true, usesOop = true)
public final class WeelReader
{
    public static ValueMap ME;

    private WeelReader()
    {
        // empty
    }
    
    /** @see java.io.BufferedReader#read() */
    @WeelMethod
    public final static int read(final ValueMap thiz)
    {
        final BufferedReader r = WeelOop
                .getInstance(thiz, BufferedReader.class);
        try
        {
            return r.read();
        }
        catch(IOException e)
        {
            return -2;
        }
    }

    /** @see java.io.BufferedReader#readLine() */
    @WeelMethod
    public final static String readln(final ValueMap thiz)
    {
        final BufferedReader r = WeelOop
                .getInstance(thiz, BufferedReader.class);
        try
        {
            return r.readLine();
        }
        catch(IOException e)
        {
            return null;
        }
    }

    /** @see java.io.BufferedReader#close() */
    @WeelMethod
    public final static void close(final ValueMap thiz)
    {
        final BufferedReader r = WeelOop
                .getInstance(thiz, BufferedReader.class);
        try
        {
            r.close();
        }
        catch(IOException e)
        {
            // ignore
        }
    }
}
