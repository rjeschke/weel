/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.FileOutputStream;

import com.github.rjeschke.weel.annotations.WeelClass;
import com.github.rjeschke.weel.annotations.WeelRawMethod;

@WeelClass(name = "myclass", usesOop = true)
public class Main
{
    @WeelRawMethod(args = 1)
    public final static void method(final Runtime runtime)
    {
        final ValueMap me = runtime.popMap();
        System.out.println("This is me: " + me);
    }
    
    public static void main(String[] args)
    {
        try
        {
            final Weel weel = new Weel();
            weel.importFunctions(Main.class);
            
            byte[] cdata = weel.compile(Main.class.getResourceAsStream("/com/github/rjeschke/weel/test/test.weel"), "test.weel");
            //byte[] cdata = weel.compile(Main.class.getResourceAsStream("/com/github/rjeschke/weel/test/bench1.weel"), "bench1.weel");
            weel.compile(Main.class.getResourceAsStream("/com/github/rjeschke/weel/test/wunitArith.weel"), "wunitArith.weel");

            FileOutputStream fos = new FileOutputStream(
                    "/home/rjeschke/Script0.class");
            fos.write(cdata);
            fos.close();
            
            weel.runStatic();
            //weel.getRuntime().wipeStack();
            //WeelUnit.runTests(weel);
            
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
