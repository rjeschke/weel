/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.FileOutputStream;

import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelMethod;

@WeelClass(name = "java.lang.StringBuilder", usesOop = true)
public class Main
{
    @WeelMethod()
    public final static void ctor(final ValueMap thiz)
    {
        WeelOop.setInstance(thiz, new StringBuilder());
    }
    
    @WeelMethod()
    public final static void ctor(final ValueMap thiz, final int size)
    {
        WeelOop.setInstance(thiz, new StringBuilder(size));
    }
    
    @WeelMethod
    public final static void append(final ValueMap thiz, final Value value)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        sb.append(value.toString());
    }

    @WeelMethod
    public final static void clear(final ValueMap thiz)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        sb.setLength(0);
    }
    
    @WeelMethod(name = "toString")
    public final static String sbToString(final ValueMap thiz)
    {
        final StringBuilder sb = WeelOop.getInstance(thiz, StringBuilder.class);
        return sb.toString();
    }
    
    public static void main(String[] args)
    {
        try
        {
            final Weel weel = new Weel();
            weel.setDebugMode(false);
            weel.importFunctions(Main.class);

            weel.compileResource("com.github.rjeschke.weel.test.test");
            weel.compileResource("com.github.rjeschke.weel.test.wunitArith");

            for(WeelLoader.ClassData cd : weel.classLoader.classData)
            {
                FileOutputStream fos = new FileOutputStream(
                        "/home/rjeschke/" + cd.name.substring(cd.name.lastIndexOf('.') + 1) + ".class");
                fos.write(cd.code);
                fos.close();
            }

//            for(final WeelFunction f : weel.functions)
//            {
//                System.out.println(f.index + " : " + f.toFullString());
//            }
            
            weel.runStatic();
            //weel.getRuntime().wipeStack();
//            WeelUnit.runTests(weel);
            
            if(weel.getRuntime().getStackPointer() != -1)
                System.err.println("Doh! You messed it up! (" + weel.getRuntime().getStackPointer() + ")");
        }
        catch (Exception e)
        {
            throw (e instanceof WeelException) ? (WeelException) e
                    : new WeelException(e);
        }
    }
}
