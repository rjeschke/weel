/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
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
