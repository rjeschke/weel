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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import com.github.rjeschke.weel.ValueMap;
import com.github.rjeschke.weel.WeelOop;
import com.github.rjeschke.weel.WeelRuntime;
import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

/**
 * Weel IO functions.
 * 
 * @author rjeschke
 *
 */
@WeelClass(name = "io")
public final class WeelIo
{
    private WeelIo()
    {
        //
    }
    
    @WeelMethod
    public final static boolean exists(final String filename)
    {
        return new File(filename).exists();
    }
    
    @WeelMethod
    public final static ValueMap openReader(final WeelRuntime runtime, final String filename)
    {
        return openReader(runtime, filename, "UTF-8");
    }
    
    @WeelMethod
    public final static ValueMap openStringReader(final WeelRuntime runtime, final String string)
    {
        final ValueMap clazz = WeelOop.newClass(runtime, WeelReader.ME);
        WeelOop.setInstance(clazz, new BufferedReader(new StringReader(string)));
        return clazz;
    }
    
    @WeelMethod
    public final static ValueMap openReader(final WeelRuntime runtime, final String filename, final String encoding)
    {
        final InputStream in;
        
        if(filename.equals(":"))
        {
            in = System.in;
        }
        else
        {
            try
            {
                in = new FileInputStream(filename);
            }
            catch(FileNotFoundException e)
            {
                return null;
            }
        }
        
        final ValueMap clazz = WeelOop.newClass(runtime, WeelReader.ME);
        try
        {
            WeelOop.setInstance(clazz, new BufferedReader(new InputStreamReader(in, encoding)));
        }
        catch(UnsupportedEncodingException e)
        {
            try
            {
                if(in != System.in)
                    in.close();
            }
            catch(IOException e1)
            {
                //
            }
            return null;
        }
        return clazz;
    }

    @WeelMethod
    public final static ValueMap openWriter(final WeelRuntime runtime, final String filename)
    {
        return openWriter(runtime, filename, "UTF-8");
    }
    
    @WeelMethod
    public final static ValueMap openWriter(final WeelRuntime runtime, final String filename, final String encoding)
    {
        final OutputStream out;
        
        if(filename.equals(":"))
        {
            out = System.out;
        }
        else
        {
            try
            {
                out = new FileOutputStream(filename);
            }
            catch(FileNotFoundException e)
            {
                return null;
            }
        }
        
        final ValueMap clazz = WeelOop.newClass(runtime, WeelWriter.ME);
        try
        {
            WeelOop.setInstance(clazz, new BufferedWriter(new OutputStreamWriter(out, encoding)));
        }
        catch(UnsupportedEncodingException e)
        {
            try
            {
                if(out != System.out)
                    out.close();
            }
            catch(IOException e1)
            {
                //
            }
            return null;
        }
        return clazz;
    }
}
