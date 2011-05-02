/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

import java.io.FileOutputStream;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            final Weel weel = new Weel();
            final Compiler compiler = new Compiler(weel);
            
            long t0 = System.nanoTime();
            compiler.compile(Main.class.getResourceAsStream("/com/github/rjeschke/weel/test/test.weel"));
            t0 = System.nanoTime() - t0;

            byte[] cdata = compiler.classWriter.build();

            FileOutputStream fos = new FileOutputStream(
                    "/home/rjeschke/Script0.class");
            fos.write(cdata);
            fos.close();

            long t1 = System.nanoTime();
            weel.runStatic();
            t1 = System.nanoTime() - t1;
            
            System.out.println();
            System.out.println("Compile: " + (t0 / 1e9) + " sec");
            System.out.println("Execute: " + (t1 / 1e9) + " sec");

            if(weel.getRuntime().getStackPointer() != -1)
                System.err.println("Doh! You messed it up!");
        }
        catch (Exception e)
        {
            throw (e instanceof WeelException) ? (WeelException) e
                    : new WeelException(e);
        }
    }
}
